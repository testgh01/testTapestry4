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

package org.apache.tapestry.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.impl.ClassFactoryImpl;
import org.apache.hivemind.util.ClasspathResource;
import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.enhance.AbstractPropertyWorker;
import org.apache.tapestry.enhance.EnhancementOperationImpl;
import org.apache.tapestry.enhance.EnhancementWorker;
import org.apache.tapestry.services.ComponentConstructor;
import org.apache.tapestry.spec.ComponentSpecification;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.util.DescribedLocation;

/**
 * A utility class that is used to instantiate abstract Tapestry pages and components. It creates,
 * at runtime, a subclass where all abstract properties are filled in (each property complete with
 * an instance variable, an accessor method and a mutator method). This isn't quite the same as how
 * the class is enhanced at runtime (though it does use a subset of the same
 * {@link org.apache.tapestry.enhance.EnhancementWorker code}), but is sufficient to unit test the
 * class, especially listener methods.
 * <p>
 * One part of the enhancement is that the
 * {@link org.apache.tapestry.IComponent#getSpecification() specification}&nbsp;and
 * {@link org.apache.tapestry.IComponent#getMessages() messages}&nbsp;properties of the page or
 * component class are converted into read/write properties that can be set via reflection
 * (including {@link #newInstance(Class, Map)}.
 * 
 * @author Howard Lewis Ship
 * @since 4.0
 */
public class Creator
{
    /**
     * Keyed on Class, value is an {@link ComponentConstructor}.
     */
    private final Map _constructors = new HashMap();

    private final ClassFactory _classFactory = new ClassFactoryImpl();

    private final ClassResolver _classResolver = new DefaultClassResolver();

    private final List _workers = new ArrayList();

    private final Resource _creatorResource = new ClasspathResource(_classResolver,
            "/CreatorLocation");

    private final Location _creatorLocation = new DescribedLocation(_creatorResource,
            "Creator Location");

    {
        // Overrride AbstractComponent's implementations of
        // these two properties (making them read/write).

        _workers.add(new CreatePropertyWorker("messages", _creatorLocation));
        _workers.add(new CreatePropertyWorker("specification", _creatorLocation));

        // Implement any abstract properties.
        // Note that we don't bother setting the errorLog property
        // so failures may turn into NPEs.

        _workers.add(new AbstractPropertyWorker());
    }

    private ComponentConstructor createComponentConstructor(Class inputClass)
    {
        if (inputClass.isInterface() || inputClass.isPrimitive() || inputClass.isArray())
            throw new IllegalArgumentException(ScriptMessages.wrongTypeForEnhancement(inputClass));

        EnhancementOperationImpl op = new EnhancementOperationImpl(_classResolver,
                new ComponentSpecification(), inputClass, _classFactory, null);

        IComponentSpecification spec = new ComponentSpecification();
        spec.setLocation(_creatorLocation);

        Iterator i = _workers.iterator();
        while (i.hasNext())
        {
            EnhancementWorker worker = (EnhancementWorker) i.next();

            worker.performEnhancement(op, spec);
        }

        return op.getConstructor();
    }

    private ComponentConstructor getComponentConstructor(Class inputClass)
    {
        ComponentConstructor result = (ComponentConstructor) _constructors.get(inputClass);

        if (result == null)
        {
            result = createComponentConstructor(inputClass);

            _constructors.put(inputClass, result);
        }

        return result;
    }

    /**
     * Given a particular abstract class; will create an instance of that class. A subclass is
     * created with all abstract properties filled in with ordinary implementations.
     */
    public Object newInstance(Class abstractClass)
    {
        ComponentConstructor constructor = getComponentConstructor(abstractClass);

        try
        {
            return constructor.newInstance();
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ScriptMessages.unableToInstantiate(
                    abstractClass,
                    ex));
        }
    }

    /**
     * Creates a new instance of a given class, and then initializes properties of the instance. The
     * map contains string keys that are property names, and object values.
     */
    public Object newInstance(Class abstractClass, Map properties)
    {
        Object result = newInstance(abstractClass);

        if (properties != null)
        {
            Iterator i = properties.entrySet().iterator();

            while (i.hasNext())
            {
                Map.Entry e = (Map.Entry) i.next();

                String propertyName = (String) e.getKey();

                PropertyUtils.write(result, propertyName, e.getValue());
            }
        }

        return result;
    }

    /**
     * A convienience (useful in test code) for invoking {@link #newInstance(Class, Map)}. The Map
     * is constructed from the properties array, which consists of alternating keys and values.
     */

    public Object newInstance(Class abstractClass, Object[] properties)
    {
        Map propertyMap = Tapestry.convertArrayToMap(properties);

        return newInstance(abstractClass, propertyMap);
    }
}