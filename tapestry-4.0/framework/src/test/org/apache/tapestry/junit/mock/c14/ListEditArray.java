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

package org.apache.tapestry.junit.mock.c14;

import org.apache.tapestry.html.BasePage;

/**
 * Test {@link org.apache.tapestry.form.ListEdit}with an object array as a source.
 * 
 * @author Howard Lewis Ship
 * @since 3.0
 */

public abstract class ListEditArray extends BasePage
{
    public String[] getItems()
    {
        return new String[]
        { "Fred", "Dino", "Wilma" };
    }
}