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

package org.apache.tapestry.bean;

import org.apache.hivemind.util.Defense;
import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IBeanProvider;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.binding.BindingConstants;
import org.apache.tapestry.binding.BindingSource;

/**
 * An {@link org.apache.tapestry.bean.IBeanInitializer} implementation that uses an
 * {@link org.apache.tapestry.IBinding} to obtain the value which will be assigned to the bean
 * property.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class BindingBeanInitializer extends AbstractBeanInitializer
{
    private BindingSource _bindingSource;

    private String _bindingReference;

    /** @since 4.0 */
    public BindingBeanInitializer(BindingSource source)
    {
        Defense.notNull(source, "source");

        _bindingSource = source;
    }

    /**
     * @since 4.0
     */
    public void setBindingReference(String bindingReference)
    {
        _bindingReference = bindingReference;
    }

    /** @since 4.0 */
    public String getBindingReference()
    {
        return _bindingReference;
    }

    public void setBeanProperty(IBeanProvider provider, Object bean)
    {
        IComponent component = provider.getComponent();

        String description = BeanMessages.propertyInitializerName(_propertyName);

        IBinding binding = _bindingSource.createBinding(
                component,
                description,
                _bindingReference,
                BindingConstants.OGNL_PREFIX,
                getLocation());

        Class propertyType = PropertyUtils.getPropertyType(bean, _propertyName);

        Object bindingValue = binding.getObject(propertyType);

        setBeanProperty(bean, bindingValue);
    }
}
