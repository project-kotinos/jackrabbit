/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.core.integration;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.jcr.RepositoryException;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.jcr.Credentials;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.imageio.spi.ServiceRegistry;

import org.apache.jackrabbit.api.jsr283.RepositoryFactory;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.RepositoryFactoryImpl;
import org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory;
import org.apache.jackrabbit.core.jndi.RegistryHelper;

import junit.framework.TestCase;

/**
 * <code>RepositoryFactoryImplTest</code> performs tests on
 * {@link RepositoryFactoryImpl}.
 */
public class RepositoryFactoryImplTest extends TestCase {

    private static final Credentials CREDENTIALS
            = new SimpleCredentials("user", "pass".toCharArray());

    private static final File TARGET = new File("target");

    private static final File REPO_HOME = new File(TARGET, "repository-factory-test");

    private static final File REPO_CONF = new File(new File(TARGET, "repository"), "repository.xml");

    private Repository repo;

    /**
     * Makes sure the repository is shutdown.
     */
    protected void tearDown() throws Exception {
        if (repo instanceof JackrabbitRepository) {
            ((JackrabbitRepository) repo).shutdown();
        }
        super.tearDown();
    }

    /**
     * Checks if a default repository is returned.
     *
     * @throws RepositoryException if an error occurs.
     */
    public void testDefaultRepository() throws RepositoryException {
        setProperties();
        repo = RepositoryManager.getRepository(null);
        checkRepository(repo);
    }

    /**
     * Checks if a repository is returned for the given parameters.
     *
     * @throws RepositoryException if an error occurs.
     */
    public void testWithParameters() throws RepositoryException {
        resetProperties();
        repo = RepositoryManager.getRepository(getParamters());
        checkRepository(repo);
    }

    /**
     * Checks if multiple {@link RepositoryFactory#getRepository(Map)} calls
     * return the same repository instance.
     *
     * @throws RepositoryException if an error occurs.
     */
    public void testMultipleConnect() throws RepositoryException {
        setProperties();
        repo = RepositoryManager.getRepository(null);
        checkRepository(repo);
        assertSame(repo, RepositoryManager.getRepository(null));
    }

    /**
     * Checks if multiple {@link RepositoryFactory#getRepository(Map)} calls
     * return the same repository instance.
     *
     * @throws RepositoryException if an error occurs.
     */
    public void testMultipleConnectWithParameters() throws RepositoryException {
        resetProperties();
        repo = RepositoryManager.getRepository(getParamters());
        checkRepository(repo);
        assertSame(repo, RepositoryManager.getRepository(getParamters()));
    }

    /**
     * Checks if {@link RepositoryFactory#getRepository(Map)} returns a repository
     * that can be used even after a previously retrieved repository had been
     * {@link JackrabbitRepository#shutdown() shutdown}.
     *
     * @throws RepositoryException if an error occurs.
     */
    public void testShutdown() throws RepositoryException {
        setProperties();
        for (int i = 0; i < 2; i++) {
            repo = RepositoryManager.getRepository(null);
            checkRepository(repo);
            ((JackrabbitRepository) repo).shutdown();
        }
    }

    /**
     * Checks if a repository can be obtained by specifying JNDI parameters
     * on {@link RepositoryFactory#getRepository(Map)}.
     *
     * @throws Exception if an error occurs.
     */
    public void testJNDI() throws Exception {
        String name = "jackrabbit-repository";
        Map parameters = new HashMap();
        parameters.put(Context.INITIAL_CONTEXT_FACTORY, DummyInitialContextFactory.class.getName());
        parameters.put(Context.PROVIDER_URL, "localhost");
        InitialContext context = new InitialContext(new Hashtable(parameters));
        RegistryHelper.registerRepository(context, name,
                REPO_CONF.getAbsolutePath(), REPO_HOME.getAbsolutePath(), false);
        try {
            parameters.put(org.apache.jackrabbit.core.jndi.RepositoryFactoryImpl.REPOSITORY_JNDI_NAME, name);
            repo = RepositoryManager.getRepository(parameters);
            checkRepository(repo);
        } finally {
            RegistryHelper.unregisterRepository(context, name);
        }
    }

    //-------------------------< internal helper >------------------------------

    private static void checkRepository(Repository r) throws RepositoryException {
        r.login(CREDENTIALS).logout();
    }

    private static void setProperties() {
        System.setProperty(RepositoryFactoryImpl.REPOSITORY_HOME, REPO_HOME.getAbsolutePath());
        System.setProperty(RepositoryFactoryImpl.REPOSITORY_CONF, REPO_CONF.getAbsolutePath());
    }

    private static void resetProperties() {
        System.setProperty(RepositoryFactoryImpl.REPOSITORY_HOME, "");
        System.setProperty(RepositoryFactoryImpl.REPOSITORY_CONF, "");
    }

    private static Map getParamters() {
        Map params = new HashMap();
        params.put(RepositoryFactoryImpl.REPOSITORY_HOME, REPO_HOME.getAbsolutePath());
        params.put(RepositoryFactoryImpl.REPOSITORY_CONF, REPO_CONF.getAbsolutePath());
        return params;
    }

    private static final class RepositoryManager {

        private static Repository getRepository(Map parameters)
                throws RepositoryException {
            Repository repo = null;
            Iterator factories = ServiceRegistry.lookupProviders(RepositoryFactory.class);
            while (factories.hasNext()) {
                RepositoryFactory factory = (RepositoryFactory) factories.next();
                repo = factory.getRepository(parameters);
                if (repo != null) {
                    break;
                }
            }
            return repo;
        }
    }
}
