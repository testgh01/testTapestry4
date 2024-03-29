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

import java.util.Locale;

import org.apache.tapestry.IPage;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.junit.TapestryTestCase;
import org.easymock.MockControl;

/**
 * Base class for tests of different {@link org.apache.tapestry.valid.IValidator}implementations.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public abstract class BaseValidatorTestCase extends TapestryTestCase
{

    protected IFormComponent newField()
    {
        return (IFormComponent) newMock(IFormComponent.class);
    }

    protected IFormComponent newField(String displayName)
    {
        return newField(displayName, 1);
    }

    protected IFormComponent newField(String displayName, int count)
    {
        return newField(displayName, Locale.ENGLISH, count);
    }

    protected IFormComponent newField(String displayName, Locale locale)
    {
        return newField(displayName, locale, 1);
    }

    protected IFormComponent newField(String displayName, Locale locale, int count)
    {
        IPage page = newPage(locale, count);

        MockControl control = newControl(IFormComponent.class);
        IFormComponent component = (IFormComponent) control.getMock();

        for (int i = 0; i < count; i++)
        {
            component.getPage();
            control.setReturnValue(page);

            component.getDisplayName();
            control.setReturnValue(displayName);
        }

        return component;
    }

    protected IPage newPage(Locale locale)
    {
        return newPage(locale, 1);
    }

    protected IPage newPage(Locale locale, int count)
    {
        MockControl pagec = newControl(IPage.class);
        IPage page = (IPage) pagec.getMock();

        page.getLocale();
        pagec.setReturnValue(locale, count);

        return page;
    }

}