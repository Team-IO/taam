package net.teamio.taam.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.teamio.taam.gui.util.CustomGui;

public class GuiConveyorSmallInventory<T extends ICapabilityProvider & IWorldNameable> extends CustomGui {
	ResourceLocation bg = new ResourceLocation("textures/gui/container/hopper.png");

	private T tileEntity;
	private InventoryPlayer inventoryPlayer;

	public GuiConveyorSmallInventory(InventoryPlayer inventoryPlayer, T tileEntity, EnumFacing side) {
		super(new ContainerConveyorSmallInventory(inventoryPlayer, tileEntity, side));
		this.tileEntity = tileEntity;
		this.inventoryPlayer = inventoryPlayer;
		ySize = 133;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(getTranslatedName(tileEntity), 8, 6, 0x404040);
		fontRendererObj.drawString(getTranslatedName(inventoryPlayer), 8, ySize - 96 + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(bg);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
