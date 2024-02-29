/*
 * Copyright 2013-2024, Seqera Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextflow.script.dsl

import java.nio.file.Path

import groovy.transform.CompileStatic
import nextflow.script.OutputSelector
import nextflow.script.WorkflowPublisher
/**
 * Implements the DSL for top-level workflow outputs
 *
 * @author Ben Sherman <bentshermann@gmail.com>
 */
@CompileStatic
class OutputDsl {

    private List<OutputSelector> selectors = []

    void path(Map opts=[:], String path, Closure closure) {
        final dsl = new OutputPathDsl(this, Path.of(path), opts)
        final cl = (Closure)closure.clone()
        cl.setResolveStrategy(Closure.DELEGATE_FIRST)
        cl.setDelegate(dsl)
        cl.call()
    }

    void select(Map opts=[:], String name) {
        this.selectors << new OutputSelector(name, Path.of('.'), opts)
    }

    void addSelector(OutputSelector selector) {
        this.selectors << selector
    }

    WorkflowPublisher build() {
        new WorkflowPublisher(selectors)
    }
}

@CompileStatic
class OutputPathDsl {

    private OutputDsl root
    private Path path
    private Map defaults

    OutputPathDsl(OutputDsl root, Path path, Map defaults) {
        this.root = root
        this.path = path
        this.defaults = defaults
    }

    void path(Map opts=[:], String subpath, Closure closure) {
        final dsl = new OutputPathDsl(root, path.resolve(subpath), defaults + opts)
        final cl = (Closure)closure.clone()
        cl.setResolveStrategy(Closure.DELEGATE_FIRST)
        cl.setDelegate(dsl)
        cl.call()
    }

    void select(Map opts=[:], String name) {
        root.addSelector(new OutputSelector(name, path, defaults + opts))
    }
}
