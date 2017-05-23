package business.shoppingcartsubsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.*;
import business.productsubsystem.ProductSubsystemFacade;
import business.util.Convert;
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
		if(liveCart == null){
			liveCart = new ShoppingCartImpl(new LinkedList<CartItem>());
		}
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
		ShoppingCartImpl sci = new ShoppingCartImpl();
		Address addr = getFacebookAddressForTest();

		sci.setBillAddress(addr);
		sci.setShipAddress(addr);
		sci.setPaymentInfo(getSamplePaymentInfoForTest());

		ProductSubsystem prodSS= new ProductSubsystemFacade();

		Product p = null;
		try {
			p = prodSS.getProductList(prodSS.getCatalogList().get(0)).get(0);
			CartItemImpl item = new CartItemImpl(0, p.getProductId() , 0, "1", "0.9", true);
			item.setCartId(0);
			sci.addItem(item);

		} catch (BackendException e) {
			LOG.warning("No items found inside first Catalog");
		}

		return sci;
	}

	private CreditCard getSamplePaymentInfoForTest() {
		return CustomerSubsystemFacade.createCreditCard("John Snow",
				Convert.localDateAsString(
						LocalDate.of(2030, 12, 31)
				),
				"4111111111111111",
				"Visa");
	}

	private Address getFacebookAddressForTest() {
		CustomerSubsystemFacade customerSubsystem = new CustomerSubsystemFacade();
		return customerSubsystem.createAddress("1 Hacker Way", "Menlo Park", "CA", "94025", true, true);
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

	@Override
	public DbClassShoppingCartForTest getGenericDbClassShoppingCart() {
		return new DbClassShoppingCart();
	}

	@Override
	public ShoppingCart getGenericShoppingCartForTest() {
		return getEmptyCartForTest();
	}

}
