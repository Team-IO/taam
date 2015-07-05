package net.teamio.taam.multinet;

public abstract class MultinetOperator {
	protected final String multinetCableType;
	protected final Multinet multinet;
	protected final boolean isReference;
	
	public MultinetOperator(String multinetCableType) {
		this.isReference = true;
		this.multinetCableType = multinetCableType;
		this.multinet = null;
	}
	
	public MultinetOperator(Multinet multinet) {
		this.isReference = false;
		this.multinetCableType = multinet.cableType;
		this.multinet = multinet;
	}
	
	public abstract MultinetOperator createNewInstance(Multinet multinet);
	
	public abstract void multinetCreated();
	public abstract void multinetAttachmentAdded(IMultinetAttachment attachment);
	public abstract void multinetAttachmentRemoved(IMultinetAttachment attachment);
	public abstract void multinetWillBeDestroyed();
}
