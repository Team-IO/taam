package founderio.taam;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static Configuration config;
	
	public static boolean genCopper = true;
	public static boolean genTin = true;
	
	public static int sensor_delay = 30;
	public static int sensor_placement_mode = 1;
	
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
		genCopper = config.getBoolean("generateCopper", Configuration.CATEGORY_GENERAL, true, Taam.CFG_COMMENT_GEN_COPPER_ORE);
		genTin = config.getBoolean("generateTin", Configuration.CATEGORY_GENERAL, true , Taam.CFG_COMMENT_GEN_TIN_ORE);
		sensor_delay = config.getInt("sensor_delay", Configuration.CATEGORY_GENERAL, 30, 10, 100, Taam.CFG_COMMENT_SENSOR_DELAY);
		sensor_placement_mode = config.getInt("sensor_placement_mode", Configuration.CATEGORY_GENERAL, 1, 1, 2, Taam.CFG_COMMENT_SENSOR_PLACEMENT_MODE);
		
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
