package dbsetup;

import alltests.AllTests;
import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.Address;
import business.externalinterfaces.Order;
import business.ordersubsystem.OrderSubsystemFacade;
import business.util.Convert;
import daotests.DbClassAddressTest;
import middleware.DbConfigProperties;
import middleware.externalinterfaces.DbConfigKey;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

//import middleware.dataaccess.DataAccessUtil;

public class DbQueries {
	static {
		AllTests.initializeProperties();
	}
	static final DbConfigProperties PROPS = new DbConfigProperties();
	static Connection con = null;
	static Statement stmt = null;
	static final String USER = PROPS.getProperty(DbConfigKey.DB_USER.getVal()); 
    static final String PWD = PROPS.getProperty(DbConfigKey.DB_PASSWORD.getVal()); 
    static final String DRIVER = PROPS.getProperty(DbConfigKey.DRIVER.getVal());
    static final int MAX_CONN = Integer.parseInt(PROPS.getProperty(DbConfigKey.MAX_CONNECTIONS.getVal()));
    static final String PROD_DBURL = PROPS.getProperty(DbConfigKey.PRODUCT_DB_URL.getVal());
    static final String ACCT_DBURL = PROPS.getProperty(DbConfigKey.ACCOUNT_DB_URL.getVal());
	static Connection prodCon = null;
	static Connection acctCon = null;
    String insertStmt = "";
	String selectStmt = "";
	
