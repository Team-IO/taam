package net.teamio.taam.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;


public class ModGuiConfig extends GuiConfig {

	
	public ModGuiConfig(GuiScreen guiScreen)
	{
		super(guiScreen,
		getElements(),
		Taam.MOD_ID,
		false,
		false,
		GuiConfig.getAbridgedConfigPath(Config.config.toString()));
		
	}
	
	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getElements() {
		List<IConfigElement> elements = new ArrayList<IConfigElement>();
		ConfigElement<Object> cfgEl = new ConfigElement<Object>(Config.config.getCategory(Configuration.CATEGORY_GENERAL));
		ConfigElement<Object> cfgElMultitronix = new ConfigElement<Object>(Config.config.getCategory("multitronix"));
		ConfigElement<Object> cfgElProductionline = new ConfigElement<Object>(Config.config.getCategory("production_line_appliances"));
		ConfigElement<Object> cfgElWorldgen = new ConfigElement<Object>(Config.config.getCategory("worldgen"));
		elements.addAll(cfgEl.getChildElements());
		elements.addAll(cfgElMultitronix.getChildElements());
		elements.addAll(cfgElProductionline.getChildElements());
		elements.addAll(cfgElWorldgen.getChildElements());
		return elements;
	}

}