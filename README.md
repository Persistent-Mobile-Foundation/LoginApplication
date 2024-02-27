PSL Mobile Foundation
===
## PinCodeSwift
A sample application demonstrating use of the CredentialsValidation Security Check.

### Tutorials
https://pmf.persistentproducts.com/tutorials/en/foundation/9.0/authentication-and-security/user-authentication/

### Usage

1. Use either Maven, MobileFoundation CLI or your IDE of choice to build and deploy the available `ResourceAdapter` and `UserLogin` adapters](https://pmf.persistentproducts.com/tutorials/en/foundation/9.0/adapters/creating-adapters/).

 The PinCodeAttempts Security Check adapter can be found in the url 
2. From a command-line window, navigate to the project's root folder and run the commands:
 - `pmfdev app register` - to register the application.
 - `pmfdev app push` - to map the `accessRestricted` scope to the `PinCodeAttempts` security check.


> **Tip:** you can update the bundled SDK by running the command `pod update` from the project's root folder.

### Supported Levels
PSL Mobile Foundation 9.0
