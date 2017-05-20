package presentation.web.controller;

import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.productsubsystem.ProductSubsystemFacade;
import presentation.data.SessionCache;
import presentation.web.util.Common;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Mo nuaimat on 5/19/17.
 */
@WebServlet
public class BrowseSelectController extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(BrowseSelectController.class.getName());


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();

        List<Catalog> catList = Common.getCategoriesList();
        request.setAttribute("categories", catList);

        String selectedCategoryId = request.getParameter("cid");
        if(selectedCategoryId != null){
            Catalog selectedCatalog = getCatalogById(Integer.parseInt(selectedCategoryId), catList);
            try {
                List<Product> selectedProducts = pss.getProductList(selectedCatalog);
                request.setAttribute("product_list", selectedProducts);
            } catch (BackendException e) {
                LOG.warning(e.getMessage());
            }

            List<Integer> cartItemsIds = getCartItemsIds(request.getSession());
            request.setAttribute("cart_items_ids", cartItemsIds);
        }
        request.getRequestDispatcher("/products.jsp").forward(request, response);
    }

    private Catalog getCatalogById(int i, List<Catalog> catList) {
        for(Catalog c:catList){
            if(c.getId() == i){
                return c;
            }
        }
        return null;
    }

    private List<Integer> getCartItemsIds(HttpSession session){
        if((session.getAttribute(SessionCache.SHOP_CART)) != null){
            return ((ShoppingCartSubsystem)session.getAttribute(SessionCache.SHOP_CART))
                    .getCartItems()
                    .stream()
                    .map(cartItem -> cartItem.getProductid())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
