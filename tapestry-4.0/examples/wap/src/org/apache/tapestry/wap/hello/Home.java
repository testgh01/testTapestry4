// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry.wap.hello;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.wml.Deck;

/**
 *  @version $Id: Home.java 365981 2006-01-04 12:44:21 -0800 (Wed, 04 Jan 2006) hlship $
 *  @author David Solis
 *
 **/

public abstract class Home extends Deck {
		
	public void submit(IRequestCycle cycle)
	{
        Hello helloDeck = (Hello) cycle.getPage("Hello");
        helloDeck.setUsername(getUsername());
		cycle.activate(helloDeck);
	}
	
	public abstract String getUsername();
	public abstract void setUsername(String username);
}
