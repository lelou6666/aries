/*
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
package org.apache.aries.application.runtime.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_VERSION;
import static org.osgi.framework.Constants.EXPORT_PACKAGE;
import static org.osgi.framework.Constants.IMPORT_PACKAGE;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.aries.application.Content;
import org.apache.aries.application.DeploymentContent;
import org.apache.aries.application.DeploymentMetadata;
import org.apache.aries.application.management.AriesApplication;
import org.apache.aries.application.management.AriesApplicationContext;
import org.apache.aries.application.management.AriesApplicationManager;
import org.apache.aries.application.management.ResolverException;
import org.apache.aries.application.management.spi.repository.RepositoryGenerator;
import org.apache.aries.application.management.spi.resolve.AriesApplicationResolver;
import org.apache.aries.application.modelling.ModelledResource;
import org.apache.aries.application.modelling.ModelledResourceManager;
import org.apache.aries.application.utils.AppConstants;
import org.apache.aries.application.utils.manifest.ContentFactory;
import org.apache.aries.itest.AbstractIntegrationTest;
import org.apache.aries.unittest.fixture.ArchiveFixture;
import org.apache.aries.unittest.fixture.ArchiveFixture.ZipFixture;
import org.apache.aries.util.filesystem.FileSystem;
import org.apache.aries.util.filesystem.IDirectory;
import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
<<<<<<< HEAD
import org.osgi.framework.Constants;
import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.w3c.dom.Document;

@RunWith(JUnit4TestRunner.class)
public class OBRResolverTest extends AbstractIntegrationTest 
{
  public static final String CORE_BUNDLE_BY_VALUE = "core.bundle.by.value";
  public static final String CORE_BUNDLE_BY_REFERENCE = "core.bundle.by.reference";
  public static final String TRANSITIVE_BUNDLE_BY_VALUE = "transitive.bundle.by.value";
  public static final String TRANSITIVE_BUNDLE_BY_REFERENCE = "transitive.bundle.by.reference";
  public static final String BUNDLE_IN_FRAMEWORK = "org.apache.aries.util";
  
  public static final String CORE_BUNDLE1_BY_VALUE = "core.bundle1.by.value";
  public static final String CORE_BUNDLE2_BY_VALUE = "core.bundle2.by.value";
  
  
  /* Use @Before not @BeforeClass so as to ensure that these resources
   * are created in the paxweb temp directory, and not in the svn tree
   */
  @Before
  public static void createApplications() throws Exception 
  {
    ZipFixture bundle; 
    FileOutputStream fout;
    
    bundle = ArchiveFixture.newJar().manifest()
                            .attribute(Constants.BUNDLE_SYMBOLICNAME, CORE_BUNDLE_BY_VALUE)
                            .attribute(Constants.BUNDLE_MANIFESTVERSION, "2")
                            .attribute(Constants.IMPORT_PACKAGE, "p.q.r, x.y.z, javax.naming, " + BUNDLE_IN_FRAMEWORK)
                            .attribute(Constants.BUNDLE_VERSION, "1.0.0").end();

    
    fout = new FileOutputStream(CORE_BUNDLE_BY_VALUE + ".jar");
    bundle.writeOut(fout);
    fout.close();

    bundle = ArchiveFixture.newJar().manifest()
                            .attribute(Constants.BUNDLE_SYMBOLICNAME, TRANSITIVE_BUNDLE_BY_VALUE)
                            .attribute(Constants.BUNDLE_MANIFESTVERSION, "2")
                            .attribute(Constants.EXPORT_PACKAGE, "p.q.r")
                            .attribute(Constants.BUNDLE_VERSION, "1.0.0").end();

    fout = new FileOutputStream(TRANSITIVE_BUNDLE_BY_VALUE + ".jar");
    bundle.writeOut(fout);
    fout.close();

    bundle = ArchiveFixture.newJar().manifest()
                            .attribute(Constants.BUNDLE_SYMBOLICNAME, TRANSITIVE_BUNDLE_BY_REFERENCE)
                            .attribute(Constants.BUNDLE_MANIFESTVERSION, "2")
                            .attribute(Constants.EXPORT_PACKAGE, "x.y.z")
                            .attribute(Constants.BUNDLE_VERSION, "1.0.0").end();
    
    fout = new FileOutputStream(TRANSITIVE_BUNDLE_BY_REFERENCE + ".jar");
    bundle.writeOut(fout);
    fout.close();

    bundle = ArchiveFixture.newJar().manifest()
                            .attribute(Constants.BUNDLE_SYMBOLICNAME, CORE_BUNDLE_BY_REFERENCE)
                            .attribute(Constants.BUNDLE_MANIFESTVERSION, "2")
                            .attribute(Constants.EXPORT_PACKAGE, "d.e.f")
                            .attribute(Constants.BUNDLE_VERSION, "1.0.0").end();
    
    fout = new FileOutputStream(CORE_BUNDLE_BY_REFERENCE + ".jar");
    bundle.writeOut(fout);
    fout.close();

    bundle = ArchiveFixture.newJar().manifest()
                            .attribute(Constants.BUNDLE_SYMBOLICNAME, CORE_BUNDLE_BY_REFERENCE)
                            .attribute(Constants.BUNDLE_MANIFESTVERSION, "2")
                            .attribute(Constants.EXPORT_PACKAGE, "d.e.f").end();

    fout = new FileOutputStream(CORE_BUNDLE_BY_REFERENCE + "_0.0.0.jar");
    bundle.writeOut(fout);
    fout.close();
    
    ZipFixture testEba = ArchiveFixture.newZip()
     .binary("META-INF/APPLICATION.MF",
        OBRResolverTest.class.getClassLoader().getResourceAsStream("obr/APPLICATION.MF"))
        .end()
      .binary(CORE_BUNDLE_BY_VALUE + ".jar", new FileInputStream(CORE_BUNDLE_BY_VALUE + ".jar")).end()
      .binary(TRANSITIVE_BUNDLE_BY_VALUE + ".jar", new FileInputStream(TRANSITIVE_BUNDLE_BY_VALUE + ".jar")).end();

    fout = new FileOutputStream("blog.eba");
    testEba.writeOut(fout);
    fout.close();
    
    
    // prepare bundles for require-bundle header test
    // bundle1
    bundle = ArchiveFixture.newJar().manifest()
    .attribute(Constants.BUNDLE_SYMBOLICNAME, CORE_BUNDLE1_BY_VALUE)
    .attribute(Constants.BUNDLE_NAME, "Bundle1")
    .attribute(Constants.BUNDLE_MANIFESTVERSION, "2")
    .attribute(Constants.REQUIRE_BUNDLE, CORE_BUNDLE2_BY_VALUE+";bundle-version=\"0.0.0\"")
    .attribute(Constants.BUNDLE_VERSION, "1.0.0")
    .end();


    fout = new FileOutputStream(CORE_BUNDLE1_BY_VALUE + ".jar");
    bundle.writeOut(fout);
    fout.close();
    
    // bundle2
    bundle = ArchiveFixture.newJar().manifest()
    .attribute(Constants.BUNDLE_SYMBOLICNAME, CORE_BUNDLE2_BY_VALUE)
    .attribute(Constants.BUNDLE_NAME, "Bundle2")
    .attribute(Constants.BUNDLE_MANIFESTVERSION, "2")
    .attribute(Constants.BUNDLE_VERSION, "1.0.0").end();


    fout = new FileOutputStream(CORE_BUNDLE2_BY_VALUE + ".jar");
    bundle.writeOut(fout);
    fout.close();
    
    //eba that made up of bundle1 and bundle2
    ZipFixture testRequireBundle = ArchiveFixture.newZip()
    .binary("META-INF/APPLICATION.MF",
       OBRResolverTest.class.getClassLoader().getResourceAsStream("obr/APPLICATION2.MF"))
       .end()
     .binary(CORE_BUNDLE1_BY_VALUE + ".jar", new FileInputStream(CORE_BUNDLE1_BY_VALUE + ".jar")).end()
     .binary(CORE_BUNDLE2_BY_VALUE + ".jar", new FileInputStream(CORE_BUNDLE2_BY_VALUE + ".jar")).end();

   fout = new FileOutputStream("testRequireBundle.eba");
   testRequireBundle.writeOut(fout);
   fout.close();

  }

  @Test(expected=ResolverException.class)
  public void testBlogAppResolveFail() throws ResolverException, Exception
  {
    startApplicationRuntimeBundle();

    generateOBRRepoXML(TRANSITIVE_BUNDLE_BY_REFERENCE + ".jar", CORE_BUNDLE_BY_REFERENCE + "_0.0.0.jar");
    
    RepositoryAdmin repositoryAdmin = getOsgiService(RepositoryAdmin.class);
    
    Repository[] repos = repositoryAdmin.listRepositories();
    for (Repository repo : repos) {
      repositoryAdmin.removeRepository(repo.getURI());
    }
    
    repositoryAdmin.addRepository(new File("repository.xml").toURI().toURL());

    AriesApplicationManager manager = getOsgiService(AriesApplicationManager.class);
    AriesApplication app = manager.createApplication(FileSystem.getFSRoot(new File("blog.eba")));
    //installing requires a valid url for the bundle in repository.xml.
    
    app = manager.resolve(app);
  }
  
  @Test
  public void testBlogApp() throws Exception 
  {
    startApplicationRuntimeBundle();
    
    //generate the repository.xml for this test
    generateOBRRepoXML(TRANSITIVE_BUNDLE_BY_REFERENCE + ".jar", CORE_BUNDLE_BY_REFERENCE + ".jar");
    
    RepositoryAdmin repositoryAdmin = getOsgiService(RepositoryAdmin.class);
    
    //clear all other repo info
    Repository[] repos = repositoryAdmin.listRepositories();
    for (Repository repo : repos) {
      repositoryAdmin.removeRepository(repo.getURI());
    }
    
    //add our generated repository.xml
    repositoryAdmin.addRepository(new File("repository.xml").toURI().toURL());

    AriesApplicationManager manager = getOsgiService(AriesApplicationManager.class);
    AriesApplication app = manager.createApplication(FileSystem.getFSRoot(new File("blog.eba")));
    //installing requires a valid url for the bundle in repository.xml.
    
    app = manager.resolve(app);
    
    DeploymentMetadata depMeta = app.getDeploymentMetadata();
    
    List<DeploymentContent> provision = depMeta.getApplicationProvisionBundles();
    
    assertEquals(provision.toString(), 3, provision.size());
    
    List<String> bundleSymbolicNames = new ArrayList<String>();
    
    for (DeploymentContent dep : provision) {
      bundleSymbolicNames.add(dep.getContentName());
    }
    
    assertTrue("Bundle " + TRANSITIVE_BUNDLE_BY_REFERENCE + " not found.", bundleSymbolicNames.contains(TRANSITIVE_BUNDLE_BY_REFERENCE));
    assertTrue("Bundle " + TRANSITIVE_BUNDLE_BY_VALUE + " not found.", bundleSymbolicNames.contains(TRANSITIVE_BUNDLE_BY_VALUE));
    assertTrue("Bundle " + BUNDLE_IN_FRAMEWORK + " not found.", bundleSymbolicNames.contains(BUNDLE_IN_FRAMEWORK));
    
    AriesApplicationContext ctx = manager.install(app);
    ctx.start();

    Set<Bundle> bundles = ctx.getApplicationContent();
    
    assertEquals("Number of bundles provisioned in the app", 4, bundles.size());
    
    ctx.stop();
    manager.uninstall(ctx);
  }

  @Test
  public void testRequireBundleResolve() throws Exception 
  {
    startApplicationRuntimeBundle();


    RepositoryAdmin repositoryAdmin = getOsgiService(RepositoryAdmin.class);
    
    //clear all other repo info
    Repository[] repos = repositoryAdmin.listRepositories();
    for (Repository repo : repos) {
      repositoryAdmin.removeRepository(repo.getURI());
    }

    AriesApplicationManager manager = getOsgiService(AriesApplicationManager.class);
    AriesApplication app = manager.createApplication(FileSystem.getFSRoot(new File("testRequireBundle.eba")));
    
    app = manager.resolve(app);
    
    DeploymentMetadata depMeta = app.getDeploymentMetadata();
    
    List<DeploymentContent> depContents = depMeta.getApplicationDeploymentContents();

    List<String> bundleSymbolicNames = new ArrayList<String>();
    
    for (DeploymentContent dep : depContents) {
      bundleSymbolicNames.add(dep.getContentName());
    }
    
    assertTrue("Bundle " + CORE_BUNDLE1_BY_VALUE + " not found.", bundleSymbolicNames.contains(CORE_BUNDLE1_BY_VALUE));
    assertTrue("Bundle " + CORE_BUNDLE2_BY_VALUE + " not found.", bundleSymbolicNames.contains(CORE_BUNDLE2_BY_VALUE));
    
    
  }

  private void generateOBRRepoXML(String ... bundleFiles) throws Exception
  {
    Set<BundleInfo> bundles = new HashSet<BundleInfo>();
    
    for (String file : bundleFiles) {
      bundles.add(createBundleInfo(new File(file).toURI().toURL().toExternalForm()));
    }
    
    Document doc = RepositoryDescriptorGenerator.generateRepositoryDescriptor("Test repo description", bundles);
    
    FileOutputStream fout = new FileOutputStream("repository.xml");
    
    TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(fout));
    
    fout.close();
    
    TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(System.out));
  }

  private BundleInfo createBundleInfo(String urlToBundle) throws Exception
  {
    ApplicationMetadataFactory factory = getOsgiService(ApplicationMetadataFactory.class);
    
    Bundle b = getBundle("org.apache.aries.application.management");
    @SuppressWarnings("unchecked")
    Class<BundleInfo> clazz = b.loadClass("org.apache.aries.application.utils.management.SimpleBundleInfo");
    Constructor<BundleInfo> c = clazz.getConstructor(ApplicationMetadataFactory.class, BundleManifest.class, String.class);
    
    return c.newInstance(factory, BundleManifest.fromBundle(new URL(urlToBundle).openStream()), urlToBundle);
  }

  @org.ops4j.pax.exam.junit.Configuration
  public static Option[] configuration() {
    Option[] options = options(
        // Log
        mavenBundle("org.ops4j.pax.logging", "pax-logging-api"),
        mavenBundle("org.ops4j.pax.logging", "pax-logging-service"),
        // Felix Config Admin
        mavenBundle("org.apache.felix", "org.apache.felix.configadmin"),
        // Felix mvn url handler
        mavenBundle("org.ops4j.pax.url", "pax-url-mvn"),

        // this is how you set the default log level when using pax
        // logging (logProfile)
        systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("DEBUG"),

        // Bundles
        mavenBundle("org.apache.aries.application", "org.apache.aries.application.api"),
        mavenBundle("org.apache.aries.application", "org.apache.aries.application.utils"),
        mavenBundle("org.apache.aries.application", "org.apache.aries.application.management"),
        mavenBundle("org.apache.aries.application", "org.apache.aries.application.runtime").noStart(),
        mavenBundle("org.apache.aries.application", "org.apache.aries.application.resolver.obr"),
        mavenBundle("org.apache.felix", "org.apache.felix.bundlerepository"),
        mavenBundle("org.apache.aries.application", "org.apache.aries.application.runtime.itest.interfaces"),
        mavenBundle("org.apache.aries", "org.apache.aries.util"),
        mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint"),
        mavenBundle("org.osgi", "org.osgi.compendium"),
        mavenBundle("org.apache.aries.testsupport", "org.apache.aries.testsupport.unit"),

        /* For debugging, uncomment the next two lines */
        //vmOption ("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7777"),
        //waitForFrameworkStartup(),

        /* For debugging, and add these imports:
        import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
        import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
        */

        equinox().version("3.5.0"));
    options = updateOptions(options);
    return options;
  }
