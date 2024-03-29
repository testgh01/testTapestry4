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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.service.impl.ClassFactoryImpl;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IPage;
import org.apache.tapestry.components.Insert;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageValidateListener;
import org.apache.tapestry.link.ServiceLink;
import org.apache.tapestry.services.ComponentConstructor;
import org.apache.tapestry.spec.IComponentSpecification;
import org.easymock.MockControl;
import org.easymock.internal.ArrayMatcher;

/**
 * Tests for {@link org.apache.tapestry.enhance.EnhancementOperationImpl}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class TestEnhancementOperation extends HiveMindTestCase
{
    protected void setUp() throws Exception
    {
        super.setUp();

        EnhancementOperationImpl._uid = 97;
    }

    public abstract static class Fixture
    {
        public abstract String getStringProperty();

        public abstract boolean isBooleanProperty();

        public abstract boolean getFlagProperty();
    }

    public abstract static class ValidatingComponent extends AbstractComponent implements
            PageValidateListener
    {
    }

    public abstract static class GetClassReferenceFixture
    {
        public abstract Class getClassReference();
    }

    public static class MissingConstructorFixture
    {
        public MissingConstructorFixture(String foo)
        {
            //
        }
    }

    public abstract static class UnclaimedAbstractPropertiesFixture
    {
        public abstract String getReadOnly();

        public abstract void setWriteOnly(String value);

        public void setConcrete(int i)
        {
            //
        }

        public int getConcrete()
        {
            return -1;
        }
    }

    public void testClaimedProperty()
    {
        EnhancementOperationImpl eo = new EnhancementOperationImpl();

        eo.claimProperty("foo");
        eo.claimProperty("bar");

        try
        {
            eo.claimProperty("foo");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(EnhanceMessages.claimedProperty("foo"), ex.getMessage());
        }
    }

    public void testClaimReadonlyPropertyDoesNotExist()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        eo.claimReadonlyProperty("foo");

        verifyControls();
    }

    public void testClaimReadonlyPropertyClaimed()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        eo.claimReadonlyProperty("foo");
        eo.claimReadonlyProperty("bar");

        try
        {
            eo.claimProperty("foo");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(EnhanceMessages.claimedProperty("foo"), ex.getMessage());
        }

        verifyControls();
    }

    public void testClaimReadonlyPropertyHasSetter()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        try
        {
            // id is a read/write property (even if it isn't abstract)
            eo.claimReadonlyProperty("id");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Property id should be read-only; remove method public void org.apache.tapestry.AbstractComponent.setId(java.lang.String).",
                    ex.getMessage());
        }

        verifyControls();
    }

    private ClassFactory newClassFactory()
    {
        return newClassFactory(BaseComponent.class);
    }

    private ClassFactory newClassFactory(Class baseClass)
    {
        return newClassFactory(baseClass, newClassFab());
    }

    private ClassFactory newClassFactory(Class baseClass, ClassFab classFab)
    {
        MockControl control = newControl(ClassFactory.class);
        ClassFactory factory = (ClassFactory) control.getMock();

        String className = baseClass.getName();
        int dotx = className.lastIndexOf('.');
        String baseName = className.substring(dotx + 1);

        factory.newClass("$" + baseName + "_97", baseClass);
        control.setReturnValue(classFab);

        return factory;
    }

    private ClassFab newClassFab()
    {
        return (ClassFab) newMock(ClassFab.class);
    }

    private IComponentSpecification newSpec()
    {
        return (IComponentSpecification) newMock(IComponentSpecification.class);
    }

    public void testConstructorAndAccessors()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        assertSame(BaseComponent.class, eo.getBaseClass());

        verifyControls();
    }

    public void testCheckImplementsNoInterface()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        assertEquals(false, eo.implementsInterface(PageValidateListener.class));

        verifyControls();
    }

    public void testCheckImplementsClassImplements()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory(ValidatingComponent.class);

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                ValidatingComponent.class, cf, null);

        assertEquals(true, eo.implementsInterface(PageValidateListener.class));

        verifyControls();
    }

    public void testCheckImplementsNoMatchForAddedInterfaces()
    {
        IComponentSpecification spec = newSpec();

        MockControl factoryc = newControl(ClassFactory.class);
        ClassFactory factory = (ClassFactory) factoryc.getMock();

        MockControl classfabc = newControl(ClassFab.class);
        ClassFab classfab = (ClassFab) classfabc.getMock();

        factory.newClass("$BaseComponent_97", BaseComponent.class);
        factoryc.setReturnValue(classfab);

        classfab.addInterface(PageDetachListener.class);

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, factory, null);

        eo.extendMethodImplementation(
                PageDetachListener.class,
                EnhanceUtils.PAGE_DETACHED_SIGNATURE,
                "foo();");

        assertEquals(false, eo.implementsInterface(PageValidateListener.class));

        verifyControls();
    }

    public void testCheckImplementsMatchAddedInterfaces()
    {
        IComponentSpecification spec = newSpec();

        MockControl factoryc = newControl(ClassFactory.class);
        ClassFactory factory = (ClassFactory) factoryc.getMock();

        MockControl classfabc = newControl(ClassFab.class);
        ClassFab classfab = (ClassFab) classfabc.getMock();

        factory.newClass("$BaseComponent_97", BaseComponent.class);
        factoryc.setReturnValue(classfab);

        classfab.addInterface(PageDetachListener.class);

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, factory, null);

        eo.extendMethodImplementation(
                PageDetachListener.class,
                EnhanceUtils.PAGE_DETACHED_SIGNATURE,
                "foo();");

        assertEquals(true, eo.implementsInterface(PageDetachListener.class));

        verifyControls();
    }

    public void testValidatePropertyNew()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        // Validates because BaseComponent doesn't have such a property.

        eo.validateProperty("abraxis", Set.class);

        verifyControls();
    }

    public void testValidatePropertyMatches()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        // Validates because BaseComponent inherits a page property of type IPage

        eo.validateProperty("page", IPage.class);

        verifyControls();
    }

    public void testValidatePropertyMismatch()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        // Validates because BaseComponent inherits a page property of type IPage

        try
        {
            eo.validateProperty("page", String.class);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(EnhanceMessages.propertyTypeMismatch(
                    BaseComponent.class,
                    "page",
                    IPage.class,
                    String.class), ex.getMessage());
        }

        verifyControls();
    }

    public void testConvertTypeName()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        assertSame(boolean.class, eo.convertTypeName("boolean"));
        assertSame(float[].class, eo.convertTypeName("float[]"));
        assertSame(double[][].class, eo.convertTypeName("double[][]"));

        assertSame(Set.class, eo.convertTypeName(Set.class.getName()));

        assertSame(List[].class, eo.convertTypeName(List.class.getName() + "[]"));

        try
        {
            eo.convertTypeName("package.DoesNotExist");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            // Ignore message, it's from HiveMind anyway.
        }

        verifyControls();

    }

    public void testAddField()
    {
        IComponentSpecification spec = newSpec();

        MockControl cfc = newControl(ClassFactory.class);
        ClassFactory cf = (ClassFactory) cfc.getMock();

        ClassFab fab = (ClassFab) newMock(ClassFab.class);

        cf.newClass("$BaseComponent_97", BaseComponent.class);

        cfc.setReturnValue(fab);

        fab.addField("fred", String.class);

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        eo.addField("fred", String.class);

        verifyControls();
    }

    public void testAddInjectedField()
    {
        IComponentSpecification spec = newSpec();

        MockControl cfc = newControl(ClassFactory.class);
        ClassFactory cf = (ClassFactory) cfc.getMock();

        MockControl fabc = newControl(ClassFab.class);
        ClassFab fab = (ClassFab) fabc.getMock();

        cf.newClass("$BaseComponent_97", BaseComponent.class);

        cfc.setReturnValue(fab);

        // String because "FRED_VALUE" is a String

        fab.addField("fred", String.class);

        replayControls();

        EnhancementOperationImpl eo = new EnhancementOperationImpl(new DefaultClassResolver(),
                spec, BaseComponent.class, cf, null);

        assertEquals("fred", eo.addInjectedField("fred", String.class, "FRED_VALUE"));

        verifyControls();

        HashMap map = new HashMap();

        fab.addField("fred_0", Map.class);

        replayControls();

        assertEquals("fred_0", eo.addInjectedField("fred", Map.class, map));

        verifyControls();

        BodyBuilder body = new BodyBuilder();
        body.begin();
        body.addln("fred = $1;");
        body.addln("fred_0 = $2;");
        body.end();

        fab.addConstructor(new Class[]
        { String.class, Map.class }, null, body.toString());
        fabc.setMatcher(new ArrayMatcher());

        replayControls();

        eo.finalizeEnhancedClass();

        verifyControls();
    }

    public void testAddMethod()
    {
        Location l = newLocation();

        Class baseClass = Insert.class;
        MethodSignature sig = new MethodSignature(void.class, "frob", null, null);

        IComponentSpecification spec = newSpec();

        MockControl cfc = newControl(ClassFactory.class);
        ClassFactory cf = (ClassFactory) cfc.getMock();

        MockControl fabc = newControl(ClassFab.class);
        ClassFab fab = (ClassFab) fabc.getMock();

        // We force the uid to 97 in setUp()

        cf.newClass("$Insert_97", baseClass);

        cfc.setReturnValue(fab);

        fab.addMethod(Modifier.PUBLIC, sig, "method body");
        fabc.setReturnValue(null);

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                baseClass, cf, null);

        eo.addMethod(Modifier.PUBLIC, sig, "method body", l);

        verifyControls();
    }

    public void testAddMethodDuplicate()
    {
        Location firstLocation = newLocation();
        Location secondLocation = newLocation();

        Class baseClass = Insert.class;
        MethodSignature sig = new MethodSignature(void.class, "frob", null, null);

        IComponentSpecification spec = newSpec();

        MockControl cfc = newControl(ClassFactory.class);
        ClassFactory cf = (ClassFactory) cfc.getMock();

        MockControl fabc = newControl(ClassFab.class);
        ClassFab fab = (ClassFab) fabc.getMock();

        // We force the uid to 97 in setUp()

        cf.newClass("$Insert_97", baseClass);

        cfc.setReturnValue(fab);

        fab.addMethod(Modifier.PUBLIC, sig, "method body");
        fabc.setReturnValue(null);

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                baseClass, cf, null);

        eo.addMethod(Modifier.PUBLIC, sig, "method body", firstLocation);

        try
        {
            eo.addMethod(Modifier.PUBLIC, sig, "second method body", secondLocation);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "A new implementation of method 'void frob()' conflicts with an existing "
                            + "implementation (at classpath:/org/apache/tapestry/enhance/TestEnhancementOperation, line 1).",
                    ex.getMessage());
            assertSame(secondLocation, ex.getLocation());
        }

        verifyControls();
    }

    public void testGetAccessorMethodName()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory(Fixture.class);

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                Fixture.class, cf, null);

        assertEquals("getStringProperty", eo.getAccessorMethodName("stringProperty"));
        assertEquals("isBooleanProperty", eo.getAccessorMethodName("booleanProperty"));
        assertEquals("getFlagProperty", eo.getAccessorMethodName("flagProperty"));
        assertEquals("getUnknownProperty", eo.getAccessorMethodName("unknownProperty"));

        verifyControls();
    }

    /**
     * On this test, instead of mocking up everything, we actually use the raw implementations to
     * construct a new class; the class gets a class reference passed to it in its constructor.
     */

    public void testGetClassReference() throws Exception
    {
        Location l = newLocation();
        MockControl specControl = newControl(IComponentSpecification.class);

        IComponentSpecification spec = (IComponentSpecification) specControl.getMock();

        spec.getLocation();
        specControl.setReturnValue(l);

        replayControls();

        EnhancementOperationImpl eo = new EnhancementOperationImpl(new DefaultClassResolver(),
                spec, GetClassReferenceFixture.class, new ClassFactoryImpl(), null);

        // This does two things; it creates a new field, and it sets up a new constructor
        // parameter to inject the class value (Map.class) into each new instance.

        String ref = eo.getClassReference(Map.class);
        String ref2 = eo.getClassReference(Map.class);

        eo.addMethod(Modifier.PUBLIC, new MethodSignature(Class.class, "getClassReference", null,
                null), "return " + ref + ";", l);

        ComponentConstructor cc = eo.getConstructor();

        GetClassReferenceFixture f = (GetClassReferenceFixture) cc.newInstance();

        assertSame(Map.class, f.getClassReference());

        verifyControls();

        assertSame(ref, ref2);
    }

    public void testGetArrayClassReference() throws Exception
    {
        MockControl specControl = newControl(IComponentSpecification.class);

        IComponentSpecification spec = (IComponentSpecification) specControl.getMock();

        replayControls();

        EnhancementOperationImpl eo = new EnhancementOperationImpl(new DefaultClassResolver(),
                spec, GetClassReferenceFixture.class, new ClassFactoryImpl(), null);

        String ref = eo.getClassReference(int[].class);

        assertTrue(ref.indexOf('[') < 0);

        verifyControls();
    }

    /**
     * Really a test for {@link org.apache.tapestry.enhance.ComponentConstructorImpl};
     * {@link #testGetClassReference()}tests the success case, just want to fill in the failure.
     */

    public void testComponentConstructorFailure()
    {
        Location l = newLocation();

        ComponentConstructor cc = new ComponentConstructorImpl(BaseComponent.class
                .getConstructors()[0], new Object[]
        { "unexpected" }, "<classfab>", l);

        try
        {
            cc.newInstance();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to instantiate instance of class org.apache.tapestry.BaseComponent");
            assertSame(l, ex.getLocation());
        }
    }

    public void testGetPropertyType()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory();

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                BaseComponent.class, cf, null);

        assertEquals(Map.class, eo.getPropertyType("assets"));
        assertEquals(IPage.class, eo.getPropertyType("page"));

        // Doesn't exist, so returns null

        assertNull(eo.getPropertyType("foosball"));

        verifyControls();
    }

    public void testFindUnclaimedAbstractProperties()
    {
        ClassResolver cr = (ClassResolver) newMock(ClassResolver.class);
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory(UnclaimedAbstractPropertiesFixture.class);

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(cr, spec,
                UnclaimedAbstractPropertiesFixture.class, cf, null);

        List l = eo.findUnclaimedAbstractProperties();

        assertEquals(2, l.size());
        assertEquals(true, l.contains("readOnly"));
        assertEquals(true, l.contains("writeOnly"));

        eo.claimProperty("readOnly");

        l = eo.findUnclaimedAbstractProperties();

        assertEquals(1, l.size());
        assertEquals(true, l.contains("writeOnly"));

        eo.claimProperty("writeOnly");

        l = eo.findUnclaimedAbstractProperties();

        assertEquals(true, l.isEmpty());

        verifyControls();
    }

    public void testGetNewMethod()
    {
        ClassResolver cr = new DefaultClassResolver();
        MockControl specc = newControl(IComponentSpecification.class);
        IComponentSpecification spec = (IComponentSpecification) specc.getMock();

        MockControl cfc = newControl(ClassFactory.class);
        ClassFactory cf = (ClassFactory) cfc.getMock();
        MockControl fabc = newControl(ClassFab.class);
        ClassFab fab = (ClassFab) fabc.getMock();

        fab.addInterface(PageDetachListener.class);

        cf.newClass("$BaseComponent_97", BaseComponent.class);
        cfc.setReturnValue(fab);

        replayControls();

        EnhancementOperationImpl eo = new EnhancementOperationImpl(cr, spec, BaseComponent.class,
                cf, null);

        MethodSignature sig = EnhanceUtils.PAGE_DETACHED_SIGNATURE;

        eo.extendMethodImplementation(PageDetachListener.class, sig, "some-code();");

        verifyControls();

        replayControls();

        // Check that repeated calls do not
        // keep adding methods.

        eo.extendMethodImplementation(PageDetachListener.class, sig, "more-code();");

        verifyControls();

        fab.addMethod(Modifier.PUBLIC, sig, "{\n  some-code();\n  more-code();\n}\n");
        fabc.setReturnValue(null);

        fab.createClass();
        fabc.setReturnValue(BaseComponent.class);

        spec.getLocation();
        specc.setReturnValue(null);

        replayControls();

        eo.getConstructor();

        verifyControls();
    }

    public void testGetExistingMethod()
    {
        ClassResolver cr = new DefaultClassResolver();
        MockControl specc = newControl(IComponentSpecification.class);
        IComponentSpecification spec = (IComponentSpecification) specc.getMock();

        MockControl cfc = newControl(ClassFactory.class);
        ClassFactory cf = (ClassFactory) cfc.getMock();
        MockControl fabc = newControl(ClassFab.class);
        ClassFab fab = (ClassFab) fabc.getMock();

        cf.newClass("$BaseComponent_97", BaseComponent.class);
        cfc.setReturnValue(fab);

        replayControls();

        EnhancementOperationImpl eo = new EnhancementOperationImpl(cr, spec, BaseComponent.class,
                cf, null);

        MethodSignature sig = EnhanceUtils.FINISH_LOAD_SIGNATURE;

        eo.extendMethodImplementation(IComponent.class, sig, "some-code();");

        verifyControls();

        fab.addMethod(Modifier.PUBLIC, sig, "{\n  super.finishLoad($$);\n  some-code();\n}\n");
        fabc.setReturnValue(null);

        fab.createClass();
        fabc.setReturnValue(BaseComponent.class);

        spec.getLocation();
        specc.setReturnValue(null);

        replayControls();

        eo.getConstructor();

        verifyControls();
    }

    public void testGetExistingProtectedMethod()
    {
        ClassResolver cr = new DefaultClassResolver();
        MockControl specc = newControl(IComponentSpecification.class);
        IComponentSpecification spec = (IComponentSpecification) specc.getMock();

        MockControl cfc = newControl(ClassFactory.class);
        ClassFactory cf = (ClassFactory) cfc.getMock();
        MockControl fabc = newControl(ClassFab.class);
        ClassFab fab = (ClassFab) fabc.getMock();

        cf.newClass("$BaseComponent_97", BaseComponent.class);
        cfc.setReturnValue(fab);

        replayControls();

        EnhancementOperationImpl eo = new EnhancementOperationImpl(cr, spec, BaseComponent.class,
                cf, null);

        // A protected method
        MethodSignature sig = EnhanceUtils.CLEANUP_AFTER_RENDER_SIGNATURE;

        eo.extendMethodImplementation(IComponent.class, sig, "some-code();");

        verifyControls();

        fab.addMethod(
                Modifier.PUBLIC,
                sig,
                "{\n  super.cleanupAfterRender($$);\n  some-code();\n}\n");
        fabc.setReturnValue(null);

        fab.createClass();
        fabc.setReturnValue(BaseComponent.class);

        spec.getLocation();
        specc.setReturnValue(null);

        replayControls();

        eo.getConstructor();

        verifyControls();
    }

    public static abstract class ExistingAbstractMethodFixture extends BaseComponent implements
            PageDetachListener
    {
        //
    }

    public void getExistingAbstractMethod()
    {
        ClassResolver cr = new DefaultClassResolver();
        MockControl specc = newControl(IComponentSpecification.class);
        IComponentSpecification spec = (IComponentSpecification) specc.getMock();

        MockControl cfc = newControl(ClassFactory.class);
        ClassFactory cf = (ClassFactory) cfc.getMock();
        MockControl fabc = newControl(ClassFab.class);
        ClassFab fab = (ClassFab) fabc.getMock();

        cf.newClass("$ExitingAbstractMethodFixture_97", ExistingAbstractMethodFixture.class);
        cfc.setReturnValue(fab);

        replayControls();

        EnhancementOperationImpl eo = new EnhancementOperationImpl(cr, spec,
                ExistingAbstractMethodFixture.class, cf, null);

        MethodSignature sig = EnhanceUtils.PAGE_DETACHED_SIGNATURE;

        eo.extendMethodImplementation(PageDetachListener.class, sig, "some-code();");

        verifyControls();

        fab.addMethod(Modifier.PUBLIC, sig, "{\n  some-code();\n}\n");
        fabc.setReturnValue(null);

        fab.createClass();
        fabc.setReturnValue(BaseComponent.class);

        spec.getLocation();
        specc.setReturnValue(null);

        replayControls();

        eo.getConstructor();

        verifyControls();
    }

    /**
     * This seems to pass on the command line, but fail inside Eclipse. I think Eclipse's Java
     * compiler works a little different from Java's ... in this example (TODO: Create test fixture
     * classes for this test) the getTarget() method doesn't show up as a declared public method
     * when Eclipse compiles the code, but does when JDK compiles the code.
     */
    public void testPropertyInheritedFromInterface()
    {
        IComponentSpecification spec = newSpec();
        ClassFactory cf = newClassFactory(ServiceLink.class);

        replayControls();

        EnhancementOperation eo = new EnhancementOperationImpl(new DefaultClassResolver(), spec,
                ServiceLink.class, cf, null);

        assertEquals(String.class, eo.getPropertyType("target"));

        verifyControls();
    }

    public void testConstructorFailure()
    {
        IComponentSpecification spec = newSpec();

        MockControl control = newControl(ClassFab.class);
        ClassFab classFab = (ClassFab) control.getMock();

        Throwable t = new RuntimeException("Inconceivable!");

        classFab.createClass();
        control.setThrowable(t);

        ClassFactory cf = newClassFactory(ServiceLink.class, classFab);

        replayControls();

        EnhancementOperationImpl eo = new EnhancementOperationImpl(new DefaultClassResolver(),
                spec, ServiceLink.class, cf, null);

        try
        {
            eo.getConstructor();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Failure enhancing class org.apache.tapestry.link.ServiceLink: Inconceivable!",
                    ex.getMessage());
            assertSame(classFab, ex.getComponent());
            assertSame(t, ex.getRootCause());
        }

        verifyControls();
    }
}