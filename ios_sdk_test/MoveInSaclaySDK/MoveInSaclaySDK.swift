//
//  MoveInSaclaySDK.swift
//  MoveInSaclaySDK
//
//  Created by Vadim Plasiciuc on 09/05/2019.
//

import UIKit
import AWSAuthCore
import AWSUserPoolsSignIn
import SwiftNotes
import CoreLocation

@objc(MoveInSaclaySDK)
public class MoveInSaclaySDK: NSObject {
  
  @objc public class func setupMoveInSaclay(application: UIApplication, options: [UIApplication.LaunchOptionsKey: Any]?) {
    AuthManager.init_AppDelegate()
    let didFinishLaunching = AWSSignInManager.sharedInstance().interceptApplication(application, didFinishLaunchingWithOptions: options)
    AWSSignInManager.sharedInstance().resumeSession(completionHandler: { (result: Any?, error: Error?) in
      print("User Signed in")
    })
  }
  
  @objc public class func moveInSaclayPermissionRequest() {
    LocomotionManager.highlander.requestLocationPermission(background: true)
  }
  
  @objc public class func application(_ application: UIApplication, open url: URL, sourceApplication: String?, annotation: Any) -> Bool {
    AWSSignInManager.sharedInstance().interceptApplication(application, open: url, sourceApplication: sourceApplication, annotation: annotation)
    return true
  }
  
  public static let bundleName: String = "MoveInSaclaySDKBundle"
  
  @objc public static var resourcesBundle: Bundle {
    return resourcesBundle(name: bundleName, classType: MoveInSaclaySDK.self)!
  }
  
  private static func resourcesBundle(name: String, classType: AnyClass) -> Bundle? {
    let bundle = Bundle(for: classType)
    let podBundleUrl = bundle.url(forResource: name, withExtension: "bundle")!
    
    return Bundle(url: podBundleUrl)
  }
}
