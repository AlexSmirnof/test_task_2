package loyalty.payment;

import atg.commerce.CommerceException;
import atg.commerce.order.OrderManager;
import atg.commerce.order.purchase.PaymentGroupInitializationException;
import atg.commerce.order.purchase.PaymentGroupInitializer;
import atg.commerce.order.purchase.PaymentGroupMapContainer;
import atg.servlet.DynamoHttpServletRequest;
import atg.userprofiling.Profile;
 
/**
 * This class adding LoyatyPoints payment group to PaymentGroupMapContainer for PaymentGroupDroplet.
 */
public class LoyaltyPointsInitializer implements PaymentGroupInitializer {
	
	private LoyaltyPointsConfig loyaltyPointsConfig;

	public LoyaltyPointsInitializer() {}
	
	public LoyaltyPointsConfig getLoyaltyPointsConfig() {
		return loyaltyPointsConfig;
	}
	public void setLoyaltyPointsConfig(LoyaltyPointsConfig loyaltyPointsConfig) {
		this.loyaltyPointsConfig = loyaltyPointsConfig;
	}

	/**
	 * This method is used by PaymentGroupDroplet to initialize LoyaltyPoints PaymentGroup and add it to PaymentGroupMapContainer.
	 */
	public void initializePaymentGroups(Profile profile, PaymentGroupMapContainer pgContainer, DynamoHttpServletRequest request)
			throws PaymentGroupInitializationException {
		
		String paymentGroupType = getLoyaltyPointsConfig().getPaymentGroupTypeName();
		String paymentGroupMethodName = getLoyaltyPointsConfig().getPaymentGroupMethodName();
		OrderManager orderManager = (OrderManager) request.resolveName("/atg/commerce/order/OrderManager");
		LoyaltyPoints loyaltyPoints = null;
		try {
			loyaltyPoints = (LoyaltyPoints) orderManager.getPaymentGroupManager().createPaymentGroup(paymentGroupType);
			loyaltyPoints.setPaymentMethod(paymentGroupMethodName);
			loyaltyPoints.setUserId(profile.getRepositoryId());
		} catch (CommerceException e) {
			throw new PaymentGroupInitializationException("Error in initializePaymentGroups: cannot create loyaltyPoints paymentgroup"+
														  "\nCause: " + e.getMessage());
		}	
		pgContainer.addPaymentGroup(paymentGroupType, loyaltyPoints);
	}

}
