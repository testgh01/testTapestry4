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

package org.apache.tapestry;

import org.apache.hivemind.Resource;

/**
 * Defines methods needed by a {@link org.apache.tapestry.IScript}to execute.
 * 
 * @author Howard Lewis Ship
 * @since 3.0
 * @see org.apache.tapestry.html.Body
 */

public interface IScriptProcessor
{
    /**
     * Adds scripting code to the main body. During the render, multiple scripts may render multiple
     * bodies; all are concatinated together to form a single block. The
     * {@link org.apache.tapestry.html.Body}&nbsp;component will write the body script contents
     * just inside the <code>&lt;body&gt;</code> tag.
     */

    public void addBodyScript(String script);

    /**
     * Adds initialization script. Initialization script is executed once, when the containing page
     * loads. Initialization script content is written only after all HTML content that could be
     * referenced from the script (in effect, just before the <code>&lt/body&gt; tag).
     */
    public void addInitializationScript(String script);

    /**
     * Adds an external script. The processor is expected to ensure that external scripts are only
     * loaded a single time per page.
     */

    public void addExternalScript(Resource resource);

    /**
     * Ensures that the given string is unique. The string is either returned unchanged, or a suffix
     * is appended to ensure uniqueness.
     */

    public String getUniqueString(String baseValue);
}