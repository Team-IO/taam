package founderio.taam.blocks.multinet;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;
import founderio.taam.Taam;
import founderio.taam.blocks.multinet.cables.CableRedstone;
import founderio.taam.blocks.multinet.cables.RedstoneBlockAdapter;

public class MultinetPartFactory implements IPartFactory {

	public MultinetPartFactory() {
		registerMultiparts();
	}
	
	public void registerMultiparts() {
		MultiPartRegistry.registerParts(this, new String[] {
				Taam.MULTIPART_MULTINET_CABLE + ".redstone",
				Taam.MULTIPART_MULTINET_MULTITRONIX + ".redstone_block_attachment"
		});
		
	}
	
	@Override
	public TMultiPart createPart(String name, boolean client) {
		if(name.equals(Taam.MULTIPART_MULTINET_CABLE + ".redstone")) {
			return new CableRedstone();
		} else if(name.equals(Taam.MULTIPART_MULTINET_MULTITRONIX + ".redstone_block_attachment")) {
			return new RedstoneBlockAdapter();
		} else {
			return null;
		}
	}

}
