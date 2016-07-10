package loyalty.payment;

import java.util.Date;

import atg.commerce.CommerceException;
import atg.commerce.order.PaymentGroup;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.nucleus.GenericService;
import atg.payment.PaymentStatus;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import loyalty.LoyaltyManager;
import loyalty.LoyaltyTransactionException;
 
/**
 *  This class actually execute authorize/debit/credit action for pipeline processors.
 */
public class LoyaltyPointsProcessorImpl extends GenericService implements LoyaltyPointsProcessor {
	
	private static final String DESCRIPTION = "Debiting loyalty points for purchase order";
	private LoyaltyManager loyaltyManager;
	
	public LoyaltyPointsProcessorImpl() {}

	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}
	public void setLoyaltyManager(LoyaltyManager loyaltyManager) {
		this.loyaltyManager = loyaltyManager;
	}

	/**
	 * Authorize LoyaltyPoints PaymentGroup.
	 */
	public LoyaltyPointsStatus authorize(LoyaltyPointsInfo loyaltyPointsInfo) {
		LoyaltyManager loyaltyManager = getLoyaltyManager();
		LoyaltyPointsStatus loyaltyPointsStatus = new LoyaltyPointsStatus();
		loyaltyPointsStatus.setTransactionSuccess(true);
		
		String userId = loyaltyPointsInfo.getUserId();
		int numOfPoints = loyaltyPointsInfo.getNumberOfPoints();
		try {
			int profileNumOfPoints = loyaltyManager.getLoyaltyPointsAmountByUser(userId);			
			if ( profileNumOfPoints < numOfPoints ) {
				if (isLoggingError()){
	                logError("Your number of LoyaltyPoints is not enough: " + profileNumOfPoints + " < " + numOfPoints); 
				}
				loyaltyPointsStatus.setTransactionSuccess(false);
				loyaltyPointsStatus.setErrorMessage("Your number of LoyaltyPoints is not enough");
			}
		} 
		catch (RepositoryException e) {
			if (isLoggingError()){
                logError("Exception occured in authorize method trying to get number of LoyaltyPoints for user", e); 
			}
			loyaltyPointsStatus.setTransactionSuccess(false);
			loyaltyPointsStatus.setErrorMessage("Exception occured in authorize method trying to get number of LoyaltyPoints for user." +
												"\nCause: " + e.getMessage());
		} 
		return loyaltyPointsStatus;	
	}

	/**
	 * Dedit number of LoyaltyPoints in purchase order from user after <code>authorize</code> method.
	 */
	public LoyaltyPointsStatus debit(LoyaltyPointsInfo loyaltyPointsInfo, LoyaltyPointsStatus loyaltyPointsStatus) throws LoyaltyTransactionException {
		String userId = loyaltyPointsInfo.getUserId();
		int numberOfPoints = loyaltyPointsInfo.getNumberOfPoints();
		numberOfPoints = -numberOfPoints;
		String transactionId = getLoyaltyManager().addCreatedLoyaltyTransactionToUser(numberOfPoints, DESCRIPTION, userId);
		loyaltyPointsStatus.setAmount(numberOfPoints);
		loyaltyPointsStatus.setTransactionId(transactionId);
		loyaltyPointsStatus.setTransactionTimestamp(new Date());
		return loyaltyPointsStatus;
	}

	public LoyaltyPointsStatus credit(LoyaltyPointsInfo loyaltyPointsInfo, LoyaltyPointsStatus loyaltyPointsStatus) throws LoyaltyTransactionException {
		return loyaltyPointsStatus;
	}
	
	/**
	   * This method will obtain the <code>LoyaltyPointsInfo</code> object from params parameter and invoke <code>authorize</code> 
	   * and then invoke <code>debit</code> methods.
	   * @param params PaymentManagerPipelineArgs object which contains the LoyaltyPointsInfo object.
	   * @return a PaymentStatus object that will detail the authorize details
	   * @exception CommerceException if an error occurs
	   * @throws CommerceException
	   */
	public PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs params) throws CommerceException {
		LoyaltyPointsInfo loyaltyPointsInfo = null;
	    try {
	    	loyaltyPointsInfo = (LoyaltyPointsInfo) params.getPaymentInfo();
	    } 
	    catch (ClassCastException e) {
	      if (isLoggingError())
	        logError("Expecting class of type LoyaltyPointsInfo, but got: " + params.getPaymentInfo().getClass().getName());
	        throw e;
	    }
	    LoyaltyPointsStatus authStatus = authorize(loyaltyPointsInfo);	
	    if (authStatus.getTransactionSuccess()){
	    	try {
				authStatus = debit(loyaltyPointsInfo, authStatus);
			} 
	    	catch (LoyaltyTransactionException e) {
				if (isLoggingError()){
		               logError("Exception occured in debit LoyaltyPoints transaction", e); 
					}
				authStatus.setTransactionSuccess(false);
				authStatus.setErrorMessage("Exception occured in debit LoyaltyPoints transaction: " + e.getMessage());
			}
	    }
	    return authStatus;
	}
}
