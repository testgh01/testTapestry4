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
 * Validates that the value, a string, is of a minimum length.
 * 
 * @author Howard Lewis Ship
 * @since 4.0
 */
public class MinLength extends BaseValidator
{
    private int _minLength;

    public MinLength()
    {
    }

    public MinLength(String initializer)
    {
        super(initializer);
    }

    public void setMinLength(int minLength)
    {
        _minLength = minLength;
    }

    public int getMinLength()
    {
        return _minLength;
    }

    public void validate(IFormComponent field, ValidationMessages messages, Object object)
            throws ValidatorException
    {
        String string = (String) object;

        if (string.length() < _minLength)
            throw new ValidatorException(buildMessage(messages, field),
                    ValidationConstraint.MINIMUM_WIDTH);
    }

    protected String buildMessage(ValidationMessages messages, IFormComponent field)
    {
        return messages.formatValidationMessage(
                getMessage(),
                ValidationStrings.VALUE_TOO_SHORT,
                new Object[]
                { new Integer(_minLength), field.getDisplayName() });
    }

    public void renderContribution(IMarkupWriter writer, IRequestCycle cycle,
            FormComponentContributorContext context, IFormComponent field)
    {
        context.includeClasspathScript("/org/apache/tapestry/form/validator/StringValidator.js");

        StringBuffer buffer = new StringBuffer("function(event) { Tapestry.validate_min_length(event, '");
        buffer.append(field.getClientId());
        buffer.append("', ");
        buffer.append(_minLength);
        buffer.append(", ");
        buffer.append(TapestryUtils.enquote(buildMessage(context, field)));
        buffer.append("); }");

        context.addSubmitHandler(buffer.toString());
    }
}