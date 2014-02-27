package founderio.taam.blocks.multinet;

import codechicken.multipart.TMultiPart;
import founderio.taam.Taam;

public class MultinetCable extends TMultiPart {


	@Override
	public String getType() {
		return Taam.MULTIPART_MULTINET_CABLE;
	}

}
