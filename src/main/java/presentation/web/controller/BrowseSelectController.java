package presentation.web.controller;

import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

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
        List<Catalog> catList = new ArrayList<>();
        try {

            catList = pss.getCatalogList();
            request.setAttribute("categories", catList);
        } catch (BackendException e) {
            LOG.warning(e.getMessage());
        }

        String selectedCategoryId = request.getParameter("cid");
        if(selectedCategoryId != null){
            Catalog selectedCatalog = getCatalogById(Integer.parseInt(selectedCategoryId), catList);
            try {
                List<Product> selectedProducts = pss.getProductList(selectedCatalog);
                request.setAttribute("product_list", selectedProducts);
            } catch (BackendException e) {
                LOG.warning(e.getMessage());
            }

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
}