	/* Connection setup */
	static {
		try {
			Class.forName(DRIVER);
		}
		catch(ClassNotFoundException e){
			//debug
			e.printStackTrace();
		}
		try {
			prodCon = DriverManager.getConnection(PROD_DBURL, USER, PWD);
			acctCon = DriverManager.getConnection(ACCT_DBURL, USER, PWD);
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	// just to test this class
	public static void testing() {
		try {
			stmt = prodCon.createStatement();
			stmt.executeQuery("SELECT * FROM Product");
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	//////////////// The public methods to be used in the unit tests ////////////
	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - product id
	 * 2 - product name
	 */
	public static String[] insertProductRow() {
		String[] vals = saveProductSql();
		String query = vals[0];
		try {
			stmt = prodCon.createStatement();
			
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return vals;
	}
	
	/**
	 */
	public static List<Address> readCustAddresses() {
		String query = readAddressesSql();
		List<Address> addressList = new LinkedList<Address>();
		try {
			stmt = acctCon.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			    
                while(rs.next()) {
                    
                    String street = rs.getString("street");
                    String city = rs.getString("city");
                    String state = rs.getString("state");
                    String zip = rs.getString("zip");
                    
             
                    Address addr 
                      = CustomerSubsystemFacade.createAddress(street,city,state,zip,true,true);
                   
                    addressList.add(addr);
                }  
                stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
			
		}
		return addressList;
		
	}
	
	
	/**
	 * Returns default bill address
	 */
	public static Address readDefaultBillAddress() {
		String query = readBillAddressSql();
		Address addr = null;
		try {
			stmt = acctCon.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			    
                if(rs.next()) {
                    
                    String street = rs.getString("street1") + rs.getString("street2");
                    String city = rs.getString("city");
                    String state = rs.getString("state");
                    String zip = rs.getString("zip");
                    
             
                    addr = CustomerSubsystemFacade.createAddress(street,city,state,zip,true,true);
                }  
                stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
			
		}
		return addr;
	}

	// return List Order
	public static List<Order> readOrderHistory(Integer custId) {
		String query = readOrderHistorySql(custId);
		List<Order> orderList = new LinkedList<Order>();
		try {
			stmt = acctCon.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			    
                while(rs.next()) {
                    
                    int orderId = rs.getInt("orderid");
                    String orderDate = rs.getString("orderdate");
                    double totalPrice = rs.getDouble("totalpriceamount");
             
                    Order order = OrderSubsystemFacade.createOrder(orderId, orderDate, ""+totalPrice);
                    orderList.add(order);
                }  
                stmt.close();
	            
		}
		catch(SQLException e) {
			e.printStackTrace();
			
		}
		return orderList;
		
	}

	/**
	 * Returns default bill address
	 */
	public static Address readDefaultShipAddress() {
		String query = readShipAddressSql();
		Address addr = null;
		try {
			stmt = acctCon.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			    
                if(rs.next()) {
                    
                    String street = rs.getString("street1") + rs.getString("street2");
                    String city = rs.getString("city");
                    String state = rs.getString("state");
                    String zip = rs.getString("zip");
                    
             
                    addr = CustomerSubsystemFacade.createAddress(street,city,state,zip,true,true);
                }  
                stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
			
		}
		return addr;
	}
	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - catalog id
	 * 2 - catalog name
	 */
	public static String[] insertCatalogRow() {
		String[] vals = saveCatalogSql();
		String query = vals[0];
		try {
			stmt = prodCon.createStatement();
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return vals;
	}
	
	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - customer id
	 * 2 - cust fname
	 * 3 - cust lname
	 */
	public static String[] insertCustomerRow() {
		String[] vals = saveCustomerSql();
		String query = vals[0];
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return vals;
	}

	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - shopping cart id
	 * 2 - customer id
	 * 3 - shipping street address
	 */
	public static String[] insertCartRow(){
		String[] vals = saveCartSql();
		String query = vals[0];
		
		try{
			stmt = acctCon.createStatement();
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vals;
	}

	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - order id
	 * 2 - cust id
	 * 3 - order date
	 */
	public static String[] insertOrderRow(){
		String[] vals = saveOrderSql();
		String query = vals[0];
		
		try{
			stmt = acctCon.createStatement();
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return vals;
	}
	public static void deleteCartRow(Integer cartId){
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteCartSql(cartId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteCartItems(Integer cartId){
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteCartItemsSql(cartId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}


	public static void deleteCatalogRow(Integer catId) {
		try {
			stmt = prodCon.createStatement();
			stmt.executeUpdate(deleteCatalogSql(catId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static void deleteProductRow(Integer prodId) {
		try {
			stmt = prodCon.createStatement();
			stmt.executeUpdate(deleteProductSql(prodId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static void deleteCustomerRow(Integer custId) {
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteCustomerSql(custId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteOrder(Integer orderId){
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteOrderSql(orderId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteOrderItems(Integer orderId){
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteOrderItemsSql(orderId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}


	
	///queries
	public static String readAddressesSql() {
		return "SELECT * from altaddress WHERE custid = " + DbClassAddressTest.DEFAULT_CUST_ID;
	}
	public static String readBillAddressSql() {
		return "SELECT billaddress1 as street1, billaddress2 as street2, billcity as city, billstate as state, billzipcode as zip  from customer WHERE custid = " + DbClassAddressTest.DEFAULT_CUST_ID;
	}
	public static String readShipAddressSql() {
		return "SELECT shipaddress1 as street1, shipaddress2 as street2, shipcity as city, shipstate as state, shipzipcode as zip  from customer WHERE custid = " + DbClassAddressTest.DEFAULT_CUST_ID;
	}
	public static String readOrderHistorySql(Integer custId){
		return "SELECT * FROM Ord WHERE custid = " + String.valueOf(custId);
	}
	public static String[] saveCatalogSql() {
		String[] vals = new String[3];
		
		String name = "testcatalog";
		vals[0] =
		"INSERT into CatalogType "+
		"(catalogid,catalogname) " +
		"VALUES(NULL, '" + name+"')";	  
		vals[1] = null;
		vals[2] = name;
		return vals;
	}
	public static String[] saveProductSql() {
		String[] vals = new String[3];
		String name = "testprod";
		vals[0] =
		"INSERT into Product "+
		"(productid,productname,totalquantity,priceperunit,mfgdate,catalogid,description) " +
		"VALUES(NULL, '" +
				  name+"',1,1,'10/22/2016',1,'test')";				  
		vals[1] = null;
		vals[2] = name;
		return vals;
	}
	public static String[] saveCustomerSql() {
		String[] vals = new String[4];
		String fname = "testf";
		String lname = "testl";
		vals[0] =
		"INSERT into Customer "+
		"(custid,fname,lname) " +
		"VALUES(NULL,'" +
				  fname+"','"+ lname+"')";
				  
		vals[2] = fname;
		vals[3] = lname;
		return vals;
	}
	public static String[] saveCartSql(){
		String[] vals = new String[4];
		String custId = "11"; //using custid =11 to avoid conflicting to custid 1
 		String shippingStrt = "1000 N 4th St";
		vals[0] = "INSERT into shopcarttbl " +
		"(custid,shipaddress1) " +
				"VALUES(" + custId + ",'" + shippingStrt + "')";
		
		vals[2] = custId;
		vals[3] = shippingStrt;
		
		return vals;
	}

	public static String[] saveOrderSql(){
		String[] vals = new String[4];
		String orderDate = Convert.localDateAsString(LocalDate.now());
		String custId = "11"; //using custid=11 for testing
		vals[0] = "INSERT into ord " + 
		"(custid,orderdate) " +
				"VALUES(" + custId + ",'" + orderDate + "')";
		vals[2] = custId;
		vals[3] = orderDate;
		
		return vals;
	}
	public static String deleteProductSql(Integer prodId) {
		return "DELETE FROM Product WHERE productid = "+prodId;
	}
	public static String deleteCatalogSql(Integer catId) {
		return "DELETE FROM CatalogType WHERE catalogid = "+catId;
	}
	
	public static String deleteCustomerSql(Integer custId) {
		return "DELETE FROM Customer WHERE custid = "+custId;
	}
	
	public static String deleteCartSql(Integer cartId){
		return "DELETE FROM shopcarttbl WHERE shopcartid = "+cartId;
	}

	public static String deleteCartItemsSql(Integer cartId){
		return "DELETE FROM ShopCartItem WHERE shopcartid = "+cartId;
	}

	public static String deleteOrderSql(Integer orderId){
		return "DELETE FROM ord WHERE orderid = " + orderId;
	}

	public static String deleteOrderItemsSql(Integer orderId){
		return "DELETE FROM OrderItem WHERE orderid = " + orderId;
	}
	public static void main(String[] args) {
		readAddressesSql();
		//System.out.println(System.getProperty("user.dir"));
		/*
		String[] results = DbQueries.insertCustomerRow();
		System.out.println("id = " + results[1]);
		DbQueries.deleteCustomerRow(Integer.parseInt(results[1]));
		results = DbQueries.insertCatalogRow();
		System.out.println("id = " + Integer.parseInt(results[1]));
		DbQueries.deleteCatalogRow(Integer.parseInt(results[1]));*/
	}
}
