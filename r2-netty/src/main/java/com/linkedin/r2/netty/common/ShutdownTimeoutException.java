/*
   Copyright (c) 2019 LinkedIn Corp.

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

package com.linkedin.r2.netty.common;

import java.util.concurrent.TimeoutException;

/**
 * @author Nizar Mankulangara
 */
public class ShutdownTimeoutException extends TimeoutException
{
  private static final long serialVersionUID = 1900926677490660714L;

  public ShutdownTimeoutException(String message)
  {
    super(message);
  }
}
