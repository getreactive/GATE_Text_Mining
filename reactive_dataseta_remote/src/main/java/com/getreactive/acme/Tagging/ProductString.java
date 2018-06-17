package com.getreactive.acme.Tagging;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductString 

{

	List<WithVersion> list_of_Products = new ArrayList<WithVersion>();
	
	
	public List<WithVersion> getList_of_Products() {
		return list_of_Products;
	}


	public void setList_of_Products(List<WithVersion> list_of_Products) {
		this.list_of_Products = list_of_Products;
	}


	public class WithVersion
	{
		String Product_String;
		String Version;
		
		String[] prod_with_version = new String[2];
	
		public String[] getProd_with_version() {
			return prod_with_version;
		}
		public void setProd_with_version(String[] prod_with_version) {
			this.prod_with_version = prod_with_version;
		}
		public String getProduct_String() {
			return Product_String;
		}
		public void setProduct_String(String product_String) {
			Product_String = product_String;
		}
		public String getVersion() {
			return Version;
		}
		public void setVersion(String version) {
			Version = version;
		}
	}
	
	
	
}
