package net.teamio.taam;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static Configuration config;
	
//	public static boolean genCopper = true;
//	public static boolean genTin = true;
//	public static boolean genAluminum = true;
//	public static boolean genBauxite = true;
//	public static boolean genKaolinite = true;
	
	public static final boolean[] genOre = new boolean[]{
			true,
			true,
			true,
			true,
			true,
	};

	public static final int[] oreSize = new int[]{
			14,
			13,
			2,
			35,
			35
	};
	public static final int[] oreAbove = new int[]{
			0,
			0,
			0,
			0,
			0
	};
	public static final int[] oreBelow = new int[]{
			59,
			59,
			59,
			128,
			100
	};
	public static final int[] oreDepositCount = new int[]{
			7,
			7,
			3,
			10,
			5
	};
	
	public static boolean debug = false;
	
	public static int sensor_delay = 30;
	public static int sensor_placement_mode = 1;
	
	public static float pl_trashcan_maxfill = 64f;
	
	public static int pl_conveyor_supportrange = 2;
	public static final byte[] pl_conveyor_speedsteps = new byte[] {
		80,
		40,
		5
	};
	public static byte pl_sieve_speedsteps = 20;
	
	public static int pl_hopper_highspeed_delay = 1;
	public static int pl_hopper_delay = 8;
	public static boolean pl_hopper_stackmode_normal_speed = false;

	public static boolean pl_processor_hurt = true;
	public static float pl_processor_hurt_chance = 0.2f;
	
