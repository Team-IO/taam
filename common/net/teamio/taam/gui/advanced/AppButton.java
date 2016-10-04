package net.teamio.taam.gui.advanced;

import net.teamio.taam.gui.util.CustomButton;

public class AppButton extends CustomButton {

	public final App app;
	
	public AppButton(App app, int xPos, int yPos, int width, int height, String displayString) {
		super(0, xPos, yPos, width, height, displayString);
		this.app = app;
	}

}
