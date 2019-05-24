import UIKit
import MoveInSaclaySDK

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        MoveInSaclaySDK.init()
        MoveInSaclaySDK.setupMoveInSaclay(application: application, options: launchOptions)
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        MoveInSaclaySDK.moveInSaclayPermissionRequest()
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        MoveInSaclaySDK.moveInSaclayPermissionRequest()
    }

}

