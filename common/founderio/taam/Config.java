package founderio.taam;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static Configuration config;
	public static boolean genCopper = true;
	
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
	}
	
	
	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent Event)
	{
		if (Event.modID.equalsIgnoreCase(Taam.MOD_ID));
	}
	
}
