package subsystemtests;

import alltests.AllTests;
import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.OrderSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import business.util.Convert;
import dbsetup.DbQueries;
import junit.framework.TestCase;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OrderSubsystemTest extends TestCase {

	static String name = "Order Subsystem Test";
	static Logger log = Logger.getLogger(OrderSubsystemTest.class.getName());
	
	static {
		AllTests.testInitializeProperties();
	}
	
	public void testGetOrderHistory(){
		//setup
				/*
				 * Returns a String[] with values:
				 * 0 - query
				 * 1 - order id
				 * 2 - cust id
				 * 3 - order date
				 */
		String[] insertResult = DbQueries.insertOrderRow();
		String expected = insertResult[3];
		
		CustomerSubsystem css = new CustomerSubsystemFacade();
		CustomerProfile custProfile = css.getGenericCustomerProfile();
		custProfile.setCustId(11); // custid=11 used in insertOrderRow
		OrderSubsystem oss = new OrderSubsystemFacade(custProfile);
		
		try{
			List<LocalDate> orderDates = oss.getOrderHistory()
								.stream()
								.map(ord -> ord.getOrderDate())
								.collect(Collectors.toList());
			boolean found = false;
			for(LocalDate ordDate : orderDates)
				if(expected.equals(Convert.localDateAsString(ordDate))) 
					found = true;
			
			assertTrue(found);
		}catch(Exception e){
			e.printStackTrace();
			fail("Inserted value not found");
		}finally{
			DbQueries.deleteOrder(Integer.parseInt(insertResult[1]));
		}
	}
}
