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

package org.apache.tapestry.enhance;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.binding.BindingSource;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.spec.IPropertySpecification;
import org.apache.tapestry.spec.PropertySpecification;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.tapestry.enhance.SpecifiedPropertyWorker}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class TestSpecifiedPropertyWorker extends HiveMindTestCase
{
    private List buildPropertySpecs(String name, String type, boolean persistent)
    {
        return buildPropertySpecs(name, type, persistent, null, null);
    }

    private List buildPropertySpecs(String name, String type, boolean persistent,
            Location location, String initialValue)
    {
        PropertySpecification ps = new PropertySpecification();
        ps.setName(name);
        ps.setType(type);
        ps.setPersistence(persistent ? "session" : null);
        ps.setInitialValue(initialValue);
        ps.setLocation(location);

        return Collections.singletonList(ps);
    }

    private IComponentSpecification buildComponentSpecification(List propertySpecs)
    {
        List names = new ArrayList();
        Iterator i = propertySpecs.iterator();
        while (i.hasNext())
        {
            IPropertySpecification ps = (IPropertySpecification) i.next();

            names.add(ps.getName());
        }

        MockControl c = newControl(IComponentSpecification.class);
        IComponentSpecification result = (IComponentSpecification) c.getMock();

        result.getPropertySpecificationNames();
        c.setReturnValue(names);

        i = propertySpecs.iterator();
        while (i.hasNext())
        {
            IPropertySpecification ps = (IPropertySpecification) i.next();

            result.getPropertySpecification(ps.getName());
            c.setReturnValue(ps);
        }

        return result;
    }

    private IComponentSpecification buildComponentSpecification(String name, String type,
            boolean persistent)
    {
        return buildComponentSpecification(buildPropertySpecs(name, type, persistent));
    }

    public void testAddNormal() throws Exception
    {
        Location l = newLocation();

        IComponentSpecification spec = buildComponentSpecification(buildPropertySpecs(
                "fred",
                "boolean",
                false,
                l,
                null));

        // Training

        MockControl opc = newControl(EnhancementOperation.class);
        EnhancementOperation op = (EnhancementOperation) opc.getMock();

        op.convertTypeName("boolean");
        opc.setReturnValue(boolean.class);

        op.validateProperty("fred", boolean.class);
        op.claimProperty("fred");
        op.addField("_$fred", boolean.class);

        op.getAccessorMethodName("fred");
        opc.setReturnValue("isFred");

        op.addMethod(
                Modifier.PUBLIC,
                new MethodSignature(boolean.class, "isFred", null, null),
                "return _$fred;",
                l);

        op.addMethod(Modifier.PUBLIC, new MethodSignature(void.class, "setFred", new Class[]
        { boolean.class }, null), "{\n  _$fred = $1;\n}\n", l);

        op.addField("_$fred$default", boolean.class);

        op.extendMethodImplementation(
                IComponent.class,
                EnhanceUtils.FINISH_LOAD_SIGNATURE,
                "_$fred$default = _$fred;");

        op.extendMethodImplementation(
                PageDetachListener.class,
                EnhanceUtils.PAGE_DETACHED_SIGNATURE,
                "_$fred = _$fred$default;");

        replayControls();

        SpecifiedPropertyWorker w = new SpecifiedPropertyWorker();

        w.performEnhancement(op, spec);

        verifyControls();
    }

    public void testAddWithInitialValue() throws Exception
    {
        BindingSource bs = (BindingSource) newMock(BindingSource.class);
        Location l = fabricateLocation(12);

        IComponentSpecification spec = buildComponentSpecification(buildPropertySpecs(
                "fred",
                "java.util.List",
                false,
                l,
                "ognl:foo()"));

        InitialValueBindingCreator expectedCreator = new InitialValueBindingCreator(bs,
                EnhanceMessages.initialValueForProperty("fred"), "ognl:foo()", l);

        // Training

        MockControl opc = newControl(EnhancementOperation.class);
        EnhancementOperation op = (EnhancementOperation) opc.getMock();

        op.convertTypeName("java.util.List");
        opc.setReturnValue(List.class);

        op.validateProperty("fred", List.class);
        op.claimProperty("fred");
        op.addField("_$fred", List.class);

        op.getAccessorMethodName("fred");
        opc.setReturnValue("getFred");

        op.addMethod(
                Modifier.PUBLIC,
                new MethodSignature(List.class, "getFred", null, null),
                "return _$fred;",
                l);

        op.addMethod(Modifier.PUBLIC, new MethodSignature(void.class, "setFred", new Class[]
        { List.class }, null), "{\n  _$fred = $1;\n}\n", l);

        op.addInjectedField(
                "_$fred$initialValueBindingCreator",
                InitialValueBindingCreator.class,
                expectedCreator);
        opc.setReturnValue("_$fred$initialValueBindingCreator");
        op.addField("_$fred$initialValueBinding", IBinding.class);
        op
                .extendMethodImplementation(
                        IComponent.class,
                        EnhanceUtils.FINISH_LOAD_SIGNATURE,
                        "_$fred$initialValueBinding = _$fred$initialValueBindingCreator.createBinding(this);\n");

        op.getClassReference(List.class);
        opc.setReturnValue("_$class$java$util$List");

        String code = "_$fred = (java.util.List) _$fred$initialValueBinding.getObject(_$class$java$util$List);\n";

        op.extendMethodImplementation(IComponent.class, EnhanceUtils.FINISH_LOAD_SIGNATURE, code);
        op.extendMethodImplementation(
                PageDetachListener.class,
                EnhanceUtils.PAGE_DETACHED_SIGNATURE,
                code);

        replayControls();

        SpecifiedPropertyWorker w = new SpecifiedPropertyWorker();
        w.setBindingSource(bs);

        w.performEnhancement(op, spec);

        verifyControls();
    }

    public void testAddPersistent() throws Exception
    {
        IComponentSpecification spec = buildComponentSpecification(
                "barney",
                "java.lang.String",
                true);

        // Training

        MockControl opc = newControl(EnhancementOperation.class);
        EnhancementOperation op = (EnhancementOperation) opc.getMock();

        op.convertTypeName("java.lang.String");
        opc.setReturnValue(String.class);

        op.validateProperty("barney", String.class);
        op.claimProperty("barney");
        op.addField("_$barney", String.class);

        op.getAccessorMethodName("barney");
        opc.setReturnValue("getBarney");

        op.addMethod(
                Modifier.PUBLIC,
                new MethodSignature(String.class, "getBarney", null, null),
                "return _$barney;",
                null);

        BodyBuilder b = new BodyBuilder();
        b.begin();
        b.addln("org.apache.tapestry.Tapestry#fireObservedChange(this, \"barney\", ($w) $1);");
        b.addln("_$barney = $1;");
        b.end();

        op.addMethod(Modifier.PUBLIC, new MethodSignature(void.class, "setBarney", new Class[]
        { String.class }, null), b.toString(), null);

        op.addField("_$barney$default", String.class);

        op.extendMethodImplementation(
                IComponent.class,
                EnhanceUtils.FINISH_LOAD_SIGNATURE,
                "_$barney$default = _$barney;");

        op.extendMethodImplementation(
                PageDetachListener.class,
                EnhanceUtils.PAGE_DETACHED_SIGNATURE,
                "_$barney = _$barney$default;");

        replayControls();

        SpecifiedPropertyWorker w = new SpecifiedPropertyWorker();

        w.performEnhancement(op, spec);

        verifyControls();
    }

    public void testFailure() throws Exception
    {
        Location l = fabricateLocation(207);
        // Should be "java.lang.Long"
        List propertySpecs = buildPropertySpecs("wilma", "Long", false, l, null);
        IComponentSpecification spec = buildComponentSpecification(propertySpecs);

        MockControl opc = newControl(EnhancementOperation.class);
        EnhancementOperation op = (EnhancementOperation) opc.getMock();

        op.convertTypeName("Long");
        Throwable ex = new ApplicationRuntimeException("Simulated error.");
        opc.setThrowable(ex);

        op.getBaseClass();
        opc.setReturnValue(BaseComponent.class);

        ErrorLog log = (ErrorLog) newMock(ErrorLog.class);

        log
                .error(
                        "Error adding property wilma to class org.apache.tapestry.BaseComponent: Simulated error.",
                        l,
                        ex);

        replayControls();

        SpecifiedPropertyWorker w = new SpecifiedPropertyWorker();
        w.setErrorLog(log);

        w.performEnhancement(op, spec);

        verifyControls();
    }

}