//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.util.resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.jetty.toolchain.test.TestingDir;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.UrlEncoded;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class FileResourceTest
{
    @Rule
    public TestingDir testdir = new TestingDir();

    private URI createDummyFile(String name) throws IOException
    {
        File file = testdir.getFile(name);
        file.createNewFile();
        return file.toURI();
    }
    
    private URL decode(URL url) throws MalformedURLException
    {
        String raw = url.toExternalForm();
        String decoded = UrlEncoded.decodeString(raw,0,raw.length(),StringUtil.__UTF8);
        return new URL(decoded);
    }

    @Test
    public void testExist_Normal() throws Exception
    {
        createDummyFile("a.jsp");

        URI ref = testdir.getDir().toURI().resolve("a.jsp");
        FileResource fileres = new FileResource(decode(ref.toURL()));
        Assert.assertThat("FileResource: " + fileres,fileres.exists(),is(true));
    }

    @Test
    public void testExist_Bad1F10() throws Exception
    {
        createDummyFile("a.jsp");

        try {
            // request with null at end
            URI ref = testdir.getDir().toURI().resolve("a.jsp%1F%10");
            FileResource fileres = new FileResource(ref.toURL());
            Assert.assertThat("FileResource exists: " + fileres,fileres.exists(),is(false));
        } catch(URISyntaxException e) {
            // Valid path
        }
    }

    @Test
    public void testExist_Bad_AddPath1F10() throws Exception
    {
        createDummyFile("a.jsp");

        try {
            // base resource
            URI ref = testdir.getDir().toURI();
            FileResource base = new FileResource(ref.toURL());
            Resource added = base.addPath("/a.jsp\014\010");
            Assert.assertThat("Is FileResource", added, instanceOf(FileResource.class));
            FileResource fileres = (FileResource) added;
            Assert.assertThat("FileResource exists: " + fileres,fileres.exists(),is(false));
        } catch(URISyntaxException e) {
            // Valid path
        }
    }

    @Test
    public void testExist_BadNull() throws Exception
    {
        createDummyFile("a.jsp");

        try {
            // request with null at end
            URI ref = testdir.getDir().toURI().resolve("a.jsp%00");
            FileResource fileres = new FileResource(ref.toURL());
            Assert.assertThat("FileResource: " + fileres,fileres.exists(),is(false));
        } catch(URISyntaxException e) {
            // Valid path
        }
    }

    @Test
    public void testExist_BadNullX() throws Exception
    {
        createDummyFile("a.jsp");

        try {
            // request with null and x at end
            URI ref = testdir.getDir().toURI().resolve("a.jsp%00x");
            FileResource fileres = new FileResource(ref.toURL());
            Assert.assertThat("FileResource: " + fileres,fileres.exists(),is(false));
        } catch(URISyntaxException e) {
            // Valid path
        }
    }
}
