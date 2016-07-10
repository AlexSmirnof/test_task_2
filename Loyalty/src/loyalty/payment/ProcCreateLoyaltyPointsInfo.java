package loyalty.payment;
 

import atg.commerce.order.Order;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This pipeline processor element is called to create generic LoyaltyPointsInfo objects 
 * from instances of the LoyaltyPoints payment group. 
 */
public class ProcCreateLoyaltyPointsInfo extends GenericService implements PipelineProcessor {

	private static final int SUCCESS = 1;
	
	private String loyaltyPointsInfoClass;
	private LoyaltyPointsConfig loyaltyPointsConfig;
	
	public String getLoyaltyPointsInfoClass() {
		return loyaltyPointsInfoClass;
	}
	public void setLoyaltyPointsInfoClass(String loyaltyPointsInfoClass) {
		this.loyaltyPointsInfoClass = loyaltyPointsInfoClass;
	}
	public LoyaltyPointsConfig getLoyaltyPointsConfig() {
		return loyaltyPointsConfig;
	}
	public void setLoyaltyPointsConfig(LoyaltyPointsConfig loyaltyPointsConfig) {
		this.loyaltyPointsConfig = loyaltyPointsConfig;
	}

	private LoyaltyPointsInfo getLoyaltyPointsInfo() throws Exception {
		if (isLoggingDebug()) {
		      logDebug("Making a new instance of type: " + getLoyaltyPointsInfoClass());
		}
		return (LoyaltyPointsInfo) Class.forName(getLoyaltyPointsInfoClass()).newInstance();
	}
	
	private void addDataToLPI(PaymentManagerPipelineArgs params) throws Exception {
		
		LoyaltyPointsInfo loyaltyPointsInfo = getLoyaltyPointsInfo();
		LoyaltyPoints loyaltyPoints = (LoyaltyPoints) params.getPaymentGroup();
		Order order = params.getOrder();
		
		LoyaltyPointsConfig loyaltyPointsConfig = getLoyaltyPointsConfig();
		loyaltyPointsInfo.setUserId(loyaltyPoints.getUserId());
		loyaltyPointsInfo.setNumberOfPoints(loyaltyPoints.getNumberOfPoints());
		loyaltyPointsInfo.setOrderTotal(order.getPriceInfo().getTotal());
		loyaltyPointsInfo.setLoyaltyPointsConfig(loyaltyPointsConfig);
		
		params.setPaymentInfo(loyaltyPointsInfo);
	}
	
	/**
	  * Generate LoyaltyPointsInfo object of the class specified by <code>loyaltyPointsInfoClass</code>, 
	  * populate it with data from <code>LoyaltyPoints</code> payment group by calling <code>addDataToLPI</code>,
	  * and add it to the pipeline argument dictionary so that downstream pipeline processors can access it.
	  * @param param Parameter dictionary of type PaymentManagerPipelineArgs.
	  * @param result Pipeline result object, not used by this method.
	  * @return An integer value used to determine which pipeline processor is called next.
	 * @throws Exception 
	  **/
	public int runProcess(Object param, PipelineResult result) throws Exception {
		PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs) param;
		
	    if (isLoggingDebug()) {
	        logDebug("Putting LoyaltyPointsInfo object into pipeline.\nGotten type of object is " + params.getPaymentGroup().getClass().getName());
	    }
		
		addDataToLPI(params);
	    
	    return SUCCESS;
	}

	/**
	* Return the possible return values for this processor.  
	*/
	public int[] getRetCodes() {
		int[] retCodes = { SUCCESS };
		return retCodes;
	}
}
