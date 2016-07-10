package loyalty.payment;

import atg.commerce.order.PaymentGroupImpl;

/**
 * This class represent LoyaltyPoints Payment Group.
 */
public class LoyaltyPoints extends PaymentGroupImpl{
	
	public LoyaltyPoints(){}
	
	public String getUserId(){
		return (String) getPropertyValue("userId");
	}
	 
	public void setUserId(String userId){
		setPropertyValue("userId", userId);
	}
	
	public int getNumberOfPoints(){
		return ((Integer) getPropertyValue("numberOfPoints")).intValue();
	}
	
	public void setNumberOfPoints(int numberOfPoints){
		setPropertyValue("numberOfPoints", new Integer(numberOfPoints));
	}
}
