package net.teamio.taam.gui.advanced;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.teamio.taam.Config;
import net.teamio.taam.gui.util.CustomButton;

public class GuiAdvancedMachine extends GuiContainer {

	public static final ResourceLocation guiTexture = new ResourceLocation("taam", "textures/gui/advanced.png");

	protected final ContainerAdvancedMachine machineContainer;

	public static final int panelWidth = 342;
	public static final int panelHeight = 267;

	private final List<AppButton> appButtons = new ArrayList<AppButton>();
	private AppButton homeButton;

	public GuiAdvancedMachine(ContainerAdvancedMachine inventorySlotsIn) {
		super(inventorySlotsIn);
		machineContainer = inventorySlotsIn;
	}

	public void initGui() {

		this.xSize = panelWidth;
		this.ySize = panelHeight;

		super.initGui();

		this.guiLeft /= 2;
		this.guiTop /= 2;

		System.out.println("Init: guiLeft " + guiLeft);

		int leftOff = xSize / 4;

		for (int i = 0; i < machineContainer.registeredApps.size(); i++) {

			int appX = leftOff + i * 40;
			int appY = 40;

			App app = machineContainer.registeredApps.get(i);
			AppButton button = new AppButton(app, guiLeft + appX, guiTop + appY, 40, 40, app.getName());
			buttonList.add(button);
			appButtons.add(button);
		}
		homeButton = new AppButton(null, guiLeft + xSize / 2 - 10, guiTop + 160, 20, 20, "Home");
		buttonList.add(homeButton);

		switchApp(null);
	};

	public void switchApp(App app) {
		if (app == null) {
			homeButton.visible = false;
			for (AppButton appButton : appButtons) {
				appButton.visible = true;
			}
		} else {
			homeButton.visible = true;
			for (AppButton appButton : appButtons) {
				appButton.visible = false;
			}
		}
		machineContainer.switchApp(app);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof AppButton) {
			App app = ((AppButton) button).app;
			switchApp(app);
		} else if (button instanceof CustomButton) {
			CustomButton cButton = (CustomButton) button;
			cButton.eventHandler.apply(cButton);
		} else {
			//TODO: do we even handle non-custom buttons?
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		/*
		 * Draw Panel + Player Inventory Slots
		 */
		Minecraft.getMinecraft().getTextureManager().bindTexture(guiTexture);

		int offset = Config.dark_theme ? 0 : 44;
		
		GuiUtils.drawContinuousTexturedBox(guiLeft, guiTop, offset, 0, panelWidth, 180, 44, 44, 20, 0);
		
		offset = Config.dark_theme ? 134 : 44;
		
		drawTexturedModalRect(guiLeft + 83, guiTop + 177, 0, offset, 176, 90);

		/*
		 * Draw app background
		 */
		if (machineContainer.activeApp != null) {
			machineContainer.activeApp.drawBackground(this, partialTicks, mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (machineContainer.activeApp == null) {
			drawHomescreen(mouseX, mouseY);
		} else {
			machineContainer.activeApp.drawForeground(this, mouseX, mouseY);
		}
	}

	private void drawHomescreen(int mouseX, int mouseY) {

		int halfWidth = xSize / 2;

		String name = machineContainer.machine.getDisplayName().getFormattedText();
		drawCenteredString(fontRendererObj, name, halfWidth, 10, 0xFFFFFF);

		for (int i = 0; i < machineContainer.registeredApps.size(); i++) {

			int appX = halfWidth / 2 + i * 40;
			int appY = 40;

			App app = machineContainer.registeredApps.get(i);

			ResourceLocation icon = app.getIcon();

			Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
			// Texture size 16, scale by 2
			drawModalRectWithCustomSizedTexture(appX + 3, appY + 3, 0, 0, 32, 32, 32, 32);
		}
	}

}
