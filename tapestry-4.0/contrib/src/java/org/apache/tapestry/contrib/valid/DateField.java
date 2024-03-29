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

package org.apache.tapestry.contrib.valid;

import java.text.DateFormat;
import java.util.Date;

import org.apache.tapestry.valid.DateValidator;
import org.apache.tapestry.valid.IValidator;
import org.apache.tapestry.valid.ValidField;

/**
 * Backwards compatible version of the 1.0.7 DateField component. <table border=1>
 * <tr>
 * <td>Parameter</td>
 * <td>Type</td>
 * <td>Read / Write</td>
 * <td>Required</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>date</td>
 * <td>java.util.Date</td>
 * <td>R / W</td>
 * <td>yes</td>
 * <td>&nbsp;</td>
 * <td>The date property to edit.</td>
 * </tr>
 * <tr>
 * <td>required</td>
 * <td>boolean</td>
 * <td>R</td>
 * <td>no</td>
 * <td>no</td>
 * <td>If true, then a value must be entered.</td>
 * </tr>
 * <tr>
 * <td>minimum</td>
 * <td>java.util.Date</td>
 * <td>R</td>
 * <td>no</td>
 * <td>&nbsp;</td>
 * <td>If provided, the date entered must be equal to or later than the provided minimum date.
 * </td>
 * </tr>
 * <tr>
 * <td>maximum</td>
 * <td>java.util.Date</td>
 * <td>R</td>
 * <td>no</td>
 * <td>&nbsp;</td>
 * <td>If provided, the date entered must be less than or equal to the provided maximum date.</td>
 * </tr>
 * <tr>
 * <td>displayName</td>
 * <td>String</td>
 * <td>R</td>
 * <td>yes</td>
 * <td>&nbsp;</td>
 * <td>A textual name for the field that is used when formulating error messages.</td>
 * </tr>
 * <tr>
 * <td>format</td>
 * <td>{@link DateFormat}</td>
 * <td>R</td>
 * <td>no</td>
 * <td>Default format <code>MM/dd/yyyy</code></td>
 * <td>The format used to display and parse dates.</td>
 * </tr>
 * <tr>
 * <td>displayFormat</td>
 * <td>{@link String}</td>
 * <td>R</td>
 * <td>no</td>
 * <td><code>MM/DD/YYYY</code></td>
 * <td>The format string presented to the user if the date entered is in an incorrect format. e.g.
 * the format object throws a ParseException.</td>
 * </tr>
 * </table>
 * <p>
 * Informal parameters are allowed. A body is not allowed.
 * 
 * @author Howard Lewis Ship, Richard Lewis-Shell
 * @since 1.0.8
 * @see ValidField
 */

public abstract class DateField extends ValidField
{
    public abstract Date getDate();

    public abstract void setDate(Date date);

    public abstract Date getMinimum();

    public abstract Date getMaximum();

    public abstract boolean isRequired();

    public abstract DateFormat getFormat();

    public abstract String getDisplayFormat();

    /**
     * Overrides {@link ValidField#getValidator()}to construct a validator on-the-fly.
     */

    public IValidator getValidator()
    {
        DateValidator validator = new DateValidator();

        if (isParameterBound("minimum"))
            validator.setMinimum(getMinimum());

        if (isParameterBound("maximum"))
            validator.setMaximum(getMaximum());

        if (isParameterBound("required"))
            validator.setRequired(isRequired());

        if (isParameterBound("format"))
            validator.setFormat(getFormat());

        if (isParameterBound("displayFormat"))
            validator.setDisplayFormat(getDisplayFormat());

        return validator;
    }

    /**
     * @see org.apache.tapestry.valid.ValidField#getValue()
     */
    public Object getValue()
    {
        return getDate();
    }

    /**
     * @see org.apache.tapestry.valid.ValidField#setValue(java.lang.Object)
     */
    public void setValue(Object value)
    {
        setDate((Date) value);
    }

}