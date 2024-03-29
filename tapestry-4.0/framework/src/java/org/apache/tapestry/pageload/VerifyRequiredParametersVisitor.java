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

package org.apache.tapestry.pageload;

import java.util.Iterator;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.spec.IParameterSpecification;

/**
 * Verify whether all required parameters in the examined component are bound, and if they are not,
 * throw an exception.
 * 
 * @author mindbridge
 * @since 3.0
 */
public class VerifyRequiredParametersVisitor implements IComponentVisitor
{
    /**
     * @see org.apache.tapestry.pageload.IComponentVisitor#visitComponent(org.apache.tapestry.IComponent)
     */
    public void visitComponent(IComponent component)
    {
        IComponentSpecification spec = component.getSpecification();

        Iterator i = spec.getRequiredParameters().iterator();

        while (i.hasNext())
        {
            IParameterSpecification parameterSpec = (IParameterSpecification) i.next();

            String name = parameterSpec.getParameterName();

            if (component.getBinding(name) == null)
                throw new ApplicationRuntimeException(PageloadMessages.requiredParameterNotBound(
                        name,
                        component), component, component.getLocation(), null);
        }
    }

}
