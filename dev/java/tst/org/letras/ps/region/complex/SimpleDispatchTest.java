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
package org.letras.ps.region.complex;

import org.letras.psi.iregion.RegionEvent;

public class SimpleDispatchTest extends AbstractDispatchTest {

	public SimpleDispatchTest() throws Exception {
		super("simpleDispatchTest.regions", "simpleDispatchTest.pen");
	}
	
	@Override
	protected void setupConsumers() {
		String[] main = {"main"};
		consumeEvents(RegionEvent.PEN_DOWN, new String[0], main);
		consumeEvents(RegionEvent.TRACE_START, new String[0], main);
		consumeSamples("main");
		consumeEvents(RegionEvent.TRACE_END, new String[0], main);
		consumeEvents(RegionEvent.PEN_UP, new String[0], main);
	}

}
