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

package org.apache.tapestry.parse;

import org.apache.hivemind.Location;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Represents the closing tag of a component element in the template.
 * 
 * @see TokenType#CLOSE
 * @author Howard Lewis Ship
 * @since 3.0
 */

public class CloseToken extends TemplateToken
{
    private String _tag;

    public CloseToken(String tag, Location location)
    {
        super(TokenType.CLOSE, location);

        _tag = tag;
    }

    public String getTag()
    {
        return _tag;
    }

    protected void extendDescription(ToStringBuilder builder)
    {
        builder.append("tag", _tag);
    }

}
