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

package org.apache.tapestry.form.translator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.TapestryUtils;
import org.apache.tapestry.form.FormComponentContributorContext;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationStrings;

/**
 * A {@link java.text.DecimalFormat}-based {@link Translator} implementation.
 * 
 * @author Paul Ferraro
 * @since 4.0
 */
public class NumberTranslator extends FormatTranslator
{
    private boolean _omitZero = true;

    public NumberTranslator()
    {
    }

    protected String formatObject(IFormComponent field, Locale locale, Object object)
    {
        Number number = (Number) object;

        if (_omitZero)
        {
            if (number.doubleValue() == 0)

                return "";
        }

        return super.formatObject(field, locale, object);
    }

    // Needed until HIVEMIND-134 fix is available
    public NumberTranslator(String initializer)
    {
        PropertyUtils.configureProperties(this, initializer);
    }

    /**
     * @see org.apache.tapestry.form.AbstractFormComponentContributor#defaultScript()
     */
    protected String defaultScript()
    {
        return "/org/apache/tapestry/form/translator/NumberTranslator.js";
    }

    /**
     * @see org.apache.tapestry.form.translator.FormatTranslator#defaultPattern()
     */
    protected String defaultPattern()
    {
        return "#";
    }

    /**
     * @see org.apache.tapestry.form.translator.FormatTranslator#getFormat(java.util.Locale)
     */
    protected Format getFormat(Locale locale)
    {
        return getDecimalFormat(locale);
    }

    public DecimalFormat getDecimalFormat(Locale locale)
    {
        return new DecimalFormat(getPattern(), new DecimalFormatSymbols(locale));
    }

    /**
     * @see org.apache.tapestry.form.translator.FormatTranslator#getMessageKey()
     */
    protected String getMessageKey()
    {
        return ValidationStrings.INVALID_NUMBER;
    }

    /**
     * @see org.apache.tapestry.form.translator.AbstractTranslator#getMessageParameters(java.util.Locale,
     *      java.lang.String)
     */
    protected Object[] getMessageParameters(Locale locale, String label)
    {
        String pattern = getDecimalFormat(locale).toLocalizedPattern();

        return new Object[]
        { label, pattern };
    }

    /**
     * @see org.apache.tapestry.form.FormComponentContributor#renderContribution(org.apache.tapestry.IMarkupWriter,
     *      org.apache.tapestry.IRequestCycle, FormComponentContributorContext,
     *      org.apache.tapestry.form.IFormComponent)
     */
    public void renderContribution(IMarkupWriter writer, IRequestCycle cycle,
            FormComponentContributorContext context, IFormComponent field)
    {
        super.renderContribution(writer, cycle, context, field);

        String message = TapestryUtils.enquote(buildMessage(context, field, getMessageKey()));

        context.addSubmitHandler("function(event) { Tapestry.validate_number(event, '"
                + field.getClientId() + "', " + message + "); }");
    }

    /**
     * @see org.apache.tapestry.form.translator.FormatTranslator#getConstraint()
     */
    protected ValidationConstraint getConstraint()
    {
        return ValidationConstraint.NUMBER_FORMAT;
    }

    /**
     * If true (which is the default for the property), then values that are 0 are rendered to an
     * empty string, not "0" or "0.00". This is useful in most cases where the field is optional; it
     * allow the field to render blank when no value is present.
     */

    public void setOmitZero(boolean omitZero)
    {
        _omitZero = omitZero;
    }

}
