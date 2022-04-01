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

package org.apache.tapestry.valid;

import java.io.Serializable;
import java.util.List;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IFormComponent;

/**
 * Interface used to track validation errors in forms and
 * {@link IFormComponent form element component}s (including
 * {@link org.apache.tapestry.form.AbstractTextField}&nbsp;and its subclasses).
 * <p>
 * In addition, controls how fields that are in error are presented (they can be <em>decorated</em>
 * in various ways by the delegate; the default implementation adds two red asterisks to the right
 * of the field).
 * <p>
 * Each {@link org.apache.tapestry.form.Form}&nbsp;must have its own validation delegate instance.
 * <p>
 * Starting with release 1.0.8, this interface was extensively revised (in a non-backwards
 * compatible way) to move the tracking of errors and invalid values (during a request cycle) to the
 * delegate. It has evolved from a largely stateless conduit for error messages into a very stateful
 * tracker of field state.
 * <p>
 * Starting with release 1.0.9, this interface was <em>again</em> reworked, to allow tracking of
 * errors in {@link IFormComponent form components}, and to allow unassociated errors to be
 * tracked. Unassociated errors are "global", they don't apply to any particular field.
 * <p>
 * <b>Fields vs. Form Element Components </b> <br>
 * For most simple forms, these terms are pretty much synonymous. Your form will render normally,
 * and each form element component will render only once. Some of your form components will be
 * {@link ValidField}&nbsp;components and handle most of their validation internally (with the help
 * of {@link IValidator}&nbsp;objects). In addition, your form listener may do additional
 * validation and notify the validation delegate of additional errors, some of which are associated
 * with a particular field, some of which are unassociated with any particular field.
 * <p>
 * But what happens if you use a {@link org.apache.tapestry.components.Foreach}&nbsp;or
 * {@link org.apache.tapestry.form.ListEdit}&nbsp;inside your form? Some of your components will
 * render multiple times. In this case you will have multiple <em>fields</em>. Each field will
 * have a unique field name (the
 * {@link org.apache.tapestry.form.FormSupport#getElementId(IFormComponent) element id}, which you
 * can see this in the generated HTML). It is this field name that the delegate keys off of, which
 * means that some fields generated by a component may have errors and some may not, it all works
 * fine (with one exception).
 * <p>
 * <b>The Exception </b> <br>
 * The problem is that a component doesn't know its field name until its <code>render()</code>
 * method is invoked (at which point, it allocates a unique field name from the
 * {@link org.apache.tapestry.IForm#getElementId(org.apache.tapestry.form.IFormComponent)}. This is
 * not a problem for the field or its {@link IValidator}, but screws things up for the
 * {@link FieldLabel}.
 * <p>
 * Typically, the label is rendered <em>before</em> the corresponding form component. Form
 * components leave their last assigned field name in their
 * {@link IFormComponent#getName() name property}. So if the form component is in any kind of loop,
 * the {@link FieldLabel}will key its name, {@link IFormComponent#getDisplayName() display name}
 * and error status off of its last renderred value. So the moral of the story is don't use
 * {@link FieldLabel}in this situation.
 * 
 * @author Howard Lewis Ship
 */

public interface IValidationDelegate extends Serializable
{
    /**
     * Invoked before other methods to configure the delegate for the given form component. Sets the
     * current field based on the {@link IFormComponent#getName() name} of the form component.
     * <p>
     * The caller should invoke this with a parameter of null to record unassociated global errors
     * (errors not associated with any particular field).
     * 
     * @since 1.0.8
     */

    public void setFormComponent(IFormComponent component);

    /**
     * Returns true if the current field is in error (that is, had bad input submitted by the end
     * user).
     * 
     * @since 1.0.8
     */

    public boolean isInError();

    /**
     * Returns the string submitted by the client as the value for the current field.
     * 
     * @since 1.0.8
     */

    public String getFieldInputValue();

    /**
     * Returns a {@link List} of {@link IFieldTracking}, in default order (the order in which
     * fields are renderred). A caller should not change the values (the List is immutable). May
     * return null if no fields are in error.
     * 
     * @since 1.0.8
     */

    public List getFieldTracking();

    /**
     * Resets any tracking information for the current field. This will clear the field's inError
     * flag, and set its error message and invalid input value to null.
     * 
     * @since 1.0.8
     */

    public void reset();

    /**
     * Clears all tracking information.
     * 
     * @since 1.0.10
     */

    public void clear();

    /**
     * Clears all errors, but maintains user input. This is useful when a form has been submitted
     * for a semantic other than "process this data". A common example of this is a dependent drop
     * down list; selecting an option in one drop down list forces a refresh submit of the form, to
     * repopulate the options in a second, dependent drop down list.
     * <p>
     * In these cases, the user input provided in the request is maintained, but any errors should
     * be cleared out (to prevent unwanted error messages and decorations).
     * 
     * @since 3.0.1
     */

    public void clearErrors();

    /**
     * Records the user's input for the current form component. Input should be recorded even if
     * there isn't an explicit error, since later form-wide validations may discover an error in the
     * field.
     * 
     * @since 3.0
     */

    public void recordFieldInputValue(String input);

