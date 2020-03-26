/*
   Copyright (c) 2020 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.darkcluster;

import java.util.concurrent.Future;

import com.linkedin.common.callback.Callback;
import com.linkedin.common.util.None;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.r2.message.rest.RestRequest;
import com.linkedin.r2.message.rest.RestResponse;
import com.linkedin.r2.transport.common.Client;

public class MockClient implements Client
{
  private final boolean _failRequests;

  public MockClient(boolean failRequests)
  {
    _failRequests = failRequests;
  }

  @Override
  public Future<RestResponse> restRequest(RestRequest request)
  {
    return null;
  }

  @Override
  public Future<RestResponse> restRequest(RestRequest request, RequestContext requestContext)
  {
    return null;
  }

  @Override
  public void restRequest(RestRequest request, Callback<RestResponse> callback)
  {
    callback.onSuccess(new TestRestResponse());
  }

  @Override
  public void restRequest(RestRequest request, RequestContext requestContext, Callback<RestResponse> callback)
  {
    if (_failRequests)
    {
      callback.onError(new RuntimeException("test"));
    }
    else
    {
      callback.onSuccess(new TestRestResponse());
    }
  }

  @Override
  public void shutdown(Callback<None> callback)
  {

  }
}