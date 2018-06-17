package com.getreactive.acme.Tagging;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;



public class Modify_JsonFormat 

{

    /*
       This class modifies json format foe elasticsearch indexing
     */
	static SimpleDateFormat format=new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss_SSS");
	

	public static JSONObject modify(JSONObject obj_for_indexing) throws IOException, InterruptedException, ParseException
	
	{
		// TODO Auto-generated method stub
		
		   JSONParser parser = new JSONParser();
		   Gson gson = new Gson();
		try{
			 test t =gson.fromJson(obj_for_indexing.get("Extracted_Info").toString(),test.class);
			 List<ProductNameConvert> list_pn = t.getList_Of_entities();
		     List<ProductString.WithVersion> list1 = new ArrayList<ProductString.WithVersion>();
			 List<ProductString.WithVersion> list2 = new ArrayList<ProductString.WithVersion>();
	 		JSONObject jobj = new JSONObject();
	   		Object obj1;
			
			for(int i=0;i<list_pn.size();i++)

             {
			    ProductString p_string = new ProductString();
				if(list_pn.get(i).getStr_name().trim().equalsIgnoreCase("ProductName"))
					{
						List<ProductNameConvert.Annotations> list_ann1 = list_pn.get(i).getInfo();

						for(int k1=0;k1<list_ann1.size();k1++)
						{
							String[] prod_array = new String[2];
													
							ProductString.WithVersion v_string = p_string.new WithVersion();						
							
							v_string.setProduct_String(list_ann1.get(k1).Product_String);
							
							v_string.setVersion(list_ann1.get(k1).Version);
							
							prod_array[0] = list_ann1.get(k1).Product_String;
							
							prod_array[1] = list_ann1.get(k1).Version;
							
							v_string.setProd_with_version(prod_array);
							
				     		list1.add(v_string);
							
						}
											
					p_string.setList_of_Products(list1);	
					
					String final_str = gson.toJson(p_string);
					
					obj1 = parser.parse(final_str);
					
					jobj = (JSONObject) obj1;
					//System.out.println("finalllll"+final_str);
					
					obj_for_indexing.put("ProductName",jobj);
		}
					

					else if(list_pn.get(i).getStr_name().trim().equalsIgnoreCase("PossibleProduct"))
					{
						
						List<ProductNameConvert.Annotations> list_ann1 = list_pn.get(i).getInfo();
						
						for(int k2=0;k2<list_ann1.size();k2++)
						{
							String[] possible_array = new String[2];
							
							ProductString.WithVersion v_string1 = p_string.new WithVersion();						
							
							v_string1.setProduct_String(list_ann1.get(k2).Product_String);
							
							v_string1.setVersion(list_ann1.get(k2).Version);
							
							possible_array[0] = list_ann1.get(k2).Product_String;
							possible_array[1] = list_ann1.get(k2).Version;
							
							v_string1.setProd_with_version(possible_array);
						
				     		list2.add(v_string1);
							
						}
											
					p_string.setList_of_Products(list2);	
					
					String final_str1 = gson.toJson(p_string);
					obj1 = parser.parse(final_str1);
					
					jobj = (JSONObject) obj1;
					
					
					obj_for_indexing.put("PossibleProducts",jobj);
					}		
							
					list1.clear();
					list2.clear();
				}
			}catch(Exception e)
			{
				
			}
			 obj_for_indexing.remove("Extracted_Info");
			 return obj_for_indexing;
			
		}
	
}
