package loyalty.payment;

import atg.repository.RepositoryException;
import loyalty.LoyaltyTransactionException;

public interface LoyaltyPointsProcessor {
	
	 /**
	   * Authorize the amount in LoyaltyPoints
	   *
	   * @param loyaltyPointsInfo the LoyaltyPointsInfo reference which contains all the authorization data
	   * @return LoyaltyPointsStatus object detailing the results of the authorization
	   * @throws LoyaltyTransactionException 
	   */
	  public LoyaltyPointsStatus authorize(LoyaltyPointsInfo loyaltyPointsInfo) throws LoyaltyTransactionException;

	  /**
	   * Debit the amount in LoyaltyPoints after authorization
	   *
	   * @param loyaltyPointsInfo the LoyaltyPointsInfo reference which contains all the debit data
	   * @param status the LoyaltyPointsStatus object which contains information about transaction. 
	   *        This should be the object which was returned from authorize().
	   * @return LoyaltyPointsStatus object detailing the results of the debit
	   * @throws LoyaltyTransactionException 
	   */
	  public LoyaltyPointsStatus debit(LoyaltyPointsInfo loyaltyPointsInfo, LoyaltyPointsStatus status) throws LoyaltyTransactionException;

	  /**
	   * Credit the amount in LoyaltyPoints after debiting
	   *
	   * @param loyaltyPointsInfo the LoyaltyPointsInfo reference which contains all the credit data
	   * @param status the LoyaltyPointsStatus object which contains information about the transaction. 
	   *        This should be the object which was returned from debit().
	   * @return LoyaltyPointsStatus object detailing the results of the credit
	 * @throws LoyaltyTransactionException 
	   */
	  public LoyaltyPointsStatus credit(LoyaltyPointsInfo loyaltyPointsInfo, LoyaltyPointsStatus status) throws LoyaltyTransactionException;

}
