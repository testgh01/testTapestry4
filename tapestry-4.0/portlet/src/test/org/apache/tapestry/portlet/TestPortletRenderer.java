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

package org.apache.tapestry.portlet;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import org.apache.hivemind.test.AggregateArgumentsMatcher;
import org.apache.hivemind.test.ArgumentMatcher;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.test.RegexpMatcher;
import org.apache.hivemind.test.TypeMatcher;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.NestedMarkupWriter;
import org.apache.tapestry.asset.AssetFactory;
import org.apache.tapestry.markup.MarkupWriterSource;
import org.apache.tapestry.util.ContentType;
import org.apache.tapestry.util.PageRenderSupportImpl;
import org.apache.tapestry.web.WebResponse;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.tapestry.portlet.PortletRendererImpl}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class TestPortletRenderer extends HiveMindTestCase
{
    private NestedMarkupWriter newNestedWriter()
    {
        return (NestedMarkupWriter) newMock(NestedMarkupWriter.class);
    }

    private PrintWriter newPrintWriter()
    {
        return new PrintWriter(new CharArrayWriter());
    }

    private AssetFactory newAssetFactory()
    {
        return (AssetFactory) newMock(AssetFactory.class);
    }

    private WebResponse newWebResponse(ContentType contentType, PrintWriter writer)
            throws Exception
    {
        MockControl control = newControl(WebResponse.class);
        WebResponse response = (WebResponse) control.getMock();

        response.getPrintWriter(contentType);
        control.setReturnValue(writer);

        response.getNamespace();
        control.setReturnValue("NAMESPACE");

        return response;
    }

    private MarkupWriterSource newSource(PrintWriter printWriter, ContentType contentType,
            IMarkupWriter writer)
    {
        MockControl control = newControl(MarkupWriterSource.class);
        MarkupWriterSource source = (MarkupWriterSource) control.getMock();

        source.newMarkupWriter(printWriter, contentType);
        control.setReturnValue(writer);

        return source;
    }

    private IPage newPage(ContentType contentType)
    {
        MockControl control = newControl(IPage.class);
        IPage page = (IPage) control.getMock();

        page.getResponseContentType();
        control.setReturnValue(contentType);

        page.getPageName();
        control.setReturnValue("ZePage");

        return page;
    }

    private IRequestCycle newCycle(String pageName, IPage page)
    {
        MockControl control = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) control.getMock();

        cycle.activate(pageName);

        cycle.getPage();
        control.setReturnValue(page);

        cycle.getAttribute("org.apache.tapestry.PageRenderSupport");
        control.setReturnValue(null);

        // We can check that an instance of PageRenderSupport is passed in, but
        // we can't (easily) check thta it's configured the way we want.
        cycle.setAttribute("org.apache.tapestry.PageRenderSupport", new PageRenderSupportImpl(
                newAssetFactory(), "", null));
        control.setMatcher(new AggregateArgumentsMatcher(new ArgumentMatcher[]
        { null, new TypeMatcher() }));

        return cycle;
    }

    public void testSuccess() throws Exception
    {
        ContentType ct = new ContentType("text/html");
        PrintWriter pw = newPrintWriter();
        WebResponse response = newWebResponse(ct, pw);
        IMarkupWriter nested = newNestedWriter();

        MockControl control = newControl(IMarkupWriter.class);
        IMarkupWriter writer = (IMarkupWriter) control.getMock();

        writer.getNestedWriter();
        control.setReturnValue(nested);

        MarkupWriterSource source = newSource(pw, ct, writer);
        IPage page = newPage(ct);
        AssetFactory assetFactory = newAssetFactory();

        IRequestCycle cycle = newCycle("ZePage", page);

        cycle.renderPage(nested);

        writer.comment("BEGIN Tapestry Portlet appId NAMESPACE");
        writer.comment("Page: ZePage");

        writer.comment("Generated:.*");
        control.setMatcher(new RegexpMatcher());

        writer.comment("Framework version:.*");

        nested.close();

        writer.comment("END Tapestry Portlet appId NAMESPACE");

        writer.close();

        replayControls();

        PortletRendererImpl r = new PortletRendererImpl();
        r.setMarkupWriterSource(source);
        r.setResponse(response);
        r.setAssetFactory(assetFactory);
        r.setApplicationId("appId");

        r.renderPage(cycle, "ZePage");

        verifyControls();
    }

    // TODO: Tests that prove the RenderPageSupport is working properly.
}