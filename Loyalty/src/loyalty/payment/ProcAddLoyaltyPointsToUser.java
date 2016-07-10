package loyalty.payment;

import java.util.HashMap;

import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import loyalty.LoyaltyManager;
import loyalty.LoyaltyTransactionException;

/**
 * This processor calculate and add points to user corresponding to order total amount.
 */
public class ProcAddLoyaltyPointsToUser extends GenericService implements PipelineProcessor {

	private static final int SUCCESS = 1;
	private static final String DESCRIPTION = "Adding loyalty points for purchase order";
	
	private LoyaltyManager loyaltyManager;
	private LoyaltyPointsConfig loyaltyPointsConfig;
	
	public ProcAddLoyaltyPointsToUser() {}
	
	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}
	public void setLoyaltyManager(LoyaltyManager loyaltyManager) {
		this.loyaltyManager = loyaltyManager;
	}
	public LoyaltyPointsConfig getLoyaltyPointsConfig() {
		return loyaltyPointsConfig;
	}
	public void setLoyaltyPointsConfig(LoyaltyPointsConfig loyaltyPointsConfig) {
		this.loyaltyPointsConfig = loyaltyPointsConfig;
	}

	private void addLoyaltyPointsToUser(String userId, double orderTotal) throws LoyaltyTransactionException {
		double amountToPoints = loyaltyPointsConfig.getAmountToPoints();
		int pointsToAdd = (int) (orderTotal * amountToPoints);
		getLoyaltyManager().addCreatedLoyaltyTransactionToUser(pointsToAdd, DESCRIPTION, userId);
	}
	
	
	public int runProcess(Object param, PipelineResult result) {
		HashMap<String, Object> params = (HashMap<String, Object>) param;
		Order order = order = (Order) params.get(PipelineConstants.ORDER);	
		String userId = order.getProfileId();
		double orderTotal = order.getPriceInfo().getTotal();
		try {
			addLoyaltyPointsToUser(userId, orderTotal);
		}
		catch(Exception e){
			if (isLoggingError()){
	               logError("Exception occured in adding LoyaltyPoints to user for purchase process", e); 
			}
			result.addError("AddLoyaltyPointsError", e.getMessage());
			return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
		}
		return SUCCESS;
	}

	public int[] getRetCodes() {
		int[] retCodes = { SUCCESS };
		return retCodes;
	}
	
}
