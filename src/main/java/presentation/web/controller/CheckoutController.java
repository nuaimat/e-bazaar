package presentation.web.controller;

import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.*;
import business.productsubsystem.ProductSubsystemFacade;
import presentation.data.BrowseSelectData;
import presentation.data.CheckoutData;
import presentation.data.ErrorMessages;
import presentation.data.SessionCache;
import presentation.gui.GuiConstants;
import presentation.web.util.Common;
import presentation.web.util.WebSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
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
        if(!WebSession.INSTANCE.isLoggedIn(request.getSession())){
            response.sendRedirect(request.getContextPath() + "/secure_cart");
            return;
        }

        HttpSession session = request.getSession();
        session.getAttribute(SessionCache.SHOP_CART);
        ShoppingCartSubsystem cachedCart
                = (ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART);

        try {
            cachedCart.runShoppingCartRules();
        } catch (BusinessException e) {
            //throw new ServletException(e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cart?errormsg="+ URLEncoder.encode(e.getMessage(),"UTF-8"));
            return;
        }

        String method = request.getParameter("method");
        if(method == null){
            response.sendRedirect(request.getContextPath() + "/secure_checkout");
            return;
        }
        switch (method){
            case "show_payment":
                if(saveAddresses(request, response)) {
                    response.sendRedirect(request.getContextPath() + "/secure_checkout?method=show-payment");
                }
                break;
            case "submitPayment":
                // save payment data
                if(savePaymentInfo(request, response)){
                    // display final review
                    response.sendRedirect(request.getContextPath() + "/secure_checkout?method=final-review");
                }


                break;
        }
    }

    private boolean savePaymentInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CustomerSubsystemFacade cust = WebSession.INSTANCE.getCustomer(request.getSession());

        String nameOnCard = request.getParameter("name");
        String expDate = request.getParameter("expdate");
        String cardNum = request.getParameter("cardnumber");
        String cardType = request.getParameter("cardtype");

        CreditCard cc = CustomerSubsystemFacade.createCreditCard(nameOnCard, expDate, cardNum, cardType);
        business.usecasecontrol.CheckoutController controller = new business.usecasecontrol.CheckoutController();

        try {
            controller.runPaymentRules(cust.getShoppingCart().getLiveCart().getBillingAddress(), cc);
        } catch(RuleException e) {
            response.sendRedirect(request.getContextPath() + "/secure_checkout?method=show-payment&errormsg="+ URLEncoder.encode(e.getMessage(),"UTF-8"));
            return false;
        } catch(BusinessException e) {
            response.sendRedirect(request.getContextPath() + "/secure_checkout?method=show-payment&errormsg="+ URLEncoder.encode(e.getMessage(),"UTF-8"));
            return false;
        }

        cust.getShoppingCart().setPaymentInfo(cc); // save in the shopping cart

        return true;
    }

    private void showPayment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CustomerSubsystemFacade cust = WebSession.INSTANCE.getCustomer(request.getSession());
        CreditCard cc = cust.getShoppingCart().getLiveCart().getPaymentInfo();
        request.setAttribute("cc", cc);
        request.setAttribute("cc_types", CheckoutData.INSTANCE.getCredCardTypes());
        request.setAttribute("tc_msg", GuiConstants.TERMS_MESSAGE);
        request.getRequestDispatcher("/checkout_payment.jsp").forward(request, response);
    }

    private boolean saveAddresses(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String selectedShippingAddress = request.getParameter("selected_ship_address");
        String selectedBillingAddress = request.getParameter("selected_bill_address");

        if(request.getParameter("b_as_s") != null && request.getParameter("b_as_s").equals("on")) {
            selectedBillingAddress = selectedShippingAddress;
        }

        boolean saveShippingAddress = false;
        if(request.getParameter("save_ship") != null && request.getParameter("save_ship").equals("on")) {
            saveShippingAddress = true;
        }
        boolean saveBillingAddress = false;
        if(request.getParameter("save_bill") != null && request.getParameter("save_bill").equals("on")) {
            saveBillingAddress = true;
        }

        CustomerSubsystemFacade cust = WebSession.INSTANCE.getCustomer(request.getSession());
        business.usecasecontrol.CheckoutController controller = new business.usecasecontrol.CheckoutController();

        Address shippingAddress = null, billingAddress = null;

        try {
            shippingAddress = parseSentAddress(selectedShippingAddress, true, false);
            billingAddress = parseSentAddress(selectedBillingAddress, false, true);
            if(shippingAddress == null || billingAddress == null){
                throw new Exception("invalid Address");
            }
        } catch (Exception e) {
            LOG.warning("Invalid Address");
            response.sendRedirect(request.getContextPath() + "/secure_checkout?errormsg=Invalid address format");
            return false;
        }



        Address cleansedShipAddress = null;
        Address cleansedBillAddress = null;
        boolean rulesOk = true;


        try {
            cleansedShipAddress
                    = controller.runAddressRules(cust, shippingAddress);
        } catch (RuleException e) {
            rulesOk = false;
            response.sendRedirect(request.getContextPath() + "/secure_checkout?errormsg="+ URLEncoder.encode("Shipping address error: "
                    + e.getMessage(), "UTF-8"));
            return false;
        } catch (BusinessException e) {
            rulesOk = false;
            response.sendRedirect(request.getContextPath() + "/secure_checkout?errormsg="+ URLEncoder.encode(ErrorMessages.GENERAL_ERR_MSG + ": Message: " + e.getMessage(), "UTF-8"));
            return false;
        }


        if (rulesOk) {

            try {
                cleansedBillAddress = controller.runAddressRules(cust, billingAddress);
            } catch (RuleException e) {
                rulesOk = false;
                response.sendRedirect(request.getContextPath() + "/secure_checkout?errormsg="+
                        URLEncoder.encode("Billing address error: " + e.getMessage(), "UTF-8"));
                return false;
            } catch (BusinessException e) {
                rulesOk = false;
                response.sendRedirect(request.getContextPath() + "/secure_checkout?errormsg="+
                        URLEncoder.encode(ErrorMessages.GENERAL_ERR_MSG + ": Message: " + e.getMessage(), "UTF-8"));
                return false;
            }

        }

        if (rulesOk) {

            LOG.info("Address Rules passed!");
            if (cleansedShipAddress != null && saveShippingAddress) {
                try {
                    controller.saveNewAddress(cust, cleansedShipAddress);
                } catch (BackendException e) {
                    response.sendRedirect(request.getContextPath() + "/secure_checkout?errormsg=" +
                            URLEncoder.encode("New shipping address not saved. Message: " + e.getMessage(), "UTF-8"));
                    return false;
                }
            }
            if (cleansedBillAddress != null && saveBillingAddress) {
                try {
                    controller.saveNewAddress(cust, cleansedBillAddress);
                } catch (BackendException e) {
                    response.sendRedirect(request.getContextPath() + "/secure_checkout?errormsg=" +
                            URLEncoder.encode("New billing address not saved. Message: " + e.getMessage(), "UTF-8"));
                    return false;
                }
            }
        }

        cust.getShoppingCart().setBillingAddress(cleansedBillAddress);
        cust.getShoppingCart().setShippingAddress(cleansedShipAddress);
        return true;
    }

    private Address parseSentAddress(String selectedBillingAddress, boolean isShip, boolean isBill) throws Exception {
        String [] addressParts = selectedBillingAddress.split("\n");
        String street = addressParts[0];
        String city = addressParts[1];
        String [] stateAndZip = addressParts[2].split(" ");
        String state = stateAndZip[0];
        String zip = stateAndZip[1];

        return CheckoutData.INSTANCE.createAddress(street.trim(),
                city.trim(),
                state.trim(),
                zip.trim(),
                isShip,
                isBill);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        if(method == null){
            method = "show-sb"; // shipping and billing screen
        }
        HttpSession session = request.getSession();
        session.getAttribute(SessionCache.SHOP_CART);
        ShoppingCartSubsystem cachedCart
                = (ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART);

        try {
            cachedCart.runShoppingCartRules();
        } catch (BusinessException e) {
            //throw new ServletException(e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cart?errormsg="+ URLEncoder.encode(e.getMessage(),"UTF-8"));
            return;
        }

        switch (method){
            case "show-payment":
                showPayment(request, response);
                break;
            case "final-review":
                showFinalReview(request, response);
                break;
            case "submit_order":
                if(submitOrder(request, response)){
                    // redirect to home page
                    response.sendRedirect(request.getContextPath() + "/order_history?msg=" +
                            URLEncoder.encode("Order Successfully submitted", "UTF-8"));
                }
                break;
            case "show-sb":
            default:
                if(!WebSession.INSTANCE.isLoggedIn(request.getSession())){
                    response.sendRedirect(request.getContextPath() + "/secure_cart");
                    return;
                } else {
                    showShoppingBillingPage(request, response);
                }

        }

    }

    private boolean submitOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // submit order
        HttpSession session = request.getSession();
        session.getAttribute(SessionCache.SHOP_CART);
        ShoppingCartSubsystem cachedCart
                = (ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART);

        boolean rulesOk = true;
        business.usecasecontrol.CheckoutController controller = new business.usecasecontrol.CheckoutController();

        try {
            controller.runFinalOrderRules(cachedCart);
        } catch (BusinessException e) {
            rulesOk = false;
            LOG.warning(e.getMessage());
            response.sendRedirect(request.getContextPath() + "/secure_checkout?method=show-payment&errormsg="+ URLEncoder.encode(e.getMessage(),"UTF-8"));
            return false;
        }

        if(rulesOk){
            try {
                controller.submitFinalOrder();
            } catch (BackendException e) {
                LOG.warning(e.getMessage());
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/secure_checkout?method=show-payment&errormsg="+ URLEncoder.encode(e.getMessage(),"UTF-8"));
                return false;
            }

        } else {
            LOG.warning("Final order rules failed.");
            response.sendRedirect(request.getContextPath() + "/secure_checkout?method=show-payment&errormsg="+ URLEncoder.encode("Final order rules failed.","UTF-8"));
            return false;
        }

        return true;
    }

    private void showFinalReview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        ShoppingCartSubsystem cachedCart
                = (ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART);


        ProductSubsystem pss = new ProductSubsystemFacade();


        Map<Integer, Double> prod_price = getProductPrices(cachedCart, pss);


        request.setAttribute("cart_items", cachedCart.getCartItems());
        request.setAttribute("prod_price", prod_price);
        request.getRequestDispatcher("/final_review.jsp").forward(request, response);

    }

    private Map<Integer, Double> getProductPrices(ShoppingCartSubsystem cachedCart, ProductSubsystem pss) {
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
        return prod_price;
    }

    private void showShoppingBillingPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        session.getAttribute(SessionCache.SHOP_CART);
        ShoppingCartSubsystem cachedCart
                = (ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART);

        CustomerSubsystem customerSubsystem = WebSession.INSTANCE.getCustomer(session);


        ProductSubsystem pss = new ProductSubsystemFacade();


        Map<Integer, Double> prod_price = getProductPrices(cachedCart, pss);


        request.setAttribute("cart_items", cachedCart.getCartItems());
        request.setAttribute("prod_price", prod_price);

        try {
            List<Address> allShip = customerSubsystem.getAllShipAddresses();
            List<Address> allBill = customerSubsystem.getAllBillAddresses();

            request.setAttribute("all_shipping_addresses", allShip);
            request.setAttribute("all_billing_addresses",allBill);
            request.setAttribute("json_all_shipping_addresses", Common.writeJSONString(allShip));
            request.setAttribute("json_all_billing_addresses",Common.writeJSONString(allBill));

        } catch (BackendException e) {
            LOG.warning("Error while fetching customer addresses custid: " +
                    customerSubsystem.getCustomerProfile().getCustId() + " error: " +
                    e.getMessage()
            );
            throw new ServletException(e.getMessage());
        }

        request.setAttribute("def_billing_addresses",customerSubsystem.getDefaultBillingAddress());
        request.setAttribute("def_shipping_addresses",customerSubsystem.getDefaultBillingAddress());

        request.setAttribute("json_def_billing_addresses", Common.writeJSONString(customerSubsystem.getDefaultBillingAddress()));
        request.setAttribute("json_def_shipping_addresses",Common.writeJSONString(customerSubsystem.getDefaultBillingAddress()));

        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }
}
