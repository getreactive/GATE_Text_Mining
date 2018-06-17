package com.getreactive.acme.Tagging;

import com.mongodb.*;
import org.elasticsearch.action.get.GetResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.*;


public class CompareVersions {

	/*
	This class processes the string for rule based tagging
	 */
	
	public static String search_word2,version2,banner;
	public static String port="",port_actualdoc="",es_ip="",mongo_ip="";
    private static String mongo_db_name="",mongo_collection_name="";
	static DBCursor product_cursor=null;
	static DBCollection product_info;
	static Map<String,List<List<String>>> map_of_all_ranges;
	static Map<String,List<List<String>>> map_for_custom_tags;
	static List<List<String>> common_attr_list;
    static DB db;
    static DB instance_db;
	static {

        /*
        This block gets the mongodb connection
         */
        Properties ph = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("/MongoDB.properties");
            // load a properties file
            ph.load(input);
            mongo_ip = (String) ph.get("Mongo_Ip");
            mongo_db_name = ph.get("Mongo_DB_Name").toString();
            mongo_collection_name = ph.get("Mongo_Collection_Name").toString();
        }catch(Exception e)
        {

        }

        MongoClient mongo = null;
        try {
            mongo = new MongoClient(mongo_ip, 27017);
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        db = mongo.getDB(mongo_db_name);

        instance_db = mongo.getDB("spot_instances");
    }
		//Creating sets and lists for different types of ranges
	
		
	//-------------------------------------------------------------------------------------------------------
    public JSONObject TagFromRule(JSONObject json_from_es) throws ParseException, IOException, InterruptedException {

        /*
            This method adds new fields according to rules
        */


        List<List<String>> less_than_set = new ArrayList<List<String>>();
		List<String> less_than_wordlist = new ArrayList<String>();
		List<String> less_than_versionlist = new ArrayList<String>();
		List<List<String>> less_than_customattr = new ArrayList<List<String>>();		
		
		List<List<String>> greater_than_set = new ArrayList<List<String>>();
		List<String> greater_than_wordlist = new ArrayList<String>();
		List<String> greater_than_versionlist = new ArrayList<String>();
		List<List<String>> greater_than_customattr = new ArrayList<List<String>>();
		
		List<List<String>> between_range_set = new ArrayList<List<String>>();
		List<String> between_range_wordlist = new ArrayList<String>();
		List<String> between_range_versionlist1 = new ArrayList<String>();
		List<String> between_range_versionlist2 = new ArrayList<String>();
		List<List<String>> between_range_customattr = new ArrayList<List<String>>();
		
		List<List<String>> and_set = new ArrayList<List<String>>();
		List<String> and_word1_list = new ArrayList<String>();
		List<String> and_word2_list = new ArrayList<String>();
		List<String> and_word3_list = new ArrayList<String>();
		List<List<String>> and_customattr = new ArrayList<List<String>>();
		
		
		List<List<String>> or_set = new ArrayList<List<String>>();
		List<String> or_word1_list = new ArrayList<String>();
		List<String> or_word2_list = new ArrayList<String>();
		List<String> or_word3_list = new ArrayList<String>();
		List<List<String>> or_customattr = new ArrayList<List<String>>();
		
		map_of_all_ranges = new HashMap<String,List<List<String>>>();
		map_for_custom_tags = new HashMap<String,List<List<String>>>();
		common_attr_list = new ArrayList<List<String>>(); ;
		DBCollection feedback_info = db.getCollection(mongo_collection_name);
    	DBCursor feedback_cursor = feedback_info.find();
	    product_cursor = product_info.find();
	    if(feedback_cursor.count()>0){
		
              while(feedback_cursor.hasNext()) {

        	 List<String> attr_list = new ArrayList<String>();
        	 DBObject feedback_obj = feedback_cursor.next();
             String search_word1 = (String) feedback_obj.get("Word1");
             search_word2 = feedback_obj.get("Word2").toString();
             String operator = feedback_obj.get("range").toString();
             attr_list.add(feedback_obj.get("TagName").toString());
             attr_list.add(feedback_obj.get("vulnerable").toString());
             attr_list.add(feedback_obj.get("risktype").toString());
             if(operator.equalsIgnoreCase("less than"))
               {
             	 less_than_wordlist.add(search_word1);
                 less_than_versionlist.add(search_word2);
                 less_than_customattr.add(attr_list);
               }
             else if(operator.equalsIgnoreCase("greater than"))
               {
                   try {
                       greater_than_wordlist.add(search_word1);
                       greater_than_versionlist.add(search_word2);
                       greater_than_customattr.add(attr_list);
                   }catch (Exception e){System.out.println("Got exception!!!!!");}
                   }
             else if(operator.equalsIgnoreCase("and")) {
                 port = feedback_obj.get("Word3").toString();
                 and_word1_list.add(search_word1);
                 and_word2_list.add(search_word2);
                 and_word3_list.add(port);
                 and_customattr.add(attr_list);
             }

             else if(operator.equalsIgnoreCase("or")) {
                 port = feedback_obj.get("Word3").toString();
                 and_word1_list.add(search_word1);
                 and_word2_list.add(search_word2);
                 and_word3_list.add(port);
                 and_customattr.add(attr_list);
             }
             else 
               {
            	 try{
                    version2 = feedback_obj.get("Word3").toString();
            	    }catch(Exception e)
            	    {
            		 version2 ="";
            	    }
            	 between_range_wordlist.add(search_word1);
            	 between_range_versionlist1.add(search_word2);
            	 between_range_versionlist2.add(version2);
            	 between_range_customattr.add(attr_list);
             }
           }
             }

        and_set.add(and_word1_list);
        and_set.add(and_word2_list);
        and_set.add(and_word2_list);
        less_than_set.add(less_than_wordlist);
        less_than_set.add(less_than_versionlist);
        less_than_set.add(less_than_versionlist);
         
         greater_than_set.add(greater_than_wordlist);
         greater_than_set.add(greater_than_versionlist);
         greater_than_set.add(greater_than_versionlist);
         
         between_range_set.add(between_range_wordlist);
         between_range_set.add(between_range_versionlist1);
         between_range_set.add(between_range_versionlist2);
         
         //storing in hashmap
         
         map_of_all_ranges.put("less than",less_than_set);
         map_of_all_ranges.put("greater than",greater_than_set);
         map_of_all_ranges.put("between",between_range_set);
         map_of_all_ranges.put("and",and_set);
         map_of_all_ranges.put("or",or_set);
         
         map_for_custom_tags.put("less than",less_than_customattr);
         map_for_custom_tags.put("greater than",greater_than_customattr);
         map_for_custom_tags.put("between", between_range_customattr);
         map_for_custom_tags.put("and",and_customattr);
         map_for_custom_tags.put("or",or_customattr);
 
   		 Modify_JsonFormat json_structure = new Modify_JsonFormat();
       	 JSONObject 	productname_json = new JSONObject();
       	 List<JSONObject> prod_extracted_list = new ArrayList<JSONObject>();
         List<String> products = new ArrayList<String>();
         List<String> versions = new ArrayList<String>();
     	 JSONObject modified_obj = new JSONObject();
     	 modified_obj = (JSONObject) json_structure.modify(json_from_es);
      	 JSONParser parser = new JSONParser();
       	 JSONObject mongo_json = new JSONObject();
       	 JSONObject prod_extracted_json = new JSONObject();
       	 JSONObject list_of_entities_json = new JSONObject();
       	 mongo_json = (JSONObject)parser.parse(modified_obj.toString());
    	 banner = mongo_json.get("banner").toString();
       	 BasicDBObject doc = new BasicDBObject();
         port_actualdoc = json_from_es.get("port").toString();
        	 try{
        	 prod_extracted_json = (JSONObject) parser.parse(mongo_json.get("ProductName").toString());
        	 prod_extracted_list = (List<JSONObject>) prod_extracted_json.get("list_of_Products");

        	 for(int j=0;j<prod_extracted_list.size();j++)
        	   {
        		 productname_json = (JSONObject)parser.parse(prod_extracted_list.get(j).toString());
        		 List<String> prod_with_version = new ArrayList<String>();
        		 prod_with_version = (List<String>) productname_json.get("prod_with_version");
        		 products.add(prod_with_version.get(0));
        		 versions.add(prod_with_version.get(1));

        	   }
        	   } catch(Exception e)
        	   {
        		   //e.printStackTrace();
        	   } 		 
          	 boolean result = false;
          	 String word2 = null,word1=null,word3=null;

  	     	 for(String operator_string:map_of_all_ranges.keySet())
        	 {
            	 if(modified_obj.get("status").toString().equalsIgnoreCase("notvulnerable")){
        		 GetResponse get_response=null;
        		 List<List<String>> list_of_words = new ArrayList<List<String>>();
        		 list_of_words = map_of_all_ranges.get(operator_string);

        		 if(list_of_words.size()==0)
        		 {
                     continue;
  		         }
        		 else if(list_of_words.get(0).size()==0) {
                     continue;
                 }
      		 try{
        	     for(int i=0;i<list_of_words.get(0).size();i++)
        		   {
          		try{
        	    	 if(products.contains(list_of_words.get(0).get(i).toString()))
        				{
        				    word1 = list_of_words.get(0).get(i).toString();
        				    int index = products.indexOf(list_of_words.get(0).get(i).toString());
                            search_word2 = list_of_words.get(1).get(i).toString();
        				    version2 = list_of_words.get(2).get(i).toString();
        				   	try{
        						word2 = versions.get(index);  //Getting version of corresponding index 
        					}catch(Exception e)
        					{
                               e.printStackTrace();
        						word2="";
        					}
      					 result = operator_check(operator_string,word1,search_word2, word2, version2,port_actualdoc);
        				}
        			
        			 else if(operator_string.equalsIgnoreCase("and") || operator_string.equalsIgnoreCase("or"))
        			 {
        				 word1 = list_of_words.get(0).get(i).toString();
        				 word2 = list_of_words.get(1).get(i).toString();
     				     word3=list_of_words.get(2).get(i).toString();
        				 result = operator_check(operator_string,word1,word2, search_word2, version2,port_actualdoc);
    			     }
        			}catch(Exception e)
        			{
        				//e.printStackTrace();
        			}

        			if(result==true)
        			    {

        		    	    UpdateIpInfo update_obj = new UpdateIpInfo();

        		    	    modified_obj.put("TagName",map_for_custom_tags.get(operator_string).get(i).get(0));
        		    	    modified_obj.put("vulnerability",map_for_custom_tags.get(operator_string).get(i).get(1));
        		    	    modified_obj.put("risk_type",map_for_custom_tags.get(operator_string).get(i).get(2));
        		    	    modified_obj.put("status","vulnerable");
        		    	    JSONObject modified_obj1 = (JSONObject)parser.parse(modified_obj.toString());

                    	    return modified_obj1;
        			    } 

        	 	   }
   		 
        		 }catch(Exception e){
        			 //e.printStackTrace();
        		 }
        	 }
        	 }
           
       	return modified_obj;
 		
           }
 	
