package net.teamio.taam.integration.jei;

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
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.recipes.IProcessingRecipe;
import net.teamio.taam.recipes.impl.SprayerRecipe;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class SprayerCategory extends BlankRecipeCategory {

	private final String localizedName = Translator.translateToLocal(Taam.INTEGRATION_JEI_CATNAME_SPRAYER);

	@Nonnull
	protected final IDrawable background;

	public static final int slotInputFluid = 0;
	public static final int slotInput = 1;
	public static final int slotOutput = 2;

	protected final ItemStack renderStackSprayer;

	public SprayerCategory(IGuiHelper guiHelper) {
		ResourceLocation bgLocation = new ResourceLocation("taam", "textures/gui/processors.png");
		background = guiHelper.createDrawable(bgLocation, 0, 189, 96, 62);
		renderStackSprayer = new ItemStack(TaamMain.blockProductionLineAppliance, 1, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.sprayer.metaData());
	}

	@Nonnull
	@Override
	public String getUid() {
		return Taam.INTEGRATION_JEI_CAT_SPRAYER;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		if(Config.jei_render_machines_into_gui) {
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
			IBakedModel model = ri.getItemModelMesher().getItemModel(renderStackSprayer);
			ri.renderItem(renderStackSprayer, model);

			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();

			GL11.glPopMatrix();
		}
		{
			FontRenderer fontRendererObj = minecraft.fontRendererObj;

			GL11.glPushMatrix();

			GL11.glScaled(.5, .5, 1);

			String display = Translator.translateToLocalFormatted(Taam.INTEGRATION_JEI_LORE_INTERNAL_CAPACITY, Config.pl_sprayer_capacity);
			fontRendererObj.drawString(display, 24*2, 3*2, 0x00555555, false);

			GL11.glPopMatrix();
		}
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
		if(!(recipeWrapper instanceof ProcessingRecipeWrapper)) {
			Log.error("RecipeWrapper type unknown: {}", recipeWrapper);
			return;
		}
		ProcessingRecipeWrapper processingWrapper = (ProcessingRecipeWrapper)recipeWrapper;
		IProcessingRecipe proRec = processingWrapper.recipe;
		if(!(proRec instanceof SprayerRecipe)) {
			Log.error("Recipe type unknown: {}", proRec);
			return;
		}
		SprayerRecipe recipe = (SprayerRecipe)proRec;
		FluidStack inputFluid = recipe.getInputFluid();
		ItemStack input = recipe.getInput();
		ItemStack output = recipe.getOutputStack();

		IGuiFluidStackGroup guiFluidStack = recipeLayout.getFluidStacks();
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiFluidStack.init(slotInputFluid, true, 5, 3, 16, 16, inputFluid.amount, false, null);
		guiFluidStack.set(slotInputFluid, inputFluid);

		guiItemStacks.init(slotInput, true, 4, 24);
		guiItemStacks.set(slotInput, input);

		guiItemStacks.init(slotOutput, false, 75, 24);
		guiItemStacks.set(slotOutput, output);
	}

}
