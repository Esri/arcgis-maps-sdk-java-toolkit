/*
 * Copyright 2022 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.utilitynetworks.UtilityTraceParameters;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceResult;

import java.util.List;

public class UtilityNetworkTraceToolCompletedTrace {

    private final List<UtilityTraceResult> utilityTraceResults;
    private final Exception exception;
    private final UtilityTraceParameters utilityTraceParameters;

    protected UtilityNetworkTraceToolCompletedTrace(
            List<UtilityTraceResult> utilityTraceResults,
            Exception exception,
            UtilityTraceParameters utilityTraceParameters) {
        this.utilityTraceResults = utilityTraceResults;
        this.exception = exception;
        this.utilityTraceParameters = utilityTraceParameters;
    }

    public List<UtilityTraceResult> getUtilityTraceResults() { return utilityTraceResults; }

    public Exception getException() { return exception; }

    public UtilityTraceParameters getUtilityTraceParameters() { return utilityTraceParameters; }
}
