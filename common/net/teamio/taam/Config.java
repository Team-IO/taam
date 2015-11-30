package net.teamio.taam;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static Configuration config;
	
	public static boolean genCopper = true;
	public static boolean genTin = true;
	public static boolean debug = false;
	
	public static int sensor_delay = 30;
	public static int sensor_placement_mode = 1;
	
	public static final byte[] pl_conveyor_speedsteps = new byte[] {
		80,
		40,
		5
	};
	
	public static int pl_hopper_highspeed_delay = 1;
	public static int pl_hopper_delay = 8;
	public static boolean pl_hopper_stackmode_normal_speed = false;
	
	public static int pl_appl_sprayer_maxProgress = 1;
	//TODO: Change to mB
	public static int pl_appl_sprayer_resourceUsage = 1;
	
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
		
		genCopper = config.getBoolean("generateCopper", "worldgen", true, Taam.CFG_COMMENT_GEN_COPPER_ORE);
		genTin = config.getBoolean("generateTin", "worldgen", true , Taam.CFG_COMMENT_GEN_TIN_ORE);
		
		debug = config.getBoolean("debug_output", Configuration.CATEGORY_GENERAL, false, Taam.CFG_COMMENT_DEBUG_OUTPUT);
		
		sensor_delay = config.getInt("sensor_delay", "multitronix", 30, 10, 100, Taam.CFG_COMMENT_SENSOR_DELAY);
		sensor_placement_mode = config.getInt("sensor_placement_mode", "multitronix", 1, 1, 2, Taam.CFG_COMMENT_SENSOR_PLACEMENT_MODE);
		
		pl_conveyor_speedsteps[0] = (byte)config.getInt("pl_conveyor_speedsteps_1", "production_line", 80, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 1 conveyors (wooden)");
		pl_conveyor_speedsteps[1] = (byte)config.getInt("pl_conveyor_speedsteps_2", "production_line", 40, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 2 conveyors (aluminum)");
		pl_conveyor_speedsteps[2] = (byte)config.getInt("pl_conveyor_speedsteps_3", "production_line", 5, 1, Byte.MAX_VALUE, "Speedsteps (1/speed) for tier 3 conveyors (high throughput)");
		
		pl_hopper_delay = config.getInt("hopper_delay", "production_line", 8, 1, 500, "Drop Delay (ticks) for the conveyor hopper.");
		pl_hopper_highspeed_delay = config.getInt("hopper_highspeed_delay", "production_line", 0, 1, 500, "Drop Delay (ticks) for the high-speed conveyor hopper.");
		pl_hopper_stackmode_normal_speed = config.getBoolean("hopper_stackmode_normal_speed", "production_line", true, "Disabling this makes the High-Speed Hoppers eject in 'slow' speed when in stack-mode. (Same delay as the regular conveyor hopper)");
		
		pl_appl_sprayer_maxProgress = config.getInt("sprayer_maxProgress", "production_line_appliances", 20, 1, 500, "Maximum processing steps for the sprayer appliance.");
		pl_appl_sprayer_resourceUsage = config.getInt("sprayer_resourceUsage", "production_line_appliances", 1, 1, 500, "Resource usage per spray step in the sprayer.");

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
