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

package org.apache.tapestry.form;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IForm;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.listener.ListenerInvokerTerminator;
import org.apache.tapestry.test.Creator;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidationConstants;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.tapestry.form.Submit}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class TestSubmit extends BaseFormComponentTestCase
{
    public void testPrerendered()
    {
        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class);

        MockControl formc = newControl(IForm.class);
        IForm form = (IForm) formc.getMock();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();
        IMarkupWriter writer = newWriter();

        trainGetForm(cycle, form);

        trainWasPrerendered(form, writer, submit, true);

        replayControls();

        submit.renderComponent(writer, cycle);

        verifyControls();
    }

    public void testRender()
    {
        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class);

        MockControl delegatec = newControl(IValidationDelegate.class);
        IValidationDelegate delegate = (IValidationDelegate) delegatec.getMock();
        MockControl formc = newControl(IForm.class);
        IForm form = (IForm) formc.getMock();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();
        IMarkupWriter writer = newWriter();

        trainGetForm(cycle, form);

        trainWasPrerendered(form, writer, submit, false);

        trainGetDelegate(form, delegate);

        delegate.setFormComponent(submit);
        delegatec.setVoidCallable();

        trainGetElementId(form, submit, "fred");

        trainIsRewinding(form, false);

        trainIsRewinding(cycle, false);

        writer.beginEmpty("input");
        writer.attribute("type", "submit");
        writer.attribute("name", "fred");
        writer.closeTag();

        trainIsInError(delegate, false);

        delegate.registerForFocus(submit, ValidationConstants.NORMAL_FIELD);

        replayControls();

        submit.renderComponent(writer, cycle);

        verifyControls();
    }

    public void testRenderDisabled()
    {
        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class, new Object[]
        { "disabled", Boolean.TRUE });

        MockControl delegatec = newControl(IValidationDelegate.class);
        IValidationDelegate delegate = (IValidationDelegate) delegatec.getMock();
        MockControl formc = newControl(IForm.class);
        IForm form = (IForm) formc.getMock();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();
        IMarkupWriter writer = newWriter();

        trainGetForm(cycle, form);

        trainWasPrerendered(form, writer, submit, false);

        trainGetDelegate(form, delegate);

        delegate.setFormComponent(submit);
        delegatec.setVoidCallable();

        trainGetElementId(form, submit, "fred");

        trainIsRewinding(form, false);

        trainIsRewinding(cycle, false);

        writer.beginEmpty("input");
        writer.attribute("type", "submit");
        writer.attribute("name", "fred");
        writer.attribute("disabled", "disabled");
        writer.closeTag();

        replayControls();

        submit.renderComponent(writer, cycle);

        verifyControls();
    }

    public void testRenderWithLabel()
    {
        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class, new Object[]
        { "label", "flintstone" });

        MockControl delegatec = newControl(IValidationDelegate.class);
        IValidationDelegate delegate = (IValidationDelegate) delegatec.getMock();
        MockControl formc = newControl(IForm.class);
        IForm form = (IForm) formc.getMock();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();
        IMarkupWriter writer = newWriter();

        trainGetForm(cycle, form);

        trainWasPrerendered(form, writer, submit, false);

        trainGetDelegate(form, delegate);

        delegate.setFormComponent(submit);
        delegatec.setVoidCallable();

        trainGetElementId(form, submit, "fred");

        trainIsRewinding(form, false);

        trainIsRewinding(cycle, false);

        writer.beginEmpty("input");
        writer.attribute("type", "submit");
        writer.attribute("name", "fred");
        writer.attribute("value", "flintstone");
        writer.closeTag();

        trainIsInError(delegate, false);

        delegate.registerForFocus(submit, ValidationConstants.NORMAL_FIELD);

        replayControls();

        submit.renderComponent(writer, cycle);

        verifyControls();
    }

    public void testRewindingDisabled()
    {
        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class, new Object[]
        { "disabled", Boolean.TRUE });

        IValidationDelegate delegate = newDelegate();
        MockControl formc = newControl(IForm.class);
        IForm form = (IForm) formc.getMock();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();
        IMarkupWriter writer = newWriter();

        trainGetForm(cycle, form);

        trainWasPrerendered(form, writer, submit, false);

        form.getDelegate();
        formc.setReturnValue(delegate);

        delegate.setFormComponent(submit);

        trainGetElementId(form, submit, "fred");

        trainIsRewinding(form, true);

        replayControls();

        submit.renderComponent(writer, cycle);

        verifyControls();
    }

    public void testRewindNotForm()
    {
        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class);

        IValidationDelegate delegate = newDelegate();
        MockControl formc = newControl(IForm.class);
        IForm form = (IForm) formc.getMock();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();
        IMarkupWriter writer = newWriter();

        trainGetForm(cycle, form);

        trainWasPrerendered(form, writer, submit, false);

        form.getDelegate();
        formc.setReturnValue(delegate);

        delegate.setFormComponent(submit);

        trainGetElementId(form, submit, "fred");

        trainIsRewinding(form, false);
        trainIsRewinding(cycle, true);

        replayControls();

        submit.renderComponent(writer, cycle);

        verifyControls();
    }

    public void testRewindNotTrigger()
    {
        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class);

        IValidationDelegate delegate = newDelegate();
        MockControl formc = newControl(IForm.class);
        IForm form = (IForm) formc.getMock();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();
        IMarkupWriter writer = newWriter();

        trainGetForm(cycle, form);

        trainWasPrerendered(form, writer, submit, false);

        form.getDelegate();
        formc.setReturnValue(delegate);

        delegate.setFormComponent(submit);

        trainGetElementId(form, submit, "fred");

        trainIsRewinding(form, true);

        trainGetParameter(cycle, "fred", null);

        replayControls();

        submit.renderComponent(writer, cycle);

        verifyControls();
    }

    public void testRewindTriggered()
    {
        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class, new Object[]
        { "tag", "clicked" });

        IBinding binding = newBinding();
        submit.setBinding("selected", binding);

        IValidationDelegate delegate = newDelegate();
        MockControl formc = newControl(IForm.class);
        IForm form = (IForm) formc.getMock();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();
        IMarkupWriter writer = newWriter();

        trainGetForm(cycle, form);

        trainWasPrerendered(form, writer, submit, false);

        form.getDelegate();
        formc.setReturnValue(delegate);

        delegate.setFormComponent(submit);

        trainGetElementId(form, submit, "fred");

        trainIsRewinding(form, true);

        trainGetParameter(cycle, "fred", "flintstone");

        replayControls();

        submit.renderComponent(writer, cycle);

        assertEquals("clicked", PropertyUtils.read(submit, "selected"));

        verifyControls();
    }

    public void testTriggerWithListener()
    {
        IActionListener listener = newListener();
        IForm form = newForm();
        IRequestCycle cycle = newCycle();

        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class, new Object[]
        { "listener", listener, "listenerInvoker", new ListenerInvokerTerminator() });

        listener.actionTriggered(submit, cycle);

        replayControls();

        submit.handleClick(cycle, form);

        verifyControls();
    }

    public void testTriggerWithAction()
    {
        IActionListener action = newListener();
        MockForm form = new MockForm();
        IRequestCycle cycle = newCycle();

        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class, new Object[]
        { "action", action, "listenerInvoker",
                new ListenerInvokerTerminator() });

        replayControls();

        submit.handleClick(cycle, form);

        verifyControls();

        action.actionTriggered(submit, cycle);

        replayControls();

        form.runDeferred();

        verifyControls();
    }

    public void testTriggerWithActionAndSingleParameter()
    {
        IActionListener action = newListener();
        MockForm form = new MockForm();
        MockControl cycleControl = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cycleControl.getMock();

        Object parameter = new Object();
        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class, new Object[]
        { "action", action, "parameters", parameter, "listenerInvoker",
                new ListenerInvokerTerminator() });

        cycle.setListenerParameters(new Object[]
        { parameter });
        cycleControl.setMatcher(MockControl.ARRAY_MATCHER);

        replayControls();

        submit.handleClick(cycle, form);

        verifyControls();

        action.actionTriggered(submit, cycle);

        replayControls();

        form.runDeferred();

        verifyControls();
    }

    public void testTriggerWithDActionAndMultipleParameters()
    {
        IActionListener action = newListener();
        MockForm form = new MockForm();
        MockControl cycleControl = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cycleControl.getMock();

        Collection parameters = new LinkedList();
        parameters.add("p1");
        parameters.add("p2");

        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class, new Object[]
        { "action", action, "parameters", parameters, "listenerInvoker",
                new ListenerInvokerTerminator() });

        cycle.setListenerParameters(new Object[]
        { "p1", "p2" });
        cycleControl.setMatcher(MockControl.ARRAY_MATCHER);

        replayControls();

        submit.handleClick(cycle, form);

        verifyControls();

        action.actionTriggered(submit, cycle);

        replayControls();

        form.runDeferred();

        verifyControls();
    }
    
    public void testTriggerWithListenerAndAction()
    {
        IActionListener listener = newListener();
        IActionListener action = newListener();
        
        MockForm form = new MockForm();
        IRequestCycle cycle = newCycle();

        Creator creator = new Creator();
        Submit submit = (Submit) creator.newInstance(Submit.class, new Object[]
        { "listener", listener, "action", action, "listenerInvoker",
                new ListenerInvokerTerminator() });

        listener.actionTriggered(submit, cycle);
        
        replayControls();

        submit.handleClick(cycle, form);

        verifyControls();

        action.actionTriggered(submit, cycle);

        replayControls();

        form.runDeferred();

        verifyControls();
    }
    
}
