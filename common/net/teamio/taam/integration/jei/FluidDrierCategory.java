package net.teamio.taam.integration.jei;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.piping.MachineFluidDrier;
import net.teamio.taam.recipes.FluidDrierRecipe;
import net.teamio.taam.recipes.IProcessingRecipe;

public class FluidDrierCategory extends BlankRecipeCategory {
	
	private final String localizedName = Translator.translateToLocal(Taam.INTEGRATION_JEI_CATNAME_FLUIDDRIER);

	@Nonnull
	protected final IDrawable background;
	
	public static final int slotInput = 0;
	public static final int slotOutput = 1;
	
	protected final ItemStack renderStackFluidDrier;
	
	public FluidDrierCategory(IGuiHelper guiHelper) {
		ResourceLocation bgLocation = new ResourceLocation("taam", "textures/gui/processors.png");
		background = guiHelper.createDrawable(bgLocation, 163, 0, 93, 62);
		renderStackFluidDrier = new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.fluid_drier.metaData());
	}
	
	@Override
	public String getUid() {
		return Taam.INTEGRATION_JEI_CAT_FLUIDDRIER;
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
	public void drawExtras(Minecraft minecraft) {
        {
            TextureManager texturemanager = minecraft.renderEngine;
            texturemanager.bindTexture(TextureMap.locationBlocksTexture);
            
			GL11.glPushMatrix();
			
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableRescaleNormal();
			
			GL11.glTranslated(46.5, 33.2, 1);
			GL11.glScaled(20, -20, 2);
			GL11.glRotated(10, 1, 0, 0);
			GL11.glRotated(45, 0, 1, 0);
			
			RenderItem ri = Minecraft.getMinecraft().getRenderItem();
			IBakedModel model = ri.getItemModelMesher().getItemModel(renderStackFluidDrier);
			ri.renderItem(renderStackFluidDrier, model);
			
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();
			
			GL11.glPopMatrix();
        }
        {
        	FontRenderer fontRendererObj = minecraft.fontRendererObj;
        	
        	GL11.glPushMatrix();
        	
        	GL11.glScaled(.5, .5, 1);
    		
        	String display = Translator.translateToLocalFormatted(Taam.INTEGRATION_JEI_LORE_INTERNAL_CAPACITY, MachineFluidDrier.capacity);
    		fontRendererObj.drawString(display, 24*2, 3*2, 0x00555555, false);
    		
    		GL11.glPopMatrix();
        }
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		if(!(recipeWrapper instanceof ProcessingRecipeWrapper)) {
			Log.error("RecipeWrapper type unknown: {}", recipeWrapper);
			return;
		}
		ProcessingRecipeWrapper processingWrapper = (ProcessingRecipeWrapper)recipeWrapper;
		IProcessingRecipe proRec = processingWrapper.recipe;
		if(!(proRec instanceof FluidDrierRecipe)) {
			Log.error("Recipe type unknown: {}", proRec);
			return;
		}
		FluidDrierRecipe recipe = (FluidDrierRecipe)proRec;
		FluidStack input = recipe.getInputFluid();
		ItemStack output = recipe.getOutputStack();
		
		IGuiFluidStackGroup guiFluidStack = recipeLayout.getFluidStacks();
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		
		guiFluidStack.init(slotInput, true, 5, 3, 16, 16, input.amount/*MachineFluidDrier.capacity*/, false, null);
		guiFluidStack.set(slotInput, input);

		guiItemStacks.init(slotOutput, false, 71, 42);
		guiItemStacks.set(slotOutput, output);
	}

}
