/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openjpa.meta;

/**
 * Comparator that keeps metadatas in inheritance order.  Also places relation
 * types used as primary keys before the primary key field owner types.
 *
 * @author Abe White
 */
public class MetaDataInheritanceComparator
    extends InheritanceComparator {

    
    private static final long serialVersionUID = 1L;

    @Override
    protected Class toClass(Object elem) {
        if (elem == null)
            return null;
        return ((ClassMetaData) elem).getDescribedType();
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == o2)
            return 0;
        if (o1 == null)
            return -1;
        if (o2 == null)
            return 1;

        ClassMetaData m1 = (ClassMetaData) o1;
        ClassMetaData m2 = (ClassMetaData) o2;

        FieldMetaData[] fmds = m1.getDeclaredFields();
        for (FieldMetaData fieldMetaData : fmds) {
            if (fieldMetaData.isPrimaryKey() && m2.getDescribedType().
                    isAssignableFrom(fieldMetaData.getDeclaredType()))
                return 1;
        }
        fmds = m2.getDeclaredFields();
        for (FieldMetaData fmd : fmds) {
            if (fmd.isPrimaryKey() && m1.getDescribedType().
                    isAssignableFrom(fmd.getDeclaredType()))
                return -1;
        }
        return super.compare(o1, o2);
    }
}
