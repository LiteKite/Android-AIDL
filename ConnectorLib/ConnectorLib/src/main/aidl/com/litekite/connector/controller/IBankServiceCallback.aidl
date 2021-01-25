/*
 * Copyright 2021 LiteKite Startup. All rights reserved.
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

// IBankServiceCallback.aidl
package com.litekite.connector.controller;

// Declare any non-default types here with import statements
import com.litekite.connector.entity.AuthResponse;
import com.litekite.connector.entity.UserDetails;
import com.litekite.connector.entity.FailureResponse;

/**
 * Example of a callback interface used by IBankService to send
 * synchronous notifications back to its clients.  Note that this is a
 * one-way interface so the server does not block waiting for the client.
 *
 * @author Vignesh S
 * @version 1.0, 11/01/2021
 * @since 1.0
 */
interface IBankServiceCallback {

    /**
     * Called upon the signup request process.
     */
    void onSignupResponse(in AuthResponse authResponse);

    /**
     * Called upon the login request process.
     */
    void onLoginResponse(in AuthResponse authResponse);

    /**
     * Called upon the signup request process.
     */
    void onUserDetailsResponse(in UserDetails userDetails);

    /**
     * Called upon the signup request process.
     */
    void onCurrentBalanceChanged(double currentBalance);

    /**
     * Called upon any failure occurs with request process.
     */
    void onFailureResponse(in FailureResponse failureResponse);

}