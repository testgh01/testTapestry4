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

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.TapestryUtils;
import org.apache.tapestry.form.FormComponentContributorContext;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.ValidationMessages;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationStrings;
import org.apache.tapestry.valid.ValidatorException;

/**
 * Expects the object to be a number, and checks that the value not smaller than a specified value.
 * 
 * @author Howard Lewis Ship
 * @since 4.0
 */
public class Min extends BaseValidator
{
    private double _min;

    public Min()
    {
    }

    public Min(String initializer)
    {
        super(initializer);
    }

    /**
     * Does comparison based on the {@link Number#doubleValue()}.
     */

    public void validate(IFormComponent field, ValidationMessages messages, Object object)
            throws ValidatorException
    {
        Number value = (Number) object;

        if (_min > value.doubleValue())
            throw new ValidatorException(buildMessage(messages, field),
                    ValidationConstraint.TOO_SMALL);
    }

    private String buildMessage(ValidationMessages messages, IFormComponent field)
    {
        return messages.formatValidationMessage(
                getMessage(),
                ValidationStrings.VALUE_TOO_SMALL,
                new Object[]
                { field.getDisplayName(), new Double(_min) });
    }

    public void renderContribution(IMarkupWriter writer, IRequestCycle cycle,
            FormComponentContributorContext context, IFormComponent field)
    {
        context.includeClasspathScript("/org/apache/tapestry/form/validator/NumberValidator.js");

        String message = buildMessage(context, field);

        StringBuffer buffer = new StringBuffer("function(event) { Tapestry.validate_min_number(event, '");
        buffer.append(field.getClientId());
        buffer.append("', ");
        buffer.append(_min);
        buffer.append(", ");
        buffer.append(TapestryUtils.enquote(message));
        buffer.append("); }");

        context.addSubmitHandler(buffer.toString());
    }

    public void setMin(double min)
    {
        _min = min;
    }

}
