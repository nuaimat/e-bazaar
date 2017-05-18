package presentation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import business.exceptions.BackendException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.util.Convert;
import business.productsubsystem.ProductSubsystemFacade;

public enum ManageProductsData {
	INSTANCE;
	
	private CatalogPres defaultCatalog = readDefaultCatalogFromDataSource();
	private CatalogPres readDefaultCatalogFromDataSource() {
		//return DefaultData.CATALOG_LIST_DATA.get(0);
		CatalogPres catalogPres = new CatalogPres();
		ProductSubsystemFacade productSubsystemFacade = new ProductSubsystemFacade();
		try {
			catalogPres.setCatalog(productSubsystemFacade.getCatalogList().get(0));
		} catch (BackendException e) {
			e.printStackTrace();
		} finally {
			return catalogPres;
		}
	}
	public CatalogPres getDefaultCatalog() {
		return defaultCatalog;
	}
	
	private CatalogPres selectedCatalog = defaultCatalog;
	public void setSelectedCatalog(CatalogPres selCatalog) {
		selectedCatalog = selCatalog;
	}
	public CatalogPres getSelectedCatalog() {
		return selectedCatalog;
	}

	//////// Catalogs List model
	private ObservableList<CatalogPres> catalogList = readCatalogsFromDataSource();

	//////////// Products List model
	private ObservableMap<CatalogPres, List<ProductPres>> productsMap
	   = readProductsFromDataSource();
	
	/** Initializes the productsMap */
	private ObservableMap<CatalogPres, List<ProductPres>> readProductsFromDataSource() {
		//return DefaultData.PRODUCT_LIST_DATA;
		HashMap<CatalogPres, List<ProductPres>> ret = new HashMap<>();

		ProductSubsystemFacade productSubsystemFacade = new ProductSubsystemFacade();

		try {
			for(CatalogPres ca:catalogList){
				List<ProductPres> prodPresList = new ArrayList<>();
				List<Product> prodList = productSubsystemFacade.getProductList(ca.getCatalog());
				for(Product p:prodList){
					ProductPres pp = new ProductPres();
					pp.setProduct(p);
					prodPresList.add(pp);
				}
				ret.put(ca, prodPresList);
			}
		} catch (BackendException e) {
			e.printStackTrace();
		}

		return FXCollections.observableMap(ret);
	}
	
	/** Delivers the requested products list to the UI */
	public ObservableList<ProductPres> getProductsList(CatalogPres catPres) {
		return FXCollections.observableList(productsMap.get(catPres));
	}
	
	public ProductPres productPresFromData(Catalog c, String name, String date,  //MM/dd/yyyy 
			int numAvail, double price, String desc) {
		
		Product product = ProductSubsystemFacade.createProduct(c, name, 
				Convert.localDateForString(date), numAvail, price);
		product.setDescription(desc);
		ProductPres prodPres = new ProductPres();
		prodPres.setProduct(product);
		return prodPres;
	}
	
	public void addToProdList(CatalogPres catPres, ProductPres prodPres) {
		ObservableList<ProductPres> newProducts =
		           FXCollections.observableArrayList(prodPres);
		List<ProductPres> specifiedProds = productsMap.get(catPres);
		
		//Place the new item at the bottom of the list
		specifiedProds.addAll(newProducts);
		
	}
	
	/** This method looks for the 0th element of the toBeRemoved list 
	 *  and if found, removes it. In this app, removing more than one product at a time
	 *  is  not supported.
	 */
	public boolean removeFromProductList(CatalogPres cat, ObservableList<ProductPres> toBeRemoved) {
		if(toBeRemoved != null && !toBeRemoved.isEmpty()) {
			boolean result = productsMap.get(cat).remove(toBeRemoved.get(0));
			return result;
		}
		return false;
	}


	/** Initializes the catalogList */
	private ObservableList<CatalogPres> readCatalogsFromDataSource() {
		//return FXCollections.observableList(DefaultData.CATALOG_LIST_DATA);
		ProductSubsystemFacade productSubsystemFacade = new ProductSubsystemFacade();

		List<CatalogPres> catalogPresList = new ArrayList<>();
		try {
			List<Catalog> cList = productSubsystemFacade.getCatalogList();
			catalogPresList = new ArrayList<>(cList.size());
			for(Catalog c:cList){
				CatalogPres cp = new CatalogPres();
				cp.setCatalog(c);
				catalogPresList.add( cp );
			}
			return FXCollections.observableList(catalogPresList);
		} catch (BackendException e) {
			e.printStackTrace();
		}

		return FXCollections.observableList(catalogPresList);
	}

	/** Delivers the already-populated catalogList to the UI */
	public ObservableList<CatalogPres> getCatalogList() {
		return catalogList;
	}

	public CatalogPres catalogPresFromData(int id, String name) {
		Catalog cat = ProductSubsystemFacade.createCatalog(id, name);
		CatalogPres catPres = new CatalogPres();
		catPres.setCatalog(cat);
		return catPres;
	}

	public void addToCatalogList(CatalogPres catPres) {
		ObservableList<CatalogPres> newCatalogs = FXCollections
				.observableArrayList(catPres);

		// Place the new item at the bottom of the list
		// catalogList is guaranteed to be non-null
		boolean result = catalogList.addAll(newCatalogs);
		if(result) { //must make this catalog accessible in productsMap
			productsMap.put(catPres, FXCollections.observableList(new ArrayList<ProductPres>()));
		}
	}

	/**
	 * This method looks for the 0th element of the toBeRemoved list in
	 * catalogList and if found, removes it. In this app, removing more than one
	 * catalog at a time is not supported.
	 * 
	 * This method also updates the productList by removing the products that
	 * belong to the Catalog that is being removed.
	 * 
	 * Also: If the removed catalog was being stored as the selectedCatalog,
	 * the next item in the catalog list is set as "selected"
	 */
	public boolean removeFromCatalogList(ObservableList<CatalogPres> toBeRemoved) {
		boolean result = false;
		CatalogPres item = toBeRemoved.get(0);
		if (toBeRemoved != null && !toBeRemoved.isEmpty()) {
			result = catalogList.remove(item);
		}
		if(item.equals(selectedCatalog)) {
			if(!catalogList.isEmpty()) {
				selectedCatalog = catalogList.get(0);
			} else {
				selectedCatalog = null;
			}
		}
		if(result) {//update productsMap
			productsMap.remove(item);
		}
		return result;
	}
	
	//Synchronizers
	public class ManageProductsSynchronizer implements Synchronizer {
		@SuppressWarnings("rawtypes")
		@Override
		public void refresh(ObservableList list) {
			productsMap.put(selectedCatalog, list);
		}
	}
	public ManageProductsSynchronizer getManageProductsSynchronizer() {
		return new ManageProductsSynchronizer();
	}
	
	private class ManageCatalogsSynchronizer implements Synchronizer {
		@SuppressWarnings("rawtypes")
		@Override
		public void refresh(ObservableList list) {
			catalogList = list;
		}
	}
	public ManageCatalogsSynchronizer getManageCatalogsSynchronizer() {
		return new ManageCatalogsSynchronizer();
	}
}
