package presentation.web.controller;

import business.exceptions.BackendException;
import business.externalinterfaces.*;
import business.productsubsystem.ProductImpl;
import business.productsubsystem.ProductSubsystemFacade;
import business.rulesubsystem.RulesSubsystemFacade;
import business.util.Convert;
import presentation.data.SessionCache;
import presentation.web.util.Common;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
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



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String method = request.getParameter("method");
        if(method == null){
            method = "browse";
        }
        HttpSession session = request.getSession();

        switch (method){
            case "manage_products":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=" + method);
                } else {
                    displayManageProducts(request, response);
                }
                break;
            case "edit_products":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_products");
                } else {
                    displayEditProducts(request, response);
                }
                break;
            case "add_products":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_products");
                } else {
                    displayAddProducts(request, response);
                }
                break;
            case "manage_catalogues":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=" + method);
                } else {
                    displayManageCatalogues(request, response);
                }
                break;
            case "edit_catalogues":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_catalogues");
                } else {
                    displayEditCatalogues(request, response);
                }
                break;
            default:
                displayProducts(request, response);
        }


    }

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
            case "save_product":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_products");
                } else {
                    updateProduct(request, response);
                }
                break;
            case "do_add_product":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=add_products&cid=" + request.getParameter("cid"));
                } else {
                    doAddProduct(request, response);
                }
                break;
            case "delete_product":
                if(!Common.isAdmin(session)){
                    response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_products&cid=" + request.getParameter("cid"));
                } else {
                    deleteProduct(request, response);
                }
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin_products?method=manage_products&msg=Invalid+action");

        }
    }

    private void displayAddProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();

        List<Catalog> catList = Common.getCategoriesList();
        request.setAttribute("categories", catList);

        request.getRequestDispatcher("/add_products.jsp").forward(request, response);
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();

        List<Catalog> catList = Common.getCategoriesList();
        request.setAttribute("categories", catList);

        int pid = Integer.parseInt(request.getParameter("pid"));

        int catid = Integer.parseInt(request.getParameter("catid"));
        Catalog cat = Common.getCatalogueById(catid);

        String prodName = request.getParameter("name");
        int qa =  Integer.parseInt(request.getParameter("quantity"));
        double up = Double.parseDouble(request.getParameter("unit_price"));
        String desc = request.getParameter("description");
        LocalDate md = Convert.localDateForString(request.getParameter("mfg_date"));


        ProductImpl p2 = new ProductImpl(cat, pid, prodName, qa, up, md, desc);
        try {
            pss.updateProduct(p2);
        } catch (BackendException e) {
            throw new ServletException(e.getMessage());
        }

        response.sendRedirect(request.getContextPath() +  "/admin_products?method=manage_products&cid=" + cat.getId() + "&msg=Product+Successfully+saved");
    }

    private void doAddProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();

        List<Catalog> catList = Common.getCategoriesList();
        request.setAttribute("categories", catList);


        int catid = Integer.parseInt(request.getParameter("catid"));
        Catalog cat = Common.getCatalogueById(catid);

        String prodName = request.getParameter("name");
        int qa =  Integer.parseInt(request.getParameter("quantity"));
        double up = Double.parseDouble(request.getParameter("unit_price"));
        String desc = request.getParameter("description");
        LocalDate md = Convert.localDateForString(request.getParameter("mfg_date"));


        ProductImpl p2 = new ProductImpl(cat, null, prodName, qa, up, md, desc);
        try {
            pss.saveNewProduct(p2, cat);
        } catch (BackendException e) {
            throw new ServletException(e.getMessage());
        }

        response.sendRedirect(request.getContextPath() +  "/admin_products?method=manage_products&cid=" + cat.getId() + "&msg=Product+Successfully+saved");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();

        List<Catalog> catList = Common.getCategoriesList();
        request.setAttribute("categories", catList);

        int pid = Integer.parseInt(request.getParameter("pid"));
        int cid = Integer.parseInt(request.getParameter("cid"));

        try {
            Product p = pss.getProductFromId(pid);
            cid = p.getCatalog().getId();
            pss.deleteProduct(p);
        } catch (BackendException e) {
            throw new ServletException(e.getMessage());
        }

        response.sendRedirect(request.getContextPath() +  "/admin_products?method=manage_products&cid=" + cid + "&msg=Product+Successfully+deleted");
    }


    private void displayEditProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();

        List<Catalog> catList = Common.getCategoriesList();
        request.setAttribute("categories", catList);

        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = null;
        try {
            p = pss.getProductFromId(pid);
        } catch (BackendException e) {
            throw new ServletException(e.getMessage());
        }

        request.setAttribute("product", p);
        request.getRequestDispatcher("/edit_products.jsp").forward(request, response);
    }

    private void displayManageProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        request.getRequestDispatcher("/manage_products.jsp").forward(request, response);
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

    private void displayProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSubsystem pss = new ProductSubsystemFacade();

        List<Catalog> catList = Common.getCategoriesList();
        request.setAttribute("categories", catList);

        String selectedCategoryId = request.getParameter("cid");
        if(selectedCategoryId != null){
            Catalog selectedCatalog = getCatalogById(Integer.parseInt(selectedCategoryId), catList);
            try {
                List<Product> selectedProducts = pss.getProductList(selectedCatalog);
                // remove items with 0 quantity
                selectedProducts = selectedProducts
                        .stream()
                        .filter( p -> p.getQuantityAvail() > 0)
                        .collect(Collectors.toList());


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
