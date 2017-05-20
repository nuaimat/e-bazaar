package presentation.web.util;

import business.exceptions.BackendException;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.ProductSubsystem;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.productsubsystem.ProductSubsystemFacade;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import presentation.data.BrowseSelectData;
import presentation.data.CartItemPres;
import presentation.data.SessionCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Mo nuaimat on 5/19/17.
 */
public class Common {
    private static final Logger LOG = Logger.getLogger(Common.class.getName());


    public static void bootstrap(HttpServletRequest req, HttpServletResponse res){
        HttpSession session = req.getSession();
        if(session.getAttribute(SessionCache.SHOP_CART) == null){
            ShoppingCartSubsystem shoppingCartSubsystem =  new ShoppingCartSubsystemFacade();
            session.setAttribute(SessionCache.SHOP_CART, shoppingCartSubsystem);
            SessionCache.getInstance().remove(SessionCache.SHOP_CART);
            SessionCache.getInstance().add(SessionCache.SHOP_CART, shoppingCartSubsystem);
        }
        session.setAttribute("cart_item_count", cartItemsCount(session));

        BrowseSelectData.INSTANCE.setSystemEnvironment(BrowseSelectData.SETypes.WEB);
        BrowseSelectData.INSTANCE.setWebSession(session);
    }



    private static Integer cartItemsCount(HttpSession session) {
        if((session.getAttribute(SessionCache.SHOP_CART)) != null &&
                ((ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART)).getCartItems() != null    ){
            return ((ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART))
                    .getCartItems().size();
        }
        return 0;
    }

    public static List<Catalog> getCategoriesList() {
        ProductSubsystem pss = new ProductSubsystemFacade();
        List<Catalog> catList = new ArrayList<>();
        try {

            catList = pss.getCatalogList();
        } catch (BackendException e) {
            LOG.warning(e.getMessage());
        }
        return catList;
    }


}
