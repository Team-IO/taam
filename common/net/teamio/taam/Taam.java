package net.teamio.taam;

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
	
	public static final String GUI_FACTORY_CLASS = "net.teamio.taam.gui.GuiFactory";

	public static final String BLOCK_SENSOR = "taam.sensor";
	public static final String BLOCK_MACHINES = "taam.machines";
	public static final String BLOCK_PRODUCTIONLINE = "taam.productionline";
	public static final String BLOCK_PRODUCTIONLINE_ATTACHABLE = "taam.productionline_attachable";
	public static final String BLOCK_LOGISTICS = "taam.logistics";
	public static final String BLOCK_SLIDINGDOOR = "taam.slidingdoor";
	public static final String BLOCK_SENSOR_MOTION = "taam.sensor.motion";
	public static final String BLOCK_SENSOR_MINECT = "taam.sensor.minect";
	public static final String BLOCK_ORE = "taam.ore";
	public static final String BLOCK_MAGNET_RAIL = "taam.magnet_rail";

	/**
	 * Implementation removed
	 */
	@Deprecated
	public static final String BLOCK_CHUTE = "taam.chute";
	
	public static enum BLOCK_ORE_META {
		copper(true, true, true),
		tin(true, true, true),
		aluminum(true, true, true),
		bauxite(true, false, true),  //No Ingot
		kaolinite(true, false, false), //Currently, Ore Only!
		
		//Vanilla requires only the "custom" stuff
		gold(false, false, true),
		iron(false, false, true),
		coal(false, false, true),
		;
		
		public final boolean ore, ingot, dust;
		
		private BLOCK_ORE_META(boolean ore, boolean ingot, boolean dust) {
			this.ore = ore;
			this.ingot = ingot;
			this.dust = dust;
		}
		
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
		
		public static BLOCK_ORE_META valueOf(int meta) {
			BLOCK_ORE_META[] valuesAsEnum = values();
			if(meta < 0 || meta > valuesAsEnum.length) {
				return copper;
			}
			return valuesAsEnum[meta];
		}
	};
	
//	/**
//	 * Skip non-ingot stuff when registering ingots & smelting recipes
//	 * @param meta
//	 * @return
//	 */
//	public static boolean isOreOnly(int meta) {
//		return meta == 3 || meta == 4;
//	}
	
	public static enum BLOCK_MACHINES_META {
		chute,
		creativecache,
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};
	
	public static enum BLOCK_PRODUCTIONLINE_META {
		conveyor1,
		conveyor2,
		conveyor3,
		hopper,
		hopper_hs,
		sieve,
		shredder,
		grinder,
		crusher,
		chute,
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};
	
	public static enum BLOCK_PRODUCTIONLINE_ATTACHABLE_META {
		itembag,
		trashcan,
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};
	
	public static enum BLOCK_LOGISTICS_META {
		logistics_manager,
		logistics_station
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};
	
	public static final String MULTIPART_MULTINET_CABLE = "taam.multinet.cable";
	public static final String MULTIPART_MULTINET_MULTITRONIX = "taam.multinet.multitronix";
	
	public static final String ITEM_MULTINET_CABLE = "taam.cable";
	public static final String ITEM_DEBUG_TOOL = "taam.debugger";
	public static final String ITEM_WRENCH = "taam.wrench";
	public static final String ITEM_MULTINET_MULTITRONIX = "taam.multitronix";
	public static final String ITEM_MATERIAL = "taam.material";
	public static final String ITEM_PART = "taam.part";
	public static final String ITEM_TOOL = "taam.tool";
	public static final String ITEM_INGOT = "taam.ingot";
	public static final String ITEM_DUST = "taam.dust";
	public static final String ITEM_CONVEYOR_APPLIANCE = "taam.conveyor_appliance";
	public static final String ITEM_LOGISTICS_CART = "taam.logistics_cart";
	
	public static enum ITEM_CONVEYOR_APPLIANCE_META {
		sprayer,
		inserter
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};
	public static enum ITEM_LOGISTICS_CART_META {
		basic
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};
	
	public static enum ITEM_TOOL_META {
		saw
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};
	
	public static enum ITEM_MATERIAL_META {
		plastic_sheet,
		rubber_bar,
		graphite,
		iron_nugget,
		silicon_wafer,
		wooden_board,
		aluminum_plate,
		resin
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};
	public static enum ITEM_PART_META {
		photocell,
		motor,
		support_frame_wood,
		support_frame,
		circuit_basic,
		circuit_advanced,
		logistics_chip,
		rubber_band,
		pump,
		tank,
		nozzle,
		magnetic_coil,
		iron_frame,
		metal_bearing,
		copper_wire,
		sieve
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};
	
	public static final String TILEENTITY_SENSOR = "taam.sensor";
	public static final String TILEENTITY_CHUTE = "taam.chute";
	public static final String TILEENTITY_CREATIVECACHE = "taam.creativecache";
	public static final String TILEENTITY_SLIDINGDOOR = "taam.slidingdoor";
	public static final String TILEENTITY_CONVEYOR = "taam.conveyor";
	public static final String TILEENTITY_CONVEYOR_HOPPER = "taam.conveyor_hopper";
	public static final String TILEENTITY_CONVEYOR_PROCESSOR = "taam.conveyor_processor";
	public static final String TILEENTITY_LOGISTICS_STATION = "taam.logistics_station";
	public static final String TILEENTITY_LOGISTICS_MANAGER = "taam.logistics_manager";
	public static final String TILEENTITY_CONVEYOR_ITEMBAG = "taam.itembag";
	public static final String TILEENTITY_CONVEYOR_TRASHCAN = "taam.trashcan";
	public static final String TILEENTITY_CONVEYOR_SIEVE = "taam.sieve";

	public static final String ENTITY_LOGISTICS_CART = "taam.logistics_manager";
	
	public static final String CFG_COMMENT_SENSOR_DELAY = "Sensor [Motion, Minect] delay (minimum activation time) in game ticks, minimum 10";
	public static final String CFG_COMMENT_SENSOR_PLACEMENT_MODE = "Sensor [Motion, Minect] placement mode when side by side. 1 = move together, 2 = merge into one";
	public static final String CFG_COMMENT_GEN_COPPER_ORE = "Should Taam generate Copper Ore in the World";
	public static final String CFG_COMMENT_GEN_TIN_ORE  = "Should Taam generate Tin Ore in the World";
	public static final String CFG_COMMENT_GEN_BAUXITE_ORE  = "Should Taam generate B Ore in the World";
	public static final String CFG_COMMENT_GEN_ALUMINUM_ORE  = "Should Taam generate Aluminum Ore in the World";
	public static final String CFG_COMMENT_GEN_KAOLINITE_ORE  = "Should Taam generate Kaolinte Ore in the World";
	public static final String CFG_COMMENT_DEBUG_OUTPUT = "Should the Debug mode form Taam be activated";
	
	public static final String APPLIANCE_SPRAYER = "taam.sprayer";

	public static enum FLUID_DYE_META {
		black,
		red,
		green,
		brown,
		blue,
		purple,
		cyan,
		lightGray,
		gray,
		pink,
		lime,
		yellow,
		lightBlue,
		magenta,
		orange,
		white
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	};

	public static final String FLUID_DYE = "taam.dye";
	public static final String CHANNEL_NAME = "TAAM";
}
