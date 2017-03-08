package logging;


import java.io.IOException;

import java.util.logging.*;


import java.io.File;


public class SetupLogger {
    /*
    *
     * Filehandlers are connected to their respective log files, with APPEND set to TRUE
     * Filehandlers/Levels;
     *      Level.SEVERE    Writes to the error log, 'ErrorLog.txt'
     *      Level.INFO      Writes to general log, 'Log.txt'
     *      Level.FINER     Writes to the debug log, 'Debug.txt'
     *
     * All handlers are then added to the logger, which is returned
     */


    public static Logger startLogger(String name) {//starts a default logger for info, debug, error logs.
        try {
            if (name == null) {
                throw new IllegalArgumentException("Parameter name null on call to startLogger!");
            }


            File dir = new File("out/LogFiles");
            if (dir.mkdirs()) {
                System.out.println("Directory out/LogFiles was created.");
            }

//            File[] array = {
//                    new File("./out/LogFiles/ErrorLog.log"),                                  //commented code made no difference on the logger running
//                    new File("./out/LogFiles/Log.log"),
//                    new File("./out/LogFiles/Debug.log"),
//
//            };
//
//
//
//            for (int i = 0; i < array.length; i++) {
//                try {
//                    if (array[i].createNewFile()) {
//                        System.out.println(array[i].getName() + " was created.");
//                    }
//                } catch (IOException ex) {
//                    System.out.println("Unexpected error initializing " + array[i].getName());
//                    System.out.println(ex.getMessage() + "\n");
//                    ex.printStackTrace();
//                }
//            }


            Formatter format = new SimpleFormatter();//uses default format
            Logger ret = Logger.getLogger(name);

            //this disables parent handlers in the root logger, prevents writing to console twice when logging Level.INFO
            // and higher levels
            ret.setUseParentHandlers(false);

            //creates file/console handlers
            FileHandler errOut = new FileHandler("./out/LogFiles/ErrorLog.log", true);
            FileHandler genLog = new FileHandler("./out/LogFiles/Log.log", true);
            FileHandler debugLog = new FileHandler("./out/LogFiles/Debug.log", true);
            Handler consoleHandler = new ConsoleHandler();//logs all Level.INFO by default

            errOut.setFilter(new SFilter(Level.SEVERE));
            genLog.setFilter(new SFilter(Level.INFO));
            debugLog.setFilter(new SFilter(Level.FINER));

            Handler handlerArray[] = new Handler[]{errOut, genLog, debugLog, consoleHandler};

            for (int i = 0; i < handlerArray.length; i++) {
                handlerArray[i].setFormatter(format);
                ret.addHandler(handlerArray[i]);
            }


            return ret;
        } catch (IOException ex) {
            System.out.println("Error creating log files");
            System.out.println(ex.getMessage() + "\n");
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            System.out.println("Error setting up logger");
            System.out.println(ex.getMessage() + "\n");
            ex.printStackTrace();
            return null;
        }
    }

}//end class