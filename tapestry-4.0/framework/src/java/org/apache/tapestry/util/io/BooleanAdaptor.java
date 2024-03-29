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

package org.apache.tapestry.util.io;

import org.apache.tapestry.services.DataSqueezer;

/**
 * Squeezes a {@link Boolean}.
 * 
 * @author Howard Lewis Ship
 */

public class BooleanAdaptor implements SqueezeAdaptor
{
    private static final String PREFIX = "TF";

    public String getPrefix()
    {
        return PREFIX;
    }

    public Class getDataClass()
    {
        return Boolean.class;
    }

    /**
     * Squeezes the {@link Boolean}data to either 'T' or 'F'.
     */

    public String squeeze(DataSqueezer squeezer, Object data)
    {
        Boolean bool = (Boolean) data;

        return bool.booleanValue() ? "T" : "F";
    }

    /**
     * Unsqueezes the string to either {@link Boolean#TRUE}or {@link Boolean#FALSE}, depending on
     * the prefix character.
     */

    public Object unsqueeze(DataSqueezer squeezer, String string)
    {
        char ch = string.charAt(0);

        if (ch == 'T')
            return Boolean.TRUE;

        return Boolean.FALSE;
    }

}