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

package org.mundo.xml.xquery;

import org.mundo.rt.TypedMap;

class XEForAll extends XEObject
{
  XEForAll(String n)
  {
    name=n;
  }
  boolean exec(XEContext ctx) throws XEngine.ExecException
  {
    Object obj=ctx.pop();
    if (!(obj instanceof TypedMap))
      return false;
    obj=((TypedMap)obj).get(name);
    if (obj==null)
      return false;
    ctx.push(obj);
    return true;
  }
  public String toString()
  {
    return "forall '"+name+"'";
  }
  String name;
}