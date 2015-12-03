package net.teamio.taam;

import org.apache.logging.log4j.*;

public final class Log {

	public static final Logger LOGGER = LogManager.getLogger(Taam.MOD_ID);
	
	public static void warn(String msg){
		LOGGER.warn(msg);
	}

	public static void error(String msg){
		LOGGER.error(msg);
	}
	
	public static void info(String msg){
		LOGGER.info(msg);
	}
	
	public static void debug(String msg){
		if(Config.debug) {
			LOGGER.info(msg);
		} else {
			LOGGER.debug(msg);
		}
	}
	
	private Log(){
	}
	
}