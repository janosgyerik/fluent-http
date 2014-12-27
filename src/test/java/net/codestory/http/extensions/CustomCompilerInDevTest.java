/**
 * Copyright (C) 2013-2014 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.http.extensions;

import net.codestory.http.compilers.CompilersConfiguration;
import net.codestory.http.misc.Env;
import net.codestory.http.testhelpers.AbstractDevWebServerTest;
import org.junit.Test;

import java.nio.file.Path;

public class CustomCompilerInDevTest extends AbstractDevWebServerTest {
  @Test
  public void custom_compiler() {
    configure(routes -> routes
      .setExtensions(new Extensions() {
        @Override
        public void configureCompilers(CompilersConfiguration compilers, Env env) {
          compilers.registerCompiler(ToLowerCaseCompiler::new, ".html", ".script");
        }
      }));

    get("/extensions/custom_compiler.html").should().contain("helloworld");
    get("/extensions/custom_compiler.script").should().contain("helloworld");
    get("/extensions/custom_compiler.html.source").should().contain("HelloWorld");
    get("/extensions/custom_compiler.script.source").should().contain("HelloWorld");
  }

  static class ToLowerCaseCompiler implements net.codestory.http.compilers.Compiler {
    @Override
    public String compile(Path path, String source) {
      return source.toLowerCase();
    }
  }
}
