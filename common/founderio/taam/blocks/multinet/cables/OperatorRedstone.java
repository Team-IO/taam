package founderio.taam.blocks.multinet.cables;

import java.util.Hashtable;
import java.util.Map;

import founderio.taam.multinet.IMultinetAttachment;
import founderio.taam.multinet.Multinet;
import founderio.taam.multinet.MultinetOperator;

public class OperatorRedstone extends MultinetOperator {

	private Map<IMultinetAttachment, Integer> poweredStateAttachments;
	
	public OperatorRedstone(String multinetCableType) {
		super(multinetCableType);
		setup();
	}

	public OperatorRedstone(Multinet multinet) {
		super(multinet);
		setup();
	}
	
	private void setup() {
		poweredStateAttachments = new Hashtable<IMultinetAttachment, Integer>();
	}

	@Override
	public MultinetOperator createNewInstance(Multinet multinet) {
		return new OperatorRedstone(multinet);
	}

	@Override
	public void multinetCreated() {
		update();
	}

	@Override
	public void multinetAttachmentAdded(IMultinetAttachment attachment) {
	}

	@Override
	public void multinetAttachmentRemoved(IMultinetAttachment attachment) {
		poweredStateAttachments.remove(attachment);
		update();
	}

	@Override
	public void multinetWillBeDestroyed() {
		poweredStateAttachments.clear();
		update();
	}
	
	public void update() {
		
	}

}
