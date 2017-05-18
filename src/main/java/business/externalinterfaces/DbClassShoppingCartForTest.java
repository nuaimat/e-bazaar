package business.externalinterfaces;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbClass;

public interface DbClassShoppingCartForTest extends DbClass{
	public Integer saveCartForTest(CustomerProfile custProfile, ShoppingCart cart) throws DatabaseException;
}
