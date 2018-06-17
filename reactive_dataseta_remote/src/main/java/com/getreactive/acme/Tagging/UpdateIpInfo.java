package com.getreactive.acme.Tagging;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by Rahul Kumar on 5/11/14.
 */
public class UpdateIpInfo
{
static Settings settings;
static Client client;
private static String es_ip="",es_cluster ="",es_index_name="",es_type_name="";
static{
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

 settings = (Settings) ImmutableSettings.settingsBuilder().put("client.transport.ping_timeout", 10000).put("cluster.name",es_cluster).build();

 client = new TransportClient((org.elasticsearch.common.settings.Settings) settings).addTransportAddress(new InetSocketTransportAddress(es_ip,9300));

}

    public void UpdateStatusOfIp(String ip) throws IOException, ParseException

    {

        /*
        This method updates the status of record with this ip
         */
        SearchHit hit = null;
        JSONObject query_obj = new JSONObject();
        JSONObject final_obj = new JSONObject();
        JSONObject hit_json = new JSONObject();
        TermQueryBuilder qb = QueryBuilders.termQuery("status", "vulnerable");
        TermQueryBuilder qb1 = QueryBuilders.termQuery("ip",ip);

       try {

         SearchResponse response = client.prepareSearch(es_index_name)
            .setTypes(es_type_name).setQuery(qb)
            .setQuery(qb1).execute().actionGet();

        JSONParser parser = new JSONParser();
        SearchHits hits = response.getHits();
        Iterator<SearchHit> iterator1 = hits.iterator();
        hit = (SearchHit) iterator1.next();
        String status = "not vulnerable";
        hit_json = (JSONObject) parser.parse(hit.getSourceAsString());

        }catch(Exception e)
        {

            //e.printStackTrace();
        }

        try {
            String id = hit.getId();

            UpdateResponse updateresponse = client.prepareUpdate(es_index_name, es_type_name, id)
                    .setScript("ctx._source.status=\"not vulnerable\"", null)
                    .execute()
                    .actionGet();
        }catch(Exception e)
        {
           // e.printStackTrace();
        }

    }
}