	public static boolean compare_for_lessthan(String v1,String actual_version)
	{
    	String[] feedback_version_num1 = v1.split("\\.");
		String[] actual_version_num = actual_version.split("\\.");
		for(int q=0;q<feedback_version_num1.length;)
		{
			int feedback_val = Integer.parseInt(feedback_version_num1[q]);
		    int extracted_val1 = Integer.parseInt(actual_version_num[q]);
			if(extracted_val1<feedback_val)
			{
				return true;
			}
			else if(extracted_val1==feedback_val)
			{
				q++;
			}
			else{
				return false;
			}
		}
		return false;
		
	}
	
	
	
	public static boolean operator_check(String op_string,String word1,String word2,String version1,String version2,String port1)
    	{
	     boolean result=false;
		 if(op_string.equalsIgnoreCase("less than"))
		  {
			 result= compare_for_lessthan(word2,version1);
			 return result;
		  }
		 else if(op_string.equalsIgnoreCase("greater than"))
		  {
          	 result= compare_for_greaterthan(word2,version1);
			 return result;
		  }
		 else if(op_string.equalsIgnoreCase("and"))
		 {
			 result = compare_for_and(word1,word2,port,port1);
			 return result;
		 }
		 else if(op_string.equalsIgnoreCase("or"))
		 {
			 result = compare_for_or(word1, word2,port,port1);
			 return result;
		 }
		 else{
			 
			 result= compare_for_betweenrange(word2,version1,version2);
			 return result;
		   }
	    }
	
