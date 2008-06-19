package javax.persistence.osgi;

import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ProviderTrackerCustomizer implements ServiceTrackerCustomizer{

	public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PersistenceProvider";

	private BundleContext ctx;
	
	public ProviderTrackerCustomizer(BundleContext context) { 
		this.ctx = context; 
	}
	
	//-------------------------------------------------------------
	// Service was added 
	//-------------------------------------------------------------	
	public Object addingService(ServiceReference reference) {
		log("New service detected...");
		
		String providerName = getProviderName(reference);
		PersistenceProvider provider = (PersistenceProvider) ctx.getService(reference);
		Persistence.addProvider(provider);

		log("Added service " + providerName);
		return provider;
	}

	//-------------------------------------------------------------
	// Service was modified 
	//-------------------------------------------------------------	
	public void modifiedService(ServiceReference reference, Object serviceObject) {
		// Rogue provider -- we don't support modifying provider services
		this.removedService(reference, serviceObject);
	}

	//-------------------------------------------------------------
	// Service was removed 
	//-------------------------------------------------------------	
	public void removedService(ServiceReference reference, Object serviceObject) {
		log("Removing service...");

		String providerName = getProviderName(reference);
		Persistence.removeProvider(providerName);

		log("Removed service " + providerName + " service object: " + serviceObject);
	}

	//-------------------------------------------------------------
	// Helper method to get provider name from the service reference.
	// Assumes that provider services are registered with property 
	// that indicates the name of the provider impl class.
	//-------------------------------------------------------------
	private String getProviderName(ServiceReference ref) {
    	String providerName = (String) ref.getProperty(PERSISTENCE_PROVIDER);
    	if (providerName == null) {
    		System.out.println("Provider service registered without name property");
    		throw new RuntimeException("Provider service registered without name property");
    	}
    	return providerName;
	}

	private void log(String message) {
    	System.out.println("ProviderTracker: " + message);
	}
}