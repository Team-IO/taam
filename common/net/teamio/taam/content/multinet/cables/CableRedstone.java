package net.teamio.taam.content.multinet.cables;

import net.teamio.taam.content.multinet.MultinetCable;

public class CableRedstone extends MultinetCable {

	public CableRedstone() {
		super("redstone");
	}

	@Override
	public void updateAttachmentState() {
		return;
		//Currently no update needed, cables don't need to update the world.
	}

}
