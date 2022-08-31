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

import java.util.List;

import com.esri.arcgisruntime.utilitynetworks.UtilityTraceParameters;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceResult;

/**
 * A model for a complete trace from a {@link UtilityNetworkTraceTool} that enables access to the results.
 *
 * @since 100.15.0
 */
public class UtilityNetworkTraceToolCompletedTrace {

  private final List<UtilityTraceResult> utilityTraceResults;
  private final Exception exception;
  private final UtilityTraceParameters utilityTraceParameters;

  /**
   * Creates a UtilityNetworkTraceToolCompletedTrace.
   *
   * @param utilityTraceResults the results associated with the trace
   * @param exception the exception associated with an unsuccessful result
   * @param utilityTraceParameters the utility trace parameters used to run the trace
   * @since 100.15.0
   */
  protected UtilityNetworkTraceToolCompletedTrace(
    List<UtilityTraceResult> utilityTraceResults,
    Exception exception,
    UtilityTraceParameters utilityTraceParameters) {
    this.utilityTraceResults = utilityTraceResults;
    this.exception = exception;
    this.utilityTraceParameters = utilityTraceParameters;
  }

  /**
   * Returns the utility trace results associated with the completed trace.
   *
   * @return a list of the results
   * @since 100.15.0
   */
  public List<UtilityTraceResult> getUtilityTraceResults() {
    return utilityTraceResults;
  }

  /**
   * Returns the exception resulting from a failed trace.
   *
   * @return the exception. Null if the trace was successful.
   * @since 100.15.0
   */
  public Exception getException() {
    return exception;
  }

  /**
   * Returns the UtilityTraceParameters used in the trace.
   *
   * @return the utility trace parameters
   * @since 100.15.0
   */
  public UtilityTraceParameters getUtilityTraceParameters() {
    return utilityTraceParameters;
  }
}
