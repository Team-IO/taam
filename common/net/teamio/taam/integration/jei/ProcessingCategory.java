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
import net.teamio.taam.recipes.ProcessingRegistry;

public class ProcessingCategory extends BlankRecipeCategory {

	private String localizedName = Translator.translateToLocal("taam.integration.jei.categories.processing");

	@Nonnull
	private final IDrawable background;
	
	public final int machineID;
	private final String uid;

	public ProcessingCategory(int machineID, IGuiHelper guiHelper) {
		this.machineID = machineID;
		
		ResourceLocation bgLocation = new ResourceLocation("taam", "gui/processors");
		switch(machineID) {
		case ProcessingRegistry.CRUSHER:
			background = guiHelper.createDrawable(bgLocation, 0, 0, 166, 66);
			uid = Taam.INTEGRATION_JEI_CAT_CRUSHER;
			break;
		case ProcessingRegistry.GRINDER:
			background = guiHelper.createDrawable(bgLocation, 0, 67, 166, 66);
			uid = Taam.INTEGRATION_JEI_CAT_GRINDER;
			break;
			// Shredder:
			// background = guiHelper.createDrawable(bgLocation, 0, 134, 166, 66);
		default:
			throw new IllegalArgumentException("Unsupported machine ID: " + machineID);
		}
	}

	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		// TODO Auto-generated method stub

	}

}
