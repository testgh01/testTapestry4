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

package org.apache.tapestry.wml;

import org.apache.tapestry.link.DefaultLinkRenderer;
import org.apache.tapestry.link.ILinkRenderer;

/**
 *  A subclass of {@link org.apache.tapestry.link.DefaultLinkRenderer} for
 *  the WML Go element.
 *
 *  @author David Solis
 *  @since 3.0
 **/
public class GoLinkRenderer extends DefaultLinkRenderer
{

	/**
	 *  A singleton for the go link. 
	 **/

	 public static final ILinkRenderer SHARED_INSTANCE = new GoLinkRenderer();

    public String getElement()
    {
        return "go";
    }

    public boolean getHasBody()
    {
        return false;
    }
}
