package utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class ReitLogger {
	
  static private FileHandler fileTxt;

  static private SimpleFormatter formatterTxt;
  public static Logger fLogger;

  public static void logThis(String toLog, String loggedFrom){
	  fLogger.log(Level.INFO, loggedFrom + " -- " + toLog +"\n" );
  }
  
  static public void setup() throws IOException {
    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    logger.setLevel(Level.INFO);
    fileTxt = new FileHandler("REITLog.txt");

    formatterTxt = new SimpleFormatter();
    fileTxt.setFormatter(formatterTxt);
    logger.addHandler(fileTxt);
    fLogger = logger;
  }

}
 
