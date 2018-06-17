import com.mongodb.*;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class MongoIPCollection {

/*
This class collects all required details from mongo
 */

	public ArrayList<String> ipCollection() throws UnknownHostException, ParseException {

        /*
        This class collects the ips of all spot instances
         */


        BufferedReader br = null;
		ArrayList<String> list_of_ips = new ArrayList<String>();
        try {

            JSONParser parser = new JSONParser();
            JSONObject ip_json = new JSONObject();
            String ipInfo="",ip="";

            File file = new File("/home/ubuntu/sinstance.txt");
            String iplist_string = FileUtils.readFileToString(file);
            System.out.println("Read in: " + iplist_string);
            Object sample1 = parser.parse(iplist_string);
            JSONArray jsonArray1= (JSONArray) sample1;
            JSONObject inner_json =new JSONObject();
            for(int i=0;i<jsonArray1.size();i++)
            {
               inner_json = (JSONObject)jsonArray1.get(i);
                ip = inner_json.get("private_ip").toString();
                list_of_ips.add(ip);
            System.out.println("Ip is...."+ip);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("ip list is...."+list_of_ips);

        return list_of_ips;
	}


 public String GetFileName() throws UnknownHostException {

     /*
     This method is used to get filename that is to be given as input
      */

        ArrayList<String> ips = new ArrayList<String>();
        Mongo mongo = new Mongo("127.0.0.1", 27017);
        DB db = mongo.getDB("s3data");
        DBCollection table = db.getCollection("FilePathDetails");
        DBCursor cursor = table.find();
        String FileName="";

        while(cursor.hasNext())
        {
            BasicDBObject document = new BasicDBObject();
            document = (BasicDBObject) cursor.next();
            FileName = document.get("JsonFileName").toString();
        }

        mongo.close();
        return FileName;
    }


}
