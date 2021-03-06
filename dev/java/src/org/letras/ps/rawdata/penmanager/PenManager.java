/*******************************************************************************
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is MundoCore Java.
 * 
 * The Initial Developer of the Original Code is Telecooperation Group,
 * Department of Computer Science, Technische Universität Darmstadt.
 * Portions created by the Initial Developer are
 * Copyright © 2009-2011 the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 * Felix Heinrichs
 * Niklas Lochschmidt
 * Jannik Jochem
 ******************************************************************************/
package org.letras.ps.rawdata.penmanager;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.rawdata.IPenAdapterFactory;
import org.letras.ps.rawdata.IPenDriver;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.TypedMap;
import org.mundo.service.IConfigure;

/**
 * The pen manager service is the main class in the pen manager component.
 * It maintains the currently used <code>IPenAdapterFactory</code> implementations
 * and provides an appropriate method for retrieving the factory to use
 * for a specific driver (c.f. <code>selectPenAdapterFactory()</code>. Additionally
 * it sets up the pen discovery protocol functionality.
 * <P>
 * The pen manager is implemented as a singleton, therefore its instance can only
 * be obtained using the <code>getInstance()</code> method. It takes the following 
 * configuration options (no runtime reconfiguration)
 * <ol>
 *  <li> <i>pap-zone</i>: the zone in which pen services hosted will be visible, this will
 *  also be the zone for the individual pen data channels
 * </ol>
 * 
 * @author felix_h
 * @version 0.0.1
 */
public class PenManager extends Service implements IConfigure {

	// logger

	private static final Logger logger = 
		Logger.getLogger("org.letras.ps.rawdata.penmanager");
	
	// defaults
	
	private static final String DEFAULT_ZONE = "rt";
	private static final String ID_FORMAT = "%s.%s";
	
	// singleton

	private static PenManager instance;

	/**
	 * Method to retrieve the singleton instance of <code>PenManager</code>.
	 */
	public static PenManager getInstance() {
		try {
			return (instance == null) ? (instance = new PenManager())
					: instance;
		} catch (IllegalAccessException e) {
			// should never happen !!!
			logger.logp(Level.SEVERE, "PenManager", "getInstance",
					"message from the OTHER side...", e);
			return null;
		}
	}
	
	// members

	private IPenAdapterFactory factory;
	
	private TypedMap config;
	
	private String zone;
	
	private Hashtable<String, PenService> pens;
	
	
	// constructors

	/**
	 * Simple no-argument constructor. The only reason for it to be publicly accessible is the
	 * instantiation policy of Mundo. Normally this constructor will never be used, use the
	 * <code>getInstance()</code> method instead. If the constructor is used after the singleton has
	 * been instantiated, it will throw an <code>IllegalAccessException</code>.
	 * 
	 * @throws IllegalAccessException If this class has already been instantiated (instance != null),
	 * this should never happen, since the contract is to use the <code>getInstance()</code> method
	 */
	public PenManager() throws IllegalAccessException {
		if (instance == null) {
			// initialize a new factory
			this.factory = new GenericPenAdapterFactory();
			this.zone = DEFAULT_ZONE;
			
			this.pens = new Hashtable<String, PenService>();
			
			// set the singleton instance (NOTE ugly but needed by Mundo)
			// this will set the instance the first time a raw data processor is created
			instance = this;
		}
		else throw new IllegalAccessException("Tried to access Singleton constructor");
	}
	
	// methods
	
	/**
	 * Used to select the {@link org.letras.ps.rawdata.IPenAdapterFactory} used for
	 * a specific <code>IPenDriver</code> implementation.
	 * 
	 * @param driver	the driver for which the factory should be provided
	 * @return			a <code>IPenAdapterFactory</code> suitable for the provided driver
	 */
	public IPenAdapterFactory selectPenAdapterFactory(IPenDriver driver) {
		// Note: currently (0.x.y, x <= 1) only a generic factory is used. This might change
		// in the future.
		logger.logp(Level.FINE, "PenManager", "selectPenAdapterFactory",
				String.format("adapter factory selected for driver: %s (factory) / %s (driver)",
				this.factory.getClass().getName(), driver.getClass().getName()));
		return this.factory;
	}

