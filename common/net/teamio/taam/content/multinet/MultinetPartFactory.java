package net.teamio.taam.content.multinet;

import net.teamio.taam.Taam;
import net.teamio.taam.content.multinet.cables.CableRedstone;
import net.teamio.taam.content.multinet.cables.RedstoneBlockAdapter;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;

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
