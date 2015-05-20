package founderio.taam.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import founderio.taam.blocks.TileEntityConveyorHopper;
import founderio.taam.client.gui.util.CustomGui;

public class GuiConveyorHopper extends CustomGui {
	ResourceLocation bg = new ResourceLocation("textures/gui/container/hopper.png");
	
	private TileEntityConveyorHopper tileEntity;
	private InventoryPlayer inventoryPlayer;
	
	private GuiButtonExt cbIgnoreRedstone;
	private GuiCheckBox cbEject;
	private GuiCheckBox cbStackMode;
	private GuiCheckBox cbLinearMode;
	
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

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		//TODO: Adjust background image & draw inside ;)
		this.buttonList.add(cbIgnoreRedstone = new GuiButtonExt(10, this.guiLeft + this.xSize, this.guiTop, "Redstone Mode"));
		this.buttonList.add(cbEject = new GuiCheckBox(20, this.guiLeft + this.xSize, this.guiTop + 20, "Eject into World", tileEntity.isEject()));
		this.buttonList.add(cbLinearMode = new GuiCheckBox(30, this.guiLeft + this.xSize, this.guiTop + 35, "Linear Mode", tileEntity.isLinearMode()));
		this.buttonList.add(cbStackMode = new GuiCheckBox(40, this.guiLeft + this.xSize, this.guiTop + 50, "Stack Mode", tileEntity.isStackMode()));
		cbStackMode.visible = tileEntity.isHighSpeed();
		setRedstoneModeText();
	}
	
	private void setRedstoneModeText() {
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
		}
		
		
		cbIgnoreRedstone.displayString = "Redstone Mode: " + appendage;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id == 10) {
			int rs = tileEntity.getRedstoneMode();
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
		this.mc.renderEngine.bindTexture(bg);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
