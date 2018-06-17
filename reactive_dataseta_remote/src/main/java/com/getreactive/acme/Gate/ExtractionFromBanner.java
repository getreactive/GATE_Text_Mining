package com.getreactive.acme.Gate;

import com.google.gson.Gson;
import com.sigmoid.kryptos.DtoClasses.ProductDto;
import com.sigmoid.kryptos.DtoClasses.ProductNameConvert;
import com.sigmoid.kryptos.DtoClasses.test;
import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExtractionFromBanner {

    /*This class tags products from bannertext
     *
     *
     */
    static SimpleDateFormat format=new SimpleDateFormat("hh:mm:ss.SSS");
    public JSONObject main(String banner_text, SerialAnalyserController analyserController) throws IOException, ResourceInstantiationException, ParseException{ 
        
    	/* This method initializes the process
    	 * 
    	 * @param banner_text String to be processed
    	 * @param analyserController Controller of gate
    	 * 
    	 * @returns String json string
    	 */
        Date date1=new Date();
        JSONObject jsonObject = null;
        try {
            Date date2=new Date();
            jsonObject=readFromPdf(analyserController,banner_text);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }
    public  JSONObject readFromPdf(SerialAnalyserController controller,String banner_text) throws IOException, ResourceInstantiationException, JSONException, ParseException{ 
       	   
    	/*This method reads from pdf and invokes process method
         * 
         * @param controller Gate Controller
         * @param banner_text String to be processed
         * 
         * @return JSONObject json object
         */
        Corpus corpus=Factory.newCorpus("Corpus");
        Date date1=new Date();
        Document doc = Factory.newDocument(banner_text);
        corpus.add(doc);
        JSONObject jsonReceive ;
        Date date2=new Date();
        jsonReceive = process(corpus,controller);
        Date date3=new Date();
        return jsonReceive;
    }
    private JSONObject process(Corpus corpus, CorpusController controller) throws ParseException
    {
    	 
   	 /*This method processes the doc and tags the entities
   	  * 
   	  * @param corpus Gate Corpus
   	  * @param controller Gate Controller
   	  * 
   	  */
        JSONObject json=null;
        Date date1=new Date();
        controller.setCorpus(corpus);  //Adding corpus to controller
        try {
            controller.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date date2=new Date();
        Iterator<Document> it = corpus.iterator();
        Document doc = it.next();
        String s1 = doc.toString();
        s1=s1.replaceAll("&nbsp"," ");
        s1=s1.replaceAll("<BR>"," ");
        String final_str="";
        AnnotationSet keySet = doc.getAnnotations();
        Set<ProductDto> productlist = new HashSet<ProductDto>();
        Set<ProductDto> possibleproductlist = new HashSet<ProductDto>();
        if(!keySet.isEmpty())
        {
            for(Annotation ann : keySet)    //Iterating within annotation set
            {
                try{
                    String type = ann.getType().trim();
                    if(type.equalsIgnoreCase("ProductName1"))
                    {
          //              System.out.println("In productName 1");
                        ProductDto productDto=new ProductDto();
                        productDto.setProduct(ann.getFeatures().get("prodname").toString().trim());
                        if(ann.getFeatures().get("version").toString().trim()!=null)
                        {
                            productDto.setVersion(ann.getFeatures().get("version").toString().trim());
                        }
                        productDto.setType("ProductName");
                        productlist.add(productDto);
                    }
                    else if(type.equalsIgnoreCase("PossibleProduct"))
                    {
                        ProductDto productDto=new ProductDto();
                        productDto.setProduct(ann.getFeatures().get("prodname").toString().trim());
                        productDto.setVersion(ann.getFeatures().get("version").toString());
                        productDto.setType(type);
                        possibleproductlist.add(productDto);

                    }

                    else if(type.equalsIgnoreCase("ProductName"))
                    {
            //            System.out.println("In productName");
                        ProductDto productDto=new ProductDto();
                        productDto.setProduct(ann.getFeatures().get("prodname").toString().trim());
                        productDto.setType(type);
                        productlist.add(productDto);
                    }
                }
                catch(NullPointerException e)
                {
                    e.printStackTrace();
                }
            }

            //Adding possible products into product list
            for(ProductDto pdo : possibleproductlist)
            {
                productlist.add(pdo);
            }
            int check=0,check1=0;
            test t = new test();
            for(ProductDto productDto: productlist)
            {
                try{
                    ProductNameConvert con = new ProductNameConvert();
                    ProductNameConvert.Annotations each_annotation=con.new Annotations();
                    List<ProductNameConvert.Annotations> ann_list = new ArrayList<ProductNameConvert.Annotations>();
                    int ch=0,ch1=0;
                    if(productDto.getType().equalsIgnoreCase("ProductName"))
                    {
                        check = 1;
                        try{
                            each_annotation.setProduct_String(productDto.getProduct());
                            if(productDto.getVersion()!=null | productDto.getVersion()!=" ")
                            {
                                each_annotation.setVersion(productDto.getVersion());
                            }
                            ann_list.add(each_annotation);
                        }catch(NullPointerException e){
                            e.printStackTrace();
                           // System.out.println("Incatch");
                        }
                        try
                        {
                            for(int i1=0;i1<t.getList_Of_entities().size();i1++)
                            {
                                if(t.getList_Of_entities().get(i1).getStr_name().equalsIgnoreCase("ProductName"))
                                {
                                    List<ProductNameConvert.Annotations> list1 = new ArrayList<ProductNameConvert.Annotations>();
                                    list1 = t.List_Of_entities.get(i1).getInfo();
                                    list1.add(each_annotation);
                                    t.List_Of_entities.get(i1).setInfo(list1);
                                    ch=1;
                                }
                            }
                        }
                        catch(IndexOutOfBoundsException e)
                        {
                            e.printStackTrace();
                        }
                        if(ch==0)
                        {
                            con.setStr_name("ProductName");
                            con.setInfo(ann_list);
                            t.setList_Of_entities(con);
                        }
                    }
                    else if(productDto.getType().equalsIgnoreCase("PossibleProduct"))
                    {
                        check1=1;
                        try{
                            each_annotation.setProduct_String(productDto.getProduct());
                            each_annotation.setVersion(productDto.getVersion());
                            ann_list.add(each_annotation);
                        }catch(NullPointerException e){
                            e.printStackTrace();
                            //System.out.println("Incatch");
                        }
                        try
                        {
                            for(int i=0;i<t.getList_Of_entities().size();i++)
                            {
                                if(t.getList_Of_entities().get(i).getStr_name().equalsIgnoreCase("PossibleProduct"))
                                {
                                    List<ProductNameConvert.Annotations> list1 = new ArrayList<ProductNameConvert.Annotations>();
                                    list1 = t.getList_Of_entities().get(i).getInfo();
                                    list1.add(each_annotation);
                                    t.getList_Of_entities().get(i).setInfo(list1);
                                    ch1=1;
                                }
                            }
                        }
                        catch(IndexOutOfBoundsException e)
                        {
                            e.printStackTrace();
                        }
                        if(ch1==0)
                        {

                            con.setStr_name("PossibleProduct");
                            con.setInfo(ann_list);
                            t.setList_Of_entities(con);
                        }
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            //Converting it into json object
            Gson g=new Gson();
            final_str = g.toJson(t);
            json = (JSONObject)new JSONParser().parse(final_str);
     //       corpus.cleanup();
            ExplicitcleanUp(corpus);
 }
        return json;
    }


  public void ExplicitcleanUp(Corpus corpus)
    {
    	Document doc1 = (Document)corpus.remove(0);
    	corpus.unloadDocument(doc1);
    	Factory.deleteResource(corpus);
    	Factory.deleteResource(doc1);
    }}