	public static boolean compare_for_greaterthan(String v1,String actual_version)
	  {

		String[] feedback_version_num1 = v1.split("\\.");
		String[] actual_version_num = actual_version.split("\\.");
	    for(int q=0;q<feedback_version_num1.length;)
		{
		  int feedback_val = Integer.parseInt(feedback_version_num1[q]);
		  int extracted_val1 = Integer.parseInt(actual_version_num[q]);
		  if(extracted_val1>feedback_val)
			{
				return true;
			}
			else if(extracted_val1==feedback_val)
			{
				q++;
			}
			else{
				return false;
			}
		}
		return false;
		
	  }
	
	
	public static boolean compare_for_betweenrange(String actual_version,String v1,String v2)
	{
		boolean result1 = compare_for_greaterthan(actual_version, v1);
		boolean result2 = compare_for_lessthan(actual_version, v2);
		if(result1==true && result2==true)
		{
			return true;
		}
		return false;
	}
	
	public static boolean compare_for_and(String word1,String word2,String word3,String port1)
	{
     	try{
		if(port1.equalsIgnoreCase(word3)){
      	    if(banner.contains(word1) && banner.contains(word2))
		    {
			 return true;
		    }
        }

	}catch(Exception e){e.printStackTrace();}
		return false;
	}

    public static boolean compare_for_or(String word1,String word2,String word3,String port1)
    {
        try{
            if(port1.equalsIgnoreCase(word3)){
                if(banner.contains(word1) || banner.contains(word2))
                {
                    return true;
                }
            }

        }catch(Exception e){e.printStackTrace();}
        return false;
    }
	
	
}