=======

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OBRResolverTest extends AbstractIntegrationTest {

    public static final String CORE_BUNDLE_BY_VALUE = "core.bundle.by.value";
    public static final String CORE_BUNDLE_BY_REFERENCE = "core.bundle.by.reference";
    public static final String TRANSITIVE_BUNDLE_BY_VALUE = "transitive.bundle.by.value";
    public static final String TRANSITIVE_BUNDLE_BY_REFERENCE = "transitive.bundle.by.reference";
    public static final String BUNDLE_IN_FRAMEWORK = "org.apache.aries.util";


    /* Use @Before not @BeforeClass so as to ensure that these resources
     * are created in the paxweb temp directory, and not in the svn tree
     */
    @Before
    public void createApplications() throws Exception {
        ZipFixture bundle = ArchiveFixture.newJar().manifest()
                .attribute(BUNDLE_SYMBOLICNAME, CORE_BUNDLE_BY_VALUE)
                .attribute(BUNDLE_MANIFESTVERSION, "2")
                .attribute(IMPORT_PACKAGE, "p.q.r, x.y.z, javax.naming, " + BUNDLE_IN_FRAMEWORK)
                .attribute(BUNDLE_VERSION, "1.0.0").end();


        FileOutputStream fout = new FileOutputStream(CORE_BUNDLE_BY_VALUE + ".jar");
        bundle.writeOut(fout);
        fout.close();

        bundle = ArchiveFixture.newJar().manifest()
                .attribute(BUNDLE_SYMBOLICNAME, TRANSITIVE_BUNDLE_BY_VALUE)
                .attribute(BUNDLE_MANIFESTVERSION, "2")
                .attribute(EXPORT_PACKAGE, "p.q.r")
                .attribute(BUNDLE_VERSION, "1.0.0").end();

        fout = new FileOutputStream(TRANSITIVE_BUNDLE_BY_VALUE + ".jar");
        bundle.writeOut(fout);
        fout.close();

        bundle = ArchiveFixture.newJar().manifest()
                .attribute(BUNDLE_SYMBOLICNAME, TRANSITIVE_BUNDLE_BY_REFERENCE)
                .attribute(BUNDLE_MANIFESTVERSION, "2")
                .attribute(EXPORT_PACKAGE, "x.y.z")
                .attribute(BUNDLE_VERSION, "1.0.0").end();

        fout = new FileOutputStream(TRANSITIVE_BUNDLE_BY_REFERENCE + ".jar");
        bundle.writeOut(fout);
        fout.close();

        bundle = ArchiveFixture.newJar().manifest()
                .attribute(BUNDLE_SYMBOLICNAME, CORE_BUNDLE_BY_REFERENCE)
                .attribute(BUNDLE_MANIFESTVERSION, "2")
                .attribute(EXPORT_PACKAGE, "d.e.f")
                .attribute(BUNDLE_VERSION, "1.0.0").end();

        fout = new FileOutputStream(CORE_BUNDLE_BY_REFERENCE + ".jar");
        bundle.writeOut(fout);
        fout.close();

        bundle = ArchiveFixture.newJar().manifest()
                .attribute(BUNDLE_SYMBOLICNAME, CORE_BUNDLE_BY_REFERENCE)
                .attribute(BUNDLE_MANIFESTVERSION, "2")
                .attribute(EXPORT_PACKAGE, "d.e.f").end();

        fout = new FileOutputStream(CORE_BUNDLE_BY_REFERENCE + "_0.0.0.jar");
        bundle.writeOut(fout);
        fout.close();

        ZipFixture testEba = ArchiveFixture.newZip()
                .binary("META-INF/APPLICATION.MF",
                        OBRResolverTest.class.getClassLoader().getResourceAsStream("obr/APPLICATION.MF"))
                .end()
                .binary(CORE_BUNDLE_BY_VALUE + ".jar", new FileInputStream(CORE_BUNDLE_BY_VALUE + ".jar")).end()
                .binary(TRANSITIVE_BUNDLE_BY_VALUE + ".jar", new FileInputStream(TRANSITIVE_BUNDLE_BY_VALUE + ".jar")).end();

        fout = new FileOutputStream("blog.eba");
        testEba.writeOut(fout);
        fout.close();
    }

    @After
    public void clearRepository() {
        RepositoryAdmin repositoryAdmin = context().getService(RepositoryAdmin.class);
        Repository[] repos = repositoryAdmin.listRepositories();
        if ((repos != null) && (repos.length > 0)) {
            for (Repository repo : repos) {
                repositoryAdmin.removeRepository(repo.getURI());
            }
        }
    }

    @Test(expected = ResolverException.class)
    public void testBlogAppResolveFail() throws ResolverException, Exception {
        //  provision against the local runtime
        System.setProperty(AppConstants.PROVISON_EXCLUDE_LOCAL_REPO_SYSPROP, "false");
        generateOBRRepoXML(TRANSITIVE_BUNDLE_BY_REFERENCE + ".jar", CORE_BUNDLE_BY_REFERENCE + "_0.0.0.jar");

        RepositoryAdmin repositoryAdmin = context().getService(RepositoryAdmin.class);

        Repository[] repos = repositoryAdmin.listRepositories();
        for (Repository repo : repos) {
            repositoryAdmin.removeRepository(repo.getURI());
        }

        repositoryAdmin.addRepository(new File("repository.xml").toURI().toURL());

        AriesApplicationManager manager = context().getService(AriesApplicationManager.class);
        AriesApplication app = manager.createApplication(FileSystem.getFSRoot(new File("blog.eba")));
        //installing requires a valid url for the bundle in repository.xml.

        app = manager.resolve(app);
    }

    /**
     * Test the resolution should fail because the required package org.apache.aries.util is provided by the local runtime,
     * which is not included when provisioning.
     *
     * @throws Exception
     */
    @Test(expected = ResolverException.class)
    public void testProvisionExcludeLocalRepo() throws Exception {
        // do not provision against the local runtime
        System.setProperty(AppConstants.PROVISON_EXCLUDE_LOCAL_REPO_SYSPROP, "true");
        generateOBRRepoXML(TRANSITIVE_BUNDLE_BY_REFERENCE + ".jar", CORE_BUNDLE_BY_REFERENCE + ".jar");

        RepositoryAdmin repositoryAdmin = context().getService(RepositoryAdmin.class);

        Repository[] repos = repositoryAdmin.listRepositories();
        for (Repository repo : repos) {
            repositoryAdmin.removeRepository(repo.getURI());
        }

        repositoryAdmin.addRepository(new File("repository.xml").toURI().toURL());

        AriesApplicationManager manager = context().getService(AriesApplicationManager.class);
        AriesApplication app = manager.createApplication(FileSystem.getFSRoot(new File("blog.eba")));
        //installing requires a valid url for the bundle in repository.xml.

        app = manager.resolve(app);
    }

    @Test
    public void test_resolve_self_contained_app_in_isolation() throws Exception {
        assertEquals(2, createAndResolveSelfContainedApp("org.osgi.framework").size());
    }

    @Test(expected = ResolverException.class)
    public void test_resolve_non_self_contained_app_in_isolation() throws Exception {
        createAndResolveSelfContainedApp("org.osgi.service.blueprint");
    }

    private Collection<ModelledResource> createAndResolveSelfContainedApp(String extraImport) throws Exception {
        FileOutputStream fout = new FileOutputStream(new File("a.bundle.jar"));
        ArchiveFixture.newJar()
                .manifest()
                .attribute(BUNDLE_SYMBOLICNAME, "a.bundle")
                .attribute(BUNDLE_VERSION, "1.0.0")
                .attribute(BUNDLE_MANIFESTVERSION, "2")
                .attribute(IMPORT_PACKAGE, "a.pack.age")
                .end().writeOut(fout);
        fout.close();

        fout = new FileOutputStream(new File("b.bundle.jar"));
        ArchiveFixture.newJar()
                .manifest()
                .attribute(BUNDLE_SYMBOLICNAME, "b.bundle")
                .attribute(BUNDLE_VERSION, "1.0.0")
                .attribute(BUNDLE_MANIFESTVERSION, "2")
                .attribute(IMPORT_PACKAGE, extraImport)
                .attribute(EXPORT_PACKAGE, "a.pack.age")
                .end().writeOut(fout);
        fout.close();

        ModelledResourceManager mrm = context().getService(ModelledResourceManager.class);
        ModelledResource aBundle = mrm.getModelledResource(FileSystem.getFSRoot(new File("a.bundle.jar")));
        ModelledResource bBundle = mrm.getModelledResource(FileSystem.getFSRoot(new File("b.bundle.jar")));

        AriesApplicationResolver resolver = context().getService(AriesApplicationResolver.class);
        return resolver.resolveInIsolation("test.app", "1.0.0",
                Arrays.asList(aBundle, bBundle),
                Arrays.<Content>asList(ContentFactory.parseContent("a.bundle", "1.0.0"), ContentFactory.parseContent("b.bundle", "1.0.0")));
    }

    @Test
    public void testBlogApp() throws Exception {
        //  provision against the local runtime
        System.setProperty(AppConstants.PROVISON_EXCLUDE_LOCAL_REPO_SYSPROP, "false");
        generateOBRRepoXML(TRANSITIVE_BUNDLE_BY_REFERENCE + ".jar", CORE_BUNDLE_BY_REFERENCE + ".jar");

        RepositoryAdmin repositoryAdmin = context().getService(RepositoryAdmin.class);

        Repository[] repos = repositoryAdmin.listRepositories();
        for (Repository repo : repos) {
            repositoryAdmin.removeRepository(repo.getURI());
        }

        repositoryAdmin.addRepository(new File("repository.xml").toURI().toURL());

        AriesApplicationManager manager = context().getService(AriesApplicationManager.class);
        AriesApplication app = manager.createApplication(FileSystem.getFSRoot(new File("blog.eba")));
        //installing requires a valid url for the bundle in repository.xml.

        app = manager.resolve(app);

        DeploymentMetadata depMeta = app.getDeploymentMetadata();

        List<DeploymentContent> provision = depMeta.getApplicationProvisionBundles();

        assertEquals(provision.toString(), 3, provision.size());

        List<String> bundleSymbolicNames = new ArrayList<String>();

        for (DeploymentContent dep : provision) {
            bundleSymbolicNames.add(dep.getContentName());
        }

        assertTrue("Bundle " + TRANSITIVE_BUNDLE_BY_REFERENCE + " not found.", bundleSymbolicNames.contains(TRANSITIVE_BUNDLE_BY_REFERENCE));
        assertTrue("Bundle " + TRANSITIVE_BUNDLE_BY_VALUE + " not found.", bundleSymbolicNames.contains(TRANSITIVE_BUNDLE_BY_VALUE));
        assertTrue("Bundle " + BUNDLE_IN_FRAMEWORK + " not found.", bundleSymbolicNames.contains(BUNDLE_IN_FRAMEWORK));

        AriesApplicationContext ctx = manager.install(app);
        ctx.start();

        Set<Bundle> bundles = ctx.getApplicationContent();

        assertEquals("Number of bundles provisioned in the app", 4, bundles.size());

        ctx.stop();
        manager.uninstall(ctx);
    }


    private void generateOBRRepoXML(String... bundleFiles) throws Exception {
        Set<ModelledResource> mrs = new HashSet<ModelledResource>();
        FileOutputStream fout = new FileOutputStream("repository.xml");
        RepositoryGenerator repositoryGenerator = context().getService(RepositoryGenerator.class);
        ModelledResourceManager modelledResourceManager = context().getService(ModelledResourceManager.class);
        for (String fileName : bundleFiles) {
            File bundleFile = new File(fileName);
            IDirectory jarDir = FileSystem.getFSRoot(bundleFile);
            mrs.add(modelledResourceManager.getModelledResource(bundleFile.toURI().toString(), jarDir));
        }
        repositoryGenerator.generateRepository("Test repo description", mrs, fout);
        fout.close();
    }

    @Configuration
    public static Option[] configuration() {
        return options(

                // framework / core bundles
                mavenBundle("org.osgi", "org.osgi.core").versionAsInProject(),
                mavenBundle("org.ops4j.pax.logging", "pax-logging-api").versionAsInProject(),
                mavenBundle("org.ops4j.pax.logging", "pax-logging-service").versionAsInProject(),

                // Logging
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"),

                // Bundles
                junitBundles(),
                mavenBundle("org.apache.aries.testsupport", "org.apache.aries.testsupport.unit").versionAsInProject(),

                // Bundles
                mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint").versionAsInProject(),
                mavenBundle("org.ow2.asm", "asm-all").versionAsInProject(),
                mavenBundle("org.apache.aries.proxy", "org.apache.aries.proxy").versionAsInProject(),
                mavenBundle("org.apache.aries", "org.apache.aries.util").versionAsInProject(),
                mavenBundle("org.apache.aries.application", "org.apache.aries.application.api").versionAsInProject(),
                mavenBundle("org.apache.aries.application", "org.apache.aries.application.utils").versionAsInProject(),
                mavenBundle("org.apache.aries.application", "org.apache.aries.application.modeller").versionAsInProject(),
                mavenBundle("org.apache.aries.application", "org.apache.aries.application.default.local.platform").versionAsInProject(),
                mavenBundle("org.apache.felix", "org.apache.felix.bundlerepository").versionAsInProject(),
                mavenBundle("org.apache.aries.application", "org.apache.aries.application.resolver.obr").versionAsInProject(),
                mavenBundle("org.apache.aries.application", "org.apache.aries.application.deployment.management").versionAsInProject(),
                mavenBundle("org.apache.aries.application", "org.apache.aries.application.management").versionAsInProject(),
                mavenBundle("org.apache.aries.application", "org.apache.aries.application.runtime").versionAsInProject(),
                mavenBundle("org.apache.aries.application", "org.apache.aries.application.runtime.itest.interfaces").versionAsInProject());
    }

>>>>>>> refs/remotes/apache/trunk
}