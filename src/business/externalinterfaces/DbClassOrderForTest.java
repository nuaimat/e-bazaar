package business.externalinterfaces;

import java.util.List;

import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbClass;

public interface DbClassOrderForTest extends DbClass {

	public Integer submitOrderForTest(CustomerProfile customer, Order order) throws DatabaseException;
	public List<Integer> readOrderHistoryForTest(CustomerProfile customer) throws DatabaseException;
}
