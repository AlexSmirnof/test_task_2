package loyalty;

import atg.repository.Repository;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.MutableRepositoryItem;

import javax.transaction.TransactionManager;
import javax.transaction.SystemException;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import java.util.Collection;
import java.util.Iterator;

public class LoyaltyManager extends atg.nucleus.GenericService {

	private static final String ITEM_TYPE_NAME = "loyaltyTransaction";
	private static final String LIST_PROPERTY_NAME = "loyaltyTransactions";
	public static final String AMOUNT_PROPERTY_NAME = "loyaltyAmount";

	private TransactionManager transactionManager;
	private Repository loyaltyRepository;
	private Repository userRepository;
	private String rolePath;

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}
	public void setLoyaltyRepository(Repository loyaltyRepository) {
		this.loyaltyRepository = loyaltyRepository;
	}
	public Repository getLoyaltyRepository() {
		return loyaltyRepository;
	}
	public void setUserRepository(Repository userRepository) {
		this.userRepository = userRepository;
	}
	public Repository getUserRepository() {
		return userRepository;
	}
	public String getRolePath() {
		return rolePath;
	}
	public void setRolePath(String rolePath) {
		this.rolePath = rolePath;
	}
	/**
	 * This method count actual number of LoyaltyPoints for user. 
	 * @param userId
	 * @return number of LoyaltyPoints.
	 * @throws RepositoryException
	 */
	public int getLoyaltyPointsAmountByUser(String userId) throws RepositoryException {
		RepositoryItem profile = null;
		Collection loyaltyTransactionsList = null;
		try {
			profile = getUserRepository().getItem(userId, "user");
			loyaltyTransactionsList = (Collection) profile.getPropertyValue(LIST_PROPERTY_NAME);
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError("Exception occured in getLoyaltyPointsAmountByUser trying to get user from repository", e);
			}
			throw e;
		}
		return countPoints(loyaltyTransactionsList, "amount");
	}
	
	/**
	 * This method creates transaction and add or debit LoyaltyPoints to user.
	 * @param amount Number of LoyaltyPoints.
	 * @param description Description of transaction.
	 * @param userId
	 * @return TransactionId.
	 * @throws LoyaltyTransactionException
	 */
	public String addCreatedLoyaltyTransactionToUser(int amount, String description, String userId) throws LoyaltyTransactionException {
		MutableRepository mutUser = (MutableRepository) getUserRepository();
		MutableRepository mutLoyaltyRepository = (MutableRepository) getLoyaltyRepository();
		MutableRepositoryItem mutLoyaltyTransactionItem = null;
		String loyaltyTransactionId = null;
		try {
			TransactionDemarcation td = new TransactionDemarcation();
			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
			try {
				mutLoyaltyTransactionItem = mutLoyaltyRepository.createItem(LoyaltyManager.ITEM_TYPE_NAME);
				mutLoyaltyTransactionItem.setPropertyValue("amount", amount);
				mutLoyaltyTransactionItem.setPropertyValue("description", description);
				mutLoyaltyTransactionItem.setPropertyValue("user", userId);
				mutLoyaltyRepository.addItem(mutLoyaltyTransactionItem);
				
				loyaltyTransactionId = mutLoyaltyTransactionItem.getRepositoryId();
				addLoyaltyPointsToUser(loyaltyTransactionId);			
			} 
			catch (RepositoryException e) {
				if (isLoggingError()) {
					logError("Exception occured trying to create loyaltyTransaction", e);
				}
				try {
					getTransactionManager().setRollbackOnly();
				} catch (Exception se) {
					if (isLoggingError()) {
						logError("Unable to set rollback for transaction", se);
					}
				}
				throw new LoyaltyTransactionException("Exception occured trying to create LoyaltyTransaction" + "\nCause: " + e.getMessage());
			}
			catch (LoyaltyTransactionException e) {
				if (isLoggingError()) {
					logError("Exception occured trying to add created loyaltyTransaction to user", e);
				}
				try {
					getTransactionManager().setRollbackOnly();
				} catch (Exception se) {
					if (isLoggingError()) {
						logError("Unable to set rollback for transaction", se);
					}
				}
				throw new LoyaltyTransactionException("Exception occured trying to add created LoyaltyTransaction to user" + "\nCause: " + e.getMessage());
			}
		}
		catch (TransactionDemarcationException e) {
			if (isLoggingError()) {
				logError("Creating transaction demarcation failed, no loyaltyTransaction created", e);
			}
			throw new LoyaltyTransactionException("Creating transaction demarcation failed, no loyaltyTransaction created" + "\nCause: " + e.getMessage());
		}
		return loyaltyTransactionId;
	}
	
	/**
	 * This method add LoyaltyPoints from transaction created by loyaltyAdministrator user.
	 * @param loyaltyTransactionId
	 * @throws LoyaltyTransactionException
	 */
	public void addLoyaltyPointsToUser(String loyaltyTransactionId) throws LoyaltyTransactionException {

		if (isLoggingDebug()) {
			logDebug("adding loyalty points from" + loyaltyTransactionId + " to user`s transactions list and count amount");
		}

		MutableRepository mutRepository = (MutableRepository) getUserRepository();
		Repository loyaltyRepository = getLoyaltyRepository();

		RepositoryItem loyaltyTransaction;
		String userId;
		try {
			loyaltyTransaction = loyaltyRepository.getItem(loyaltyTransactionId, ITEM_TYPE_NAME);
			userId = (String) loyaltyTransaction.getPropertyValue("user");
		} catch (RepositoryException e) {
			throw new LoyaltyTransactionException("Exception occured trying to get loyaltyTransactionItem from Repository" + "\nCause: " + e.getMessage());
		}
		try {
			TransactionDemarcation td = new TransactionDemarcation();
			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
			try {

				addLoyaltyTransactionToUser(mutRepository, loyaltyTransaction, userId);

			} 
			catch (Exception e) {
				if (isLoggingError()) {
					logError("Exception occured trying to add loyaltyTransaction", e);
				}
				try {
					getTransactionManager().setRollbackOnly();
				} catch (final SystemException se) {
					if (isLoggingError()) {
						logError("Unable to set rollback for transaction", se);
					}
				}
				throw new LoyaltyTransactionException("Exception occured trying to add loyaltyTransaction" + "\nCause: " + e.getMessage());
			} 
			finally {
				td.end();
			}
		} catch (TransactionDemarcationException e) {
			if (isLoggingError()) {
				logError("Creating transaction demarcation failed, no loyalty points added", e);
			}
			throw new LoyaltyTransactionException("Creating transaction demarcation failed, no loyalty points added" + "\nCause: " + e.getMessage());
		}
	}
	
	/**
	 * Helper method that add transaction to list of user`s loyaltyTransactions and set actual number of LoyaltyPoints to user property loyaltyAmount.
	 * @param mutRepository
	 * @param loyaltyTransaction
	 * @param userId
	 * @throws Exception
	 */
	private void addLoyaltyTransactionToUser(MutableRepository mutRepository, RepositoryItem loyaltyTransaction, String userId) throws Exception {

		MutableRepositoryItem mutUser = mutRepository.getItemForUpdate(userId, "user");
		Collection loyaltyTransactionsList = (Collection) mutUser.getPropertyValue(LIST_PROPERTY_NAME);
		loyaltyTransactionsList.add(loyaltyTransaction);

		int amount = countPoints(loyaltyTransactionsList, "amount");

		mutUser.setPropertyValue(LIST_PROPERTY_NAME, loyaltyTransactionsList);
		mutUser.setPropertyValue(AMOUNT_PROPERTY_NAME, (Integer) amount);
		mutRepository.updateItem(mutUser);

	}

	/**
	 * Helper method that count actual number of LoyaltyPoints from user`s transaction list.
	 * @param list User`s transaction list.
	 * @param propertyName Name of LoyaltyPoints amount property.
	 * @return
	 */
	private int countPoints(Collection list, String propertyName) {
		int result = 0;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			RepositoryItem userLoyaltyTransaction = (RepositoryItem) iterator.next();
			int value = (Integer) userLoyaltyTransaction.getPropertyValue(propertyName);
			result += value;
		}
		return result;
	}

}