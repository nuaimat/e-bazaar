package presentation.data;

import static presentation.util.UtilForUIClasses.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import business.exceptions.BackendException;
import business.externalinterfaces.*;
import business.productsubsystem.ProductSubsystemFacade;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import business.usecasecontrol.BrowseAndSelectController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import presentation.control.BrowseSelectUIControl;
import presentation.gui.GuiConstants;
import presentation.util.CacheReader;

import javax.servlet.http.HttpSession;

public enum BrowseSelectData  {
	INSTANCE;
	private static final Logger LOG = Logger.getLogger(BrowseSelectData.class.getName());

	public static enum SETypes {WEB, DESKTOP};
	public HttpSession webSession;

	//Fields that are maintained as user interacts with UI
	private CatalogPres selectedCatalog;
	private ProductPres selectedProduct;
	private CartItemPres selectedCartItem;
	private SETypes systemEnvironment = SETypes.DESKTOP;
	
	private BrowseAndSelectController controller = new BrowseAndSelectController();
	
	public CatalogPres getSelectedCatalog() {
		return selectedCatalog;
	}

	public void setSelectedCatalog(CatalogPres selectedCatalog) {
		this.selectedCatalog = selectedCatalog;
	}

	public ProductPres getSelectedProduct() {
		return selectedProduct;
	}
	
	public Product getProductForProductName(String name) throws BackendException {
		return controller.getProductForProductName(name);
	}

	public void setSelectedProduct(ProductPres selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public CartItemPres getSelectedCartItem() {
		return selectedCartItem;
	}

	public void setSelectedCartItem(CartItemPres selectedCartItem) {
		this.selectedCartItem = selectedCartItem;
	}
	
	
	//ShoppingCart model
	private ObservableList<CartItemPres> cartData;
	
	public ObservableList<CartItemPres> getCartData() {
		return cartData;
	}
	
	public CartItemPres cartItemPresFromData(String name, double unitPrice, int quantAvail) {
		CartItemData item = new CartItemData();
		item.setItemName(name);
		item.setPrice(unitPrice);
		item.setQuantity(quantAvail);
		CartItemPres cartPres = new CartItemPres();
		cartPres.setCartItem(item);
		return cartPres;
	}
	
	/**
	 * Adds a new item to the displayed shopping cart based on user
	 * input. New item is placed at the top of the displayed table.
	 * Then it updates the items in the liveCart stored in the
	 * ShoppingCartSubsystem
	 */
	public void addToCart(CartItemPres cartPres) {
		ObservableList<CartItemPres> newCartItems =
		           FXCollections.observableArrayList(cartPres);
		//Place the new item at the top of the list
		if(cartData != null) {
			newCartItems.addAll(cartData);
		}
		
		//replace the backing data for displayed table by the updated list
		cartData = newCartItems;
		
		//updates the liveCart in ShoppingCartSubsystem
		updateShoppingCart();
	}

	public boolean removeFromCart(ObservableList<CartItemPres> toBeRemoved) {
		if(cartData != null && toBeRemoved != null && !toBeRemoved.isEmpty()) {
			cartData.remove(toBeRemoved.get(0));
			updateShoppingCart();
			return true;
		}
		return false;
	}
	
	/** Sets the latest version of cartData to the ShoppingCartSubsystem */
	public void updateShoppingCart() {
		List<CartItem> theCartItems = cartItemPresToCartItemList(cartData);
		controller.updateShoppingCartItems(obtainCurrentShoppingCartSubsystem(), theCartItems);
	}
	
	/** Used to update cartData (in this class) when shopping cart subsystem is changed at back end */
	public void updateCartData() {
		List<CartItem> cartItems = new ArrayList<CartItem>();
		List<CartItem> newlist = obtainCurrentShoppingCartSubsystem().getCartItems();
		if(newlist != null) cartItems = newlist;
		cartData = FXCollections.observableList(cartItemsToCartItemPres(cartItems));
		BrowseSelectUIControl.INSTANCE.updateCartItems(cartData);
	}
	
	/** Returns number of units of this product available according to the database */
	public int quantityAvailable(Product product) {
		int quantityAvailable = 0;
		ProductSubsystem productSubsystem = new ProductSubsystemFacade();
		try {
			quantityAvailable = productSubsystem.getProductFromId(product.getProductId()).getQuantityAvail();
		} catch (BackendException e) {
			LOG.warning("BrowseSelectData.quantityAvailable Exception: " + e.getMessage());
		}

		return quantityAvailable;
		
	}
	
	
	 public ShoppingCartSubsystem obtainCurrentShoppingCartSubsystem() {
		 if(getSystemEnvironment() == SETypes.WEB){

			 ShoppingCartSubsystem cachedCart
					 = (ShoppingCartSubsystem)webSession.getAttribute(SessionCache.SHOP_CART);

			 CustomerSubsystem cust = (CustomerSubsystem) webSession.getAttribute(SessionCache.CUSTOMER);

			 //Return value is not null
			 ShoppingCartSubsystem retVal =
					 controller.obtainCurrentShoppingCartSubsystem(cust, cachedCart);
			 if (cachedCart == null) { //this can happen only if cust==null && extShopCart == null
				 webSession.setAttribute(SessionCache.SHOP_CART, retVal);
			 }
			 return retVal;
		 } else {
			 SessionCache session = SessionCache.getInstance();
			 ShoppingCartSubsystem cachedCart
					 = (ShoppingCartSubsystem)session.get(SessionCache.SHOP_CART);

			 CustomerSubsystem cust = (CustomerSubsystem) CacheReader.readCustomer();

			 //Return value is not null
			 ShoppingCartSubsystem retVal =
					 controller.obtainCurrentShoppingCartSubsystem(cust, cachedCart);
			 if (cachedCart == null) { //this can happen only if cust==null && extShopCart == null
				 session.add(SessionCache.SHOP_CART, retVal);
			 }
			 return retVal;
		 }

	    }
	
	
	//CatalogList data
	public List<CatalogPres> getCatalogList() throws BackendException {	
		return controller.getCatalogs()
			    .stream()
			    .map(catalog -> catalogToCatalogPres(catalog))
			    .collect(Collectors.toList());	
	}
	
	//ProductList data
	public List<ProductPres> getProductList(CatalogPres selectedCatalog) throws BackendException {
		return controller.getProducts(selectedCatalog.getCatalog())
			    .stream()
			    .map(prod -> productToProductPres(prod))
			    .collect(Collectors.toList());
	}
	
	//ProductDetails data
	// List<String> displayValues = 
	public List<String> getProductDisplayValues(ProductPres productPres) {
		return Arrays.asList(productPres.nameProperty().get(),
				productPres.unitPriceProperty().get(),
				productPres.quantityAvailProperty().get(),
				productPres.descriptionProperty().get());
	}
	
	public List<String> getProductFieldNamesForDisplay() {
		return GuiConstants.DISPLAY_PRODUCT_FIELDS;
	}
	
	
	//Synchronizers
	private class ShoppingCartSynchronizer implements Synchronizer {
		@SuppressWarnings("rawtypes")
		@Override
		public void refresh(ObservableList list) {
			cartData = list;
		}
	}
	public ShoppingCartSynchronizer getShoppingCartSynchronizer() {
		return new ShoppingCartSynchronizer();
	}

	public SETypes getSystemEnvironment() {
		return systemEnvironment;
	}

	public void setSystemEnvironment(SETypes systemEnvironment) {
		this.systemEnvironment = systemEnvironment;
	}

	public void setWebSession(HttpSession s){
		this.webSession = s;
	}
}
