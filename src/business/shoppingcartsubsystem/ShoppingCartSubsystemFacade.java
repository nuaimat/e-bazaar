package business.shoppingcartsubsystem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.*;
import middleware.exceptions.DatabaseException;
import presentation.data.CartItemPres;
import presentation.data.DefaultData;

public class ShoppingCartSubsystemFacade implements ShoppingCartSubsystem {
	
	ShoppingCartImpl liveCart = new ShoppingCartImpl(new LinkedList<CartItem>());
	ShoppingCartImpl savedCart;
	Integer shopCartId;
	CustomerProfile customerProfile;
	Logger LOG = Logger.getLogger(this.getClass().getPackage().getName());

	// interface methods
	public void setCustomerProfile(CustomerProfile customerProfile) {
		this.customerProfile = customerProfile;
	}
	
	public void makeSavedCartLive() {
		liveCart = savedCart;
	}

	@Override
	public void saveLiveCart() throws BackendException {
			DbClassShoppingCart dbClass = new DbClassShoppingCart();
		try {
			dbClass.saveCart(customerProfile, liveCart);
		} catch (DatabaseException e) {
			LOG.warning("ShoppingCartSubsystemFacade:saveLiveCart Exception: " + e.getMessage());
			throw new BackendException(e);
		}
	}

	public ShoppingCart getLiveCart() {
		return liveCart;
	}

	

	public void retrieveSavedCart() throws BackendException {
		//set savedCart to an instance of ShoppingCartImpl
		//need to create a cartitems list (with at least one element) 
		//and insert into shop cart impl 
		List<CartItem> itemsList = new ArrayList<CartItem>();
		//popluate this list
		savedCart = new ShoppingCartImpl(itemsList);

		if(customerProfile != null){
			DbClassShoppingCart dbClass = new DbClassShoppingCart();
			try {
				savedCart = dbClass.retrieveSavedCart(customerProfile);
			} catch (DatabaseException e) {
				savedCart = new ShoppingCartImpl(itemsList);
			}
		}

	}
	
	@Override
	public void setShippingAddress(Address addr) {
		liveCart.setShipAddress(addr);
		
	}

	@Override
	public void setBillingAddress(Address addr) {
		liveCart.setBillAddress(addr);
		
	}

	@Override
	public void setPaymentInfo(CreditCard cc) {
		liveCart.setPaymentInfo(cc);
		
	}
	
	public void setCartItems(List<CartItem> list) {
		liveCart.setCartItems(list);
	}
	
	public List<CartItem> getCartItems() {
		return liveCart.getCartItems();
	}
	
	//static methods
	public static CartItem createCartItem(String productName, String quantity,
            String totalprice) {
		try {
			return new CartItemImpl(productName, quantity, totalprice);
		} catch(BackendException e) {
			throw new RuntimeException("Can't create a cartitem because of productid lookup: " + e.getMessage());
		}
	}


	
	//interface methods for testing
	
	public ShoppingCart getEmptyCartForTest() {
		return new ShoppingCartImpl();
	}

	
	public CartItem getEmptyCartItemForTest() {
		return new CartItemImpl();
	}

	@Override
	public void clearLiveCart() {
		liveCart.clearCart();
	}

	@Override
	public List<CartItem> getLiveCartItems() {
		return liveCart.getCartItems();
	}

	@Override
	public void runShoppingCartRules() throws RuleException, BusinessException {
		Rules rulesShoppingCartObject = new RulesShoppingCart(this.liveCart);
		rulesShoppingCartObject.runRules();
	}

	@Override
	public void runFinalOrderRules() throws RuleException, BusinessException {
		Rules rulesShoppingCartObject = new RulesFinalOrder(this.liveCart);
		rulesShoppingCartObject.runRules();
	}

}
