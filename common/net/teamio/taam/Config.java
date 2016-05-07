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
	
	public static boolean multipart_load = true;
	public static boolean multipart_register_items = true;

	/**
	 * Set by {@link TaamMain} in {@link TaamMain#preInit(net.minecraftforge.fml.common.event.FMLPreInitializationEvent)}.
	 * If true, mcmultipart was found & can be used.
	 */
	public static boolean multipart_present;
	
	public static final String SECTION_WORLDGEN = "worldgen";
	public static final String SECTION_PRODUCTIONLINE = "production_line";
	public static final String SECTION_PRODUCTIONLINE_FLUIDS = "production_line_fluids";
	public static final String SECTION_COMPAT_MULTIPART = "compat_multipart";
	
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
		Taam.BLOCK_ORE_META[] oreMeta = Taam.BLOCK_ORE_META.values();
		for(int i = 0; i < 5; i++) {
			String name = oreMeta[i].config_name;
			genOre[i] = config.getBoolean("generate" + name, SECTION_WORLDGEN, true,
					"Should Taam generate " + name + " ore in the world?");
			oreSize[0] =	config.getInt("oreSize_" + name, SECTION_WORLDGEN, oreMeta[i].gen_default_size, 0, Integer.MAX_VALUE ,
					"Size of " + name + " ore veins");
			oreAbove[0] =	config.getInt("oreAbove_" + name, SECTION_WORLDGEN, oreMeta[i].gen_default_above, 0, 255 ,
					name + " ore veins spawn above this y-level");
			oreBelow[0] =	config.getInt("oreBelow_" + name, SECTION_WORLDGEN, oreMeta[i].gen_default_below, 0, 255 ,
					name + " ore veins spawn below this y-level");
			oreDepositCount[0] = config.getInt("oreDepositCount_Copper", SECTION_WORLDGEN, oreMeta[i].gen_default_count, 0, Integer.MAX_VALUE ,
					"Number of " + name + " ore veins per chunk");
		}

		debug = config.getBoolean("debug_output", Configuration.CATEGORY_GENERAL, false, "Should the Debug mode form Taam be activated");
		
		sensor_delay = config.getInt("sensor_delay", "multitronix", 30, 10, 100, "Sensor [Motion, Minect] delay (minimum activation time) in game ticks, minimum 10");
		sensor_placement_mode = config.getInt("sensor_placement_mode", "multitronix", 1, 1, 2, "Sensor [Motion, Minect] placement mode when side by side. 1 = move together, 2 = merge into one");
		
		pl_trashcan_maxfill = config.getFloat("pl_trashcan_maxfill", SECTION_PRODUCTIONLINE, 64f, 1f, Float.MAX_VALUE, "Maximum fill level of trashcans. This counts stacks. (Fill Level 1 == 1 stack and it is full)");
		
		pl_conveyor_supportrange = config.getInt("pl_conveyor_supportrange", SECTION_PRODUCTIONLINE, 2, 0, Integer.MAX_VALUE, "!!Performance critical!! Keep this value low! Determines, how many blocks away a conveyor can be supported by other conveyors in the same direction.");
		pl_conveyor_speedsteps[0] = (byte)config.getInt("pl_conveyor_speedsteps_1", SECTION_PRODUCTIONLINE, 80, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 1 conveyors (wooden)");
		pl_conveyor_speedsteps[1] = (byte)config.getInt("pl_conveyor_speedsteps_2", SECTION_PRODUCTIONLINE, 40, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 2 conveyors (aluminum)");
		pl_conveyor_speedsteps[2] = (byte)config.getInt("pl_conveyor_speedsteps_3", SECTION_PRODUCTIONLINE, 5, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 3 conveyors (high throughput)");
		
		pl_hopper_delay = config.getInt("hopper_delay", SECTION_PRODUCTIONLINE, 8, 1, 500, "Drop Delay (ticks) for the conveyor hopper.");
		pl_hopper_highspeed_delay = config.getInt("hopper_highspeed_delay", SECTION_PRODUCTIONLINE, 0, 1, 500, "Drop Delay (ticks) for the high-speed conveyor hopper.");
		pl_hopper_stackmode_normal_speed = config.getBoolean("hopper_stackmode_normal_speed", SECTION_PRODUCTIONLINE, true, "Disabling this makes the High-Speed Hoppers eject in 'slow' speed when in stack-mode. (Same delay as the regular conveyor hopper)");
		
		pl_processor_hurt = config.getBoolean("pl_processor_hurt", SECTION_PRODUCTIONLINE, true, "Decides, whether standing on conveyor processors (Grinder, Crusher, Shredder) should hurt players/NPCs.");
		pl_processor_hurt_chance = config.getFloat("pl_processor_hurt_chance", SECTION_PRODUCTIONLINE, 0.2f, 0.00001f, 1f, "The likelyhood of being hurt by a processor. Calculated every tick.");
		pl_processor_shredder_timeout = config.getInt("pl_processor_shredder_timeout", SECTION_PRODUCTIONLINE, 1, 1, 200, "Ticks between each shredded item in the conveyor shredder.");
		pl_processor_grinder_timeout = config.getInt("pl_processor_grinder_timeout", SECTION_PRODUCTIONLINE, 15, 1, 200, "Ticks between each shredded item in the conveyor grinder.");
		pl_processor_crusher_timeout = config.getInt("pl_processor_crusher_timeout", SECTION_PRODUCTIONLINE, 15, 1, 200, "Ticks between each shredded item in the conveyor crusher.");
		pl_sieve_speedsteps = (byte)config.getInt("pl_sieve_speedsteps", SECTION_PRODUCTIONLINE, 20, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for conveyor sieves.");
		
		//pl_appl_sprayer_resourceUsage = config.getInt("sprayer_resourceUsage", "production_line_appliances", 1, 1, 500, "Resource usage per spray step in the sprayer.");

		pl_processor_fluid_drier_timeout = config.getInt("pl_processor_fluid_drier_timeout", SECTION_PRODUCTIONLINE_FLUIDS, 50, 1, 200, "Ticks between each processed item in the fluid drier.");
		
		
		multipart_load = config.getBoolean("load_multiparts", SECTION_COMPAT_MULTIPART, true, "Load machines as multiparts if McMultipart is found.");
		multipart_register_items = config.getBoolean("register_multipart_items", SECTION_COMPAT_MULTIPART, true, "Allows you to disable registering of the multipart-variants of the items. Setting this to false means that multiparts will load without an issue, but all new machines will be created as full blocks and not as multiparts.");
		
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
			
			if(multipart_load && !multipart_present) {
				Log.warn("Config has multipart enabled, but it was not found. Multipart support will not be loaded.");
				multipart_load = false;
			}
		}
	}
	
}
