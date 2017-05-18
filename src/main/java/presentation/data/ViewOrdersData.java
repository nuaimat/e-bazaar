package presentation.data;

import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.Order;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

public enum ViewOrdersData {
	INSTANCE;
	private static final Logger LOG = 
		Logger.getLogger(ViewOrdersData.class.getSimpleName());
	private OrderPres selectedOrder;
	public OrderPres getSelectedOrder() {
		return selectedOrder;
	}
	public void setSelectedOrder(OrderPres so) {
		selectedOrder = so;
	}
	
	public List<OrderPres> getOrders() {
		List<Order> orders = (List<Order>) SessionCache.getInstance().get(SessionCache.CUSTOMER_ORDER_HISTORY);
		List<OrderPres> orderPresList = new ArrayList<>();
		if(orders != null && orders.size() > 0 ){
			for(Order o:orders){
				OrderPres orderPres = new OrderPres();
				orderPres.setOrder(o);
				orderPresList.add(orderPres);
			}
		}

		return orderPresList;
	}
}
