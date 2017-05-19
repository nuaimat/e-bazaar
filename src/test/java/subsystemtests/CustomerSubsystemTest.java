package subsystemtests;

import alltests.AllTests;
import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.Address;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.DbClassAddressForTest;
import dbsetup.DbQueries;
import junit.framework.TestCase;

import java.util.logging.Logger;

public class CustomerSubsystemTest extends TestCase {
	static String name = "Customer Subsystem Test";
	static Logger log = Logger.getLogger(ProductSubsystemTest.class.getName());
	static {
		AllTests.testInitializeProperties();
	}
	public void testReadDefaultBillAddress() {
		final int DEFAULT_CUST_ID = 1;
		Address defaultBillAddr = DbQueries.readDefaultBillAddress();
		
		CustomerSubsystem css = new CustomerSubsystemFacade();
		DbClassAddressForTest dbclass = css.getGenericDbClassAddress();
		CustomerProfile custProfile = css.getGenericCustomerProfile();
		custProfile.setCustId(DEFAULT_CUST_ID);
		
		Address defaultAddr;
		
		try {
			defaultAddr = dbclass.readDefaultBillAddressforTest(custProfile);
			
			assertTrue(defaultAddr.getZip().equals(defaultBillAddr.getZip()));
			
		} catch(Exception e) {
			System.out.println("Default Bill Address is not match");
		}
	}

	public void testReadDefaultShipAddress() {
		final int DEFAULT_CUST_ID = 1;
		Address defaultShipAddr = DbQueries.readDefaultShipAddress();
		
		CustomerSubsystem css = new CustomerSubsystemFacade();
		DbClassAddressForTest dbclass = css.getGenericDbClassAddress();
		CustomerProfile custProfile = css.getGenericCustomerProfile();
		custProfile.setCustId(DEFAULT_CUST_ID);
		
		Address defaultAddr;
		
		try {
			defaultAddr = dbclass.readDefaultShipAddressforTest(custProfile);
			
			assertTrue(defaultAddr.getZip().equals(defaultShipAddr.getZip()));
			
		} catch(Exception e) {
			System.out.println("Default Ship Address is not match");
		}
	}
}
