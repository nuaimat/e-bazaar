package business.externalinterfaces;

import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbClass;

public interface DbClassCatalogForTest extends DbClass{
	public int saveNewCatalogForTest(String catName) throws DatabaseException;
	public void deleteCatalogForTest(Integer catalogId) throws DatabaseException;
}
