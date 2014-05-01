package founderio.taam.blocks.multinet;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;
import founderio.taam.Taam;
import founderio.taam.blocks.multinet.cables.CableRedstone;

public class MultinetPartFactory implements IPartFactory {

	public MultinetPartFactory() {
		registerMultiparts();
	}
	
	public void registerMultiparts() {
		MultiPartRegistry.registerParts(this, new String[] {
				Taam.MULTIPART_MULTINET_CABLE + ".redstone"
		});
		
	}
	
	@Override
	public TMultiPart createPart(String name, boolean client) {
		if(name.equals(Taam.MULTIPART_MULTINET_CABLE + ".redstone")) {
			return new CableRedstone();
		} else {
			return null;
		}
	}

}