	/**
	 * Used to obtain a pen service for a pen with the given id. In case this pen
	 * service already exists and is registered at the <code>ServiceManager</code>, a
	 * reference to this service is returned. If there is currently no such service,
	 * a new service will be created and registered appropriately.
	 * 
	 * @param penId the unique pen id of the pen
	 * @return a reference to the service associated with the pen
	 */
	public PenService penService(String penId) {
		if (penId == null) {
			logger.logp(Level.WARNING, "PenManager", "penService", 
					"penId cannot be null");
			return null;
		}
		
		// check whether the service is already contained (and if so return this service)
		if (this.pens.containsKey(penId)) {
			logger.logp(Level.FINE, "PenManager", "penService", 
					"pen available (returning pen service)");
			return this.pens.get(penId);
		}
		else {
			logger.logp(Level.FINE, "PenManager", "penService", 
					"pen not available (creating new pen service)");

			PenService ps = new PenService(penId); 
			
			// set the zone where the service should be visible
			ps.setServiceZone(this.zone);
			
			// register the pen service as new service in the service manager
			// this will allow discovery of the new service (in the specified zone)
			Mundo.registerService(ps);
			
			// store the pen in the list of available pens
			this.pens.put(penId, ps);
			
			return ps;
		}
	}
	
	/**
	 * Used to unregister and shutdown a given pen service.
	 * 
	 * @param penId the unique pen id of the pen
	 * @return true if the service could be shutdown false otherwise
	 */
	public boolean killPenService(String penId) {
		if (penId == null) {
			logger.logp(Level.FINE, "PenManager", "killPenService",
					"penId is null");
			return false;
		}
		
		logger.logp(Level.FINE, "PenManager", "killPenService", 
					String.format("killing pen service (id=%s)", penId));
		
		// remove the pen service if it is available: remove returns null
		// if there is no such key in the map
		PenService ps = this.pens.remove(penId);
		
		if (ps != null) {
			// first the service needs to be shutdown by removing it from the
			// local service manager
			Mundo.unregisterService(ps);
			return true;
		}
		else return false;
	}
	
	/**
	 * Overwritten method from {@link org.mundo.rt.Service}
	 */
	@Override
	public void init() {
		super.init();
		logger.logp(Level.FINE, "PenManager", "init", "initializing pen management");
	}

	/**
	 * Overwritten method from {@link org.mundo.rt.Service}
	 */
	@Override
	public void shutdown() {
		logger.logp(Level.FINE, "PenManager", "shutdown", "shutting down pen management");
		super.shutdown();
	}

	/**
	 * Interface method from {@link org.mundo.service.IConfigure}. Used
	 * for configuration of this service.
	 */
	@Override
	public Object getServiceConfig() {
		return this.config;
	}

	/**
	 * Interface method from {@link org.mundo.service.IConfigure}. Used
	 * for configuration of this service.
	 */
	@Override
	public void setServiceConfig(Object cfg) {
		
		// check whether this service has already been initialized (or is currently
		// being initialized)
		if (this.getState() >= Service.STATE_INITIALIZED) {
			logger.logp(Level.INFO, "PenManager", "setServiceConfig",
					"no dynamic reconfiguration supported");
			return;
		}
		
		// check whether the passed object is a TypedMap
		if ((cfg == null)||!(cfg instanceof TypedMap)) {
			logger.logp(Level.WARNING, "PenManager", "setServiceConfig",
					"configuration parameter must be a TypedMap");
			return;
		}

		// obtain the typed map holding the configuration options
		this.config = (TypedMap) cfg;
		
		String zne = (this.config.containsKey("pap-zone")) ?
				this.config.getString("pap-zone") : null;
		if (zne != null) this.zone = zne;
		
		logger.logp(Level.CONFIG, "PenManager", "setServiceConfig",
		String.format("configured parameters (zone=%s)", this.zone));
		
		this.setServiceZone(this.zone);
	}
	
	// static methods
	
	/**
	 * Creates a new unique pen id.
	 * 
	 * @param driverName	name of the driver this pen connects to
	 * @param token			a unique token attached to a pen (provided by driver)
	 */
	public static String penId(String driverName, String token) {
		// current way to form the id: combine bluetooth address a and class name c of the class
		// implementing the IPenDriver interface to  form "a.c", then hash this string and prepend a
		// "p" to the hex representation of this string (WARNING: duplicate ids possible due to collision,
		// this should only be a temporary solution)
		return String.format("p%x", String.format(ID_FORMAT, token, driverName).hashCode());
	}
}
