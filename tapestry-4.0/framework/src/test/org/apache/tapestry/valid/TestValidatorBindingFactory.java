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
import org.apache.hivemind.lib.BeanFactory;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.coerce.ValueConverter;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.tapestry.valid.ValidatorBindingFactory}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class TestValidatorBindingFactory extends HiveMindTestCase
{
    public void testFactory()
    {
        IValidator validator = (IValidator) newMock(IValidator.class);
        ValueConverter vc = (ValueConverter) newMock(ValueConverter.class);

        MockControl vbfc = newControl(BeanFactory.class);
        BeanFactory vbf = (BeanFactory) vbfc.getMock();

        vbf.get("foo,bar=baz");
        vbfc.setReturnValue(validator);

        Location l = newLocation();

        replayControls();

        ValidatorBindingFactory factory = new ValidatorBindingFactory();
        factory.setValueConverter(vc);
        factory.setValidatorBeanFactory(vbf);

        IBinding binding = factory.createBinding(null, "validator bean", "foo,bar=baz", l);

        assertSame(validator, binding.getObject());
        assertSame(l, binding.getLocation());

        verifyControls();
    }
}