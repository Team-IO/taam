package net.teamio.taam.integration.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamClientProxy;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.conveyors.api.ChancedOutput;
import net.teamio.taam.conveyors.api.IProcessingRecipe;
import net.teamio.taam.conveyors.api.ProcessingRegistry;
import net.teamio.taam.rendering.TaamRenderer;

public abstract class ProcessingRecipeHandler extends TemplateRecipeHandler {

	
	public static class Crusher extends ProcessingRecipeHandler {
		public Crusher() {
			super(ProcessingRegistry.CRUSHER);
		}

		@Override
		public void loadTransferRects() {
			transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(
					new Rectangle(37, 22, 28, 23), "TaamCrusher",
					new Object[0]));
		}
	}
	
	public static class Grinder extends ProcessingRecipeHandler {
		public Grinder() {
			super(ProcessingRegistry.GRINDER);
		}

		@Override
		public void loadTransferRects() {
			transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(
					new Rectangle(37, 22, 28, 23), "TaamGrinder",
					new Object[0]));
		}
	}
	
	
	protected final int machine;
	
	public ProcessingRecipeHandler(int machine) {
		this.machine = machine;
	}
	
	@Override
	public String getRecipeName() {
		switch(machine) {
		case ProcessingRegistry.CRUSHER:
			return "Crusher";
		case ProcessingRegistry.GRINDER:
			return "Grinder";
		default:
			return "Broken Implementation";
		}
	}

	@Override
	public String getGuiTexture() {
		return Taam.MOD_ID + ":textures/gui/processors.png";
	}

	@Override
	public void drawBackground(int recipe) {

		/*
		 * Draw Background
		 */
		
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, (int)(166), (int)(65));
        
        GL11.glPopMatrix();
        
        /*
         * Draw Processor Machine
         */
        
		TaamRenderer renderer = TaamClientProxy.taamRenderer;
		GL11.glPushMatrix();

		GL11.glTranslated(69.25, 40, 30);
		GL11.glScaled(25, 25, -50);

		// Rorate from upside-down
		GL11.glRotated(180, 0, 0, 1);
		// Rotate towards screen
		GL11.glRotated(-20, 1, 0, 0);
		// Align a bit into view (diagonal)
		GL11.glRotated(45, 0, 1, 0);
		switch(machine) {
		case ProcessingRegistry.CRUSHER:
			renderer.renderConveyorProcessor(null, 0, 0, 0, TileEntityConveyorProcessor.Crusher);
			break;
		case ProcessingRegistry.GRINDER:
			renderer.renderConveyorProcessor(null, 0, 0, 0, TileEntityConveyorProcessor.Grinder);
			break;
		}
		GL11.glPopMatrix();
	}
	
	public void drawExtras(int recipe) {
		GL11.glPushMatrix();
		CachedChancedRecipe ccr = (CachedChancedRecipe)arecipes.get(recipe);
		ChancedOutput[] output = ccr.recipe.getOutput();
		GL11.glScaled(0.5, 0.5, 1);
		for(int i = 0; i < output.length; i++) {
			int r = i % CachedChancedRecipe.MAX_ROWS;
			int c = i / CachedChancedRecipe.MAX_ROWS;
			float display = Math.round(output[i].chance * 10000) / 100f;
			GuiDraw.drawString(display + "%", (85 + c*18)*2, (5 + r*18)*2, 0x00AAAA00);
		}
		GL11.glPopMatrix();
	}
	
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		IProcessingRecipe matching = ProcessingRegistry.getRecipe(machine, ingredient);
		if(matching != null) {
			arecipes.add(new CachedChancedRecipe(matching));
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		Log.debug("Loading crafting recipes for ItemStack" + result);
		Collection<IProcessingRecipe> matching = ProcessingRegistry.getRecipes(machine, result);
		
		for(final IProcessingRecipe recipe : matching) {
			arecipes.add(new CachedChancedRecipe(recipe));
		}
	}
	
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		Log.debug("Loading crafting recipes for output id" + outputId);
		if (outputId.equals("Taam" + getRecipeName())) {
			Collection<IProcessingRecipe> matching = ProcessingRegistry
					.getRecipes(machine);

			for (final IProcessingRecipe recipe : matching) {
				arecipes.add(new CachedChancedRecipe(recipe));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}
  
  public final class CachedChancedRecipe extends CachedRecipe {
		public final IProcessingRecipe recipe;

		private List<PositionedStack> input;
		
		public CachedChancedRecipe(IProcessingRecipe recipe) {
			this.recipe = recipe;
			input = new ArrayList<PositionedStack>();
			ItemStack inputStack = recipe.getInput();
			if(inputStack == null) {
				input.add(new PositionedStack(OreDictionary.getOres(recipe.getInputOreDict()), 5, 3));
			} else {
				input.add(new PositionedStack(inputStack, 5, 3));
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return this.getCycledIngredients(cycleticks / 20, input);
		}

		@Override
		public PositionedStack getResult() {
			return new PositionedStack(recipe.getOutput()[0].output, 85, 5);
		}

		public static final int MAX_ROWS = 3;
		
		@Override
		public List<PositionedStack> getOtherStacks() {
			ChancedOutput[] output = recipe.getOutput();
			List<PositionedStack> otherStacks = new ArrayList<PositionedStack>(output.length - 1);
			for(int i = 1; i < output.length; i++) {
				int r = i % MAX_ROWS;
				int c = i / MAX_ROWS;
				otherStacks.add(new PositionedStack(output[i].output, 85 + c*18, 5 + r*18));
			}
			return otherStacks;
		}
		
	}
}
