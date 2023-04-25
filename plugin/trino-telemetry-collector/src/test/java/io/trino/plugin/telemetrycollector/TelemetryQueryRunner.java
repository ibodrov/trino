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
package io.trino.plugin.telemetrycollector;

import com.google.common.collect.ImmutableMap;
import io.airlift.log.Logger;
import io.trino.testing.DistributedQueryRunner;

import java.util.Map;

import static io.trino.plugin.base.util.Closables.closeAllSuppress;
import static io.trino.testing.TestingSession.testSessionBuilder;

public final class TelemetryQueryRunner
{
    private TelemetryQueryRunner() {}

    public static DistributedQueryRunner createQueryRunner()
            throws Exception
    {
        return builder()
                .build();
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
            extends DistributedQueryRunner.Builder<Builder>
    {
        protected Builder()
        {
            super(testSessionBuilder().build());
        }

        @Override
        public DistributedQueryRunner build()
                throws Exception
        {
            DistributedQueryRunner queryRunner = super.build();
            try {
                queryRunner.installPlugin(new TrinoTelemetryPlugin());
                queryRunner.createCatalog("telemetry_data", "telemetry_data", ImmutableMap.of());
                return queryRunner;
            }
            catch (Exception e) {
                closeAllSuppress(e, queryRunner);
                throw e;
            }
        }
    }

    public static final class SimpleTelemetryQueryRunnerMain
    {
        private SimpleTelemetryQueryRunnerMain() {}

        public static void main(String[] args)
                throws Exception
        {
            DistributedQueryRunner queryRunner = TelemetryQueryRunner.builder()
                    .setNodeCount(1)
                    .setCoordinatorProperties(Map.of("http-server.http.port", "8080"))
                    .build();

            Logger log = Logger.get(SimpleTelemetryQueryRunnerMain.class);
            log.info("======== SERVER STARTED ========");
            log.info("\n====\n%s\n====", queryRunner.getCoordinator().getBaseUrl());
        }
    }
}
