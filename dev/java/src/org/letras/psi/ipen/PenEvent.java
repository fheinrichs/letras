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
package org.letras.psi.ipen;

import org.mundo.annotation.mcSerialize;

/**
 * Wrapper class for pen state changes. Communicated on the specific pen channels.
 * 
 * @author felix_h
 * @version 0.0.1
 */
@mcSerialize
public class PenEvent {
	
	protected int oldState;
	
	protected int newState;

	public int getOldState() {
		return oldState;
	}

	public int getNewState() {
		return newState;
	}
	
	// members

	// constructors

	/**
	 * Simple no-argument constructor.
	 */
	public PenEvent() {
		this.oldState = this.newState = IPenState.OFF;
	}
	
	/**
	 * Simple constructor taking the old and the new
	 * state as arguments.
	 * 
	 * @param oldState		the old state of the pen
	 * @param newState		the new state of the pen
	 */
	public PenEvent(int oldState, int newState) {
		this.oldState = oldState;
		this.newState = newState;
	}
	
	// methods
	
	/**
	 * Returns the state of the pen (synonymous for <code>newState()</code>).
	 * 
	 * @return the state of the pen communicated by this event
	 */
	public int state() {
		return this.newState;
	}
}
