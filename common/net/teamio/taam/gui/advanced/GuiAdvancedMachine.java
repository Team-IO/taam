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

	private final List<AppButton> appButtons = new ArrayList<AppButton>();
	private AppButton homeButton;

	public GuiAdvancedMachine(ContainerAdvancedMachine inventorySlotsIn) {
		super(inventorySlotsIn);
		machineContainer = inventorySlotsIn;
	}

	/**
	 * Allow adding buttons externally, namely from
	 * {@link App#initGui(GuiAdvancedMachine)}.
	 * 
	 * @param customButton
	 */
	public void addButton(CustomButton customButton) {
		this.buttonList.add(customButton);
	}

	@Override
	public void initGui() {

		this.xSize = ContainerAdvancedMachine.panelWidth;
		this.ySize = ContainerAdvancedMachine.panelHeight + 87;

		super.initGui();

		this.guiLeft /= 2;
		this.guiTop /= 2;

		
		int buttonSpace = 40;
		int buttonSize = 40;
		int buttonsPerRow = 4;


		int leftOff = xSize / 2 - (buttonsPerRow / 2) * buttonSpace;
		
		/*
		 * Init home screen buttons
		 */
		for (int i = 0; i < machineContainer.registeredApps.size(); i++) {

			int appX = leftOff + (i % buttonsPerRow) * buttonSpace;
			int appY = 40 + (i / buttonsPerRow) * buttonSpace;

			App app = machineContainer.registeredApps.get(i);
			AppButton button = new AppButton(app, guiLeft + appX, guiTop + appY, buttonSize, buttonSize, app.getName());
			buttonList.add(button);
			appButtons.add(button);

		}
		homeButton = new AppButton(null, guiLeft + xSize / 2 - 10, guiTop + ContainerAdvancedMachine.panelHeight - 20, 20, 20, "Home");
		buttonList.add(homeButton);

		/*
		 * App-specific gui stuff
		 */

		for (App app : machineContainer.registeredApps) {
			app.initGui(this);
		}

		/*
		 * Initialize with no app loaded (starts with the home screen)
		 */
		switchApp(null);
	};

	/**
	 * Switches to the given app. Updates the GUI & relays the switch to the
	 * container below.
	 * 
	 * @param app
	 */
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
		if (machineContainer.activeApp != null) {
			machineContainer.activeApp.onHide(this);
		}
		machineContainer.switchApp(app);
		if (machineContainer.activeApp != null) {
			machineContainer.activeApp.onShow(this);
		}
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
			// TODO: do we even handle non-custom buttons?
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		/*
		 * Draw Panel + Player Inventory Slots
		 */
		Minecraft.getMinecraft().getTextureManager().bindTexture(guiTexture);

		int offset = Config.dark_theme ? 0 : 44;

		GuiUtils.drawContinuousTexturedBox(guiLeft, guiTop, offset, 0, ContainerAdvancedMachine.panelWidth, ContainerAdvancedMachine.panelHeight, 44, 44, 20, 0);

		offset = Config.dark_theme ? 134 : 44;

		drawTexturedModalRect(guiLeft + ContainerAdvancedMachine.panelWidth / 2 - 176 / 2, guiTop + ContainerAdvancedMachine.panelHeight - 3, 0, offset, 176, 90);

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

	/**
	 * Draws the app buttons for the home screen.
	 * 
	 * @param mouseX
	 * @param mouseY
	 */
	private void drawHomescreen(int mouseX, int mouseY) {

		int halfWidth = xSize / 2;

		String name = machineContainer.machine.getDisplayName().getFormattedText();
		drawCenteredString(fontRendererObj, name, halfWidth, 10, 0xFFFFFF);

		int buttonSpace = 40;
		int buttonsPerRow = 4;


		int leftOff = halfWidth - (buttonsPerRow / 2) * buttonSpace;
		
		for (int i = 0; i < machineContainer.registeredApps.size(); i++) {

			int appX = leftOff + (i % buttonsPerRow) * buttonSpace;
			int appY = 40 + (i / buttonsPerRow) * buttonSpace;

			App app = machineContainer.registeredApps.get(i);

			ResourceLocation icon = app.getIcon();

			Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
			// Texture size 16, scale by 2
			drawModalRectWithCustomSizedTexture(appX + 3, appY + 3, 0, 0, 32, 32, 32, 32);
		}
	}

}
