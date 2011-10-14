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
package org.letras.tools.designer;

import java.awt.Color;

import javax.swing.JPanel;

import org.letras.util.region.document.RegionDocument;

public abstract class RegionDocumentEditor extends JPanel {
	private static final long serialVersionUID = 6371947947385267780L;
	private RegionDesigner regionDesigner;
	
	public abstract void setDocument(RegionDocument document);
	
	public void setRegionDesigner(RegionDesigner designer) {
		this.regionDesigner = designer;
	}
	
	protected void setStatusMessage(String message, Color color) {
		regionDesigner.setStatusMessage(message, color);
	}
}