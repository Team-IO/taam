package net.teamio.taam.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Translator;
import net.minecraft.util.ResourceLocation;
import net.teamio.taam.Taam;

import javax.annotation.Nonnull;

public class GrinderCategory extends ProcessingCategory {


	private final String localizedName = Translator.translateToLocal(Taam.INTEGRATION_JEI_CATNAME_GRINDER);

	@Nonnull
	protected final IDrawable background;

	public GrinderCategory(IGuiHelper guiHelper) {
		ResourceLocation bgLocation = new ResourceLocation("taam", "textures/gui/processors.png");
		background = guiHelper.createDrawable(bgLocation, 0, 63, 162, 62);
	}

	@Nonnull
	@Override
	public String getTitle() {
		return localizedName;
	}

	@Nonnull
	@Override
	public String getUid() {
		return Taam.INTEGRATION_JEI_CAT_GRINDER;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

}
