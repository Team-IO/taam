package net.teamio.taam;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static Configuration config;

	public static final int NUM_ORES = 5;
	public static final boolean[] genOre = new boolean[NUM_ORES];
	public static final int[] oreSize = new int[NUM_ORES];
	public static final int[] oreAbove = new int[NUM_ORES];
	public static final int[] oreBelow = new int[NUM_ORES];
	public static final int[] oreDepositCount = new int[NUM_ORES];

	public static boolean debug_output;
	public static boolean use_iinventory_compat;
	public static boolean render_tank_content;
	public static boolean render_items;
	public static boolean dark_theme;

	public static int sensor_delay;
	public static int sensor_placement_mode;

	public static float pl_trashcan_maxfill;

	public static int pl_conveyor_supportrange;
	public static final byte[] pl_conveyor_speedsteps = new byte[3];
	
	public static byte pl_sieve_speedsteps;

	public static int pl_hopper_highspeed_delay;
	public static int pl_hopper_delay;
	public static boolean pl_hopper_stackmode_normal_speed;

	public static boolean pl_processor_hurt;
	public static float pl_processor_hurt_chance;
	public static int pl_processor_shredder_timeout;
	public static int pl_processor_grinder_timeout;
	public static int pl_processor_crusher_timeout;
	
	public static int pl_fluid_drier_timeout;
	public static int pl_fluid_drier_capacity;
	
	public static int pl_mixer_timeout;
	public static int pl_mixer_capacity_input;
	public static int pl_mixer_capacity_output;
	
	public static int pl_pipe_capacity;
	public static boolean pl_pipe_wrap_ifluidhandler;

	public static int pl_pump_capacity;
	public static int pl_pump_pressure;
	public static int pl_pump_suction;

	public static int pl_tank_capacity;
	public static int pl_tank_suction;

	public static int pl_creativewell_pressure;

	public static int pl_sprayer_capacity;
	
	public static boolean multipart_load = true;
	public static boolean multipart_register_items = true;

	/**
	 * Set by {@link TaamMain} in {@link TaamMain#preInit(net.minecraftforge.fml.common.event.FMLPreInitializationEvent)}.
	 * If true, mcmultipart was found & can be used.
	 */
	public static boolean multipart_present;
	
	public static boolean jei_render_machines_into_gui = true;

	public static final String SECTION_WORLDGEN = "worldgen";
	public static final String SECTION_MULTITRONIX = "multitronix";
	public static final String SECTION_MULTITRONIX_SENSOR = SECTION_MULTITRONIX + ".sensor";
	public static final String SECTION_PRODUCTIONLINE = "production_line";
	public static final String SECTION_PRODUCTIONLINE_FLUIDDRIER = SECTION_PRODUCTIONLINE + ".fluiddrier";
	public static final String SECTION_PRODUCTIONLINE_MIXER = SECTION_PRODUCTIONLINE + ".mixer";
	public static final String SECTION_PRODUCTIONLINE_PIPE = SECTION_PRODUCTIONLINE + ".pipe";
	public static final String SECTION_PRODUCTIONLINE_PUMP = SECTION_PRODUCTIONLINE + ".pump";
	public static final String SECTION_PRODUCTIONLINE_TANK = SECTION_PRODUCTIONLINE + ".tank";
	public static final String SECTION_PRODUCTIONLINE_CREATIVEWELL = SECTION_PRODUCTIONLINE + ".creativewell";
	public static final String SECTION_PRODUCTIONLINE_CONVEYORS = SECTION_PRODUCTIONLINE + ".conveyors";
	public static final String SECTION_PRODUCTIONLINE_SIEVE = SECTION_PRODUCTIONLINE + ".sieve";
	public static final String SECTION_PRODUCTIONLINE_TRASHCAN = SECTION_PRODUCTIONLINE + ".trashcan";
	public static final String SECTION_PRODUCTIONLINE_HOPPER = SECTION_PRODUCTIONLINE + ".hopper";
	public static final String SECTION_PRODUCTIONLINE_PROCESSORS = SECTION_PRODUCTIONLINE + ".processors";
	public static final String SECTION_PRODUCTIONLINE_SPRAYER = SECTION_PRODUCTIONLINE + ".appliance_sprayer";
	public static final String SECTION_INTEGRATION = "integration";
	public static final String SECTION_INTEGRATION_MULTIPART = SECTION_INTEGRATION + ".multipart";
	public static final String SECTION_INTEGRATION_JEI = SECTION_INTEGRATION + ".jei";
	
	public static final String[] guiSections = {
			Configuration.CATEGORY_GENERAL,
			SECTION_WORLDGEN,
			SECTION_PRODUCTIONLINE,
			SECTION_INTEGRATION
	};
	

	public static void init(File configFile)
	{


		if (config == null)
		{
			config = new Configuration(configFile);

			loadConfig();
		}

	}
	
	private static int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment) {
		String langKey = String.format("taam.config.%s.%s", category, name);
		return config.getInt(name, category, defaultValue, minValue, maxValue, comment, langKey);
	}
	
	/**
	 * Sets needsWorldRestart
	 * @param name
	 * @param category
	 * @param defaultValue
	 * @param minValue
	 * @param maxValue
	 * @param comment
	 * @return
	 */
	private static int getIntWR(String name, String category, int defaultValue, int minValue, int maxValue, String comment) {
		String langKey = String.format("taam.config.%s.%s", category, name);
		Property prop = config.get(category, name, defaultValue, comment, minValue, maxValue);
		prop.setLanguageKey(langKey);
		prop.setRequiresWorldRestart(true);
		return prop.getInt();
	}

	private static float getFloat(String name, String category, float defaultValue, float minValue, float maxValue, String comment) {
		String langKey = String.format("taam.config.%s.%s", category, name);
		return config.getFloat(name, category, defaultValue, minValue, maxValue, comment, langKey);
	}

	private static byte getByte(String name, String category, int defaultValue, int minValue, int maxValue, String comment) {
		String langKey = String.format("taam.config.%s.%s", category, name);
		return (byte)config.getInt(name, category, defaultValue, minValue, maxValue, comment, langKey);
	}
	
	private static boolean getBoolean(String name, String category, boolean defaultValue, String comment) {
		String langKey = String.format("taam.config.%s.%s", category, name);
		return config.getBoolean(name, category, defaultValue, comment, langKey);
	}
	
	private static void loadConfig()
	{
		Taam.BLOCK_ORE_META[] oreMeta = Taam.BLOCK_ORE_META.values();
		for(int i = 0; i < NUM_ORES; i++) {
			String name = oreMeta[i].config_name;
			String sectionName = SECTION_WORLDGEN + "." + name;
			genOre[i] = getBoolean("generate", sectionName, true, "Should Taam generate " + name + " ore in the world?");
			oreSize[0] = getInt("oreSize", sectionName, oreMeta[i].gen_default_size, 0, Integer.MAX_VALUE, "Size of " + name + " ore veins");
			oreAbove[0] = getInt("oreAbove", sectionName, oreMeta[i].gen_default_above, 0, 255, name + " ore veins spawn above this y-level");
			oreBelow[0] = getInt("oreBelow", sectionName, oreMeta[i].gen_default_below, 0, 255, name + " ore veins spawn below this y-level");
			oreDepositCount[0] = getInt("oreDepositCount", sectionName, oreMeta[i].gen_default_count, 0, Integer.MAX_VALUE, "Number of " + name + " ore veins per chunk");
		}

		config.getCategory(SECTION_INTEGRATION_MULTIPART).setRequiresMcRestart(true);
		
		debug_output = getBoolean("debug_output", Configuration.CATEGORY_GENERAL, false, "Should the Debug mode of Taam be activated");
		use_iinventory_compat = getBoolean("use_iinventory_compat", Configuration.CATEGORY_GENERAL, true, "Enable or disable compatibility for IInventory. If enabled, IInventory will be wrapped in IItemHandler etc.");
		render_tank_content = getBoolean("render_tank_content", Configuration.CATEGORY_GENERAL, true, "Enable or disable rendering of tank content. Troubleshooting only; should remain enabled, else tanks et. al. will always look empty.");
		render_items = getBoolean("render_items", Configuration.CATEGORY_GENERAL, true, "Enable or disable rendering of items on machines. Troubleshooting only; should remain enabled, else conveyors et. al. will always look empty.");
		
		dark_theme = getBoolean("dark_theme", Configuration.CATEGORY_GENERAL, true, "Enable dark theme for some of the GUIs.");
		
		sensor_delay = getInt("sdelay", SECTION_MULTITRONIX_SENSOR, 30, 10, 100, "Sensor [Motion, Minect] delay (minimum activation time) in game ticks, minimum 10");
		sensor_placement_mode = getInt("placement_mode", SECTION_MULTITRONIX_SENSOR, 1, 1, 2, "Sensor [Motion, Minect] placement mode when side by side. 1 = move together, 2 = merge into one");

		pl_trashcan_maxfill = getFloat("maxfill", SECTION_PRODUCTIONLINE_TRASHCAN, 64f, 1f, Float.MAX_VALUE, "Maximum fill level of trashcans. This counts stacks. (Fill Level 1 == 1 stack and it is full)");

		pl_conveyor_supportrange = getInt("supportrange", SECTION_PRODUCTIONLINE_CONVEYORS, 2, 0, Integer.MAX_VALUE, "!!Performance critical!! Keep this value low! Determines, how many blocks away a conveyor can be supported by other conveyors in the same direction.");
		pl_conveyor_speedsteps[0] = getByte("speedsteps_1", SECTION_PRODUCTIONLINE_CONVEYORS, 80, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 1 conveyors (wooden)");
		pl_conveyor_speedsteps[1] = getByte("speedsteps_2", SECTION_PRODUCTIONLINE_CONVEYORS, 40, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 2 conveyors (aluminum)");
		pl_conveyor_speedsteps[2] = getByte("speedsteps_3", SECTION_PRODUCTIONLINE_CONVEYORS, 5, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 3 conveyors (high throughput)");

		pl_hopper_delay = getInt("delay", SECTION_PRODUCTIONLINE_HOPPER, 8, 1, 500, "Drop Delay (ticks) for the conveyor hopper.");
		pl_hopper_highspeed_delay = getInt("highspeed_delay", SECTION_PRODUCTIONLINE_HOPPER, 0, 1, 500, "Drop Delay (ticks) for the high-speed conveyor hopper.");
		pl_hopper_stackmode_normal_speed = getBoolean("stackmode_normal_speed", SECTION_PRODUCTIONLINE_HOPPER, true, "Disabling this makes the High-Speed Hoppers eject in 'slow' speed when in stack-mode. (Same delay as the regular conveyor hopper)");

		pl_processor_hurt = getBoolean("hurt", SECTION_PRODUCTIONLINE_PROCESSORS, true, "Decides, whether standing on conveyor processors (Grinder, Crusher, Shredder) should hurt players/NPCs.");
		pl_processor_hurt_chance = getFloat("hurt_chance", SECTION_PRODUCTIONLINE_PROCESSORS, 0.2f, 0.00001f, 1f, "The likelyhood of being hurt by a processor. Calculated every tick.");
		pl_processor_shredder_timeout = getInt("shredder_timeout", SECTION_PRODUCTIONLINE_PROCESSORS, 1, 1, 200, "Ticks between each shredded item in the conveyor shredder.");
		pl_processor_grinder_timeout = getInt("grinder_timeout", SECTION_PRODUCTIONLINE_PROCESSORS, 15, 1, 200, "Ticks between each processed item in the conveyor grinder.");
		pl_processor_crusher_timeout = getInt("crusher_timeout", SECTION_PRODUCTIONLINE_PROCESSORS, 15, 1, 200, "Ticks between each processed item in the conveyor crusher.");
		
		pl_sieve_speedsteps = getByte("speedsteps", SECTION_PRODUCTIONLINE_SIEVE, 20, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for conveyor sieves.");

		pl_fluid_drier_timeout = getInt("timeout", SECTION_PRODUCTIONLINE_FLUIDDRIER, 50, 1, 200, "Ticks between each processed item in the fluid drier.");
		pl_fluid_drier_capacity = getIntWR("capacity", SECTION_PRODUCTIONLINE_FLUIDDRIER, 1000, 0, Integer.MAX_VALUE, "Capacity of the pipe end of the fluid drier. Keep in mind that lowering this too much can make some recipes impossible! Unit: mB");
		
		pl_mixer_timeout = getInt("timeout", SECTION_PRODUCTIONLINE_MIXER, 15, 1, 200, "Ticks between each processed item in the mixer.");
		pl_mixer_capacity_input = getIntWR("capacity_input", SECTION_PRODUCTIONLINE_MIXER, 2000, 0, Integer.MAX_VALUE, "Capacity of the input pipe end of the mixer. Keep in mind that lowering this too much can make some recipes impossible! Unit: mB");
		pl_mixer_capacity_output = getIntWR("capacity_output", SECTION_PRODUCTIONLINE_MIXER, 2000, 0, Integer.MAX_VALUE, "Capacity of the output pipe end of the mixer. Does not accect recipes, only output speed & loss when breaking the block. Unit: mB");

		pl_pipe_capacity = getIntWR("capacity", SECTION_PRODUCTIONLINE_PIPE, 500, 1, Integer.MAX_VALUE, "Capacity of the pipes. Higher capacity means higher loss when breaking a pipe, but also faster transfer of fluids. Unit: mB");
		pl_pipe_wrap_ifluidhandler = getBoolean("wrap_ifluidhandler", SECTION_PRODUCTIONLINE_PIPE, true, "Enable or disable pipes connecting to 'regular' IFluidHandler-based machines. Setting this to false makes pipes only connect to other pipes & pipe ends in machines.");
		
		pl_pump_capacity = getIntWR("capacity", SECTION_PRODUCTIONLINE_PUMP, 125, 1, Integer.MAX_VALUE, "Capacity of the pumps. Higher capacity means higher loss when breaking a pump, but also faster transfer of fluids. Unit: mB");
		pl_pump_pressure = getIntWR("pressure", SECTION_PRODUCTIONLINE_PUMP, 50, 1, Integer.MAX_VALUE, "Pressure of the pumps. Higher pressure means higher output range.");
		pl_pump_suction = getIntWR("suction", SECTION_PRODUCTIONLINE_PUMP, 50, 1, Integer.MAX_VALUE, "Suction of the pumps. Higher suction means higher input range.");

		pl_tank_capacity = getIntWR("capacity", SECTION_PRODUCTIONLINE_TANK, 8000, 1, Integer.MAX_VALUE, "Capacity of the tanks. Higher capacity means higher loss when breaking a tank, but also more storage. Transfer rate is limited by connected pipe, not by the tank. Unit: mB");
		pl_tank_suction = getIntWR("suction", SECTION_PRODUCTIONLINE_TANK, 10, 1, Integer.MAX_VALUE, "Suction of the tanks. Higher suction means higher input range. Suction on the lower end of the tank is always 1 lower than on the top, so stacked tanks always transfer down.");
		
		pl_creativewell_pressure = getIntWR("pressure", SECTION_PRODUCTIONLINE_CREATIVEWELL, 20, 1, Integer.MAX_VALUE, "Pressure of the creative wells. Higher pressure means higher output range.");
		
		pl_sprayer_capacity = getIntWR("capacity", SECTION_PRODUCTIONLINE_SPRAYER, 2000, 1, Integer.MAX_VALUE, "Capacity of the pipe end of the sprayer appliance. Keep in mind that lowering this too much can make some recipes impossible! Unit: mB");
		
		multipart_load = getBoolean("load_multiparts", SECTION_INTEGRATION_MULTIPART, true, "Load machines as multiparts if McMultipart is found.");
		multipart_register_items = getBoolean("register_multipart_items", SECTION_INTEGRATION_MULTIPART, true, "Allows you to disable registering of the multipart-variants of the items. Setting this to false means that multiparts will load without an issue, but all new machines will be created as full blocks and not as multiparts.");

		jei_render_machines_into_gui = getBoolean("render_machines_into_gui", SECTION_INTEGRATION_JEI, true, "Enable or disable rendering the machine into the recipe display in JEI. For troubleshooting only; you should leave this enabled normally.");
		
		if(config.hasChanged())
		{
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equalsIgnoreCase(Taam.MOD_ID))
		{
			loadConfig();

			if(multipart_load && !multipart_present) {
				Log.warn("Config has multipart enabled, but it was not found. Multipart support will not be loaded.");
				multipart_load = false;
			}
		}
	}

}
