package daotests;

import alltests.AllTests;
import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.*;
import business.ordersubsystem.OrderImpl;
import business.ordersubsystem.OrderItemImpl;
import business.ordersubsystem.OrderSubsystemFacade;
import dbsetup.DbQueries;
import junit.framework.TestCase;
import middleware.exceptions.DatabaseException;
import performancetests.rulesstubs.AddressImpl;
import performancetests.rulesstubs.CreditCardImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DbClassOrderTest extends TestCase {
	
	static String name = "Order Test";
	static Logger log = Logger.getLogger(DbClassOrderTest.class.getName());
	CustomerSubsystem css = new CustomerSubsystemFacade();
	CustomerProfile custProfile = css.getGenericCustomerProfile();
	OrderSubsystem oss = new OrderSubsystemFacade(custProfile);
	DbClassOrderForTest dbClass = oss.getGenericDbClassOrder();
	
	static {
		AllTests.testInitializeProperties();
	}
	
	public void testSubmitOrderData(){
		
	}
	
	public void testReadOrderHistory(){
		
		List<Order> listOrders = DbQueries.readOrderHistory(custProfile.getCustId());
		List<Integer> expected = new ArrayList<>();
		for(Order order : listOrders){
			expected.add(order.getOrderId());
		}
		
		List<Integer> found = null;
		try {
			found = dbClass.readOrderHistoryForTest(custProfile);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(expected.size() == found.size());
	}
	
	public void testSubmitOrder(){
		int id = 0;
		Order order = new OrderImpl();
		order.setBillAddress(new AddressImpl("test", "test", "test", "test", false, false));
		order.setShipAddress(new AddressImpl("test", "test", "test", "test", false, false));
		order.setDate(LocalDate.now());
		order.setPaymentInfo(new CreditCardImpl("test", "test", "test", "test"));

		List<OrderItem> items = new ArrayList<>();

		items.add(new OrderItemImpl("test", 1, 0.2));


		order.setOrderItems(items);

		try {
			id = dbClass.submitOrderForTest(custProfile, order);
			assertTrue(id > 0);
		} catch (DatabaseException e) {
			fail("unable to insert data to database...");
		} finally {
			DbQueries.deleteOrder(id);
			DbQueries.deleteOrderItems(id);

		}
	}
}
