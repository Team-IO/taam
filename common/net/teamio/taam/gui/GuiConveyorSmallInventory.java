package net.teamio.taam.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.teamio.taam.gui.util.CustomGui;

public class GuiConveyorSmallInventory extends CustomGui {
	ResourceLocation bg = new ResourceLocation("textures/gui/container/hopper.png");

	private IInventory tileEntity;
	private InventoryPlayer inventoryPlayer;

	public GuiConveyorSmallInventory(InventoryPlayer inventoryPlayer, IInventory tileEntity) {
		super(new ContainerConveyorSmallInventory(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		this.inventoryPlayer = inventoryPlayer;
		ySize = 133;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		fontRendererObj.drawString(getTranslatedInventoryName(tileEntity), 8, 6, 0x404040);
		fontRendererObj.drawString(getTranslatedInventoryName(inventoryPlayer), 8, ySize - 96 + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(bg);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
