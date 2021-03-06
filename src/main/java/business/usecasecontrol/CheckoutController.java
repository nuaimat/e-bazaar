package business.usecasecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import business.BusinessConstants;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.*;
import presentation.data.SessionCache;
import presentation.util.CacheReader;

public class CheckoutController  {
		
	private static final Logger LOG = Logger.getLogger(CheckoutController.class
			.getPackage().getName());
	
	
	public void runShoppingCartRules(ShoppingCartSubsystem shopCart) throws RuleException, BusinessException {
		shopCart.runShoppingCartRules();
		
	}
	
	public void runPaymentRules(Address addr, CreditCard cc) throws RuleException, BusinessException {
		CustomerSubsystem css = CacheReader.readCustomer();
		css.runPaymentRules(addr, cc);


	}
	
	public Address runAddressRules(CustomerSubsystem cust, Address addr) throws RuleException, BusinessException {
		return cust.runAddressRules(addr);
	}
	
	public List<Address> getShippingAddresses(CustomerSubsystem cust) throws BackendException {
		return cust.getAllShipAddresses();
	}
	
	public List<Address> getBillingAddresses(CustomerSubsystem cust) throws BackendException {
		return cust.getAllShipAddresses();
	}
	
	/** Asks the ShoppingCart Subsystem to run final order rules */
	public void runFinalOrderRules(ShoppingCartSubsystem scss) throws RuleException, BusinessException {
		scss.runFinalOrderRules();
	}
	
	/** Asks Customer Subsystem to check credit card against 
	 *  Credit Verification System 
	 */
	public void verifyCreditCard(CustomerSubsystem cust) throws BusinessException {
		cust.checkCreditCard();
	}
	
	public void saveNewAddress(CustomerSubsystem cust, Address addr) throws BackendException {		
		cust.saveNewAddress(addr);
	}
	
	/** Asks Customer Subsystem to submit final order */
	public void submitFinalOrder() throws BackendException {
		CustomerSubsystem css = CacheReader.readCustomer();
		css.submitOrder();

	}


}
