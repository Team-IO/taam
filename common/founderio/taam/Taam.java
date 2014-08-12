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
	
	public static final String GUI_FACTORY_CLASS = "founderio.taam.client.gui.GuiFactory";

	public static final String BLOCK_SENSOR = "taam.sensor";
	public static final String BLOCK_SLIDINGDOOR = "taam.slidingdoor";
	public static final String BLOCK_SENSOR_MOTION = "taam.sensor.motion";
	public static final String BLOCK_SENSOR_MINECT = "taam.sensor.minect";
	public static final String BLOCK_COPPPER_ORE = "taam.copperore";
	public static final String BLOCK_TIN_ORE = "taam.tinore";
	
	public static final String MULTIPART_MULTINET_CABLE = "taam.multinet.cable";
	public static final String MULTIPART_MULTINET_MULTITRONIX = "taam.multinet.multitronix";
	
	public static final String ITEM_MULTINET_CABLE = "taam.item.cable";
	public static final String ITEM_MULTINET_DEBUGGER = "taam.item.debugger";
	public static final String ITEM_MULTINET_MULTITRONIX = "taam.item.multitronix";
	public static final String ITEM_PHOTOCELL = "taam.item.photocell";
	public static final String ITEM_PLASTIC = "taam.item.plastic";
	public static final String ITEM_COPPER_INGOT = "taam.item.copper_ingot";
	public static final String ITEM_TIN_INGOT = "taam.item.tin_ingot";
	
	public static final String TILEENTITY_SENSOR = "taam.sensor";
	public static final String TILEENTITY_SLIDINGDOOR = "taam.slidingdoor";
	
	public static final String CFG_COMMENT_SENSOR_DELAY = "Sensor [Motion, Minect] delay (minimum activation time) in game ticks, minimum 10";
	public static final String CFG_COMMENT_SENSOR_PLACEMENT_MODE = "Sensor [Motion, Minect] placement mode when side by side. 1 = move together, 2 = merge into one";
	public static final String CFG_COMMENT_GEN_COPPER_ORE = "Should Taam generate Copper Ore in the World";
	public static final String CFG_COMMENT_GEN_TIN_ORE  = "Should Taam generate Tin Ore in the World";
	public static final String CFG_COMMENT_DEBUG_OUTPUT = "Should the Debug mode form Taam be activated";
}
