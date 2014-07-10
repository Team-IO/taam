package founderio.taam.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.GuiConfig;
import founderio.taam.Config;
import founderio.taam.Taam;


public class ModGuiConfig extends GuiConfig {

	
	public ModGuiConfig(GuiScreen guiScreen)
	{
		super(guiScreen,
		new ConfigElement<Object>(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
		Taam.MOD_ID,
		false,
		false,
		GuiConfig.getAbridgedConfigPath(Config.config.toString()));
		
	}

}