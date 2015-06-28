package founderio.taam.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Function;

import founderio.taam.content.logistics.EntityLogisticsCart;
import founderio.taam.content.logistics.TileEntityLogisticsManager;
import founderio.taam.gui.util.CustomButton;
import founderio.taam.gui.util.CustomGui;
import founderio.taam.logistics.WorldCoord;

public class GuiLogisticsCart extends CustomGui {
	public static ResourceLocation bg = new ResourceLocation("taam:textures/gui/logistics_station.png");
	
	private EntityLogisticsCart entity;
	private InventoryPlayer inventoryPlayer;
	
	private CustomButton changeName;
	
	public GuiLogisticsCart(InventoryPlayer inventoryPlayer, EntityLogisticsCart entity) {
		super(new ContainerLogisticsCart(inventoryPlayer, entity));
		this.entity = entity;
		this.inventoryPlayer = inventoryPlayer;
		this.xSize = 342;
        this.ySize = 267;
	}
	
	protected void actionPerformed(GuiButton button) {
		if(button instanceof CustomButton) {
			CustomButton cb = ((CustomButton) button);
			Function<CustomButton, Boolean> handler = cb.eventHandler;
			if(handler != null) {
				if(handler.apply(cb)) {
					return;
				}
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
        this.buttonList.add(changeName = new CustomButton(0, 0, 0, 14, 14, ""));
        initList();
        disableList();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		this.mc.renderEngine.bindTexture(bg);
		
		int textWidth;
		/*
		 * Station Name
		 */
		String tileEntityName = fontRendererObj.trimStringToWidth(entity.getName(), 200);
		textWidth = fontRendererObj.getStringWidth(tileEntityName);
		/*
		 * Edit Button
		 */
		changeName.xPosition = guiLeft + this.xSize / 2 + textWidth / 2;
		changeName.yPosition = guiTop + 12;
		changeName.enabled = true;
		GL11.glEnable(GL11.GL_BLEND);
		drawTexturedModalRect(this.xSize / 2 + textWidth / 2+2, 14, 358, 0, 10, 10, 512, 512);
		GL11.glDisable(GL11.GL_BLEND);
		/*
		 * Station Name
		 */
		this.fontRendererObj.drawString(tileEntityName, (this.xSize - textWidth) / 2, 16, 0x00DDFF);
		
		/*
		 * Owner + Player Inventory Title
		 */
		//TODO: Owner
//		this.fontRendererObj.drawString("Owner: " + entity.getOwner(), 12, 160, 0x800000);
		this.fontRendererObj.drawString(getTranslatedInventoryName(inventoryPlayer), 91, 175, 0x404040);
		
//		if(tileEntity.getControlledInventory() == null) {
//
//			this.fontRendererObj.drawString("No Inventory", 12, 21, 0xAA0000);
//		}
		
		if(entity.isConnectedToManager()) {

			disableList();
			
			this.fontRendererObj.drawString("Connected", 12, 12, 0x00AA00);
			
			
			/*
			 * Station Status
			 */
			int scaleFactor = 4;
			
			GL11.glScalef(scaleFactor, scaleFactor, 0);
			
			// TODO: Status
//			String status = "<<" + entity.getStatus().toString() + ">>";
			
//			textWidth = this.fontRendererObj.getStringWidth(status);
//			this.fontRendererObj.drawString(status, (this.xSize / 2) / scaleFactor - textWidth / 2, (90 - fontRendererObj.FONT_HEIGHT / 2) / scaleFactor, 0x00DDFF);
		} else {
			this.fontRendererObj.drawString("No Connection", 12, 12, 0xAA0000);
			
			//TODO: Get this from the Server!
			List<TileEntityLogisticsManager> managers = TileEntityLogisticsManager.getActiveManagers();
			
			listContent.clear();
			listContent.addAll(managers);
			listContent.trimToSize();
			
			enableList();
			
		}
		
		
	}
	
	private static final int listLength = 6;
	private static final int listWidth = 280;
	private int scrollPosition = 0;
	private List<CustomButton> listButtons;
	private ArrayList<Object> listContent;
	private CustomButton scrollUp;
	private CustomButton scrollDown;
	
	@SuppressWarnings("unchecked")
	private void initList() {
		buttonList.add(scrollUp = new CustomButton(0, (width + listWidth) / 2, guiTop + 50, "^"));
		buttonList.add(scrollDown = new CustomButton(0, (width + listWidth) / 2, guiTop + 50 + 16 * listLength - 16, "v"));
		//TODO: Scroll-Button Click Handler
		
		listContent = new ArrayList<Object>(0);
		listButtons = new ArrayList<CustomButton>(listLength);
		Function<CustomButton, Boolean> clickHandler = new Function<CustomButton, Boolean>() {
			
			@Override
			public Boolean apply(CustomButton input) {
				TileEntityLogisticsManager manager = (TileEntityLogisticsManager) listContent.get(input.id + scrollPosition);
				entity.linkToManager(new WorldCoord(manager));
				return true;
			}
		};
		for(int i = 0; i < listLength; i++) {
			CustomButton btn = new CustomButton(i, (width - listWidth) / 2, guiTop + 50 + 16 * i, listWidth, 16, "");
			btn.eventHandler = clickHandler;
			listButtons.add(btn);
			buttonList.add(btn);
		}
	}
	
	private void enableList() {
		scrollUp.visible = true;
		scrollDown.visible = true;

		int scrollArea = listContent.size() - listLength;
		boolean hasToScroll = scrollArea > 0;

		scrollUp.enabled = hasToScroll;
		scrollDown.enabled = hasToScroll;
		
		for(int i = 0; i < listLength; i++) {
			CustomButton btn = listButtons.get(i);
			btn.visible = true;
			String buttonText = getListText(i + scrollPosition);
			if(buttonText == null) {
				btn.enabled = false;
				btn.displayString = "";
			} else {
				btn.enabled = true;
				btn.displayString = buttonText;
			}
		}
	}
	
	private void disableList() {
		scrollUp.visible = false;
		scrollDown.visible = false;
		
		for(int i = 0; i < listLength; i++) {
			CustomButton btn = listButtons.get(i);
			btn.visible = false;
		}
	}
	
	private String getListText(int position) {
		if(position < listContent.size()) {
			return String.valueOf(listContent.get(position));
		} else {
			return null;
		}
	}
	
	private void scrollListDown() {
		scrollPosition++;
		checkScrollPosition();
	}
	
	private void scrollListUp() {
		scrollPosition--;
		checkScrollPosition();
	}
	
	private void checkScrollPosition() {
		int scrollArea = listContent.size() - listLength;
		if(scrollPosition < 0 || scrollArea <= 0) {
			scrollPosition = 0;
		} else if(scrollPosition > scrollArea) {
			scrollPosition = scrollArea;
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(bg);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize, 512, 512);
	}
}
