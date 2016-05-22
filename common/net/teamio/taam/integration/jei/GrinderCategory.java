package net.teamio.taam.integration.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.util.Translator;
import net.minecraft.util.ResourceLocation;
import net.teamio.taam.Taam;

public class GrinderCategory extends ProcessingCategory {

	private String localizedName = Translator.translateToLocal("taam.integration.jei.categories.grinder");

	@Nonnull
	protected final IDrawable background;
	protected final String uid;

	public GrinderCategory(IGuiHelper guiHelper) {
		super();
		ResourceLocation bgLocation = new ResourceLocation("taam", "textures/gui/processors.png");
		background = guiHelper.createDrawable(bgLocation, 0, 67, 162, 62);
		uid = Taam.INTEGRATION_JEI_CAT_GRINDER;
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

}
