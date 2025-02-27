// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.flink.sink.writer;

import org.apache.doris.flink.catalog.doris.FieldSchema;

import org.apache.flink.shaded.guava30.com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class SchemaChangeHelperTest {

    private final Map<String, FieldSchema> originFieldSchemaMap = Maps.newHashMap();
    private final Map<String, FieldSchema> updateFieldSchemaMap = Maps.newHashMap();

    @Before
    public void setUp() {
        originFieldSchemaMap.put("id", new FieldSchema("id", "INT", "", ""));
        originFieldSchemaMap.put("c2", new FieldSchema("c2", "INT", "", ""));
        originFieldSchemaMap.put("c3", new FieldSchema("c3", "VARCHAR(30)", "", ""));

        updateFieldSchemaMap.put("id", new FieldSchema("id", "INT", "", ""));
        updateFieldSchemaMap.put("c2", new FieldSchema("c2", "INT", "", ""));
        updateFieldSchemaMap.put("c3", new FieldSchema("c3", "VARCHAR(30)", "", ""));
        updateFieldSchemaMap.put("c4", new FieldSchema("c4", "BIGINT", "", ""));
        updateFieldSchemaMap.put("c5", new FieldSchema("c5", "DATETIMEV2(0)", "", ""));
    }

    @Test
    public void testGenerateRenameDDLSql() {
        String table = "test.test_sink";
        String oldColumnName = "c3";
        String newColumnName = "c33";
        List<String> ddlSqls = SchemaChangeHelper.generateRenameDDLSql(table, oldColumnName, newColumnName,
                originFieldSchemaMap);
        Assert.assertEquals(ddlSqls.get(0), "ALTER TABLE test.test_sink RENAME COLUMN c3 c33");
    }

    @Test
    public void testGenerateDDLSql() {
        SchemaChangeHelper.compareSchema(updateFieldSchemaMap, originFieldSchemaMap);
        List<String> ddlSqls = SchemaChangeHelper.generateDDLSql("test.test_sink");
        Assert.assertEquals(ddlSqls.get(0), "ALTER TABLE test.test_sink ADD COLUMN c4 BIGINT");
        Assert.assertEquals(ddlSqls.get(1), "ALTER TABLE test.test_sink ADD COLUMN c5 DATETIMEV2(0)");
    }
}
