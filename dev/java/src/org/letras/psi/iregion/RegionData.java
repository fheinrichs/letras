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
package org.letras.psi.iregion;

import org.letras.psi.iregion.shape.IShape;
import org.mundo.annotation.mcSerialize;

/**
 * The RegionData class represents Data Transfer Objects for Regions. This 
 * is useful when transfering a set of regions and can greatly reduce the
 * reduce the number of necessary remote method calls. This class is used
 * in the {@link IRegionSet}-Interfaces.
 * @author niklas
 *
 */
@mcSerialize
public class RegionData implements IRegion {
	/**
	 * The URI of this region
	 */
	private String uri;
	/**
	 * The channel for this region
	 */
	private String channel;
	/**
	 * Whether this region wants to receive events and samples that lie within its children's bounds
	 */
	private boolean hungry;
	/**
	 * The shape of this region
	 */
	private IShape shape;

	public RegionData(String uri, String channel, boolean hungry, IShape shape) {
		this.uri = uri;
		this.channel = channel;
		this.hungry = hungry;
		this.shape = shape;
	}
	
	public RegionData(String uri, boolean hungry, IShape shape) {
		this(uri,uri,hungry,shape);
	}
	
	public RegionData() {

	}

	public String uri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String channel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public boolean hungry() {
		return hungry;
	}

	public void setHungry(boolean hungry) {
		this.hungry = hungry;
	}

	public IShape shape() {
		return shape;
	}

	public void setShape(IShape shape) {
		this.shape = shape;
	}

	public boolean deepEquals(RegionData other) {
		return uri().equals(other.uri()) && channel().equals(other.channel()) && hungry() == other.hungry() && shape().equals(other.shape());
	}
	
	public RegionData copy() {
		//TODO shape is not yet a true copy
		return new RegionData(this.uri,this.channel,this.hungry,this.shape);
	}
}
