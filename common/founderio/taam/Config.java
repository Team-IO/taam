package founderio.taam;

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
		
		pl_appl_sprayer_maxProgress = config.getInt("sprayer_maxProgress", "production_line_appliances", 20, 1, 500, "Maximum processing steps for the sprayer appliance.");
		pl_appl_sprayer_resourceUsage = config.getInt("sprayer_resourceUsage", "production_line_appliances", 1, 1, 500, "Resource Usage per spray step in the sprayer.");

		if(config.hasChanged())
		{
			config.save();
		}
	}
	
	
	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent Event)
	{
		if (Event.modID.equalsIgnoreCase(Taam.MOD_ID));
		{
			loadConfig();
		}
	}
	
}
