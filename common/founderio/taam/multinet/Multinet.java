package founderio.taam.multinet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class Multinet {
	
	private static List<Multinet> networks;
	private static Map<String, MultinetOperator> operatorReferences;
	
	static {
		networks = new ArrayList<Multinet>();
		operatorReferences = new Hashtable<String, MultinetOperator>();
	}
	
	public static void registerOperator(MultinetOperator operatorReference) {
		if(!operatorReference.isReference) {
			throw new IllegalArgumentException("Can only register reference operators. (Created without multinet assigned)");
		}
	}
	
	public final String cableType;
	
	private boolean isDestroyed = false;
	private final List<IMultinetAttachment> cables;
	
	

	public Multinet(String cableType) {
		this.cableType = cableType;
		cables = new ArrayList<IMultinetAttachment>();
		networks.add(this);
		System.out.println("Multinet created");
	}
	
	public void addCable(IMultinetAttachment cable) {
		if(isDestroyed) {
			throw new IllegalStateException("Multinet is destroyed.");
		}
		cables.add(cable);
		cable.setNetwork(this);
		System.out.println("Cable added");
	}
	
	public void removeCable(IMultinetAttachment cable) {
		if(isDestroyed) {
			throw new IllegalStateException("Multinet is destroyed.");
		}
		cables.remove(cable);
		cable.setNetwork(null);
		System.out.println("Cable removed");
	}
	
	public boolean mergeOtherMultinet(Multinet net) {
		if(isDestroyed) {
			throw new IllegalStateException("Multinet is destroyed.");
		}
		if(this == net) {
			return false;
		}
		if(!net.cableType.equals(cableType)) {
			return false;
		}

		System.out.println("Merging Multinet...");
		
		for(IMultinetAttachment cable : net.cables) {
			addCable(cable);
		}
		net.cables.clear();
		Multinet.networks.remove(net);
		System.out.println("Multinet merged");
		
		return true;
	}
	
	public boolean isEmpty() {
		if(isDestroyed) {
			throw new IllegalStateException("Multinet is destroyed.");
		}
		return cables.isEmpty();
	}
	
	public boolean contains(IMultinetAttachment attachment) {
		if(isDestroyed) {
			throw new IllegalStateException("Multinet is destroyed.");
		}
		return cables.contains(attachment);
	}
	
	public void destroy() {
		if(isDestroyed) {
			return;
		}
		cables.clear();
		isDestroyed = true;
		networks.remove(this);
		System.out.println("Multinet destroyed!");
	}
	
}