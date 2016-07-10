package loyalty.payment;
 
import atg.commerce.CommerceException;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.payment.processor.ProcProcessPaymentGroup;
import atg.payment.PaymentStatus;

/**
 * This pipeline processor element is called to authorize, debit, and credit LoyaltyPoints payment groups.  
 * But actually it`s will perform only authorization.
 * It calls through to a LoyaltyPointsProcessor object to perform these operations.
 */
public class ProcProcessLoyaltyPoints extends ProcProcessPaymentGroup {
	
	private LoyaltyPointsProcessorImpl loyaltyPointsProcessor;
	
	public ProcProcessLoyaltyPoints() {}
	
	public LoyaltyPointsProcessorImpl getLoyaltyPointsProcessor() {
		return loyaltyPointsProcessor;
	}

	public void setLoyaltyPointsProcessor(LoyaltyPointsProcessorImpl loyaltyPointsProcessor) {
		this.loyaltyPointsProcessor = loyaltyPointsProcessor;
	}

	@Override
	public PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs params) throws CommerceException {
		return getLoyaltyPointsProcessor().authorizePaymentGroup(params);
	}

	@Override
	public PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs params) throws CommerceException {
//		NOTHING TO DO
		return params.getPaymentManager().getLastDebitStatus(params.getPaymentGroup());
	}

	@Override
	public PaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs params) throws CommerceException {
//		NOTHING TO DO
		return params.getPaymentManager().getLastAuthorizationStatus(params.getPaymentGroup());
	}
}
