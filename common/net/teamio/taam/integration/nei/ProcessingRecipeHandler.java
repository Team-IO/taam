package net.teamio.taam.integration.nei;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamClientProxy;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.conveyors.api.ChancedOutput;
import net.teamio.taam.conveyors.api.IProcessingRecipe;
import net.teamio.taam.conveyors.api.ProcessingRegistry;
import net.teamio.taam.rendering.TaamRenderer;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.recipe.TemplateRecipeHandler;

public abstract class ProcessingRecipeHandler extends TemplateRecipeHandler {

	
	public static class Crusher extends ProcessingRecipeHandler {
		public Crusher() {
			super(ProcessingRegistry.CRUSHER);
		}
	}
	
	public static class Grinder extends ProcessingRecipeHandler {
		public Grinder() {
			super(ProcessingRegistry.GRINDER);
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
        
		TaamRenderer renderer = ((TaamClientProxy)TaamMain.proxy).taamRenderer;
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
			arecipes.add(new CachedChancedRecipe(this, matching));
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		Collection<IProcessingRecipe> matching = ProcessingRegistry.getRecipes(machine, result);
		
		for(final IProcessingRecipe recipe : matching) {
			arecipes.add(new CachedChancedRecipe(this, recipe));
		}
	}
}
