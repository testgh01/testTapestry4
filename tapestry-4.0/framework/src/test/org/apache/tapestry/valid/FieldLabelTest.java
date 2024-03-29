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

package org.apache.tapestry.valid;

import org.apache.hivemind.Location;
import org.apache.tapestry.BindingException;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IForm;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.BaseFormComponentTestCase;
import org.apache.tapestry.form.IFormComponent;
import org.easymock.MockControl;

/**
 * Tests for the {@link org.apache.tapestry.valid.FieldLabel} component.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class FieldLabelTest extends BaseFormComponentTestCase
{
    private IForm newForm(IValidationDelegate delegate)
    {
        IForm form = (IForm) newMock(IForm.class);

        trainGetDelegate(delegate, form);

        return form;
    }

    private void trainGetDelegate(IValidationDelegate delegate, IForm form)
    {
        form.getDelegate();
        setReturnValue(form, delegate);
    }

    private IPage newFred()
    {
        IPage page = (IPage) newMock(IPage.class);

        trainGetPageName(page, "Fred");

        trainGetIdPath(page, null);

        return page;
    }

    private void trainGetIdPath(IPage page, String idPath)
    {
        page.getIdPath();
        setReturnValue(page, idPath);
    }

    private IFormComponent newField(String displayName, String clientId)
    {
        IFormComponent field = (IFormComponent) newMock(IFormComponent.class);

        trainGetDisplayName(field, displayName);

        trainGetClientId(clientId, field);

        return field;
    }

    private void trainGetClientId(String clientId, IFormComponent field)
    {
        field.getClientId();
        setReturnValue(field, clientId);
    }

    private void trainGetDisplayName(IFormComponent field, String displayName)
    {
        field.getDisplayName();
        setReturnValue(field, displayName);
    }

    public void testRewinding()
    {
        Location l = newLocation();
        IMarkupWriter writer = newWriter();
        IRequestCycle cycle = newCycle();
        IForm form = newForm();
        IFormComponent field = newField();

        trainGetForm(cycle, form);

        form.prerenderField(writer, field, l);

        trainIsRewinding(cycle, true);

        replayControls();

        FieldLabel fl = (FieldLabel) newInstance(FieldLabel.class, new Object[]
        { "field", field, "location", l, "prerender", true });

        fl.render(writer, cycle);

        verifyControls();
    }

    public void testNoField()
    {
        IValidationDelegate delegate = new MockDelegate();
        IForm form = newForm(delegate);
        IMarkupWriter writer = newBufferWriter();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();

        trainGetForm(cycle, form);

        trainIsRewinding(cycle, false);

        replayControls();

        FieldLabel fl = (FieldLabel) newInstance(FieldLabel.class, new Object[]
        { "displayName", "FredFlintstone" });

        fl.render(writer, cycle);

        assertBuffer("{LABEL-PREFIX}<label>FredFlintstone</label>{LABEL-SUFFIX}");

        verifyControls();
    }

    public void testNoFieldRaw()
    {
        IValidationDelegate delegate = new MockDelegate();
        IForm form = newForm(delegate);
        IMarkupWriter writer = newBufferWriter();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();

        trainGetForm(cycle, form);

        trainIsRewinding(cycle, false);

        replayControls();

        FieldLabel fl = (FieldLabel) newInstance(FieldLabel.class, new Object[]
        { "displayName", "<b>FredFlintstone</b>", "raw", Boolean.TRUE });

        fl.render(writer, cycle);

        assertBuffer("{LABEL-PREFIX}<label><b>FredFlintstone</b></label>{LABEL-SUFFIX}");

        verifyControls();
    }

    public void testNoFieldOrDisplayName()
    {
        IForm form = newForm();
        IMarkupWriter writer = newBufferWriter();
        MockControl cyclec = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) cyclec.getMock();

        Location l = newLocation();
        IBinding binding = newBinding(l);
        IPage page = newFred();

        trainGetForm(cycle, form);

        trainIsRewinding(cycle, false);

        replayControls();

        FieldLabel fl = (FieldLabel) newInstance(FieldLabel.class, new Object[]
        { "id", "label", "page", page, "container", page });

        fl.setBinding("field", binding);

        try
        {
            fl.render(writer, cycle);
            unreachable();
        }
        catch (BindingException ex)
        {
            assertEquals(
                    "Value for parameter 'field' in component Fred/label is null, and a non-null value is required.",
                    ex.getMessage());
            assertSame(l, ex.getLocation());
            assertSame(binding, ex.getBinding());
        }

        verifyControls();
    }

    public void testDisplayNameFromField()
    {
        IValidationDelegate delegate = new MockDelegate();

        IForm form = newForm();

        IMarkupWriter writer = newBufferWriter();
        IFormComponent field = newField("MyLabel", null);
        Location l = newLocation();

        IRequestCycle cycle = newCycle();

        trainGetForm(cycle, form);

        trainIsRewinding(cycle, false);

        FieldLabel fl = (FieldLabel) newInstance(FieldLabel.class, new Object[]
        { "location", l, "field", field, "prerender", true });

        form.prerenderField(writer, field, l);

        trainGetDelegate(form, delegate);

        replayControls();

        fl.render(writer, cycle);

        assertBuffer("{LABEL-PREFIX}<label>MyLabel</label>{LABEL-SUFFIX}");

        verifyControls();
    }

    public void testPrerenderOff()
    {
        IValidationDelegate delegate = new MockDelegate();

        IForm form = newForm();

        IMarkupWriter writer = newBufferWriter();
        IFormComponent field = newField("MyLabel", null);
        Location l = newLocation();

        IRequestCycle cycle = newCycle();

        trainGetForm(cycle, form);

        trainIsRewinding(cycle, false);

        FieldLabel fl = (FieldLabel) newInstance(FieldLabel.class, new Object[]
        { "location", l, "field", field });

        trainGetDelegate(form, delegate);

        replayControls();

        fl.render(writer, cycle);

        assertBuffer("{LABEL-PREFIX}<label>MyLabel</label>{LABEL-SUFFIX}");

        verifyControls();
    }

    public void testFieldWithClientId()
    {
        IValidationDelegate delegate = new MockDelegate();

        IForm form = newForm();

        IMarkupWriter writer = newBufferWriter();
        IFormComponent field = newField("MyLabel", "clientId");
        Location l = newLocation();

        IRequestCycle cycle = newCycle();

        trainGetForm(cycle, form);

        trainIsRewinding(cycle, false);

        FieldLabel fl = (FieldLabel) newInstance(FieldLabel.class, new Object[]
        { "location", l, "field", field, "prerender", true });

        form.prerenderField(writer, field, l);

        trainGetDelegate(form, delegate);

        replayControls();

        fl.render(writer, cycle);

        assertBuffer("{LABEL-PREFIX}<label for=\"clientId\">MyLabel</label>{LABEL-SUFFIX}");

        verifyControls();
    }

    public void testNoDisplayNameInField()
    {
        IForm form = newForm();
        IMarkupWriter writer = newBufferWriter();
        IFormComponent field = newField();

        IRequestCycle cycle = newCycle();

        trainGetForm(cycle, form);

        trainIsRewinding(cycle, false);

        Location l = newLocation();
        IPage page = newFred();

        FieldLabel fl = (FieldLabel) newInstance(FieldLabel.class, new Object[]
        { "id", "label", "location", l, "field", field, "page", page, "container", page,
                "prerender", true });

        form.prerenderField(writer, field, l);

        trainGetDisplayName(field, null);
        trainGetExtendedId(field, "Fred/field");

        replayControls();

        try
        {
            fl.render(writer, cycle);
            unreachable();
        }
        catch (BindingException ex)
        {
            assertEquals(
                    "Display name for Fred/label was not specified and was not provided by field Fred/field.",
                    ex.getMessage());
        }

        verifyControls();
    }
}