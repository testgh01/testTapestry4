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

package org.apache.tapestry.form.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.tapestry.IBeanProvider;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.ValidationMessages;
import org.apache.tapestry.junit.TapestryTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.tapestry.form.validator.ValidatorFactoryImpl}.
 * 
 * @author Howard Lewis Ship
 * @since 4.0
 */
public class TestValidatorFactory extends TapestryTestCase
{
    private Map buildContributions(String name, boolean configurable)
    {
        ValidatorContribution vc = newContribution(configurable, ValidatorFixture.class);

        return Collections.singletonMap(name, vc);
    }

    private ValidatorContribution newContribution(boolean configurable, Class validatorClass)
    {
        ValidatorContribution vc = new ValidatorContribution();
        vc.setConfigurable(configurable);
        vc.setValidatorClass(validatorClass);

        return vc;
    }

    public void testEmpty()
    {
        IComponent component = newComponent();
        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();

        replayControls();

        List result = vf.constructValidatorList(component, "");

        assertTrue(result.isEmpty());

        verifyControls();
    }

    public void testSingle()
    {
        IComponent component = newComponent();

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(buildContributions("value", true));

        List result = vf.constructValidatorList(component, "value=foo");

        ValidatorFixture fixture = (ValidatorFixture) result.get(0);

        assertEquals("foo", fixture.getValue());

        verifyControls();
    }

    public void testMessage()
    {
        IComponent component = newComponent();

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(buildContributions("fred", false));

        List result = vf.constructValidatorList(component, "fred[fred's message]");

        ValidatorFixture fixture = (ValidatorFixture) result.get(0);

        assertEquals("fred's message", fixture.getMessage());

        verifyControls();
    }

    public void testConfigureAndMessage()
    {
        IComponent component = newComponent();

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(buildContributions("value", true));

        List result = vf.constructValidatorList(component, "value=biff[fred's message]");

        ValidatorFixture fixture = (ValidatorFixture) result.get(0);

        assertEquals("biff", fixture.getValue());
        assertEquals("fred's message", fixture.getMessage());

        verifyControls();
    }

    public void testMissingConfiguration()
    {
        IComponent component = newComponent();

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(buildContributions("fred", true));

        try
        {
            vf.constructValidatorList(component, "fred");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Validator 'name' must be configured in order to be used. "
                    + "The value is configured by changing 'name' to 'name=value'.", ex
                    .getMessage());
        }

        verifyControls();
    }

    public void testMultiple()
    {
        IComponent component = newComponent();

        replayControls();

        Map map = new HashMap();
        map.put("required", newContribution(false, Required.class));
        map.put("email", newContribution(false, Email.class));
        map.put("minLength", newContribution(true, MinLength.class));

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(map);

        List result = vf
                .constructValidatorList(
                        component,
                        "required[EMail is required],email,minLength=10[EMail must be at least ten characters long]");

        assertEquals(3, result.size());

        Required r = (Required) result.get(0);
        assertEquals("EMail is required", r.getMessage());

        assertTrue(result.get(1) instanceof Email);

        MinLength minLength = (MinLength) result.get(2);

        assertEquals(10, minLength.getMinLength());
        assertEquals("EMail must be at least ten characters long", minLength.getMessage());

        verifyControls();
    }

    public void testUnparseable()
    {
        IComponent component = newComponent();

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(buildContributions("fred", false));

        try
        {
            vf.constructValidatorList(component, "fred,=foo");
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Unable to parse 'fred,=foo' into a list of validators.", ex.getMessage());
        }

        verifyControls();
    }

    public void testUnwantedConfiguration()
    {
        IComponent component = newComponent();

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(buildContributions("fred", false));

        try
        {
            vf.constructValidatorList(component, "fred=biff");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Validator 'fred' is not configurable, "
                    + "'fred=biff' should be changed to just 'fred'.", ex.getMessage());
        }

        verifyControls();
    }

    public void testMissingValidator()
    {
        IComponent component = newComponent();

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(Collections.EMPTY_MAP);

        try
        {
            vf.constructValidatorList(component, "missing");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("No validator named 'missing' has been defined.", ex.getMessage());
        }

        verifyControls();
    }

    public void testInstantiateFailure()
    {
        IComponent component = newComponent();

        replayControls();

        Map map = new HashMap();

        map.put("fred", newContribution(false, Object.class));

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(map);

        try
        {
            vf.constructValidatorList(component, "fred");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Error initializing validator 'fred' (class java.lang.Object): java.lang.Object",
                    ex.getMessage());
        }

        verifyControls();
    }

    private Validator newValidator()
    {
        return (Validator) newMock(Validator.class);
    }

    private IBeanProvider newBeanProvider(String beanName, Object bean)
    {
        MockControl control = newControl(IBeanProvider.class);
        IBeanProvider provider = (IBeanProvider) control.getMock();

        provider.getBean(beanName);
        control.setReturnValue(bean);

        return provider;
    }

    private IComponent newComponent(IBeanProvider provider)
    {
        MockControl control = newControl(IComponent.class);
        IComponent component = (IComponent) control.getMock();

        component.getBeans();
        control.setReturnValue(provider);

        return component;
    }

    public void testBeanReference() throws Exception
    {
        Validator validator = newValidator();
        IBeanProvider provider = newBeanProvider("fred", validator);
        IComponent component = newComponent(provider);

        IFormComponent field = newField();
        ValidationMessages messages = newMessages();
        Object value = new Object();

        validator.validate(field, messages, value);

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(Collections.EMPTY_MAP);

        List validators = vf.constructValidatorList(component, "$fred");

        assertEquals(1, validators.size());

        Validator wrapper = (Validator) validators.get(0);

        wrapper.validate(field, messages, value);

        verifyControls();
    }

    private ValidationMessages newMessages()
    {
        return (ValidationMessages) newMock(ValidationMessages.class);
    }

    private IFormComponent newField()
    {
        return (IFormComponent) newMock(IFormComponent.class);
    }

    public void testBeanReferenceNotValidator()
    {
        Object bean = new Object();
        IBeanProvider provider = newBeanProvider("fred", bean);
        IComponent component = newComponent(provider);

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(Collections.EMPTY_MAP);

        try
        {
            List l = vf.constructValidatorList(component, "$fred");

            Validator wrapper = (Validator) l.get(0);

            wrapper.getAcceptsNull();

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Bean 'fred' does not implement the org.apache.tapestry.form.validator.Validator interface.",
                    ex.getMessage());
            assertSame(bean, ex.getComponent());
        }

        verifyControls();
    }

    public void testBeanReferenceWithValue()
    {
        IComponent component = newComponent();

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(Collections.EMPTY_MAP);

        try
        {
            vf.constructValidatorList(component, "$fred=10");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Validator 'fred' is a reference to a managed bean of the component, and may not have a value or a message override specified.",
                    ex.getMessage());
        }

        verifyControls();
    }

    public void testBeanReferenceWithMessage()
    {
        IComponent component = newComponent();

        replayControls();

        ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
        vf.setValidators(Collections.EMPTY_MAP);

        try
        {
            vf.constructValidatorList(component, "$fred[custom message]");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Validator 'fred' is a reference to a managed bean of the component, and may not have a value or a message override specified.",
                    ex.getMessage());
        }

        verifyControls();
    }
}
