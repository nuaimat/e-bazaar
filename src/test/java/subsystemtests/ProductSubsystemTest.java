package subsystemtests;

import alltests.AllTests;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;
import dbsetup.DbQueries;
import junit.framework.TestCase;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductSubsystemTest extends TestCase {
	
	static String name = "Product Subsystem Test";
	static Logger log = Logger.getLogger(ProductSubsystemTest.class.getName());
	
	static {
		AllTests.testInitializeProperties();
	}
	
	public void testGetCatalogList() {
		//setup
		/*
		 * Returns a String[] with values:
		 * 0 - query
		 * 1 - catalog id
		 * 2 - catalog name
		 */
		String[] insertResult = DbQueries.insertCatalogRow();
		String expected = insertResult[2];
		
		ProductSubsystem pss = new ProductSubsystemFacade();
		try {
			List<String> found = pss.getCatalogList()
				      .stream()
				      .map(cat -> cat.getName())
				      .collect(Collectors.toList());
			boolean valfound = false;
			for(String catData : found) {
				
					if(catData.equals(expected)) valfound = true;
				
			}
			assertTrue(valfound);
			
		} catch(Exception e) {
			fail("Get Catalog List failed");
		} finally {
			DbQueries.deleteCatalogRow(Integer.parseInt(insertResult[1]));
		}
	
	}
	
}
