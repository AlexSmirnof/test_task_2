package loyalty.checkout;

import atg.commerce.order.processor.ValidatePaymentGroupPipelineArgs;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import loyalty.payment.LoyaltyPoints;
import loyalty.payment.LoyaltyPointsConfig;

/**
 * This pipeline processor validate LoyaltyPoints payment group. 
 */
public class ProcValidateLoyaltyPoints extends GenericService implements PipelineProcessor {
	
	private static int SUCCESS_CODE = 1;
	private static int[] RETURN_CODES = { SUCCESS_CODE };
	
	private LoyaltyPointsConfig loyaltyPointsConfig;

	public ProcValidateLoyaltyPoints() {}

	public LoyaltyPointsConfig getLoyaltyPointsConfig() {
		return loyaltyPointsConfig;
	}
	public void setLoyaltyPointsConfig(LoyaltyPointsConfig loyaltyPointsConfig) {
		this.loyaltyPointsConfig = loyaltyPointsConfig;
	}

    /**
    *  Perform validation for LoyaltyPoints payment group.
    *  First check if user input points amount is greater than zero.
    *  Second check if user input points amount is not greater than allowed amount for order.
    **/
	private int validateLoyaltyPointsForOrder(ValidatePaymentGroupPipelineArgs params, PipelineResult result) {
		
		int resultCode = SUCCESS_CODE;
		LoyaltyPoints loyaltyPoints = (LoyaltyPoints) params.getPaymentGroup();
		
		double orderTotal = params.getOrder().getPriceInfo().getTotal();
		double maxPartOfAmount = getLoyaltyPointsConfig().getMaxPartOfAmount();
		double amountToPoints = getLoyaltyPointsConfig().getAmountToPoints();
		double pointsAmount = loyaltyPoints.getAmount();
		
	    double orderAllowedAmount = orderTotal * maxPartOfAmount;
	    int numberOfPoints = (int) (pointsAmount * amountToPoints);
	    
		if (isLoggingDebug()){
			logDebug("Validate LoyaltyPoints amount is " + pointsAmount + "\nOrder allowed amount is " + orderAllowedAmount);
		}
	    
		if ( pointsAmount <= 0 ) {
			if (isLoggingError()){
				logError("Points amount must be greater than zero");
			}
			resultCode = STOP_CHAIN_EXECUTION_AND_ROLLBACK;
			result.addError("NoPointsUsed", "Points amount must be greater than zero");
		} 
		else if ( orderAllowedAmount < pointsAmount ) {
			if (isLoggingError()){
				logError("LoyaltyPoints amount cannot pay for more than order`s allowed amount " + orderAllowedAmount);
			}
			resultCode = STOP_CHAIN_EXECUTION_AND_ROLLBACK;
			result.addError("AllowedAmountExceeded", "LoyaltyPoints amount cannot pay for more than order`s allowed amount " + orderAllowedAmount);				
		} 
		else {
			loyaltyPoints.setNumberOfPoints(numberOfPoints);
		}
		return resultCode;
	}
	
	public int runProcess(Object param, PipelineResult result) {
	
		ValidatePaymentGroupPipelineArgs params = (ValidatePaymentGroupPipelineArgs) param;
		try {
			return validateLoyaltyPointsForOrder(params, result);
		}
		catch(ClassCastException e){
			result.addError("ClassNotCasted", "Expected class type is " + LoyaltyPoints.class.getName() + 
					        ", but got " + params.getPaymentGroup().getClass().getName());
			return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
		}
	}
	
	/**
	 * Return list of possible return values from this processor. This processor always returns single value indicating success.  
	 * In case of errors, it adds messages to the pipeline result object.
	 **/
	public int[] getRetCodes() {
		return RETURN_CODES;
	}  
}
