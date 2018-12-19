package net.teamio.taam.gui.advanced;

import net.teamio.taam.gui.util.CustomButton;

/**
 * A button implementation for switching to an app in the advanced gui.
 * When used with the {@link GuiAdvancedMachine}, switching is automatically handled.
 * The app needs to be registered with the related {@link ContainerAdvancedMachine} prior to use,
 * which happens automatically in the constructor of the {@link App}.
 *
 * @author Oliver Kahrmann
 */
public class AppButton extends CustomButton {

	public final App app;

	public AppButton(App app, int xPos, int yPos, int width, int height, String displayString) {
		super(0, xPos, yPos, width, height, displayString);
		this.app = app;
	}

}
