package net.teamio.taam;

import net.minecraft.util.IStringSerializable;

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

	public static final String BLOCK_SENSOR = "sensor";
	public static final String BLOCK_MACHINES = "machines";
	public static final String BLOCK_PRODUCTIONLINE = "productionline";
	public static final String BLOCK_PRODUCTIONLINE_ATTACHABLE = "productionline_attachable";
	public static final String BLOCK_PRODUCTIONLINE_APPLIANCE = "productionline_appliance";
	public static final String BLOCK_LOGISTICS = "logistics";
	public static final String BLOCK_SLIDINGDOOR = "slidingdoor";
	public static final String BLOCK_SENSOR_MOTION = "sensor.motion";
	public static final String BLOCK_SENSOR_MINECT = "sensor.minect";
	public static final String BLOCK_ORE = "ore";
	public static final String BLOCK_CONCRETE = "concrete";
	public static final String BLOCK_MAGNET_RAIL = "magnet_rail";
	public static final String BLOCK_SUPPORT_BEAM = "support_beam";

	public static final String BLOCK_PIPE = "pipe";
	public static final String BLOCK_PIPEMACHINES = "pipemachines";
	
	public static enum BLOCK_ORE_META implements IStringSerializable {
		/*0*/copper(true, true, true),
		/*1*/tin(true, true, true),
		/*2*/aluminum(true, true, true),
		/*3*/bauxite(true, false, true),  //No Ingot
		/*4*/kaolinite(true, false, true), //No Ingot
		// Reserved for future use as blocks
		/*5*/reserved1(false, false, false),
		/*6*/reserved2(false, false, false),
		/*7*/reserved3(false, false, false),
		/*8*/reserved4(false, false, false),
		/*9*/reserved5(false, false, false),
		/*10*/reserved6(false, false, false),
		/*11*/reserved7(false, false, false),
		/*12*/reserved8(false, false, false),
		/*13*/reserved9(false, false, false),
		/*14*/reserved10(false, false, false),
		/*15*/reserved11(false, false, false),
		
		//Vanilla requires only the "custom" stuff
		/*16*/gold(false, false, true),
		/*17*/iron(false, false, true),
		/*18*/coal(false, false, true),
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

		@Override
		public String getName() {
			return name();
		}
	};
	
	public static enum BLOCK_CONCRETE_META implements IStringSerializable {
		rough,
		rough_chiseled,
		fine,
		fine_chiseled,
		coated,
		coated_chiseled,
		black,
		black_chiseled,
		warn1,
		warn2
		;
		
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}

		@Override
		public String getName() {
			return name();
		}
	};
	
	public static enum BLOCK_MACHINES_META implements IStringSerializable {
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

		@Override
		public String getName() {
			return name();
		}
	};
	
	public static enum BLOCK_PRODUCTIONLINE_META implements IStringSerializable {
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

		@Override
		public String getName() {
			return name();
		}
	};
	
	public static enum BLOCK_PRODUCTIONLINE_ATTACHABLE_META implements IStringSerializable {
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

		@Override
		public String getName() {
			return name();
		}
	};
	
	public static enum BLOCK_PRODUCTIONLINE_APPLIANCE_META implements IStringSerializable {
		sprayer,
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}

		@Override
		public String getName() {
			return name();
		}
	};
	

	
	public static enum BLOCK_PIPEMACHINES_META implements IStringSerializable {
		tank,
		creativewell,
		pump,
		mixer,
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}

		@Override
		public String getName() {
			return name();
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
	
	public static final String MULTIPART_MULTINET_CABLE = "multinet.cable";
	public static final String MULTIPART_MULTINET_MULTITRONIX = "multinet.multitronix";
	
	public static final String ITEM_MULTINET_CABLE = "cable";
	public static final String ITEM_DEBUG_TOOL = "debugger";
	public static final String ITEM_WRENCH = "wrench";
	public static final String ITEM_MULTINET_MULTITRONIX = "multitronix";
	public static final String ITEM_MATERIAL = "material";
	public static final String ITEM_PART = "part";
	public static final String ITEM_TOOL = "tool";
	public static final String ITEM_INGOT = "ingot";
	public static final String ITEM_DUST = "dust";
	public static final String ITEM_LOGISTICS_CART = "logistics_cart";
	
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
	
	public static final String TILEENTITY_PIPE = "taam.pipe";
	public static final String TILEENTITY_TANK = "taam.tank";
	public static final String TILEENTITY_CREATIVEWELL = "taam.creativewell";
	public static final String TILEENTITY_PUMP = "taam.pump";
	public static final String TILEENTITY_MIXER = "taam.mixer";
	
	public static final String TILEENTITY_APPLIANCE_SPRAYER = "taam.appliance.sprayer";

	public static final String ENTITY_LOGISTICS_CART = "taam.logistics_manager";
	
	public static final String CFG_COMMENT_SENSOR_DELAY = "Sensor [Motion, Minect] delay (minimum activation time) in game ticks, minimum 10";
	public static final String CFG_COMMENT_SENSOR_PLACEMENT_MODE = "Sensor [Motion, Minect] placement mode when side by side. 1 = move together, 2 = merge into one";
	public static final String CFG_COMMENT_GEN_COPPER_ORE = "Should Taam generate Copper Ore in the World";
	public static final String CFG_COMMENT_GEN_TIN_ORE  = "Should Taam generate Tin Ore in the World";
	public static final String CFG_COMMENT_GEN_BAUXITE_ORE  = "Should Taam generate B Ore in the World";
	public static final String CFG_COMMENT_GEN_ALUMINUM_ORE  = "Should Taam generate Aluminum Ore in the World";
	public static final String CFG_COMMENT_GEN_KAOLINITE_ORE  = "Should Taam generate Kaolinte Ore in the World";
	public static final String CFG_COMMENT_DEBUG_OUTPUT = "Should the Debug mode form Taam be activated";
	
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

	public static final String FLUID_DYE = "dye_";
	public static final String CHANNEL_NAME = "TAAM";
}