    /**
     * The error notification method, invoked during the rewind phase (that is, while HTTP
     * parameters are being extracted from the request and assigned to various object properties).
     * <p>
     * Typically, the delegate simply invokes {@link #record(String, ValidationConstraint)}or
     * {@link #record(IRender, ValidationConstraint)}, but special delegates may override this
     * behavior to provide (in some cases) different error messages or more complicated error
     * renderers.
     */

    public void record(ValidatorException ex);

    /**
     * Records an error in the current field, or an unassociated error if there is no current field.
     * 
     * @param message
     *            message to display (@see RenderString}
     * @param constraint
     *            the constraint that was violated, or null if not known
     * @since 1.0.9
     */

    public void record(String message, ValidationConstraint constraint);

    /**
     * Convienience for recording a standard string messages against a field.
     * 
     * @param field
     *            the field to record the error message against, or null to record an unassociated
     *            error
     * @param message
     *            the error message to record
     * @since 4.0
     */

    public void record(IFormComponent field, String message);

    /**
     * Records an error in the current component, or an unassociated error. The maximum flexibility
     * recorder.
     * 
     * @param errorRenderer
     *            object that will render the error message (@see RenderString}
     * @param constraint
     *            the constraint that was violated, or null if not known
     */

    public void record(IRender errorRenderer, ValidationConstraint constraint);

    /**
     * Invoked before the field is rendered. If the field is in error, the delegate may decorate the
     * field in some way (to highlight its error state).
     * 
     * @param writer
     *            the writer to which output should be sent
     * @param cycle
     *            the active request cycle
     * @param component
     *            the component being decorated
     * @param validator
     *            the validator for the component, or null if the component does have (or doesn't
     *            support) a validator
     */

    public void writePrefix(IMarkupWriter writer, IRequestCycle cycle, IFormComponent component,
            IValidator validator);

    /**
     * Invoked just before the &lt;input&gt; element is closed. The delegate can write additional
     * attributes. This is often used to set the CSS class of the field so that it can be displayed
     * differently, if in error (or required). *
     * 
     * @param writer
     *            the writer to which output should be sent
     * @param cycle
     *            the active request cycle
     * @param component
     *            the component being decorated
     * @param validator
     *            the validator for the component, or null if the component does have (or doesn't
     *            support) a validator
     * @since 1.0.5
     */

    public void writeAttributes(IMarkupWriter writer, IRequestCycle cycle,
            IFormComponent component, IValidator validator);

    /**
     * Invoked after the form component is rendered, so that the delegate may decorate the form
     * component (if it is in error). *
     * 
     * @param writer
     *            the writer to which output should be sent
     * @param cycle
     *            the active request cycle
     * @param component
     *            the component being decorated
     * @param validator
     *            the validator for the component, or null if the component does have (or doesn't
     *            support) a validator
     */

    public void writeSuffix(IMarkupWriter writer, IRequestCycle cycle, IFormComponent component,
            IValidator validator);

    /**
     * Invoked by a {@link FieldLabel} just before writing the name of the form component.
     */

    public void writeLabelPrefix(IFormComponent component, IMarkupWriter writer, IRequestCycle cycle);

    /**
     * Invoked just before the &lt;label&gt; element is closed. The delegate can write additional
     * attributes. This is often used to set the CSS class of the label so that it can be displayed
     * differently, if in error (or required). Any attributes written here will be overriden by any
     * informal parameters specified in the {@link FieldLabel} implementation.
     * 
     * @param writer
     *            the writer to which output should be sent
     * @param cycle
     *            the active request cycle
     * @param component
     *            the component field that label decorates
     * @since 4.0.1
     */

    public void writeLabelAttributes(IMarkupWriter writer, IRequestCycle cycle,
    		IFormComponent component);
    
    /**
     * Invoked by a {@link FieldLabel} just after writing the name of the form component.
     */

    public void writeLabelSuffix(IFormComponent component, IMarkupWriter writer, IRequestCycle cycle);

    /**
     * Returns true if any form component has errors.
     */

    public boolean getHasErrors();

    /**
     * Returns the {@link IFieldTracking}&nbsp;for the current component, if any. Useful when
     * displaying error messages for individual fields.
     * 
     * @since 3.0.2
     */
    public IFieldTracking getCurrentFieldTracking();

    /**
     * Returns a list of {@link org.apache.tapestry.IRender} objects, each of which will render an
     * error message for a field tracked by the delegate, plus any unassociated errors (for which no
     * specific field is identified). These objects can be rendered or converted to a string (via
     * toString()).
     * 
     * @return non-empty List of {@link org.apache.tapestry.IRender}.
     */

    public List getErrorRenderers();

    /**
     * Registers a field for automatic focus. The goal is for the first field that is in error to
     * get focus; failing that, the first required field; failing that, any field.
     * 
     * @param field
     *            the field requesting focus
     * @param priority
     *            a priority level used to determine whether the registered field becomes the focus
     *            field. Constants for this purpose are defined in {@link ValidationConstants}.
     * @since 4.0
     */

    public void registerForFocus(IFormComponent field, int priority);

    /**
     * Returns the field to focus upon, based on prior calls to
     * {@link #registerForFocus(IFormComponent, int)}.
     * 
     * @return the field name, or null if no field should receive focus.
     * @since 4.0
     */
    public String getFocusField();

}