package org.apache.aries.plugin.esa.stubs;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

public class EsaMavenProjectStub9
    extends EsaMavenProjectStub
{
    public File getFile()
    {
        return new File( getBasedir(), "src/test/resources/unit/basic-esa-content-type/plugin-config.xml" );
    }

    public Set getArtifacts()
    {
        try
        {
            Set artifacts = new HashSet();

            artifacts.add( createArtifact( "org.apache.maven.test", "maven-artifact01", "1.0-SNAPSHOT", false ) );
            Artifact artifact02 = createArtifact( "org.apache.maven.test", "maven-artifact02", "1.0-SNAPSHOT", false );
            artifact02.setVersionRange(VersionRange.createFromVersionSpec("[1.3, 2.5)"));
            artifacts.add( artifact02 );
            artifacts.add( createArtifact( "org.apache.maven.test", "maven-artifact03", "1.1-SNAPSHOT", false ) );
            artifacts.add( createArtifact( "org.apache.maven.test", "maven-artifact04", "1.2-SNAPSHOT", "esa", true ) );
            return artifacts;
        }
        catch (InvalidVersionSpecificationException e)
        {
            throw new RuntimeException(e);
        }
    }
}
