package business.productsubsystem;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import business.exceptions.BackendException;
import business.externalinterfaces.*;
import business.util.TwoKeyHashMap;
import middleware.exceptions.DatabaseException;

public class ProductSubsystemFacade implements ProductSubsystem {
	private static final Logger LOG = 
			Logger.getLogger(ProductSubsystemFacade.class.getPackage().getName());
	public static Catalog createCatalog(int id, String name) {
		return new CatalogImpl(id, name);
	}
	public static Product createProduct(Catalog c, String name, 
			LocalDate date, int numAvail, double price) {
		return new ProductImpl(c, name, date, numAvail, price);
	}
	public static Product createProduct(Catalog c, Integer pi, String pn, int qa, 
			double up, LocalDate md, String desc) {
		return new ProductImpl(c, pi, pn, qa, up, md, desc);
	}
	
	/** obtains product for a given product name */
    public Product getProductFromName(String prodName) throws BackendException {
    	try {
			DbClassProduct dbclass = new DbClassProduct();
			return dbclass.readProduct(getProductIdFromName(prodName));
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}	
    }
    public Integer getProductIdFromName(String prodName) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			TwoKeyHashMap<Integer,String,Product> table = dbclass.readProductTable();
			return table.getFirstKey(prodName);
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
		
	}
    public Product getProductFromId(Integer prodId) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			return dbclass.readProduct(prodId);
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}
    public CatalogTypes getCatalogTypes() throws BackendException {
    	try {
			DbClassCatalogTypes dbClass = new DbClassCatalogTypes();
			return dbClass.getCatalogTypes();
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
    }
    
    public List<Catalog> getCatalogList() throws BackendException {
    	try {
			DbClassCatalogTypes dbClass = new DbClassCatalogTypes();
			return dbClass.getCatalogTypes().getCatalogs();
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
		
    }
   
    @SuppressWarnings("serial")
	public List<Product> getProductList(Catalog catalog) throws BackendException {
		DbClassProduct dbClassProduct = new DbClassProduct();
		List<Product> ret;
		try {
			ret = dbClassProduct.readProductList(catalog);
		} catch (DatabaseException e) {
			LOG.warning("ProductSubsystemFacade.getProductList: exception" + e.getMessage());
			return new ArrayList<>();
		}

		return ret;
    }
    
    
	public int readQuantityAvailable(Product product) throws BackendException {
		return product.getQuantityAvail();
	}
	
	public int saveNewCatalog(String catalogName) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			return dbclass.saveNewCatalog(catalogName);
		} catch(DatabaseException e) {
    		throw new BackendException(e);
    	}
	}

	@Override
	public Catalog getCatalogFromName(String catName) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			return dbclass.readCatalog(getCatalogIdFromName(catName));
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}

	private int getCatalogIdFromName(String catName) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			TwoKeyHashMap<Integer,String,Catalog> table = dbclass.readCataloguesTable();
			return table.getFirstKey(catName);
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}

	@Override
	public void saveNewProduct(Product product, Catalog catalog) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			dbclass.saveNewProduct(product, catalog);
			return;
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}

	}

	@Override
	public void deleteProduct(Product product) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			dbclass.deleteProduct(product.getProductId());
			return;
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}

	@Override
	public void deleteCatalog(Catalog catalog) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			dbclass.deleteCatalog(catalog.getId());
			return;
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}

	public DbClassProductForTest getGenericDbClassProduct() {
		return new DbClassProduct();
	}

	@Override
	public DbClassCatalogForTest getGenericDbClassCatalog() {
		return new DbClassCatalog();
	}

	@Override
	public void updateCatalog(Catalog cat) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			dbclass.updateCatalogName(cat.getId(), cat.getName());
			return;
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}


}
