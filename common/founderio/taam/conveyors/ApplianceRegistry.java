package founderio.taam.conveyors;

import java.util.HashMap;
import java.util.Map;

import founderio.taam.conveyors.appliances.ApplianceSprayer;

public class ApplianceRegistry {
	private ApplianceRegistry() {
		//Util Class
	}
	
	private static Map<String, IConveyorApplianceFactory> applianceFactories;
	
	static {
		applianceFactories = new HashMap<String, IConveyorApplianceFactory>();
		registerFactory("taam.sprayer", new ApplianceSprayer.Factory());
	}
	
	public static void registerFactory(String name, IConveyorApplianceFactory factory) {
		if(applianceFactories.containsKey(name)) {
			throw new RuntimeException("Duplicate registration of appliance factory: " + name + " Previously registered: " + applianceFactories.get(name));
		}
		applianceFactories.put(name, factory);
	}
	
	public static IConveyorApplianceFactory getFactory(String name) {
		return applianceFactories.get(name);
	}
}
