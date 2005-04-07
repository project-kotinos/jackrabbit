/*
 * Copyright 2004-2005 The Apache Software Foundation or its licensors,
 *                     as applicable.
  *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.core.xml;

import org.apache.jackrabbit.core.NamespaceResolver;
import org.apache.jackrabbit.core.QName;

import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * The <code>Importer</code> interface ...
 */
public interface Importer {

    public static final int IMPORT_UUID_CREATE_NEW =
            Workspace.IMPORT_UUID_CREATE_NEW;
    public static final int IMPORT_UUID_COLLISION_REMOVE_EXISTING =
            Workspace.IMPORT_UUID_COLLISION_REMOVE_EXISTING;
    public static final int IMPORT_UUID_COLLISION_REPLACE_EXISTING =
            Workspace.IMPORT_UUID_COLLISION_REPLACE_EXISTING;
    public static final int IMPORT_UUID_COLLISION_THROW =
            Workspace.IMPORT_UUID_COLLISION_THROW;

    /**
     * @throws RepositoryException
     */
    public void start() throws RepositoryException;

    /**
     * @param nodeInfo
     * @param propInfos list of <code>PropInfo</code> instances
     * @param nsContext prefix mappings of current context
     * @throws RepositoryException
     */
    public void startNode(NodeInfo nodeInfo, List propInfos,
                          NamespaceResolver nsContext) throws RepositoryException;

    /**
     * @param nodeInfo
     * @throws RepositoryException
     */
    public void endNode(NodeInfo nodeInfo) throws RepositoryException;

    /**
     * @throws RepositoryException
     */
    public void end() throws RepositoryException;

    //--------------------------------------------------------< inner classes >
    public static class NodeInfo {
        private QName name;
        private QName nodeTypeName;
        private QName[] mixinNames;
        private String uuid;

        public NodeInfo() {
        }

        public NodeInfo(QName name, QName nodeTypeName, QName[] mixinNames,
                        String uuid) {
            this.name = name;
            this.nodeTypeName = nodeTypeName;
            this.mixinNames = mixinNames;
            this.uuid = uuid;
        }

        public void setName(QName name) {
            this.name = name;
        }

        public QName getName() {
            return name;
        }

        public void setNodeTypeName(QName nodeTypeName) {
            this.nodeTypeName = nodeTypeName;
        }

        public QName getNodeTypeName() {
            return nodeTypeName;
        }

        public void setMixinNames(QName[] mixinNames) {
            this.mixinNames = mixinNames;
        }

        public QName[] getMixinNames() {
            return mixinNames;
        }

        public void setUUID(String uuid) {
            this.uuid = uuid;
        }

        public String getUUID() {
            return uuid;
        }
    }

    public static class PropInfo {
        private QName name;
        private int type;
        private TextValue[] values;

        public PropInfo() {
        }

        public PropInfo(QName name, int type, TextValue[] values) {
            this.name = name;
            this.type = type;
            this.values = values;
        }

        public void setName(QName name) {
            this.name = name;
        }

        public QName getName() {
            return name;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setValues(TextValue[] values) {
            this.values = values;
        }

        public TextValue[] getValues() {
            return values;
        }
    }

    /**
     * <code>TextValue</code> represents a serialized property value read
     * from a System or Document View XML document.
     */
    public interface TextValue {
        /**
         * Returns the length of the serialized value.
         *
         * @return the length of the serialized value
         * @throws IOException if an I/O error occurs
         */
        public long length() throws IOException;

        /**
         * Retrieves the serialized value.
         *
         * @return the serialized value
         * @throws IOException if an I/O error occurs
         */
        public String retrieve() throws IOException;

        /**
         * Returns a <code>Reader</code> for reading the serialized value.
         *
         * @return a <code>Reader</code> for reading the serialized value.
         * @throws IOException if an I/O error occurs
         */
        public Reader reader() throws IOException;
    }
}
