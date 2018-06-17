package com.getreactive.acme.DtoClasses;

import java.util.ArrayList;
import java.util.List;

public class test {
	
	/*
	 * This class defines an object type for list of productName Objects
	 */
	
	public List<ProductNameConvert> List_Of_entities=new ArrayList<ProductNameConvert>();

	public List<ProductNameConvert> getList_Of_entities() {
		return List_Of_entities;
	}

	public void setList_Of_entities(ProductNameConvert total_types) {
		this.List_Of_entities.add(total_types);
	}

	
	
	

}
