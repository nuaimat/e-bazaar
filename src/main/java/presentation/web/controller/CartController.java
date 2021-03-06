package presentation.web.controller;

import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.externalinterfaces.*;
import business.productsubsystem.ProductSubsystemFacade;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import business.usecasecontrol.BrowseAndSelectController;
import presentation.data.BrowseSelectData;
import presentation.data.CartItemData;
import presentation.data.CartItemPres;
import presentation.data.SessionCache;
import presentation.util.CacheReader;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Mo nuaimat on 5/19/17.
 */
@WebServlet
public class CartController extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(CartController.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        if(method == null){
            method = "display";
        }
        switch (method){
            case "add_to_cart":
                addItemToCart(request, response);
                break;
            case "remove_item":
                removeCartItem(request, response);
                break;
            case "display_product":
                displayProduct(request, response);
                break;
            case "update_quantity":
                updateItemQuantity(request, response);
                break;
            case "retrieveSavedCart":
                if(!WebSession.INSTANCE.isLoggedIn(request.getSession())){
                    response.sendRedirect(request.getContextPath() + "/secure_cart?action=retrieveSavedCart");
                    return;
                } else {
                    retreiveSavedCart(request, response);
                }
                break;
            case "save":
                if(!WebSession.INSTANCE.isLoggedIn(request.getSession())){
                    response.sendRedirect(request.getContextPath() + "/secure_cart?action=save");
                    return;
                } else {
                    saveCart(request, response);
                }
                break;
            case "clear":
                clearCart(request, response);
                break;
            default:
                listCartItems(request, response);
        }


    }

    private void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ShoppingCartSubsystem shoppingCartSubsystem = (ShoppingCartSubsystemFacade) request.getSession().getAttribute(SessionCache.SHOP_CART);

        shoppingCartSubsystem.clearLiveCart();
        BrowseSelectData.INSTANCE.updateCartData();

        response.sendRedirect(request.getContextPath() + "/cart?msg=Cart+successfully+cleared.");
    }

    private void saveCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ShoppingCartSubsystem shoppingCartSubsystem = (ShoppingCartSubsystemFacade) request.getSession().getAttribute(SessionCache.SHOP_CART);

        try {
            shoppingCartSubsystem.saveLiveCart();
            BrowseSelectData.INSTANCE.updateCartData();
        } catch (BackendException e) {
            throw new ServletException(e);
        }

        response.sendRedirect(request.getContextPath() + "/cart?msg=Cart+successfully+saved.");
    }

    private void retreiveSavedCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ShoppingCartSubsystem shoppingCartSubsystem = (ShoppingCartSubsystemFacade) request.getSession().getAttribute(SessionCache.SHOP_CART);

        try {
            shoppingCartSubsystem.retrieveSavedCart();
            shoppingCartSubsystem.makeSavedCartLive();
            BrowseSelectData.INSTANCE.updateCartData();
        } catch (BackendException e) {
            throw new ServletException(e);
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }


    private void displayProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int prodid = Integer.parseInt(request.getParameter("pid"));
        ProductSubsystem productSubsystem = new ProductSubsystemFacade();
        Product product;
        try {
            product = productSubsystem.getProductFromId(prodid);
        } catch (BackendException e) {
            LOG.warning(e.getMessage());
            return;
        }

        request.setAttribute("product", product);
        request.setAttribute("categories", Common.getCategoriesList());

        request.getRequestDispatcher("/display_product.jsp").forward(request, response);
    }

    private void listCartItems(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        session.getAttribute(SessionCache.SHOP_CART);
        ShoppingCartSubsystem cachedCart
                = (ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART);


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

        request.getRequestDispatcher("/cart.jsp").forward(request, response);
    }

    private void addItemToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int prodid = Integer.parseInt(request.getParameter("pid"));
        ProductSubsystem productSubsystem = new ProductSubsystemFacade();
        Product product = null;
        ShoppingCartSubsystem shoppingCartSubsystem = (ShoppingCartSubsystemFacade) request.getSession().getAttribute(SessionCache.SHOP_CART);

        try {
            product = productSubsystem.getProductFromId(prodid);
        } catch (BackendException e) {
            throw new ServletException(e);
        }

        int quant = 1; // INIT_QUANT_REQUESTED;
        double unitPrice = product.getUnitPrice();
        String name = product.getProductName();

        CartItemPres cartPres = BrowseSelectData.INSTANCE.cartItemPresFromData(name, unitPrice, quant);

        /* Then add it to the cart in the ShoppingCartSubsystem */
        BrowseSelectData.INSTANCE.addToCart(cartPres);

        // move items from shopping cart sub system to http session
        WebSession.INSTANCE.sync(request.getSession(), SessionCache.getInstance());

        String referrer = request.getHeader("referer");
        response.sendRedirect(referrer);
    }

    private void updateItemQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int prodid = Integer.parseInt(request.getParameter("pid"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        BrowseAndSelectController controller = new BrowseAndSelectController();


        ShoppingCartSubsystem shoppingCartSubsystem = (ShoppingCartSubsystemFacade) request.getSession().getAttribute(SessionCache.SHOP_CART);
        if(shoppingCartSubsystem == null){
            return;
        }
        ProductSubsystem productSubsystem = new ProductSubsystemFacade();
        Product product = null;
        try {
            product = productSubsystem.getProductFromId(prodid);
        } catch (BackendException e) {
            LOG.warning("removeCartItem: " + e.getMessage());
            return;
        }

        try {
            controller.runQuantityRules(product, ""+quantity);
        } catch (BusinessException e) {
            LOG.warning("quantity exceeded supply for prod id: " + product.getProductId());
            response.sendRedirect(request.getContextPath() + "/cart?msg=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            return;
        }

        List<CartItem> list = shoppingCartSubsystem
                .getLiveCart().getCartItems();
        for(int i=0; i < list.size(); i++){
            CartItem cartItem = list.get(i);
            if(cartItem.getProductid() == prodid){
                CartItem newItem = ShoppingCartSubsystemFacade.createCartItem(
                        cartItem.getProductName(),
                        "" + quantity, Double.toString(quantity * product.getUnitPrice())
                );
                list.set(i, newItem);
                break;
            }
        }

        shoppingCartSubsystem.getLiveCart().setCartItems(list);
        BrowseSelectData.INSTANCE.updateCartData();
        WebSession.INSTANCE.sync(request.getSession(), SessionCache.getInstance());

        String referrer = request.getHeader("referer");
        response.sendRedirect(request.getContextPath() + "/cart");
    }


    private void removeCartItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int prodid = Integer.parseInt(request.getParameter("pid"));
        ShoppingCartSubsystem shoppingCartSubsystem = (ShoppingCartSubsystemFacade) request.getSession().getAttribute(SessionCache.SHOP_CART);
        if(shoppingCartSubsystem == null){
            return;
        }
        ProductSubsystem productSubsystem = new ProductSubsystemFacade();
        Product product = null;
        try {
            product = productSubsystem.getProductFromId(prodid);
        } catch (BackendException e) {
            LOG.warning("removeCartItem: " + e.getMessage());
            return;
        }

        shoppingCartSubsystem.getLiveCart().deleteCartItem(product.getProductName());
        BrowseSelectData.INSTANCE.updateCartData();

        WebSession.INSTANCE.sync(request.getSession(), SessionCache.getInstance());

        String referrer = request.getHeader("referer");
        response.sendRedirect(request.getContextPath() + "/cart");

        // remove

    }


}
