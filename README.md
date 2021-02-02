# Android-AIDL

Making <b>Inter Process Communication (IPC) via Android Interface Definition Language (AIDL)</b> that helps to communicate between two or more different apps that are running on different processes.

## Getting Started

1) Install Server and Client app apks on the device.

2) Launch client app and perform login, sign-up, deposit and withdrawal operations. These operations are handled with <b>AIDL Binders</b> that are shared with client applications by server application.

3) Server app runs a BankService, a service that shares AIDL binders with the client applications which are all bound with the service.

4) Server app stores and maintains user credentials in a Database.

5) Connector library has AIDL interfaces that are commonly shared with server and client app.

6) Client app binds with the BankService with the help of BankServiceController and BankServiceConnector from Connector library that handles service connections, requests and responses with the BankService.

##

<br>

<p align="center">
    <img src="https://github.com/svignesh93/Android-AIDL/blob/assets/assets/aidl_login.png" alt="AIDL Login Screen" width="30%">
    <img src="https://github.com/svignesh93/Android-AIDL/blob/assets/assets/aidl_sign_up.png" alt="AIDL Sign Up" width="30%" hspace="2%">
    <img src="https://github.com/svignesh93/Android-AIDL/blob/assets/assets/aidl_home.png" alt="AIDL Home" width="30%">
</p>

##

#### please note that, due to background restrictions on Android running API level 30 and above, third-party application can no longer start or bind with the service that belongs to another process or app.

#### You may need to start or bind a service as a foreground service for Android API level 29.

#### It is recommended to start or bind with the service that are present in the same application.

#### If you are developing system level android apps for a custom android system, there will be no restrictions applied.

## Libraries Used

`Data Binding Library` -> for updating, handling views from layouts with ViewModels.</br>

`Lifecycle Components` -> LiveData for observing changes and ViewModel for MVVM Architecture.</br>

`Room Persistence Storage` -> An ORM for SQLite Database.</br>

`Hilt DI` -> Dependency Injection Library for Android.</br>

`Kotlin Coroutines Library` -> A light-weight concurrency thread handles async and blocking works.</br>

## Support

If you've found an error in this sample, please file an issue:
https://github.com/LiteKite/Android-AIDL/issues

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub.

## License

~~~

Copyright 2021 LiteKite Startup

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

~~~
