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
 * Squeezes a String (which is pretty simple, most of the time).
 * 
 * @author Howard Lewis Ship
 */

public class StringAdaptor implements SqueezeAdaptor
{
    private static final String PREFIX = "S";

    public String getPrefix()
    {
        return PREFIX;
    }

    public Class getDataClass()
    {
        return String.class;
    }

    public String squeeze(DataSqueezer squeezer, Object data)
    {
        String string = (String) data;

        return PREFIX + string;
    }

    /**
     * Strips the prefix from the string. This method is only invoked by the
     * {@link DataSqueezerImpl} if the string leads with its normal prefix (an 'S').
     */

    public Object unsqueeze(DataSqueezer squeezer, String string)
    {
        if (string.length() == 1)
            return "";

        return string.substring(1);
    }
}