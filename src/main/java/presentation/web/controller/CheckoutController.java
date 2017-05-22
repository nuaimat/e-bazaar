package presentation.web.controller;

import business.exceptions.BackendException;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.productsubsystem.ProductSubsystemFacade;
import presentation.data.SessionCache;
import presentation.web.util.WebSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Mo nuaimat on 5/21/17.
 */
@WebServlet
public class CheckoutController extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(CheckoutController.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*if(!WebSession.INSTANCE.isLoggedIn(request.getSession())){
            response.sendRedirect(request.getContextPath() + "/secure_checkout");
            return;
        } else {
            showShoppingBillingPage(request, response);
        }*/

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        if(method == null){
            method = "show-sb"; // shipping and billing screen
        }
        switch (method){
            case "show-sb":
            default:
                showShoppingBillingPage(request, response);
        }

    }

    private void showShoppingBillingPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        session.getAttribute(SessionCache.SHOP_CART);
        ShoppingCartSubsystem cachedCart
                = (ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART);

        CustomerSubsystem customerSubsystem = WebSession.INSTANCE.getCustomer(session);


        ProductSubsystem pss = new ProductSubsystemFacade();
        Map<Integer, Double> prod_price = new HashMap<>();
        if(cachedCart.getCartItems() != null){
            prod_price = cachedCart.getCartItems()
                    .stream()
                    .map(e -> {
                        Product p = null;
                        try {
                            p = pss.getProductFromId(e.getProductid());
                        } catch (BackendException e1) {

                        }
                        return p;
                    })
                    .collect(Collectors.toMap(p -> p.getProductId(), p -> p.getUnitPrice()));
        }


        request.setAttribute("cart_items", cachedCart.getCartItems());
        request.setAttribute("prod_price", prod_price);

        try {
            request.setAttribute("all_shipping_addresses",customerSubsystem.getAllShipAddresses());
            request.setAttribute("all_billing_addresses",customerSubsystem.getAllBillAddresses());
        } catch (BackendException e) {
            LOG.warning("Error while fetching customer addresses custid: " +
                    customerSubsystem.getCustomerProfile().getCustId() + " error: " +
                    e.getMessage()
            );
            throw new ServletException(e.getMessage());
        }

        request.setAttribute("def_billing_addresses",customerSubsystem.getDefaultBillingAddress());
        request.setAttribute("def_shipping_addresses",customerSubsystem.getDefaultBillingAddress());

        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }
}
