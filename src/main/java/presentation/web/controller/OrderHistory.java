package presentation.web.controller;

import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.OrderSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import presentation.data.SessionCache;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Mo nuaimat on 5/20/17.
 */
@WebServlet(name = "OrderHistory")
public class OrderHistory extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(OrderHistory.class.getName());


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Order> orderHistory = listAllOrders(request, response);
        request.setAttribute("order_history", orderHistory);

        String op = request.getParameter("op");
        if(op == null){
            op = "list";
        }
        switch (op){
            case "details":
                int oid = Integer.parseInt(request.getParameter("oid"));
                List<OrderItem> orderItemList = getOrderItemsList(orderHistory, oid);
                request.setAttribute("order_items", orderItemList);
                request.getRequestDispatcher("/order_history.jsp")
                        .forward(request, response);

                break;
            default:
                request.getRequestDispatcher("/order_history.jsp")
                        .forward(request, response);
        }


    }

    private List<OrderItem> getOrderItemsList(List<Order> orderHistory, int oid) {
        Order order = getOrderById(orderHistory, oid);
        return order.getOrderItems();
    }

    private Order getOrderById(List<Order> orderHistory, int oid) {
        for(Order o:orderHistory){
            if(o.getOrderId() == oid) {
                return o;
            }
        }
        return null;
    }


    private List<Order> listAllOrders(HttpServletRequest request, HttpServletResponse response) {
        CustomerSubsystemFacade customerSubsystemFacade = (CustomerSubsystemFacade) request.getSession().getAttribute(SessionCache.CUSTOMER);
        CustomerProfile customerProfile = customerSubsystemFacade.getCustomerProfile();
        OrderSubsystem orderSubsystem = new OrderSubsystemFacade(customerProfile);
        List<Order> orderHistory = null;
        try {
            orderHistory = orderSubsystem.getOrderHistory();
        } catch (BackendException e) {
            LOG.warning(e.getMessage());
        }

        return orderHistory;

    }
}
