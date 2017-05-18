package business.externalinterfaces;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbClass;

public interface DbClassProductForTest extends DbClass{
	Integer saveNewProductForTest(Product product, Catalog catalog) throws DatabaseException;
	void deleteProductForTest(Integer productId) throws DatabaseException;
}
