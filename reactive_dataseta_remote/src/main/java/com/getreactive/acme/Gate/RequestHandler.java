package com.getreactive.acme.Gate;

import com.mongodb.*;
import com.sigmoid.kryptos.Tagging.CompareVersions;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Servlet implementation class RequestHandler
 */


public class RequestHandler {


	/*
	 * This class handles the request sent
	 *
	 */

    static DB db;
    static MongoClientOptions options;
    static MongoClient mongo;
    static DBCollection collection;
    static BulkWriteOperation builder;
    static Client client;
    static IndexResponse response;
    static int global_count=0,iteration_Count=0,accurate_flag =0 ;
    static BulkRequestBuilder bulkRequestBuilder;
    static String es_ip="",es_cluster ="",es_index_name="",es_type_name="";
    static{

        /*

        This block initializes elasticsearch connection while loading the class


         */
        Properties ph = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("/Elasticsearch.properties");
            // load a properties file
            ph.load(input);
            es_ip = (String) ph.get("Es_Ip");
            es_cluster = (String) ph.get("Es_Cluster_Name");
            es_index_name = (String) ph.get("Es_Index_Name");
            es_type_name = (String) ph.get("Es_Index_Type");
        }catch(Exception e)
        {

        }

        Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.ping_timeout", 10000).put("cluster.name", "es_cluster").build();

        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(es_ip,9300));

        bulkRequestBuilder = client.prepareBulk();

        System.out.println("Done with ES connection**************************");


    }
    SimpleDateFormat format=new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss_SSS");
    private String banner;
    BasicDBObject document = null;

    public RequestHandler() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void process_request(String json_str) throws IOException, ParseException, InterruptedException {

        /*
        This method processes the given string and extracts the required entities

         */

   if(json_str.equalsIgnoreCase(""))
      {
             accurate_flag=1;
      }
   else
     {
        CompareVersions tagging = new CompareVersions();
        JSONObject tagged_json = new JSONObject();
        global_count++;
        this.banner = banner;
        ControllerPool pool = GateInitListener.controllerPool;
        ExtractionFromBanner banner_ex = new ExtractionFromBanner();
        JSONObject json = new JSONObject() ;
        JSONParser parser = new JSONParser();
        Object obj =null;
        try {
            obj = parser.parse(json_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject) obj;
      
        String ip_for_id = jsonObject.get("ip").toString();
        String port = jsonObject.get("port").toString();
        String banner_text="";
        try {
            banner_text = jsonObject.get("banner").toString();
        }catch(Exception e)
        {

        }
        JSONObject date_json = new JSONObject();
        String date = "";
        date_json = (JSONObject)jsonObject.get("t");
        String date_for_id = date_json.get("date").toString();
        String custom_id = ip_for_id+port;
        jsonObject.put("custom_id",custom_id);
        if(banner_text.equalsIgnoreCase(""))
        {
            List<String> empty_list = new ArrayList<String>();
            json.put("List_Of_entities",empty_list);
            jsonObject.put("Extracted_Info", json);
            jsonObject.put("status","notvulnerable");
        }
        else {
            SerialAnalyserController controller = pool.getController();
            try {
                json = banner_ex.main(banner_text, controller);
                jsonObject.put("Extracted_Info", json);

                jsonObject.put("status","notvulnerable");

            } catch (ResourceInstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pool.free(controller);
        }
    //tagged_json = tagging.TagFromRule(jsonObject);

     if (accurate_flag == 0) {

     synchronized (this)
     {
         try {

                if (global_count < 1000) {

                    bulkRequestBuilder.add(client.prepareIndex(es_index_name,es_type_name)
                    .setSource(jsonObject));

                    }
                else {

                   try{

                     iteration_Count++;
                     bulkRequestBuilder.add(client.prepareIndex(es_index_name,es_type_name)
                    .setSource(jsonObject));
                    bulkRequestBuilder.execute().actionGet();

                    SearchResponse response1 = client.prepareSearch(es_index_name).setTypes(es_type_name).execute().actionGet();

                    }catch(Exception e)

                    {
                         bulkRequestBuilder.add(client.prepareIndex(es_index_name, es_type_name)
                         .setSource(jsonObject));
                         e.printStackTrace();
                    }
                    bulkRequestBuilder = client.prepareBulk();
                    global_count=0;
                   }
            }catch(Exception e)
                {
                   e.printStackTrace();
                }
               }
     } else
        {

          IndexResponse response = client.prepareIndex(es_index_name,es_type_name)
                         .setSource(jsonObject)
                            .execute()
                            .actionGet();
        }
      }
    }
}
