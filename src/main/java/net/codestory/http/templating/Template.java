/**
 * Copyright (C) 2013 all@code-story.net
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
package net.codestory.http.templating;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.compilers.Compiler;
import net.codestory.http.io.*;

public class Template {
  private final Path path;

  public Template(String folder, String name) {
    this(folder + (name.startsWith("/") ? name : "/" + name));
  }

  public Template(String uri) {
    Path existing = Resources.findExistingPath(uri);
    if (existing == null) {
      throw new IllegalArgumentException("Template not found " + uri);
    }
    this.path = existing;
  }

  public String render(Model model) {
    return render(model.getKeyValues());
  }

  String render(Map<String, Object> keyValues) {
    try {
      YamlFrontMatter yamlFrontMatter = YamlFrontMatter.parse(path);
      Map<String, Object> allKeyValues = merge(yamlFrontMatter.getVariables(), keyValues);

      String content = Compiler.compile(path, yamlFrontMatter.getContent());
      String body = new HandlebarsCompiler().compile(content, allKeyValues);

      String layout = (String) yamlFrontMatter.getVariables().get("layout");
      if (layout == null) {
        return body;
      }

      return new Template("_layouts", layout).render(allKeyValues).replace("[[body]]", body);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to render template", e);
    }
  }

  private static Map<String, Object> merge(Map<String, Object> first, Map<String, Object> second) {
    Map<String, Object> merged = new HashMap<>();
    merged.putAll(first);
    merged.putAll(second);
    merged.put("body", "[[body]]");
    return merged;
  }
}
