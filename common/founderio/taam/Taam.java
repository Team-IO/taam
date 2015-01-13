package founderio.taam;

public final class Taam {
	private Taam() {
		//Util Class
	}
	
	public static final String MOD_ID = "taam";
	public static final String MOD_NAME = "Taam";
	public static final String MOD_VERSION = "@VERSION@";
	public static final String MOD_AUTHOR1 = "founderio";
	public static final String MOD_AUTHOR2 = "Xander112";
	public static final String MOD_DESCRIPTION = "Tech and Acessories Mod";
	public static final String MOD_CREDITS = "";
	public static final String MOD_LOGO_PATH = "";
	
	public static final String GUI_FACTORY_CLASS = "founderio.taam.client.gui.GuiFactory";

	public static final String BLOCK_SENSOR = "taam.sensor";
	public static final String BLOCK_PRODUCTIONLINE = "taam.productionline";
	public static final String BLOCK_SLIDINGDOOR = "taam.slidingdoor";;
	public static final String BLOCK_SENSOR_MOTION = "taam.sensor.motion";
	public static final String BLOCK_SENSOR_MINECT = "taam.sensor.minect";
	public static final String BLOCK_ORE = "taam.ore";
	public static final String BLOCK_MAGNET_RAIL = "taam.magnet_rail";
	
	public static final String[] BLOCK_ORE_META = new String[] {
		"copper",
		"tin",
		"aluminum",
		"bauxite",
		"kaolinite"
	};
	
	public static final String[] BLOCK_PRODUCTIONLINE_META = new String[] {
		"conveyor",
		"hopper",
		"hopper_hs",
		"hopper_hs_standalone",
		"logistics_manager",
		"logistics_station"
	};
	
	public static final String MULTIPART_MULTINET_CABLE = "taam.multinet.cable";
	public static final String MULTIPART_MULTINET_MULTITRONIX = "taam.multinet.multitronix";
	
	public static final String ITEM_MULTINET_CABLE = "taam.cable";
	public static final String ITEM_DEBUG_TOOL = "taam.debugger";
	public static final String ITEM_WRENCH = "taam.wrench";
	public static final String ITEM_MULTINET_MULTITRONIX = "taam.multitronix";
	public static final String ITEM_MATERIAL = "taam.material";
	public static final String ITEM_PART = "taam.part";
	public static final String ITEM_INGOT = "taam.ingot";
	public static final String ITEM_CONVEYOR_APPLIANCE = "taam.conveyor_appliance";
	public static final String ITEM_LOGISTICS_CART = "taam.logistics_cart";
	
	public static final String[] ITEM_CONVEYOR_APPLIANCE_META = new String[] {
		"sprayer"
	};
	public static final String[] ITEM_LOGISTICS_CART_META = new String[] {
		"basic"
	};
	public static final String[] ITEM_MATERIAL_META = new String[] {
		"plastic",
		"rubber",
		"graphite",
		"silicon_wafer"
	};
	public static final String[] ITEM_PART_META = new String[] {
		"photocell",
		"motor",
		"support_frame",
		"circuit_basic",
		"circuit_advanced",
		"logistics_chip"
	};
	
	public static final String TILEENTITY_SENSOR = "taam.sensor";
	public static final String TILEENTITY_SLIDINGDOOR = "taam.slidingdoor";
	public static final String TILEENTITY_CONVEYOR = "taam.conveyor";
	public static final String TILEENTITY_CONVEYOR_HOPPER = "taam.conveyor_hopper";
	public static final String TILEENTITY_LOGISTICS_STATION = "taam.logistics_station";
	public static final String TILEENTITY_LOGISTICS_MANAGER = "taam.logistics_manager";

	public static final String ENTITY_LOGISTICS_CART = "taam.logistics_manager";
	
	public static final String CFG_COMMENT_SENSOR_DELAY = "Sensor [Motion, Minect] delay (minimum activation time) in game ticks, minimum 10";
	public static final String CFG_COMMENT_SENSOR_PLACEMENT_MODE = "Sensor [Motion, Minect] placement mode when side by side. 1 = move together, 2 = merge into one";
	public static final String CFG_COMMENT_GEN_COPPER_ORE = "Should Taam generate Copper Ore in the World";
	public static final String CFG_COMMENT_GEN_TIN_ORE  = "Should Taam generate Tin Ore in the World";
	public static final String CFG_COMMENT_DEBUG_OUTPUT = "Should the Debug mode form Taam be activated";
	
	public static final String APPLIANCE_SPRAYER = "taam.sprayer";

	public static final String[] FLUID_DYE_META = new String[] {
		"black",
		"red",
		"green",
		"brown",
		"blue",
		"purple",
		"cyan",
		"lightGray",
		"gray",
		"pink",
		"lime",
		"yellow",
		"lightBlue",
		"magenta",
		"orange",
		"white"
	};

	public static final String FLUID_DYE = "taam.dye";
	public static final String CHANNEL_NAME = "TAAM";
}
