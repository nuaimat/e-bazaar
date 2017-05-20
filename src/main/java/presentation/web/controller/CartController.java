package presentation.web.controller;

import business.exceptions.BackendException;
import business.externalinterfaces.*;
import business.productsubsystem.ProductSubsystemFacade;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import presentation.data.BrowseSelectData;
import presentation.data.CartItemPres;
import presentation.data.SessionCache;
import presentation.util.CacheReader;
import presentation.web.util.WebSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Mo nuaimat on 5/19/17.
 */
@WebServlet
public class CartController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        if ("add_to_cart".equals(method)){
            addItemToCart(request, response);
        } else {
            listCartItems(request, response);
        }


    }

    private void listCartItems(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.getAttribute(SessionCache.SHOP_CART);
        ShoppingCartSubsystem cachedCart
                = (ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART);
        for(CartItem cartItem:cachedCart.getCartItems()){
            response.getWriter().println(cartItem.getProductid() + " : " + cartItem.getProductName());
        }
        response.flushBuffer();
    }

    private void addItemToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int prodid = Integer.parseInt(request.getParameter("pid"));
        ShoppingCartSubsystem shoppingCartSubsystem = new ShoppingCartSubsystemFacade();
        ProductSubsystem productSubsystem = new ProductSubsystemFacade();
        Product product = null;

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


}
