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
        HttpSession session = request.getSession();
        String method = request.getParameter("method");
        if(method == null){
            method = "browse";
        }

        switch (method){
            case "new_cat":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_catalogues");
                } else {
                    addNewCatalogue(request, response);
                }
                break;
            case "del_cat":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_catalogues");
                } else {
                    deleteCatalogue(request, response);
                }
                break;
            case "save_cat":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_catalogues");
                } else {
                    updateCatalogue(request, response);
                }
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_catalogues&msg=Invalid+action");

        }
    }

    private void updateCatalogue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();
        int catalogueId = Integer.parseInt(request.getParameter("id"));
        if(catalogueId > 0){
            try {
                Catalog cat = Common.getCatalogueById(catalogueId);
                if(cat == null) {
                    throw new ServletException("Catalogue not found!");
                }
                String catName = request.getParameter("name");
                if(catName == null) {
                    throw new ServletException("Invalid Catalogue Name!");
                }
                cat.setName(catName);
                pss.updateCatalog(cat);
            } catch (BackendException e) {
                throw new ServletException(e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_catalogues&msg=Successfully+Updated");

    }

    private void deleteCatalogue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();
        int catalogueId = Integer.parseInt(request.getParameter("id"));
        if(catalogueId > 0){
            try {
                Catalog cat = Common.getCatalogueById(catalogueId);
                if(cat == null) {
                    throw new ServletException("Catalogue not found!");
                }
                pss.deleteCatalog(cat);
            } catch (BackendException e) {
                throw new ServletException(e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_catalogues&msg=Successfully+Deleted");

    }

    private void addNewCatalogue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();
        String catalogueName = request.getParameter("name");
        if(catalogueName != null){
            try {
                pss.saveNewCatalog(catalogueName);
            } catch (BackendException e) {
                throw new ServletException(e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_catalogues&msg=Successfully+Added");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String method = request.getParameter("method");
        if(method == null){
            method = "browse";
        }
        HttpSession session = request.getSession();

        switch (method){
            case "manage_catalogues":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?action=" + method);
                } else {
                    displayManageCatalogues(request, response);
                }
                break;
            case "edit_catalogues":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?action=manage_catalogues");
                } else {
                    displayEditCatalogues(request, response);
                }
                break;
            default:
                display_products(request, response);
        }


    }

    private void displayEditCatalogues(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("catid"));
        request.setAttribute("method","edit_cat");
        request.setAttribute("catid",id);
        displayManageCatalogues(request, response);
    }

    private void displayManageCatalogues(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();
        List<Catalog> catList = Common.getCategoriesList();
        request.setAttribute("categories", catList);
        request.getRequestDispatcher("/manage_categories.jsp").forward(request, response);
    }

    private void display_products(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
