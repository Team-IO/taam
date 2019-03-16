package net.teamio.taam.gui.advanced;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.conveyors.filters.FilterSlot;
import net.teamio.taam.gui.util.CustomButton;
import net.teamio.taam.gui.util.Drawable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gui class for the advanced gui.
 * Handles communication with a {@link ContainerAdvancedMachine} and handles custom button classes.
 * Currently supported: {@link AppButton}, {@link CustomButton}, {@link FilterSlot}.
 * Regular {@link GuiButton} instances don't work, as they only have a button ID and we don't have any matching logic in this gui class.
 * <p>
 * Also draws the home screen and the app buttons when no app is active.
 *
 * @author Oliver Kahrmann
 */
public class GuiAdvancedMachine extends GuiContainer {

	public static final ResourceLocation guiTexture = new ResourceLocation("taam", "textures/gui/advanced.png");

	protected final ContainerAdvancedMachine machineContainer;

	private final List<AppButton> appButtons = new ArrayList<>();
	private AppButton homeButton;

	public static final Drawable iconCheckbox = new Drawable(guiTexture, 144, 0, 10, 10);

	public static final Drawable iconHome = new Drawable(guiTexture, 194, 0, 11, 11);

	public static final Drawable iconEdit = new Drawable(guiTexture, 166, 0, 10, 10);

	public static final Drawable iconMatchExact = new Drawable(guiTexture, 166, 10, 10, 10);
	public static final Drawable iconMatchMod = new Drawable(guiTexture, 166, 20, 10, 10);
	public static final Drawable iconMatchOredict = new Drawable(guiTexture, 166, 30, 10, 10);

	public static final Drawable iconCheckMeta = new Drawable(guiTexture, 155, 0, 10, 10);
	public static final Drawable iconCheckNBT = new Drawable(guiTexture, 155, 10, 10, 10);
	public static final Drawable iconDontCheckMeta = new Drawable(guiTexture, 155, 20, 10, 10);
	public static final Drawable iconDontCheckNBT = new Drawable(guiTexture, 155, 30, 10, 10);


	private static final int buttonSpace = 60;
	private static final int buttonSize = 40;
	private static final int buttonsPerRow = 4;

	public GuiAdvancedMachine(ContainerAdvancedMachine inventorySlotsIn) {
		super(inventorySlotsIn);
		machineContainer = inventorySlotsIn;
	}

	/**
	 * Allow adding buttons or checkboxes from outside this class.
	 *
	 * @param button any {@link GuiButton} instance that is handled. Currently supported: {@link AppButton}, {@link CustomButton}, {@link FilterSlot}.
	 *               Regular {@link GuiButton} instances don't work, as they only have a button ID and we don't have any matching logic in this gui class.
	 */
	@Nonnull
	public <T extends GuiButton> T addButton(@Nonnull T button) {
		return super.addButton(button);
	}

	@Override
	public void initGui() {

		this.xSize = ContainerAdvancedMachine.panelWidth;
		this.ySize = ContainerAdvancedMachine.panelHeight + 87;

		super.initGui();

		this.guiLeft /= 2;
		this.guiTop /= 2;


		int buttonsPerRow = GuiAdvancedMachine.buttonsPerRow;
		if (buttonsPerRow > machineContainer.registeredApps.size()) {
			buttonsPerRow = machineContainer.registeredApps.size();
		}


		int leftOff = xSize / 2 - (buttonsPerRow / 2) * buttonSpace;
		int buttonHalfSize = buttonSpace / 2 - buttonSize / 2;

		/*
		 * Init home screen buttons & Create App GUIs
		 */
		for (int i = 0; i < machineContainer.registeredApps.size(); i++) {
			int appX = leftOff + (i % buttonsPerRow) * buttonSpace;
			int appY = 40 + (i / buttonsPerRow) * buttonSpace;

			App app = machineContainer.registeredApps.get(i);
			app.gui = app.createGui();

			String appNameTranslated = I18n.format(app.getName());
			AppButton button = new AppButton(app, guiLeft + appX + buttonHalfSize, guiTop + appY, buttonSize, buttonSize, appNameTranslated);
			button.textVerticalAlignment = 4;
			button.trimText = false;
			button.image = app.gui.getIcon();
			buttonList.add(button);
			appButtons.add(button);
		}
		homeButton = new AppButton(null, guiLeft + xSize / 2 - 10, guiTop + ContainerAdvancedMachine.panelHeight - 20, 20, 20, null);
		homeButton.textVerticalAlignment = 4;
		homeButton.image = iconHome;
		buttonList.add(homeButton);

		/*
		 * App-specific gui stuff
		 */

		for (App app : machineContainer.registeredApps) {
			app.gui.initGui(this);
			app.gui.onHide(this);
		}

		/*
		 * Initialize with no app loaded (starts with the home screen)
		 */
		switchApp(null);
	}

	/**
	 * Switches to the given app. Updates the GUI & relays the switch to the
	 * container below.
	 *
	 * @param app the target application. Displays the home screen when null is passed.
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
			machineContainer.activeApp.gui.onHide(this);
		}
		machineContainer.switchApp(app);
		if (machineContainer.activeApp != null) {
			machineContainer.activeApp.gui.onShow(this);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button == null) return;

		if (button instanceof AppButton) {
			App app = ((AppButton) button).app;
			switchApp(app);
		} else if (button instanceof CustomButton) {
			CustomButton cButton = (CustomButton) button;
			if (cButton.eventHandler == null) {
				Log.warn("Skipping button handler, as it is null.");
			} else {
				cButton.eventHandler.apply(cButton);
			}
		} else if (button instanceof FilterSlot) {
			FilterSlot filterSlot = (FilterSlot) button;
			filterSlot.clicked(mc);
		} else {
			Log.warn("Unknown button class found: {} This may be an error", button.getClass());
		}
	}

	public int getGuiLeft() {
		return guiLeft;
	}

	public int getGuiTop() {
		return guiTop;
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
			machineContainer.activeApp.gui.drawBackground(this, partialTicks, mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (machineContainer.activeApp == null) {
			int halfWidth = xSize / 2;

			String name = machineContainer.machine.getDisplayName().getFormattedText();
			drawCenteredString(fontRendererObj, name, halfWidth, 10, 0xFFFFFF);
		} else {
			machineContainer.activeApp.gui.drawForeground(this, mouseX, mouseY);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for (GuiButton aButtonList : this.buttonList) {
			if (aButtonList instanceof FilterSlot) {
				FilterSlot filterSlot = (FilterSlot) aButtonList;
				filterSlot.drawTooltip(mc, mouseX, mouseY);
			}
		}
	}

	public void drawTooltip(List<String> text, int mouseX, int mouseY) {
		this.drawHoveringText(text, mouseX - guiLeft, mouseY - guiTop);
	}

	private static final List<String> textList = new ArrayList<>();

	public void drawTooltipTranslated(String unlocalized, int mouseX, int mouseY) {
		textList.clear();

		String localized = I18n.format(unlocalized);
		// Split at literal \n in the translated text. a lot of escaping here.
		String[] split = localized.split("\\\\n");
		Collections.addAll(textList, split);

		drawTooltip(textList, mouseX, mouseY);
	}

}
