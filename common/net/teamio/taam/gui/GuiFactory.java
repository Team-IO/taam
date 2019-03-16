package net.teamio.taam.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

/**
 * Gui factory for creating the mod config GUI {@link ModGuiConfig}.
 * This is registered with the mod instance via {@link net.teamio.taam.Taam#GUI_FACTORY_CLASS}
 * in {@link net.teamio.taam.TaamMain} - {@code @Mod} annotation.
 *
 * @author Oliver Kahrmann
 */
public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {

		return ModGuiConfig.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

}
