package founderio.taam.multinet.logistics;

public class Station {
	private String name = "";
	
	public Station(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Station [" + name + "]";
	}
}
