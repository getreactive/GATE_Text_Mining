package com.getreactive.acme.Tagging;


import java.util.ArrayList;

import java.util.List;




	public class ProductNameConvert
	{
		public String Str_name;
		
		public String getStr_name() {
			return Str_name;
		}
		public void setStr_name(String str_name) {
			Str_name = str_name;
		}
		List<Annotations> Info = new ArrayList<Annotations>();
		public List<Annotations> getInfo() {
			return Info;
		}
		public void setInfo(List<Annotations> info) {
			Info = info;
		}
		
		
		
		
	//Class for annotations
		
		
	public class Annotations
	{
		public String Product_String;
		public String Version;
		
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
	
	//class for id
	
	
	}