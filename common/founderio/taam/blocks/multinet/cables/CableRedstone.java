package founderio.taam.blocks.multinet.cables;

import founderio.taam.blocks.multinet.MultinetCable;

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