//	public static int pl_appl_sprayer_resourceUsage = 1;

	public static int pl_processor_shredder_timeout = 1;
	public static int pl_processor_grinder_timeout = 15;
	public static int pl_processor_crusher_timeout = 15;
	public static int pl_processor_fluid_drier_timeout = 50;
	
	public static void init(File configFile)
	{
				
		
		if (config == null)
		{
		config = new Configuration(configFile);
	
		loadConfig();
		}
		
	}
	private static void loadConfig()
	{
		
		genOre[0] = config.getBoolean("generateCopper", "worldgen", true, Taam.CFG_COMMENT_GEN_COPPER_ORE);
		genOre[1] = config.getBoolean("generateTin", "worldgen", true , Taam.CFG_COMMENT_GEN_TIN_ORE);
		genOre[2]= config.getBoolean("generateAluminum", "worldgen", true , Taam.CFG_COMMENT_GEN_ALUMINUM_ORE);
		genOre[3] = config.getBoolean("generateBauxite", "worldgen", true , Taam.CFG_COMMENT_GEN_BAUXITE_ORE);
		genOre[4] = config.getBoolean("generateKaolinite", "worldgen", true , Taam.CFG_COMMENT_GEN_KAOLINITE_ORE);
				
		oreSize[0] = config.getInt("oreSize_Copper", "worldgen", 14 , 0, Integer.MAX_VALUE , "");
		oreSize[1] = config.getInt("oreSize_Tin", "worldgen", 13 , 0, Integer.MAX_VALUE , "");
		oreSize[2] = config.getInt("oreSize_Aluminum", "worldgen", 2 , 0, Integer.MAX_VALUE , "");
		oreSize[3] = config.getInt("oreSize_Bauxite", "worldgen", 35 , 0, Integer.MAX_VALUE , "");
		oreSize[4] = config.getInt("oreSize_Kaolinite", "worldgen", 35 , 0, Integer.MAX_VALUE , "");
		
		oreAbove[0] = config.getInt("oreAbove_Copper", "worldgen", 0 , 0, 255 , "");
		oreAbove[1] = config.getInt("oreAbove_Tin", "worldgen", 0 , 0, 255 , "");
		oreAbove[2] = config.getInt("oreAbove_Aluminum", "worldgen", 0 , 0, 255 , "");
		oreAbove[3] = config.getInt("oreAbove_Bauxite", "worldgen", 0 , 0, 255 , "");
		oreAbove[4] = config.getInt("oreAbove_Kaolinite", "worldgen", 0 , 0, 255 , "");

		oreBelow[0] = config.getInt("oreBelow_Copper", "worldgen", 64 , 0, 255 , "");
		oreBelow[1] = config.getInt("oreBelow_Tin", "worldgen", 64 , 0, 255 , "");
		oreBelow[2] = config.getInt("oreBelow_Aluminum", "worldgen", 64 , 0, 255 , "");
		oreBelow[3] = config.getInt("oreBelow_Bauxite", "worldgen", 128 , 0, 255 , "");
		oreBelow[4] = config.getInt("oreBelow_Kaolinite", "worldgen", 100 , 0, 255 , "");
		
		oreDepositCount[0] = config.getInt("oreDepositCount_Copper", "worldgen", 64 , 0, Integer.MAX_VALUE , "");
		oreDepositCount[1] = config.getInt("oreDepositCount_Tin", "worldgen", 64 , 0, Integer.MAX_VALUE , "");
		oreDepositCount[2] = config.getInt("oreDepositCount_Aluminum", "worldgen", 64 , 0, Integer.MAX_VALUE , "");
		oreDepositCount[3] = config.getInt("oreDepositCount_Bauxite", "worldgen", 128 , 0, Integer.MAX_VALUE , "");
		oreDepositCount[4] = config.getInt("oreDepositCount_Kaolinite", "worldgen", 100 , 0, Integer.MAX_VALUE , "");

		debug = config.getBoolean("debug_output", Configuration.CATEGORY_GENERAL, false, Taam.CFG_COMMENT_DEBUG_OUTPUT);
		
		sensor_delay = config.getInt("sensor_delay", "multitronix", 30, 10, 100, Taam.CFG_COMMENT_SENSOR_DELAY);
		sensor_placement_mode = config.getInt("sensor_placement_mode", "multitronix", 1, 1, 2, Taam.CFG_COMMENT_SENSOR_PLACEMENT_MODE);
		
		pl_trashcan_maxfill = config.getFloat("pl_trashcan_maxfill", "production_line", 64f, 1f, Float.MAX_VALUE, "Maximum fill level of trashcans. This counts stacks. (Fill Level 1 == 1 stack and it is full)");
		
		pl_conveyor_supportrange = config.getInt("pl_conveyor_supportrange", "production_line", 2, 0, Integer.MAX_VALUE, "!!Performance critical!! Keep this value low! Determines, how many blocks away a conveyor can be supported by other conveyors in the same direction.");
		pl_conveyor_speedsteps[0] = (byte)config.getInt("pl_conveyor_speedsteps_1", "production_line", 80, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 1 conveyors (wooden)");
		pl_conveyor_speedsteps[1] = (byte)config.getInt("pl_conveyor_speedsteps_2", "production_line", 40, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 2 conveyors (aluminum)");
		pl_conveyor_speedsteps[2] = (byte)config.getInt("pl_conveyor_speedsteps_3", "production_line", 5, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 3 conveyors (high throughput)");
		
		pl_hopper_delay = config.getInt("hopper_delay", "production_line", 8, 1, 500, "Drop Delay (ticks) for the conveyor hopper.");
		pl_hopper_highspeed_delay = config.getInt("hopper_highspeed_delay", "production_line", 0, 1, 500, "Drop Delay (ticks) for the high-speed conveyor hopper.");
		pl_hopper_stackmode_normal_speed = config.getBoolean("hopper_stackmode_normal_speed", "production_line", true, "Disabling this makes the High-Speed Hoppers eject in 'slow' speed when in stack-mode. (Same delay as the regular conveyor hopper)");
		
		pl_processor_hurt = config.getBoolean("pl_processor_hurt", "production_line", true, "Decides, whether standing on conveyor processors (Grinder, Crusher, Shredder) should hurt players/NPCs.");
		pl_processor_hurt_chance = config.getFloat("pl_processor_hurt_chance", "production_line", 0.2f, 0.00001f, 1f, "The likelyhood of being hurt by a processor. Calculated every tick.");
		pl_processor_shredder_timeout = config.getInt("pl_processor_shredder_timeout", "production_line", 1, 1, 200, "Ticks between each shredded item in the conveyor shredder.");
		pl_processor_grinder_timeout = config.getInt("pl_processor_grinder_timeout", "production_line", 15, 1, 200, "Ticks between each shredded item in the conveyor grinder.");
		pl_processor_crusher_timeout = config.getInt("pl_processor_crusher_timeout", "production_line", 15, 1, 200, "Ticks between each shredded item in the conveyor crusher.");
		pl_sieve_speedsteps = (byte)config.getInt("pl_sieve_speedsteps", "production_line", 20, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for conveyor sieves.");
		
		//pl_appl_sprayer_resourceUsage = config.getInt("sprayer_resourceUsage", "production_line_appliances", 1, 1, 500, "Resource usage per spray step in the sprayer.");

		pl_processor_fluid_drier_timeout = config.getInt("pl_processor_fluid_drier_timeout", "production_line_fluids", 50, 1, 200, "Ticks between each processed item in the fluid drier.");
		
		if(config.hasChanged())
		{
			config.save();
		}
	}
	
	
	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase(Taam.MOD_ID))
		{
			loadConfig();
		}
	}
	
}
