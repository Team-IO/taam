package net.teamio.taam.multinet;

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
		if(operatorReference == null) {
			throw new IllegalArgumentException("Registering NULL value as operator.");
		}
		if(!operatorReference.isReference) {
			throw new IllegalArgumentException("Can only register reference operators. (Created without multinet assigned)");
		}
		if(operatorReference.multinetCableType == null || "".equals(operatorReference.multinetCableType)) {
			throw new IllegalArgumentException("Registering NULL/Empty value as operator multinet cable type.");
		}
		operatorReferences.put(operatorReference.multinetCableType, operatorReference);
	}
	
	public final String cableType;
	public final MultinetOperator operator;
	
	private boolean isDestroyed = false;
	private final List<IMultinetAttachment> cables;
	
	

	public Multinet(String cableType) {
		this.cableType = cableType;
		if(operatorReferences.containsKey(cableType)) {
			operator = operatorReferences.get(cableType).createNewInstance(this);
			if(operator == null) {
				throw new IllegalStateException("Operator reference returned null operator instance.");
			}
			if(operator.isReference) {
				throw new IllegalStateException("Operator reference returned operator reference instead of an instance.");
			}
			if(operator.multinet != this) {
				throw new IllegalStateException("Operator reference returned operator instance for a different multinet.");
			}
		} else {
			operator = null;
		}
		cables = new ArrayList<IMultinetAttachment>();
		networks.add(this);
		if(operator != null) {
			operator.multinetCreated();
		}
		System.out.println("Multinet created");
	}
	
	public void addCable(IMultinetAttachment cable) {
		if(isDestroyed) {
			throw new IllegalStateException("Multinet is destroyed.");
		}
		cables.add(cable);
		cable.setNetwork(this);
		if(operator != null) {
			operator.multinetAttachmentAdded(cable);
		}
		System.out.println("IMultinetAttachment added");
	}
	
	public void removeCable(IMultinetAttachment cable) {
		if(isDestroyed) {
			throw new IllegalStateException("Multinet is destroyed.");
		}
		cables.remove(cable);
		cable.setNetwork(null);
		if(operator != null) {
			operator.multinetAttachmentRemoved(cable);
		}
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
		if(operator != null) {
			operator.multinetWillBeDestroyed();
		}
		cables.clear();
		isDestroyed = true;
		networks.remove(this);
		System.out.println("Multinet destroyed!");
	}

	public void update() {
		for(IMultinetAttachment attachment : cables) {
			attachment.updateAttachmentState();
		}
	}
	
}