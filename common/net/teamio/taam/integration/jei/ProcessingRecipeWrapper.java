package net.teamio.taam.integration.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.recipes.ChanceBasedRecipe;
import net.teamio.taam.recipes.ChancedOutput;
import net.teamio.taam.recipes.IProcessingRecipe;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ProcessingRecipeWrapper implements IRecipeWrapper {

	public final IProcessingRecipe recipe;

	public ProcessingRecipeWrapper(IProcessingRecipe recipe) {
		this.recipe = recipe;
	}

	public List<ItemStack> getInputs() {
		ItemStack input = recipe.getInput();
		if (input == null) {
			String oreDictName = recipe.getInputOreDict();
			if (oreDictName == null) {
				return Lists.newArrayList();
			}
			List<ItemStack> oreDictInput = OreDictionary.getOres(oreDictName);
			return Lists.newArrayList(oreDictInput);
		}
		return Lists.newArrayList(input);
	}

	public List<ItemStack> getOutputs() {
		List<ItemStack> outputs = Lists.newArrayList();
		ChancedOutput[] output = recipe.getOutput();
		if(output == null || output.length == 0) {
			return Lists.newArrayList();
		}
		for (ChancedOutput co : output) {
			outputs.add(co.output);
		}
		return outputs;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, getInputs());
		ingredients.setOutputs(VanillaTypes.ITEM, getOutputs());
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if(!(recipe instanceof ChanceBasedRecipe)) {
			return;
		}
		ChancedOutput[] output = recipe.getOutput();
		if(output == null || output.length == 0) {
			return;
		}
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 1);
		FontRenderer fontRendererObj = minecraft.fontRenderer;

		for(int i = 0; i < output.length; i++) {
			int r = i % ProcessingCategory.MAX_ROWS;
			int c = i / ProcessingCategory.MAX_ROWS;
			float display = Math.round(output[i].chance * 10000) / 100f;
			fontRendererObj.drawString(display + "%", (83 + c*18)*2, (3 + r*18)*2, 0x00AAAA00);
		}
		GL11.glPopMatrix();
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

}
