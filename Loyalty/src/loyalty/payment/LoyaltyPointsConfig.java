package loyalty.payment;

/**
 * This class store payment properties for LoyaltyPoints PaymentGroup.
 */
public class LoyaltyPointsConfig {
	
	private String paymentGroupTypeName; 
	private String paymentGroupMethodName; 
	private double pointsToCurrency;
	private double maxPartOfAmount;
	private double amountToPoints;
	   
	public LoyaltyPointsConfig() {}

	public double getPointsToCurrency() {
		return pointsToCurrency;
	}
	public void setPointsToCurrency(double pointsToCurrency) {
		this.pointsToCurrency = pointsToCurrency;
	}

	public double getMaxPartOfAmount() {
		return maxPartOfAmount;
	}
	public void setMaxPartOfAmount(double maxPartOfAmount) {
		this.maxPartOfAmount = maxPartOfAmount;
	}

	public double getAmountToPoints() {
		return amountToPoints;
	}
	public void setAmountToPoints(double amountToPoints) {
		this.amountToPoints = amountToPoints;
	}

	public String getPaymentGroupTypeName() {
		return paymentGroupTypeName;
	}
	public void setPaymentGroupTypeName(String paymentGroupTypeName) {
		this.paymentGroupTypeName = paymentGroupTypeName;
	}

	public String getPaymentGroupMethodName() {
		return paymentGroupMethodName;
	}
	public void setPaymentGroupMethodName(String paymentGroupMethodName) {
		this.paymentGroupMethodName = paymentGroupMethodName;
	}
	
}
