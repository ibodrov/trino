/*
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
package io.trino.testing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public enum TestingConnectorBehavior
{
    SUPPORTS_ARRAY,
    SUPPORTS_ROW_TYPE,

    SUPPORTS_PREDICATE_PUSHDOWN,
    SUPPORTS_PREDICATE_PUSHDOWN_WITH_VARCHAR_EQUALITY(SUPPORTS_PREDICATE_PUSHDOWN),
    SUPPORTS_PREDICATE_PUSHDOWN_WITH_VARCHAR_INEQUALITY(SUPPORTS_PREDICATE_PUSHDOWN),
    SUPPORTS_PREDICATE_EXPRESSION_PUSHDOWN(SUPPORTS_PREDICATE_PUSHDOWN),
    SUPPORTS_PREDICATE_ARITHMETIC_EXPRESSION_PUSHDOWN(SUPPORTS_PREDICATE_EXPRESSION_PUSHDOWN),
    SUPPORTS_PREDICATE_EXPRESSION_PUSHDOWN_WITH_LIKE(SUPPORTS_PREDICATE_EXPRESSION_PUSHDOWN),

    SUPPORTS_DYNAMIC_FILTER_PUSHDOWN(false),

    SUPPORTS_LIMIT_PUSHDOWN,

    SUPPORTS_TOPN_PUSHDOWN,
    SUPPORTS_TOPN_PUSHDOWN_WITH_VARCHAR(and(SUPPORTS_TOPN_PUSHDOWN, SUPPORTS_PREDICATE_PUSHDOWN_WITH_VARCHAR_INEQUALITY)),

    SUPPORTS_AGGREGATION_PUSHDOWN,
    // Most connectors don't support aggregation pushdown for statistical functions
    SUPPORTS_AGGREGATION_PUSHDOWN_STDDEV(false),
    SUPPORTS_AGGREGATION_PUSHDOWN_VARIANCE(false),
    SUPPORTS_AGGREGATION_PUSHDOWN_COVARIANCE(false),
    SUPPORTS_AGGREGATION_PUSHDOWN_CORRELATION(false),
    SUPPORTS_AGGREGATION_PUSHDOWN_REGRESSION(false),
    SUPPORTS_AGGREGATION_PUSHDOWN_COUNT_DISTINCT(false),

    SUPPORTS_JOIN_PUSHDOWN(
            // Currently no connector supports Join pushdown by default. JDBC connectors may support Join pushdown and BaseJdbcConnectorTest
            // verifies truthfulness of SUPPORTS_JOIN_PUSHDOWN declaration, so it is a safe default.
            false),
    SUPPORTS_JOIN_PUSHDOWN_WITH_FULL_JOIN(SUPPORTS_JOIN_PUSHDOWN),
    SUPPORTS_JOIN_PUSHDOWN_WITH_DISTINCT_FROM(SUPPORTS_JOIN_PUSHDOWN),
    SUPPORTS_JOIN_PUSHDOWN_WITH_VARCHAR_EQUALITY(and(SUPPORTS_JOIN_PUSHDOWN, SUPPORTS_PREDICATE_PUSHDOWN_WITH_VARCHAR_EQUALITY)),
    SUPPORTS_JOIN_PUSHDOWN_WITH_VARCHAR_INEQUALITY(and(SUPPORTS_JOIN_PUSHDOWN, SUPPORTS_PREDICATE_PUSHDOWN_WITH_VARCHAR_INEQUALITY)),

    SUPPORTS_CREATE_SCHEMA,
    // Expect rename to be supported when create schema is supported, to help make connector implementations coherent.
    SUPPORTS_RENAME_SCHEMA(SUPPORTS_CREATE_SCHEMA),

    SUPPORTS_CREATE_TABLE,
    SUPPORTS_CREATE_TABLE_WITH_DATA(SUPPORTS_CREATE_TABLE),
    SUPPORTS_CREATE_TABLE_WITH_TABLE_COMMENT(SUPPORTS_CREATE_TABLE),
    SUPPORTS_CREATE_TABLE_WITH_COLUMN_COMMENT(SUPPORTS_CREATE_TABLE),
    SUPPORTS_RENAME_TABLE,
    SUPPORTS_RENAME_TABLE_ACROSS_SCHEMAS(SUPPORTS_RENAME_TABLE),

    SUPPORTS_ADD_COLUMN,
    SUPPORTS_ADD_COLUMN_WITH_COMMENT(SUPPORTS_ADD_COLUMN),
    SUPPORTS_DROP_COLUMN(SUPPORTS_ADD_COLUMN),
    SUPPORTS_DROP_FIELD(and(SUPPORTS_DROP_COLUMN, SUPPORTS_ROW_TYPE)),
    SUPPORTS_RENAME_COLUMN,
    SUPPORTS_SET_COLUMN_TYPE,

    SUPPORTS_COMMENT_ON_TABLE,
    SUPPORTS_COMMENT_ON_VIEW(false),
    SUPPORTS_COMMENT_ON_COLUMN,
    SUPPORTS_COMMENT_ON_VIEW_COLUMN(false),

    SUPPORTS_CREATE_VIEW(false),

    SUPPORTS_CREATE_MATERIALIZED_VIEW(false),
    SUPPORTS_CREATE_MATERIALIZED_VIEW_GRACE_PERIOD(SUPPORTS_CREATE_MATERIALIZED_VIEW),
    SUPPORTS_CREATE_FEDERATED_MATERIALIZED_VIEW(SUPPORTS_CREATE_MATERIALIZED_VIEW), // i.e. an MV that spans catalogs
    SUPPORTS_MATERIALIZED_VIEW_FRESHNESS_FROM_BASE_TABLES(SUPPORTS_CREATE_MATERIALIZED_VIEW),
    SUPPORTS_RENAME_MATERIALIZED_VIEW(SUPPORTS_CREATE_MATERIALIZED_VIEW),
    SUPPORTS_RENAME_MATERIALIZED_VIEW_ACROSS_SCHEMAS(SUPPORTS_RENAME_MATERIALIZED_VIEW),

    SUPPORTS_INSERT,
    SUPPORTS_NOT_NULL_CONSTRAINT(SUPPORTS_CREATE_TABLE),
    SUPPORTS_ADD_COLUMN_NOT_NULL_CONSTRAINT(and(SUPPORTS_NOT_NULL_CONSTRAINT, SUPPORTS_ADD_COLUMN)),

    SUPPORTS_DELETE(false),
    SUPPORTS_ROW_LEVEL_DELETE(SUPPORTS_DELETE),

    SUPPORTS_UPDATE(false),

    SUPPORTS_MERGE(false),

    SUPPORTS_TRUNCATE(false),

    SUPPORTS_NEGATIVE_DATE,

    SUPPORTS_CANCELLATION(false),

    SUPPORTS_MULTI_STATEMENT_WRITES(false),

    SUPPORTS_NATIVE_QUERY(true), // system.query or equivalent PTF for query passthrough

    /**/;

    private final Predicate<Predicate<TestingConnectorBehavior>> hasBehaviorByDefault;

    TestingConnectorBehavior()
    {
        this(true);
    }

    TestingConnectorBehavior(boolean hasBehaviorByDefault)
    {
        this(fallback -> hasBehaviorByDefault);
    }

    TestingConnectorBehavior(TestingConnectorBehavior defaultBehaviorSource)
    {
        this(fallback -> fallback.test(defaultBehaviorSource));
    }

    TestingConnectorBehavior(Predicate<Predicate<TestingConnectorBehavior>> hasBehaviorByDefault)
    {
        this.hasBehaviorByDefault = requireNonNull(hasBehaviorByDefault, "hasBehaviorByDefault is null");
    }

    /**
     * Determines whether a connector should be assumed to have this behavior.
     *
     * @param fallback callback to be used to inspect connector behaviors, if this behavior is assumed to be had conditionally
     */
    // Intentionally package private. This is just a convenience method for sharing behavior between reusable test base classes.
    // If it was to be public, a different API would need to be provided.
    boolean hasBehaviorByDefault(Predicate<TestingConnectorBehavior> fallback)
    {
        return hasBehaviorByDefault.test(fallback);
    }

    private static Predicate<Predicate<TestingConnectorBehavior>> and(TestingConnectorBehavior first, TestingConnectorBehavior... rest)
    {
        List<TestingConnectorBehavior> conjuncts = ImmutableList.copyOf(Lists.asList(first, rest));
        return fallback -> conjuncts.stream().allMatch(fallback);
    }
}
