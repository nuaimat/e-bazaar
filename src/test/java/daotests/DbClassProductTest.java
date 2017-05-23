package daotests;

import alltests.AllTests;
import business.externalinterfaces.*;
import business.productsubsystem.ProductSubsystemFacade;
import business.util.Convert;
import dbsetup.DbQueries;
import junit.framework.TestCase;

import java.util.logging.Logger;

public class DbClassProductTest extends TestCase {
	static String name = "Product Test";
	static Logger log = Logger.getLogger(DbClassProductTest.class.getName());
	
	static {
		AllTests.testInitializeProperties();
	}
	
	
	public void testSaveNewProduct() {
		ProductSubsystem psc = new ProductSubsystemFacade();
		DbClassProductForTest dbclass = psc.getGenericDbClassProduct();
		Catalog c = new MockCatalog();
		c.setId(1); //this might break

		Product product = ProductSubsystemFacade.createProduct(c, 0, "Test Product", 15, 15.0,
				Convert.localDateForString("10/21/2016"), "");
		Integer prodId = -1;
		try{
			prodId =  dbclass.saveNewProductForTest(product, c);
			assertTrue(prodId > 0);
		}catch(Exception ex){
			fail("Cannot save a new product");
		} finally {
			DbQueries.deleteProductRow(prodId);
		}
	}
	public void testSaveNewCatalog() {
		ProductSubsystem psc = new ProductSubsystemFacade();
		DbClassCatalogForTest dbclass = psc.getGenericDbClassCatalog();

		Integer catId = -1;
		try{
            catId =  dbclass.saveNewCatalogForTest("Test Catalog");
			assertTrue(catId > 0);
		}catch(Exception ex){
			fail("Cannot save a new product");
		} finally {
			DbQueries.deleteProductRow(catId);
		}
	}
	
	public void testDeleteProduct() {
		String[] vals = null;
		try{
			vals = DbQueries.insertProductRow();
		} catch(Exception ex) {
			fail("implementation of unittest has problem");
			return;
		}

		try {
			ProductSubsystem psc = new ProductSubsystemFacade();
			DbClassProductForTest dbclass = psc.getGenericDbClassProduct();
			dbclass.deleteProductForTest(Integer.parseInt(vals[1]));
		}catch(Exception ex){
			fail("Cannot delete a product");
		}
	}
}
