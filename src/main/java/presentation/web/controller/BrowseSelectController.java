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
        try {

            List<Catalog> catList = pss.getCatalogList();
            for(Catalog c:catList){
                List<Product> prodList = pss.getProductList(c);
            }
            request.setAttribute("categories", catList);
            request.getRequestDispatcher("/products.jsp").forward(request, response);
        } catch (BackendException e) {
            LOG.warning(e.getMessage());
        }
    }
}
