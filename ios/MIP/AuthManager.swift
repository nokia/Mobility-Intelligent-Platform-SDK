import Foundation
import AWSCore
import AWSMobileClient
import AWSAuthCore
import AWSAuthUI
import AWSUserPoolsSignIn
import SwiftNotes

class AuthManager {
    static func init_AppDelegate(){
        AWSMobileClient.sharedInstance().initialize { (userState, error) in
            if let userState = userState {
                print("UserState: \(userState.rawValue)")
            } else if let error = error {
                print("error: \(error.localizedDescription)")
            }
        }
    }
    
    static func signIn(navigationController: UINavigationController) {
        AWSMobileClient.sharedInstance().initialize { (userState, error) in
            if let userState = userState {
                switch(userState){
                case .signedIn:
                    DispatchQueue.main.async {
                        print("UserState: signedIN")
                    }
                case .signedOut:
                    let config = SignInUIOptions(canCancel: false, logoImage: UIImage(named: "lg_mis"), backgroundColor: UIColor(red:0.03, green:0.13, blue:0.37, alpha:1.0))
                    AWSMobileClient.sharedInstance().showSignIn(navigationController: navigationController, signInUIOptions: config, { (userState, error) in
                        if(error == nil){       //Successful signin
                            DispatchQueue.main.async {
                                print("UserState: Logged IN")
                                trigger(Notification.Name("AuthOK"))
                            }
                        }
                    })
                default:
                    AWSMobileClient.sharedInstance().signOut()
                }
                
            } else if let error = error {
                print(error.localizedDescription)
            }
        }
    }
}
