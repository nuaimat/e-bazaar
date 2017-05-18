package business.customersubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Logger;

import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;
import presentation.util.CacheReader;

class DbClassCreditCard implements DbClass {
	enum Type {READ_DEFAULT_PAYMENT_INFO};
	private static final Logger LOG
			= Logger.getLogger(DbClassCreditCard.class.getPackage().getName());
	private DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();

	private String readDefaultPaymentQuery = "SELECT  nameoncard, expdate, cardtype, cardnum " +
			"FROM Customer WHERE custid = ?";

	private Object[] readDefaultPaymentParams;
	private int[] readDefaultPaymentTypes;

	private Type queryType;

	private CreditCardImpl defaultPaymentCC;

	@Override
	public String getDbUrl() {
		DbConfigProperties props = new DbConfigProperties();
		return props.getProperty(DbConfigKey.ACCOUNT_DB_URL.getVal());
	}

	@Override
	public String getQuery() {
		switch(queryType) {
			case READ_DEFAULT_PAYMENT_INFO :
				return readDefaultPaymentQuery;
			default :
				return null;
		}
	}

	@Override
	public Object[] getQueryParams() {
		switch(queryType) {
			case READ_DEFAULT_PAYMENT_INFO :
				return readDefaultPaymentParams;
			default :
				return null;
		}
	}

	@Override
	public int[] getParamTypes() {
		switch(queryType) {
			case READ_DEFAULT_PAYMENT_INFO:
				return readDefaultPaymentTypes;
			default :
				return null;
		}
	}
	//stub
	public CreditCardImpl readDefaultPaymentInfo(CustomerProfile custProfile) 
			 throws DatabaseException {
		queryType = Type.READ_DEFAULT_PAYMENT_INFO;
		readDefaultPaymentParams = new Object[]{custProfile.getCustId()};
		readDefaultPaymentTypes = new int[] {Types.INTEGER};
		dataAccessSS.atomicRead(this);

		return defaultPaymentCC;

	 }

	@Override
	public void populateEntity(ResultSet rs) throws DatabaseException {
		switch(queryType) {
			case READ_DEFAULT_PAYMENT_INFO:
				populateDefaultPayment(rs);
				break;
			default:
				//do nothing
		}
		
	}

	private void populateDefaultPayment(ResultSet rs) throws DatabaseException {
		if(rs != null){
			try {
				while(rs.next()) {

					String nameOnCard = rs.getString("nameoncard");
					String expirationDate = rs.getString("expdate");
					String cardNumber = rs.getString("cardnum");
					String cardType = rs.getString("cardtype");
					defaultPaymentCC = new CreditCardImpl(nameOnCard, expirationDate, cardNumber, cardType);
				}
			}
			catch(SQLException e){
				throw new DatabaseException(e);
			}
		}
	}

}
