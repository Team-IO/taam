package net.teamio.taam.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.util.TaamUtil;
import org.lwjgl.opengl.GL11;

public class GuiConveyorHopper extends GuiContainer {
	final ResourceLocation bg = new ResourceLocation("textures/gui/container/hopper.png");

	private final TileEntityConveyorHopper tileEntity;
	private final InventoryPlayer inventoryPlayer;

	private GuiButtonExt cbIgnoreRedstone;
	private GuiCheckBox cbEject;
	private GuiCheckBox cbStackMode;
	private GuiCheckBox cbLinearMode;

	public GuiConveyorHopper(InventoryPlayer inventoryPlayer, TileEntityConveyorHopper tileEntity) {
		super(new ContainerConveyorSmallInventory(inventoryPlayer, tileEntity, null));
		this.tileEntity = tileEntity;
		this.inventoryPlayer = inventoryPlayer;
		ySize = 133;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		fontRenderer.drawString(TaamUtil.getTranslatedName(tileEntity), 8, 6, 0x404040);
		fontRenderer.drawString(TaamUtil.getTranslatedName(inventoryPlayer), 8, ySize - 96 + 2, 0x404040);
	}

	@Override
	public void initGui() {
		super.initGui();
		//TODO: Adjust background image & draw inside ;)
		buttonList.add(cbIgnoreRedstone = new GuiButtonExt(10, guiLeft + xSize, guiTop, "Redstone Mode"));
		buttonList.add(cbEject = new GuiCheckBox(20, guiLeft + xSize, guiTop + 20, "Eject into World", tileEntity.isEject()));
		buttonList.add(cbLinearMode = new GuiCheckBox(30, guiLeft + xSize, guiTop + 35, "Linear Mode", tileEntity.isLinearMode()));
		buttonList.add(cbStackMode = new GuiCheckBox(40, guiLeft + xSize, guiTop + 50, "Stack Mode", tileEntity.isStackMode()));
		cbStackMode.visible = tileEntity.isHighSpeed();
		setRedstoneModeText();
	}

	private void setRedstoneModeText() {
		//TODO: move to I18N
		String appendage;
		switch(tileEntity.getRedstoneMode()) {
		case 0:
			appendage = "Ignore";
			break;
		case 1:
			appendage = "High";
			break;
		case 2:
			appendage = "Low";
			break;
		case 3:
			appendage = "Pulse (High Edge)";
			break;
		case 4:
			appendage = "Pulse (Low Edge)";
			break;
		default:
			appendage = "Wonky";
			break;
		}


		cbIgnoreRedstone.displayString = "Redstone Mode: " + appendage;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id == 10) {
			byte rs = tileEntity.getRedstoneMode();
			rs++;
			if(rs > 4 || rs < 0) {
				rs = 0;
			}
			tileEntity.setRedstoneMode(rs);
			setRedstoneModeText();
		} else if(button.id == 20) {
			tileEntity.setEject(cbEject.isChecked());
		} else if(button.id == 30) {
			tileEntity.setLinearMode(cbLinearMode.isChecked());
		} else if(button.id == 40) {
			tileEntity.setStackMode(cbStackMode.isChecked());
		}
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
