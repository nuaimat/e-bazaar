package daotests;

import alltests.AllTests;
import business.externalinterfaces.DbClassCatalogForTest;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;
import dbsetup.DbQueries;
import junit.framework.TestCase;
import middleware.exceptions.DatabaseException;

import java.util.logging.Logger;

public class DbClassCatalogTest extends TestCase {
	
	static String name = "Catalog Test";
	static Logger log = Logger.getLogger(DbClassCatalogTest.class.getName());
	
	static {
		AllTests.initializeProperties();
	}
	
	
	public void testSaveNewCatalog() {
		ProductSubsystem prodsub = new ProductSubsystemFacade();
		DbClassCatalogForTest dbclass = prodsub.getGenericDbClassCatalog();
		Integer catId = -1;
		try{
			catId = dbclass.saveNewCatalogForTest("Test catalog");		
			assertTrue(catId>0);
		}catch(Exception ex){
			fail("Cannot save a new catalog");
		}finally {
			DbQueries.deleteCatalogRow(catId);
		}
	}
	
	public void testDeleteCatalog(){
		ProductSubsystem prodsub = new ProductSubsystemFacade();
		DbClassCatalogForTest dbclass = prodsub.getGenericDbClassCatalog();
		boolean success = true;
		try {
			String[] result = DbQueries.insertCatalogRow();
			int catId = Integer.parseInt(result[1]);
			dbclass.deleteCatalogForTest(catId);
		} catch(DatabaseException ex){
			fail("Cannot delete a product");
			success = false;
		}
		assertTrue(success);
	}
}
