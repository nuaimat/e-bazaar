
package business.usecasecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import middleware.exceptions.DatabaseException;

import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;


public class ManageProductsController   {
    
    private static final Logger LOG = 
    	Logger.getLogger(ManageProductsController.class.getName());
    
    public List<Product> getProductsList(String catalog) throws BackendException {
    	ProductSubsystem pss = new ProductSubsystemFacade();    
    	LOG.warning("ManageProductsController method getProductsList has not been implemented");

    	return pss.getProductList(pss.getCatalogFromName(catalog));
    }
    
    public List<Catalog> getCatalogs() throws BackendException {
    	ProductSubsystem pss = new ProductSubsystemFacade();
    	return pss.getCatalogList();
    }
    
    public int saveNewCatalog(String catName) throws BackendException {
    	ProductSubsystem pss = new ProductSubsystemFacade(); 
    	return pss.saveNewCatalog(catName);
    }

    public int saveNewProduct(Product p, Catalog c) throws BackendException {
        ProductSubsystem pss = new ProductSubsystemFacade();
        pss.saveNewProduct(p, c);
        return p.getProductId();
    }
    
    
    public void deleteProduct(Product product) {
        ProductSubsystem pss = new ProductSubsystemFacade();
        try {
            pss.deleteProduct(product);
        } catch (BackendException e) {
           LOG.warning("ManageProductsController:deleteProduct exception " + e.getMessage());
        }

    }
    
    
}
