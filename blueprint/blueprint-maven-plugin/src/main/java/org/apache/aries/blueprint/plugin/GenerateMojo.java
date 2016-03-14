/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.blueprint.plugin;

import java.io.File;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.aries.blueprint.plugin.model.Context;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.xbean.finder.ClassFinder;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Generates blueprint from spring annotations
 * @goal blueprint-generate
 * @phase process-classes
 * @requiresDependencyResolution compile
 * @inheritByDefault false
 * @description Generates blueprint file from spring annotations @Component, @Autowire and @Value
 */
public class GenerateMojo extends AbstractMojo {

    /**
     * The maven project.
     *
     * @parameter default-value="${project}"
     * @required
     */
    protected MavenProject project;

    /**
     * @parameter
     * @required
     */
    protected List<String> scanPaths;
    
    /**
     * Which extension namespaces should the plugin support
     * 
     * @parameter 
     */
    protected Set<String> namespaces;
    
    /**
     * @component
     */
    private BuildContext buildContext;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!buildContext.hasDelta(new File(project.getCompileSourceRoots().iterator().next()))) {
            return;
        }
        
        try {
            ClassFinder finder = createProjectScopeFinder();
            
            Set<Class<?>> classes = FilteredClassFinder.findClasses(finder, scanPaths);
            Context context = new Context(classes);
            context.resolve();
            if (context.getBeans().size() > 0) {
                writeBlueprint(context);
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error building commands help", e);
        }
    }

    private void writeBlueprint(Context context) throws Exception {
        String buildDir = project.getBuild().getDirectory();
        String generatedDir = buildDir + "/generated-resources";
        Resource resource = new Resource();
        resource.setDirectory(generatedDir);
        project.addResource(resource);

        File file = new File(generatedDir, "OSGI-INF/blueprint/autowire.xml");
        file.getParentFile().mkdirs();
        System.out.println("Generating blueprint to " + file);

        OutputStream fos = buildContext.newFileOutputStream(file);
        new Generator(context, fos, namespaces).generate();
        fos.close();
    }

    private ClassFinder createProjectScopeFinder() throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();

        urls.add( new File(project.getBuild().getOutputDirectory()).toURI().toURL() );
        for ( Object artifactO : project.getArtifacts() ) {
            Artifact artifact = (Artifact)artifactO;
            File file = artifact.getFile();
            if ( file != null ) {
                urls.add( file.toURI().toURL() );
            }
        }
        ClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
        return new ClassFinder(loader, urls);
    }

}
