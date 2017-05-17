package business.ordersubsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import business.externalinterfaces.*;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import business.util.Convert;
import middleware.exceptions.DatabaseException;
import business.exceptions.BackendException;
import presentation.data.CartItemPres;
import presentation.data.OrderPres;
import presentation.util.CacheReader;

public class OrderSubsystemFacade implements OrderSubsystem {
	private static final Logger LOG = 
			Logger.getLogger(OrderSubsystemFacade.class.getPackage().getName());
	CustomerProfile custProfile;
	    
    public OrderSubsystemFacade(CustomerProfile custProfile){
        this.custProfile = custProfile;
    }
	
	/** 
     *  Used by customer subsystem at login to obtain this customer's order history from the database.
	 *  Assumes cust id has already been stored into the order subsystem facade 
	 *  This is created by using auxiliary methods at the bottom of this class file.
	 *  First get all order ids for this customer. For each such id, get order data
	 *  and form an order, and with that order id, get all order items and insert
	 *  into the order.
	 */
    public List<Order> getOrderHistory() throws BackendException {
        List<Order> res = new ArrayList<Order>();
        DbClassOrder dbClass = new DbClassOrder();
        try {
            List<Integer> previousOrdersIds = getAllOrderIds();
            for(Integer id:previousOrdersIds){
                Order order = dbClass.getOrderData(id);

                DbClassOrder dbClassOrder = new DbClassOrder();
                order.setOrderItems(dbClassOrder.getOrderItems(order.getOrderId()));
                res.add(order);
            }

        } catch (DatabaseException e) {
            throw new BackendException(e);
        }
    	return res;
    }
    
    public void submitOrder(ShoppingCart cart) throws BackendException {

        DbClassOrder dbClass = new DbClassOrder();
        Order o = createOrder(0, Convert.localDateAsString(LocalDate.now()), ""+0);

        List<OrderItem> orderItemList = new ArrayList<>();
        for(CartItem cartItem:cart.getCartItems()){
            OrderItem oi = createOrderItem(
                    cartItem.getProductid(), o.getOrderId(), cartItem.getQuantity(),
                    cartItem.getTotalprice()
                    );
            oi.setProductName(cartItem.getProductName());
            orderItemList.add(oi);
        }
        o.setOrderItems(orderItemList);
        o.setBillAddress(cart.getBillingAddress());
        o.setShipAddress(cart.getShippingAddress());
        o.setPaymentInfo(cart.getPaymentInfo());

        try {
            dbClass.submitOrder(CacheReader.readCustomer().getCustomerProfile(), o );
        } catch (DatabaseException e) {
            LOG.warning("OrderSubsystemFacade:submitOrder Exception: " + e.getMessage());
            throw new BackendException(e);
        }
    }
	
	/** Used whenever an order item needs to be created from outside the order subsystem */
    public static OrderItem createOrderItem(
    		Integer prodId, Integer orderId, String quantityReq, String totalPrice) {
        OrderItemImpl oi = new OrderItemImpl(
                "",
                Integer.parseInt(quantityReq),
                Double.parseDouble(totalPrice)
        );
        oi.setProductId(prodId);
        oi.setOrderId(orderId);

        return oi;
    }
    
    /** to create an Order object from outside the subsystem */
    public static Order createOrder(Integer orderId, String orderDate, String totalPrice) {
    	Order o = new OrderImpl();
        OrderPres orderPres = new OrderPres();
        o.setOrderId(orderId);
        o.setDate(Convert.localDateForString(orderDate));
    	return o;
    }
    
    ///////////// Methods internal to the Order Subsystem -- NOT public
    List<Integer> getAllOrderIds() throws DatabaseException {
        DbClassOrder dbClass = new DbClassOrder();
        return dbClass.getAllOrderIds(custProfile);
        
    }
    
    /** Part of getOrderHistory */
    List<OrderItem> getOrderItems(Integer orderId) throws DatabaseException {
        DbClassOrder dbClass = new DbClassOrder();
        return dbClass.getOrderItems(orderId);
    }
    
    /** Uses cust id to locate top-level order data for customer -- part of getOrderHistory */
    OrderImpl getOrderData(Integer orderId) throws DatabaseException {
    	DbClassOrder dbClass = new DbClassOrder();
    	return dbClass.getOrderData(orderId);
    }
}
