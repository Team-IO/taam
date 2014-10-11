package founderio.taam.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import founderio.taam.blocks.TileEntityConveyorHopper;

public class GuiConveyorHopper extends GuiContainer {
	ResourceLocation bg = new ResourceLocation("textures/gui/container/hopper.png");
	
	private TileEntityConveyorHopper tileEntity;
	private InventoryPlayer inventoryPlayer;
	
	public GuiConveyorHopper(InventoryPlayer inventoryPlayer, TileEntityConveyorHopper tileEntity) {
		super(new ContainerConveyorHopper(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		this.inventoryPlayer = inventoryPlayer;
        this.ySize = 133;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		this.fontRendererObj.drawString(getTranslatedInventoryName(tileEntity), 8, 6, 0x404040);
		this.fontRendererObj.drawString(getTranslatedInventoryName(inventoryPlayer), 8, this.ySize - 96 + 2, 0x404040);
	}

	private String getTranslatedInventoryName(IInventory inventory) {
		if(inventory.hasCustomInventoryName()) {
			return inventory.getInventoryName();
		} else {
			return I18n.format(inventory.getInventoryName(), new Object[0]);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(bg);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
