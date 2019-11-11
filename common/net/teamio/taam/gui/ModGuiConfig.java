package net.teamio.taam.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;

import java.util.ArrayList;
import java.util.List;

/**
 * Config GUI implementation for Taam.
 * See {@link GuiFactory}.
 *
 * @author Oliver Kahrmann
 */
public class ModGuiConfig extends GuiConfig {

	public ModGuiConfig(GuiScreen guiScreen) {
		super(guiScreen, getElements(), Taam.MOD_ID, false, false,
				GuiConfig.getAbridgedConfigPath(Config.configContainer.toString()));

	}

	private static List<IConfigElement> getElements() {
		List<IConfigElement> elements = new ArrayList<>();
		// Create config elements for all sections mentioned in Config
		for (String category : Config.guiSections) {
			ConfigElement cfgEl = new ConfigElement(Config.configContainer.getCategory(category));
			elements.add(cfgEl);
		}
		return elements;
	}

}
