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
package org.apache.jackrabbit.test.api.query.qom;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.query.Query;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.QueryObjectModelConstants;

/**
 * <code>UpperLowerCaseTest</code> performs tests with upper- and lower-case
 * operands.
 */
public class UpperLowerCaseTest extends AbstractQOMTest {

    private ValueFactory vf;

    private Node node;

    protected void setUp() throws Exception {
        super.setUp();
        vf = superuser.getValueFactory();
        node = testRootNode.addNode(nodeName1, testNodeType);
        node.setProperty(propertyName1, "abc");
        node.setProperty(propertyName2, "ABC");
        testRootNode.save();
    }

    protected void tearDown() throws Exception {
        vf = null;
        node = null;
        super.tearDown();
    }

    public void testFullTextSearchScore() throws RepositoryException {
        // TODO
    }

    public void testLength() throws RepositoryException {
        // TODO
    }

    public void testNodeLocalName() throws RepositoryException {
        // TODO
    }

    public void testNodeName() throws RepositoryException {
        node.setProperty(propertyName1, "abc", PropertyType.NAME);
        node.setProperty(propertyName2, "ABC", PropertyType.NAME);
        node.save();

        // upper case
        checkQueries(qf.propertyValue("s", propertyName1),
                true, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.NAME,
                new boolean[]{false, false, false, false, true});

        checkQueries(qf.propertyValue("s", propertyName2),
                true, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.NAME,
                new boolean[]{false, false, false, false, true});

        // lower case
        checkQueries(qf.propertyValue("s", propertyName1),
                false, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.NAME,
                new boolean[]{true, false, false, false, false});

        checkQueries(qf.propertyValue("s", propertyName2),
                false, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.NAME,
                new boolean[]{true, false, false, false, false});
    }

    public void testPropertyValue() throws RepositoryException {
        // upper case
        checkQueries(qf.propertyValue("s", propertyName1),
                true, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.STRING,
                new boolean[]{false, false, false, false, true});

        checkQueries(qf.propertyValue("s", propertyName2),
                true, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.STRING,
                new boolean[]{false, false, false, false, true});

        // lower case
        checkQueries(qf.propertyValue("s", propertyName1),
                false, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.STRING,
                new boolean[]{true, false, false, false, false});

        checkQueries(qf.propertyValue("s", propertyName2),
                false, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.STRING,
                new boolean[]{true, false, false, false, false});
    }

    public void testUpperLowerCase() throws RepositoryException {
        // first upper case, then lower case again
        checkQueries(qf.upperCase(qf.propertyValue("s", propertyName1)),
                false, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.STRING,
                new boolean[]{true, false, false, false, false});
    }

    public void testUpperCaseTwice() throws RepositoryException {
        // upper case twice
        checkQueries(qf.upperCase(qf.propertyValue("s", propertyName1)),
                true, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.STRING,
                new boolean[]{false, false, false, false, true});
    }

    public void testLowerUpperCase() throws RepositoryException {
        // first lower case, then upper case again
        checkQueries(qf.lowerCase(qf.propertyValue("s", propertyName1)),
                true, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.STRING,
                new boolean[]{false, false, false, false, true});
    }

    public void testLowerCaseTwice() throws RepositoryException {
        // lower case twice
        checkQueries(qf.lowerCase(qf.propertyValue("s", propertyName1)),
                false, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
                new String[]{"abc", "Abc", "aBc", "abC", "ABC"},
                PropertyType.STRING,
                new boolean[]{true, false, false, false, false});
    }

    //-------------------------------< internal >-------------------------------

    private void checkQueries(DynamicOperand operand,
                              boolean toUpper,
                              String operator,
                              String[] literals,
                              int type,
                              boolean[] matches) throws RepositoryException {
        for (int i = 0; i < literals.length; i++) {
            Query query = createQuery(operand, toUpper, operator, vf.createValue(literals[i], type));
            checkResult(query.execute(), matches[i] ? new Node[]{node} : new Node[0]);
        }
    }
    
    private Query createQuery(DynamicOperand operand,
                              boolean toUpper,
                              String operator,
                              Value literal) throws RepositoryException {
        if (toUpper) {
            operand = qf.upperCase(operand);
        } else {
            operand = qf.lowerCase(operand);
        }
        return qf.createQuery(
                qf.selector(testNodeType, "s"),
                qf.and(
                        qf.childNode("s", testRoot),
                        qf.comparison(
                                operand,
                                operator,
                                qf.literal(literal)
                        )
                ), null, null);
    }
}
