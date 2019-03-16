package net.teamio.taam.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
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
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.recipes.IProcessingRecipe;
import net.teamio.taam.recipes.impl.MixerRecipe;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.List;

public class MixerCategory extends BlankRecipeCategory {

	private final String localizedName = Translator.translateToLocal(Taam.INTEGRATION_JEI_CATNAME_MIXER);

	@Nonnull
	protected final IDrawable background;

	public static final int slotInput = 0;
	public static final int slotInputItem = 1;
	public static final int slotOutput = 2;

	protected final ItemStack renderStackFluidDrier;

	public MixerCategory(IGuiHelper guiHelper) {
		ResourceLocation bgLocation = new ResourceLocation("taam", "textures/gui/processors.png");
		background = guiHelper.createDrawable(bgLocation, 0, 189, 96, 62);
		renderStackFluidDrier = new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.mixer.metaData());
	}

	@Nonnull
	@Override
	public String getUid() {
		return Taam.INTEGRATION_JEI_CAT_MIXER;
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
	public void drawExtras(@Nonnull Minecraft minecraft) {
		if (Config.jei_render_machines_into_gui) {
			TextureManager texturemanager = minecraft.renderEngine;
			texturemanager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			GL11.glPushMatrix();

			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableRescaleNormal();

			GL11.glTranslated(48.5, 33.2, 1);
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

			String display = Translator.translateToLocalFormatted(Taam.INTEGRATION_JEI_LORE_INTERNAL_CAPACITY_IN, Config.pl_mixer_capacity_input);
			fontRendererObj.drawString(display, 4 * 2, 47 * 2, 0x00555555, false);
			display = Translator.translateToLocalFormatted(Taam.INTEGRATION_JEI_LORE_INTERNAL_CAPACITY_OUT, Config.pl_mixer_capacity_output);
			fontRendererObj.drawString(display, 6 * 2, 47 * 2, 0x00555555, false);

			GL11.glPopMatrix();
		}
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
		if (!(recipeWrapper instanceof ProcessingRecipeWrapper)) {
			Log.error("RecipeWrapper type unknown: {}", recipeWrapper);
			return;
		}
		ProcessingRecipeWrapper processingWrapper = (ProcessingRecipeWrapper) recipeWrapper;
		IProcessingRecipe proRec = processingWrapper.recipe;
		if (!(proRec instanceof MixerRecipe)) {
			Log.error("Recipe type unknown: {}", proRec);
			return;
		}
		MixerRecipe recipe = (MixerRecipe) proRec;
		FluidStack input = recipe.getInputFluid();
		FluidStack output = recipe.getOutputFluid();

		IGuiFluidStackGroup guiFluidStack = recipeLayout.getFluidStacks();
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiFluidStack.init(slotInput, true, 5, 25, 16, 16, input.amount/*MachineFluidDrier.capacity*/, false, null);
		guiFluidStack.set(slotInput, input);

		guiFluidStack.init(slotOutput, false, 76, 25, 16, 16, output.amount/*MachineFluidDrier.capacity*/, false, null);
		guiFluidStack.set(slotOutput, output);

		guiItemStacks.init(slotInputItem, true, 4, 2);
		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		if (inputs == null || inputs.size() != 1) {
			throw new IllegalStateException("Recipe inputs invalid: " + inputs);
		}
		guiItemStacks.set(slotInputItem, inputs.get(0));
	}
}
