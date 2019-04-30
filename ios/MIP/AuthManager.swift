//  Created by Ayoub Benyahya
//  Copyright © 2019 Ayoub Benyahya. All rights reserved.

import Foundation
import AWSCore
import AWSMobileClient
import AWSAuthCore
import AWSAuthUI
import AWSUserPoolsSignIn
import SwiftNotes

class AuthManager {
    static func init_AppDelegate(){
        AWSSignInManager.sharedInstance().register(signInProvider: AWSCognitoUserPoolsSignInProvider.sharedInstance())
    }
    
    static func signIn(navigationController: UINavigationController) {
        if !AWSSignInManager.sharedInstance().isLoggedIn {
            let config = AWSAuthUIConfiguration()
            config.enableUserPoolsUI = true
            config.canCancel = false
            config.logoImage = UIImage(named: "lg_mis")
            config.backgroundColor = UIColor(red:0.03, green:0.13, blue:0.37, alpha:1.0)
            AWSAuthUIViewController
                .presentViewController(with: navigationController,
                                       configuration: config,
                                       completionHandler: { (provider: AWSSignInProvider, error: Error?) in
                                        if error != nil {
                                            print("Error occurred: \(String(describing: error))")
                                        } else {
                                            trigger(Notification.Name("AuthOK"))
                                        }
                })
        }
    }
}
