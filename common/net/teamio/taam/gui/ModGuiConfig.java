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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<IConfigElement> getElements() {
		List<IConfigElement> elements = new ArrayList<IConfigElement>();
		ConfigElement cfgEl = new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL));
		ConfigElement cfgElMultitronix = new ConfigElement(Config.config.getCategory("multitronix"));
		ConfigElement cfgElProductionline = new ConfigElement(Config.config.getCategory("production_line"));
		ConfigElement cfgElProductionline_appliances = new ConfigElement(Config.config.getCategory("production_line_appliances"));
		ConfigElement cfgElWorldgen = new ConfigElement(Config.config.getCategory("worldgen"));
		elements.addAll(cfgEl.getChildElements());
		elements.addAll(cfgElMultitronix.getChildElements());
		elements.addAll(cfgElProductionline.getChildElements());
		elements.addAll(cfgElProductionline_appliances.getChildElements());
		elements.addAll(cfgElWorldgen.getChildElements());
		return elements;
	}

}