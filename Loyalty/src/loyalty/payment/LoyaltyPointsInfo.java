package loyalty.payment;

/**
 * This class store info for execution LoyaltyPoints pipeline process. 
 */
public class LoyaltyPointsInfo {
	
	private String userId;
	private int numberOfPoints;
	private double orderTotal;
	private LoyaltyPointsConfig loyaltyPointsConfig;

	public LoyaltyPointsInfo() {}
		
	public LoyaltyPointsConfig getLoyaltyPointsConfig() {
		return loyaltyPointsConfig;
	}
	public void setLoyaltyPointsConfig(LoyaltyPointsConfig loyaltyPointsConfig) {
		this.loyaltyPointsConfig = loyaltyPointsConfig;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}
	public void setNumberOfPoints(int numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
	}

	public double getPointsToCurrency() {
		return getLoyaltyPointsConfig().getPointsToCurrency();
	}
	public void setPointsToCurrency(double pointsToCurrency) {
		getLoyaltyPointsConfig().setPointsToCurrency(pointsToCurrency);
	}

	public double getMaxPartOfAmount() {
		return getLoyaltyPointsConfig().getMaxPartOfAmount();
	}
	public void setMaxPartOfAmount(double maxPartOfAmount) {
		getLoyaltyPointsConfig().setMaxPartOfAmount(maxPartOfAmount);
	}

	public double getAmountToPoints() {
		return getLoyaltyPointsConfig().getAmountToPoints();
	}
	public void setAmountToPoints(double amountToPoints) {
		getLoyaltyPointsConfig().setAmountToPoints(amountToPoints);
	}

	public double getOrderTotal() {
		return orderTotal;
	}
	public void setOrderTotal(double orderTotal) {
		this.orderTotal = orderTotal;
	}
}
