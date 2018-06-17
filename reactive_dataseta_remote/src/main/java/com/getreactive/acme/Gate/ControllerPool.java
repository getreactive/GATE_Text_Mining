package com.getreactive.acme.Gate;

import gate.*;
import gate.creole.ANNIEConstants;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.persist.PersistenceException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;


public class ControllerPool{
    /*This class creates pool of controllers
     *
     */
    public static Queue<String> document_Path=new LinkedList<String>();
    public static Queue<ControllerPool> poolqueue= new LinkedList<ControllerPool>();
    public static String FileSavePath;
    Vector<SerialAnalyserController> availableController,busyController;
    String listURL,  grammerURL,  patternFileURL;
    int initialConnections=1;int maxConnections=1;
    public ControllerPool(String listURL, String grammerURL, String patternFileURL, int minController, int maxController) {

		/*This constructor creates a vector of controllers
		 *
		 * @param listURL url for gazetteer list
		 * @param grammerURL Url for Jape file
		 * @param patternFileURL url for regex of version
		 * @param minController Minimum number of controllers to be initialized
		 * @param maXController Maximum number of controllers to be initialized
		 *
		 */

        this.listURL=listURL;
        this.grammerURL=grammerURL;
        this.patternFileURL=patternFileURL;
        if(minController>initialConnections)
            this.initialConnections=minController;
        if(maxController>maxConnections)
            this.maxConnections=maxController;
       // System.out.println("the urls are..."+listURL+grammerURL+patternFileURL+"--"+minController+"--"+maxController);
        if (initialConnections > maxConnections) {
            initialConnections = maxConnections;
        }
        availableController = new Vector(initialConnections);
        busyController = new Vector();
        for(int i=0; i<initialConnections; i++) {
            availableController.addElement(makeNewController());
        }
      //  System.out.println("!!!!!!!! size of availableController "+availableController.size()+" busyController "+busyController.size());
    }
    public synchronized void closeAllControllers()
    {
		 /*
		  * This method calls close controller
		  */
        closeController(availableController);
        availableController = new Vector();
        closeController(busyController);
        busyController = new Vector();
    }
    private void closeController(Vector listOfControllers)
    {
		 /*
		  * This method deletes controller
		  *
		  * @param listOfControllers it has list of all controllers initialized
		  */
        try
        {
            for(int i=0; i<listOfControllers.size(); i++)
            {
                SerialAnalyserController controller = (SerialAnalyserController) listOfControllers.elementAt(i);
                Factory.deleteResource(controller);
            }
        } catch(Exception e) {
            // Ignore errors; garbage collect anyhow
            e.printStackTrace();
        }
    }
    public synchronized void free(SerialAnalyserController controller)
    {
		/*
		 * This method removes controller from busy controllers list when it is done with its execution
		 *
		 * @param controller Controller to be made available
		 */
        busyController.removeElement(controller);
        availableController.addElement(controller);
        // Wake up threads that are waiting for a connection
      //  System.out.println("!!!!!!!! size of availableController "+availableController.size()+" busyController "+busyController.size());
        notifyAll();
    }

    private SerialAnalyserController makeNewController() {

		/*
		 * This method creates a new controller and initializes it
		 */

        try {
            SerialAnalyserController controller = (SerialAnalyserController)
                    PersistenceManager.loadObjectFromFile(new File(new File(
                            Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR), ANNIEConstants.DEFAULT_FILE));

            controller=initGateController(controller);

            return controller;
        } catch (PersistenceException | ResourceInstantiationException
                | IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    private SerialAnalyserController initGateController(SerialAnalyserController controller) {
		/*
		 * This method initializes controller with required resource urls and adds Processing resources to it
		 *
		 * @param controller controller to be initialized
		 *
		 * @return SerialAnalyserController Controller after initialization
		 */
        try{

            //System.out.println("In controller pool");

            FeatureMap paramsg = Factory.newFeatureMap();
            paramsg.put("listsURL", new File(listURL+File.separator+"equixlist.def").toURI().toURL());
            paramsg.put("caseSensitive",false);
            LanguageAnalyser mainGazetteer = (LanguageAnalyser)Factory.createResource(
                    "gate.creole.gazetteer.DefaultGazetteer", paramsg);
            FeatureMap regexparams = Factory.newFeatureMap();
            regexparams.put("patternFileURL", new File(patternFileURL+File.separator+"Version.txt").toURI().toURL());
            ProcessingResource regexann = (ProcessingResource)Factory.createResource(
                    "at.ofai.gate.regexpannotator.SimpleRegexpAnnotator", regexparams);
            FeatureMap regexparams1 = Factory.newFeatureMap();
            regexparams1.put("patternFileURL", new File(patternFileURL+File.separator+"Version1.txt").toURI().toURL());
            ProcessingResource regexann1 = (ProcessingResource)Factory.createResource(
                    "at.ofai.gate.regexpannotator.SimpleRegexpAnnotator", regexparams1);
            FeatureMap params = Factory.newFeatureMap();
            params.put("grammarURL", new File(grammerURL+File.separator+"Mainlist.jape").toURI().toURL());
            ProcessingResource transducer = (ProcessingResource)Factory.createResource(
                    "gate.jape.plus.Transducer", params);
            ((SerialAnalyserController) controller).add(mainGazetteer);
            ((SerialAnalyserController) controller).add(regexann);
            ((SerialAnalyserController) controller).add(regexann1);
            ((SerialAnalyserController) controller).add(transducer);

          //  System.out.println("Controllers initialized");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return controller;
    }
    public synchronized  SerialAnalyserController getController()
    {
		/*
		 * This method checks whether a controller is free or not and allocates it
		 *
		 * @return SerialAnalyserController Controller if Available
		 *
		 */
        if (!availableController.isEmpty())
        {
            SerialAnalyserController existingController =
                    (SerialAnalyserController) availableController.lastElement();
            int lastIndex = availableController.size() - 1;
            availableController.removeElementAt(lastIndex);
            busyController.addElement(existingController);
         //   System.out.println("!!!!!!!! size of availableController "+availableController.size()+" busyController "+busyController.size());
            return existingController;

        } else {
            try {
                wait();
            } catch(InterruptedException ie) {}
            // Someone freed up a connection, so try again.
            return(getController());
        }
    }

    private void makeBackgroundController() {
		/*
		 *This method creates a thread class instance and starts it
		 */
        try {
            Thread connectThread = new Thread((Runnable) this);
            connectThread.start();
        } catch(OutOfMemoryError oome) {
        }
    }
    public void run() {
        try {
            SerialAnalyserController controller = (SerialAnalyserController) makeNewController();
            synchronized(this)
            {
                availableController.addElement(controller);
                notifyAll();
            }
        } catch(Exception e) { // SQLException or OutOfMemory
            // Give up on new connection and wait for existing one
            // to free up.
        }
    }

}
