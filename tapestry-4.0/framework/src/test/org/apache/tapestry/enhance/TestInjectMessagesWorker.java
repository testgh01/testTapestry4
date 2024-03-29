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

package org.apache.tapestry.enhance;

import java.lang.reflect.Modifier;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.Messages;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.services.ComponentMessagesSource;
import org.apache.tapestry.spec.IComponentSpecification;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.tapestry.enhance.InjectMessagesWorker}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class TestInjectMessagesWorker extends BaseEnhancementTestCase
{
    private ComponentMessagesSource newSource()
    {
        return (ComponentMessagesSource) newMock(ComponentMessagesSource.class);
    }

    public void testSuccess()
    {
        Location l = newLocation();
        InjectMessagesWorker w = new InjectMessagesWorker();

        IComponentSpecification spec = newSpec(l);
        ComponentMessagesSource source = newSource();

        MockControl control = newControl(EnhancementOperation.class);
        EnhancementOperation op = (EnhancementOperation) control.getMock();

        op.claimReadonlyProperty(w.MESSAGES_PROPERTY);
        op.addInjectedField("_$componentMessagesSource", ComponentMessagesSource.class, source);
        control.setReturnValue("fred");

        BodyBuilder builder = new BodyBuilder();
        builder.begin();
        builder.addln("if (_$messages == null)");
        builder.addln("  _$messages = fred.getMessages(this);");
        builder.addln("return _$messages;");
        builder.end();

        op.addField("_$messages", Messages.class);
        op.addMethod(Modifier.PUBLIC, w.METHOD_SIGNATURE, builder.toString(), l);

        replayControls();

        w.setComponentMessagesSource(source);

        w.performEnhancement(op, spec);

        verifyControls();
    }

    public void testFailure()
    {
        Location l = newLocation();
        InjectMessagesWorker w = new InjectMessagesWorker();

        Throwable ex = new ApplicationRuntimeException(EnhanceMessages
                .claimedProperty(w.MESSAGES_PROPERTY));

        IComponentSpecification spec = newSpec(l);

        MockControl control = newControl(EnhancementOperation.class);
        EnhancementOperation op = (EnhancementOperation) control.getMock();

        op.claimReadonlyProperty(w.MESSAGES_PROPERTY);
        control.setThrowable(ex);

        op.getBaseClass();
        control.setReturnValue(BaseComponent.class);

        ErrorLog log = (ErrorLog) newMock(ErrorLog.class);

        log.error(
                EnhanceMessages.errorAddingProperty(w.MESSAGES_PROPERTY, BaseComponent.class, ex),
                l,
                ex);

        replayControls();

        w.setErrorLog(log);

        w.performEnhancement(op, spec);

        verifyControls();
    }
}