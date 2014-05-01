package founderio.taam;

public final class Taam {
	private Taam() {
		//Util Class
	}
	
	public static final String MOD_ID = "taam";
	public static final String MOD_NAME = "Taam";
	public static final String MOD_VERSION = "0.1";
	public static final String MOD_AUTHOR1 = "founderio";
	public static final String MOD_AUTHOR2 = "Xander112";
	public static final String MOD_DESCRIPTION = "Tech and Acessories Mod";
	public static final String MOD_CREDITS = "";
	public static final String MOD_LOGO_PATH = "";

	public static final String BLOCK_SENSOR = "taam.sensor";
	public static final String BLOCK_SLIDINGDOOR = "taam.slidingdoor";
	public static final String BLOCK_SENSOR_MOTION = "taam.sensor.motion";
	public static final String BLOCK_SENSOR_MINECT = "taam.sensor.minect";
	
	public static final String MULTIPART_MULTINET_CABLE = "taam.multinet.cable";
	public static final String MULTIPART_MULTINET_MULTITRONIX = "taam.multinet.multitronix";
	
	public static final String ITEM_MULTINET_CABLE = "taam.item.cable";
	public static final String ITEM_MULTINET_DEBUGGER = "taam.item.debugger";
	public static final String ITEM_MULTINET_MULTITRONIX = "taam.item.multitronix";
	
	
	public static final String TILEENTITY_SENSOR = "taam.sensor";
	public static final String TILEENTITY_SLIDINGDOOR = "taam.slidingdoor";
	
	public static final String CFG_COMMENT_SENSOR_DELAY = "Sensor [Motion, Minect] delay (minimum activation time) in game ticks, minimum 10";
	public static final String CFG_COMMENT_SENSOR_PLACEMENT_MODE = "Sensor [Motion, Minect] placement mode when side by side. 1 = move together, 2 = merge into one";
}
