/*
 * Copyright 2020 LiteKite Startup. All rights reserved.
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

// IBankService.aidl
package com.litekite.connector;

// Declare any non-default types here with import statements
import com.litekite.connector.IBankServiceCallback;

/**
 * Example of defining an interface for calling on to a remote service
 * (running in another process).
 *
 * @author Vignesh S
 * @version 1.0, 11/01/2020
 * @since 1.0
 */
interface IBankService {

    /**
    * Often you want to allow a service to call back to its clients.
    * This shows how to do so, by registering a callback interface with
    * the service.
    */
    void registerCallback(IBankServiceCallback cb);

    /**
    * Remove a previously registered callback interface.
    */
    void unregisterCallback(IBankServiceCallback cb);

    /**
     * Called upon the signup request process.
     */
    void signupRequest(String username, String password);

    /**
     * Called upon the signup request process.
     */
    void loginRequest(String username, String password);

}