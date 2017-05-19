package presentation.web.util;

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
        LOG.info("Synchronizing session with web session");
        String [] keys = {
                SessionCache.CUSTOMER, SessionCache.CUSTOMER_ORDER_HISTORY,
                SessionCache.LOGGED_IN, SessionCache.SHOP_CART
        };

        for(String key:keys){
            session.setAttribute(key, sc.get(key));
        }

        if(CacheReader.readLoggedIn()){
            session.setAttribute("cust_firstname", CacheReader.readCustomer().getCustomerProfile().getFirstName());
            session.setAttribute("cust_id", CacheReader.readCustomer().getCustomerProfile().getCustId());
        } else {
            session.removeAttribute("cust_firstname");
            session.removeAttribute("cust_id");
        }

    }


    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("cust_id") != null;
    }
}
