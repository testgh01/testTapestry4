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

import org.apache.tapestry.AbstractPage;
import org.apache.tapestry.util.ContentType;

/**
 * Concrete class for WML decks. Most decks should be able to simply subclass this, adding new
 * properties and methods. An unlikely exception would be a deck that was not based on a template.
 * 
 * @author David Solis
 * @since 0.2.9
 */

public abstract class Deck extends AbstractPage
{
    /**
     * @return "text/vnd.wap.wml"
     */
    public ContentType getResponseContentType()
    {
        return new ContentType("text/vnd.wap.wml");
    }

}