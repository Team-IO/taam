package net.teamio.taam.integration.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Translator;
import net.minecraft.util.ResourceLocation;
import net.teamio.taam.Taam;

public class ProcessingCategory extends BlankRecipeCategory {

	private String localizedName = Translator.translateToLocal("taam.integration.jei.categories.processing");

	@Nonnull
	private final IDrawable background_grinder;
	@Nonnull
	private final IDrawable background_crusher;

	public ProcessingCategory(IGuiHelper guiHelper) {
		ResourceLocation bgLocation = new ResourceLocation("taam", "gui/processors");
		background_crusher = guiHelper.createDrawable(bgLocation, 0, 0, 166, 66);
		background_grinder = guiHelper.createDrawable(bgLocation, 0, 67, 166, 66);
		// background_shredder = guiHelper.createDrawable(bgLocation, 0, 134, 166, 66);
	}

	@Override
	public String getUid() {
		return Taam.INTEGRATION_JEI_CAT_PROCESSING;
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public IDrawable getBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		// TODO Auto-generated method stub

	}

}
