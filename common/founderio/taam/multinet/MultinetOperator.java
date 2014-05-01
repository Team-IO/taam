package founderio.taam.multinet;

public abstract class MultinetOperator {
	protected final String multinetCableType;
	protected final Multinet multinet;
	protected final boolean isReference;
	
	public MultinetOperator(String multinetCableType) {
		this.isReference = true;
		this.multinetCableType = multinetCableType;
		this.multinet = null;
	}
	
	public MultinetOperator(String multinetCableType, Multinet multinet) {
		this.isReference = false;
		this.multinetCableType = multinetCableType;
		this.multinet = multinet;
	}
	
	public abstract MultinetOperator createNewInstance();
}
