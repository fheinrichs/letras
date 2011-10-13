/*
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
 * Department of Computer Science, Darmstadt University of Technology.
 * Portions created by the Initial Developer are
 * Copyright (C) 2001-2008 the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Erwin Aitenbichler
 */

package org.mundo.rt;

/**
 * The methods provided by this interface are used to transform an object
 * tree from an active into a passive representation and vice versa.
 * Method names start with an _, because the actual implementations
 * are usually generated by the <code>idlc</code> compiler.
 *
 * @author Erwin Aitenbichler
 * @see TypedMap
 */
public interface IActivate
{
  /**
   * Writes the state of this object into the specified <code>TypedMap</code>.
   * 
   * @param passiveMap  passive object.
   * @throws Exception
   */
  public void _passivate(TypedMap passiveMap) throws Exception;
  /**
   * Initializes this object with the state contained in the specified
   * <code>TypedMap</code>.
   * 
   * @param passiveMap  the passive object.
   * @param context     a map with context information. For example, client stubs
   *                    require a <code>Session</code> object as context
   *                    information during activation.
   * @throws Exception
   */
  public void _activate(TypedMap passiveMap, TypedMap context) throws Exception;
  /**
   * Returns the unique type name for the external representation of
   * this object, that is shared by all platforms and implementations.
   * Typically this is the XSI (XML Schema Instance) type name.
   */
  public String _getExternalTypeName();
}
