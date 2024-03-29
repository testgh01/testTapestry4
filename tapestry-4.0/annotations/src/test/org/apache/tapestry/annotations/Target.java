// Copyright 2005 The Apache Software Foundation
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

package org.apache.tapestry.annotations;

import java.util.ArrayList;
import java.util.Map;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;

/**
 * Used by {@link org.apache.tapestry.annotations.TestAnnotationUtils}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public abstract class Target
{
    public abstract String getStringValue();

    public abstract void setIntValue(int value);

    public abstract boolean isBooleanValue();

    public abstract void setNoParameters();

    public abstract String setNonVoidMethod(String value);

    public abstract String getHasParameters(String value);

    public abstract void isVoidGetter();

    public abstract String notAGetter();

    public abstract IComponent getBarney();

    public abstract Map getMapBean();

    public abstract ArrayList getArrayListBean();

    public abstract IAsset getMyAsset();
}
