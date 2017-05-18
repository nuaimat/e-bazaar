package business.productsubsystem;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.logging.Logger;

import business.externalinterfaces.Catalog;
import business.externalinterfaces.DbClassCatalogForTest;
import business.externalinterfaces.Product;
import business.util.TwoKeyHashMap;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

/**
 * This class is concerned with managing data for a single
 * catalog. To read or update the entire list of catalogs in
 * the database, see DbClassCatalogs
 *
 */
class DbClassCatalog implements DbClass, DbClassCatalogForTest {

	enum Type {LOAD_CATALOG_TABLE, INSERT, READ_CATALOG, READ_CATALOG_BY_NAME, DELETE_CATALOG};
	@SuppressWarnings("unused")
	private static final Logger LOG = 
		Logger.getLogger(DbClassCatalog.class.getPackage().getName());
	private DataAccessSubsystem dataAccessSS = 
    	new DataAccessSubsystemFacade();
	
	private Type queryType;
	
	private String insertQuery = "INSERT into CatalogType (catalogname) VALUES(?)";
	private String readCatalogByNameQuery = "SELECT * FROM CatalogType WHERE catalogname = '?'";
	private String readCatalogQuery = "SELECT * FROM CatalogType WHERE catalogid = ?";
	private String readAllCatalogesQuery = "SELECT * FROM CatalogType";
	private String deleteCatalogQuery = "DELETE FROM CatalogType WHERE catalogid = ?";

	private Object[] insertParams, loadCatalogTableParams, readCatalogParams, deleteCatalogParams;
	private int[] insertTypes, loadCatalogTableTypes, readCatalogTypes, deleteCatalogTypes;
	private static TwoKeyHashMap<Integer, String, Catalog> catalogTable;

	private Catalog catalog;
    
    public int saveNewCatalog(String catalogName) throws DatabaseException {
    	queryType = Type.INSERT;
    	insertParams = new Object[]{catalogName};
    	insertTypes = new int[]{Types.VARCHAR};
    	return dataAccessSS.insertWithinTransaction(this);  	
    }
    
    @Override
	public String getDbUrl() {
		DbConfigProperties props = new DbConfigProperties();	
    	return props.getProperty(DbConfigKey.PRODUCT_DB_URL.getVal());
	}
    
    @Override
	public String getQuery() {
		switch(queryType) {
			case INSERT:
				return insertQuery;
			case LOAD_CATALOG_TABLE:
				return readAllCatalogesQuery;
			case READ_CATALOG:
				return readCatalogQuery;
			case READ_CATALOG_BY_NAME:
				return readCatalogByNameQuery;
			case DELETE_CATALOG:
				return deleteCatalogQuery;
			default:
				return null;
		}
	}
    @Override
   	public Object[] getQueryParams() {
   		switch(queryType) {
   			case INSERT:
   				return insertParams;
			case DELETE_CATALOG:
				return deleteCatalogParams;
   			default:
   				return null;
   		}
    }		
	 @Override
	public int[] getParamTypes() {
		 switch(queryType) {
			case INSERT:
				return insertTypes;
			 case DELETE_CATALOG:
				 return deleteCatalogTypes;
			default:
				return null;
		}
	 }
    @Override
	public void populateEntity(ResultSet resultSet) throws DatabaseException {
		// do nothing
		
	}

	public TwoKeyHashMap<Integer,String,Catalog> readCataloguesTable()
			throws DatabaseException {
		if (catalogTable != null) {
			return catalogTable.clone();
		}
		//catalogTable needs to be populated, so call refresh
		return refreshCatalogTable();

	}

	private TwoKeyHashMap<Integer, String, Catalog> refreshCatalogTable() throws DatabaseException {
		queryType = Type.LOAD_CATALOG_TABLE;
		loadCatalogTableParams = new Object[]{};
		loadCatalogTableTypes = new int[]{};
		dataAccessSS.atomicRead(this);

		// Return a clone since productTable must not be corrupted
		return catalogTable.clone();
	}

	public Catalog readCatalog(int catalogId)
			throws DatabaseException {
		if (catalogTable != null && catalogTable.isAFirstKey(catalogId)) {
			return catalogTable.getValWithFirstKey(catalogId);
		}
		queryType = Type.READ_CATALOG;
		readCatalogParams = new Object[] {catalogId};
		readCatalogTypes = new int[] {Types.INTEGER};
		dataAccessSS.atomicRead(this);
		return catalog;
	}

	public void deleteCatalog(Integer catalogId) throws DatabaseException {
		queryType = Type.DELETE_CATALOG;
		deleteCatalogParams = new Object[]{catalogId};
		deleteCatalogTypes = new int[] {Types.INTEGER};

		dataAccessSS.deleteWithinTransaction(this);
	}

	@Override
	public int saveNewCatalogForTest(String catName) throws DatabaseException {
		return saveNewCatalog(catName);
	}

	@Override
	public void deleteCatalogForTest(Integer catalogId) throws DatabaseException {
		deleteCatalog(catalogId);

	}


}
