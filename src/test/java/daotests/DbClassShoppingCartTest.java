package daotests;

import alltests.AllTests;
import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.DbClassShoppingCartForTest;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import dbsetup.DbQueries;
import junit.framework.TestCase;
import middleware.exceptions.DatabaseException;

import java.util.logging.Logger;

public class DbClassShoppingCartTest extends TestCase {

	static String name = "Shopping Cart Test";
	static Logger log = Logger.getLogger(DbClassShoppingCartTest.class.getName());

	static {
		AllTests.testInitializeProperties();
	}

	public void testSaveLiveCart() {
		ShoppingCartSubsystem sss = new ShoppingCartSubsystemFacade();
		DbClassShoppingCartForTest dbClass = sss.getGenericDbClassShoppingCart();
		CustomerProfile custProfile = new CustomerSubsystemFacade().getGenericCustomerProfile();
		custProfile.setCustId(11);
		ShoppingCart cart = sss.getGenericShoppingCartForTest();

		int id = 0;
		try {
			id = dbClass.saveCartForTest(custProfile, cart);
			assertTrue(id > 0);
		} catch (DatabaseException e) {
			fail("Cannot save a new cart");
		} finally {
			DbQueries.deleteCartRow(id);
			DbQueries.deleteCartItems(id);
		}
	}

	public void testRetrieveShoppingCart() {
		ShoppingCartSubsystem sss = new ShoppingCartSubsystemFacade();
		DbClassShoppingCartForTest dbClass = sss.getGenericDbClassShoppingCart();
		CustomerProfile custProfile = new CustomerSubsystemFacade().getGenericCustomerProfile();
		custProfile.setCustId(11);
		ShoppingCart cart = sss.getGenericShoppingCartForTest();
		int id = 0;
		int retid = 0;
		try {
			id = dbClass.saveCartForTest(custProfile, cart);
			retid = dbClass.RetrieveSavedCartTest(custProfile);
			assertTrue(id == retid);
		} catch (DatabaseException e) {
			fail("not retrieving the saved cart");
		} finally {
			DbQueries.deleteCartRow(id);
			DbQueries.deleteCartItems(id);
		}
	}
}


