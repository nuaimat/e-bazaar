package presentation.web.util;

import business.customersubsystem.CustomerSubsystemFacade;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import presentation.data.SessionCache;
import presentation.util.CacheReader;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * Created by Mo nuaimat on 5/18/17.
 */
public enum WebSession {
    INSTANCE;
    private static final Logger LOG = Logger.getLogger(WebSession.class.getName());


    public void sync(HttpSession session, SessionCache sc){
        /*LOG.info("Synchronizing session with web session");
        String [] keys = {
                SessionCache.CUSTOMER, SessionCache.CUSTOMER_ORDER_HISTORY,
                SessionCache.LOGGED_IN, SessionCache.SHOP_CART
        };

        for(String key:keys){
            session.setAttribute(key, sc.get(key));
        }*/

        if(session.getAttribute(SessionCache.LOGGED_IN) == null ||
                !(Boolean) session.getAttribute(SessionCache.LOGGED_IN)){
            session.removeAttribute("cust_firstname");
            session.removeAttribute("cust_id");
        }
        session.setAttribute("cart_item_count",
                ((ShoppingCartSubsystemFacade) session.getAttribute(SessionCache.SHOP_CART)).getLiveCartItems().size());

    }


    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("cust_id") != null;
    }

    public void logout(HttpSession session) {
        SessionCache cache = SessionCache.getInstance();

        cache.add(SessionCache.LOGGED_IN, Boolean.FALSE);
        String [] keys = {
                SessionCache.CUSTOMER, SessionCache.CUSTOMER_ORDER_HISTORY,
                SessionCache.LOGGED_IN, SessionCache.SHOP_CART
        };

        for(String key:keys){
            session.removeAttribute(key);
            SessionCache.getInstance().remove(key);
        }

        session.removeAttribute("cust_id");
        session.removeAttribute("cust_firstname");

    }

    public CustomerSubsystemFacade getCustomer(HttpSession session) {
        return (CustomerSubsystemFacade) session.getAttribute(SessionCache.CUSTOMER);
    }
}
