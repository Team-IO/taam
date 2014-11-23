package founderio.taam.multinet.logistics;

public class Station implements IStation {
	private String name = "";
	
	public Station(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Station [" + name + "]";
	}
	
	/* (non-Javadoc)
	 * @see founderio.taam.multinet.logistics.IStation#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
}
