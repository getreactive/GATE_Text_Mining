package com.getreactive.acme.Gate;

import gate.Gate;
import gate.util.GateException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
 public class GateInitListener {
    /*
     * This class registers required plugins in gate and sets properties in servletcontext object
     *
     */
    static ControllerPool controllerPool = null;
    static SimpleDateFormat format=new SimpleDateFormat("hh:mm:ss.SSS");
/*    static {

        try {
            context();
            System.out.println("Pool intialized");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
*/
        public static void context() {

		/*
		 * This method gets attributes from properties file and sets to Servletcontext attribute
		 */
            Properties props = System.getProperties();

            props.setProperty("gate.home", "/home/ubuntu/Gate");
            props.setProperty("gate.plugins.home", "/home/ubuntu/Gate/plugins");

            System.setProperties(props);
            try {
                Gate.init();
                System.out.println("Gate Intialized");

            } catch (GateException e) {
                e.printStackTrace();
            }
            Properties ph = new Properties();
            InputStream input = null;

            try {

                input = new FileInputStream("/api.properties");

                // load a properties file
                ph.load(input);

                System.out.println("Properties file loaded");
                String OUTPUT = (String) ph.get("File_location");
                String listsURL = (String) ph.get("listsURL");
                String grammarURL = (String) ph.get("grammarURL");
                String patternFileURL = (String) ph.get("patternFileURL");
                String FileSavePath = (String) ph.get("FileSavePath");


                Gate.init();
                Gate.getCreoleRegister().registerDirectories(
                        new File(Gate.getPluginsHome(), "StringAnnotation").toURI().toURL());
                Gate.getCreoleRegister().registerDirectories(
                        new File(Gate.getPluginsHome(), "JAPE_Plus").toURI().toURL());
                System.out.println("min & max controller to start " + ph.get("minControllerToStart").toString() + "," + ph.get("maxControllerToStart").toString());
                int minController = Integer.parseInt(ph.get("minControllerToStart").toString().trim());
                int maxController = Integer.parseInt(ph.get("maxControllerToStart").toString().trim());
                Date date2=new Date();
                System.out.println("****Start creating : "+format.format(date2));
                controllerPool = new ControllerPool(ph.get("listsURL").toString(), ph.get("grammarURL").toString(), ph.get("patternFileURL").toString(), minController, maxController);
                Date date3=new Date();
                System.out.println("****End creating : "+format.format(date3));

                //servletContextEvent.getServletContext().setAttribute("controllerPool", controllerPool);
                System.out.println("Gate plugins loaded!!!!");
            } catch (GateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

