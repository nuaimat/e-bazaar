package subsystemtests;

import alltests.AllTests;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import dbsetup.DbQueries;
import junit.framework.TestCase;

import java.util.logging.Logger;

public class ShoppingCartSubsystemTest extends TestCase{

	static String name = "Shopping Cart Subsystem Test";
	static Logger log = Logger.getLogger(ShoppingCartSubsystemTest.class.getName());
	
	static {
		AllTests.testInitializeProperties();
	}
	
	public void testRetrieveSavedCart(){
		
		String[] insertResult = DbQueries.insertCartRow();
		String expected = insertResult[3];
		
		CustomerSubsystem css = new CustomerSubsystemFacade();
		CustomerProfile custProfile = css.getGenericCustomerProfile();
		custProfile.setCustId(11); //default custid, used in insertCartRow();
		ShoppingCartSubsystem sss = new ShoppingCartSubsystemFacade();
		
		try{
			sss.setCustomerProfile(custProfile);
			sss.retrieveSavedCart();
			sss.makeSavedCartLive();
			
			ShoppingCart cart = sss.getLiveCart();
			String found = cart.getShippingAddress().getStreet();
			
			assertTrue(expected.equals(found));
		}catch(BackendException e){
			fail("Inserted value not found");
		}finally{
			DbQueries.deleteCartRow(Integer.parseInt(insertResult[1]));
		}
	}
}
