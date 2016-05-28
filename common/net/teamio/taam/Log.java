package net.teamio.taam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Log {

	public static final Logger LOGGER = LogManager.getLogger(Taam.MOD_ID);

	public static void warn(String msg){
		LOGGER.warn(msg);
	}

	public static void warn(String msg, Object... params){
		LOGGER.warn(msg, params);
	}

	public static void error(String msg){
		LOGGER.error(msg);
	}

	public static void error(String msg, Object... params){
		LOGGER.error(msg, params);
	}

	public static void error(String msg, Throwable e){
		LOGGER.error(msg, e);
	}

	public static void info(String msg){
		LOGGER.info(msg);
	}

	public static void info(String msg, Object... params){
		LOGGER.info(msg, params);
	}

	public static void debug(String msg){
		if(Config.debug) {
			LOGGER.info(msg);
		} else {
			LOGGER.debug(msg);
		}
	}

	public static void debug(String msg, Object... params){
		if(Config.debug) {
			LOGGER.info(msg, params);
		} else {
			LOGGER.debug(msg, params);
		}
	}

	private Log(){
	}

}