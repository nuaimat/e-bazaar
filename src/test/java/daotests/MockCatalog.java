package daotests;

import business.externalinterfaces.Catalog;

public class MockCatalog implements Catalog {
	private int id;
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

}
