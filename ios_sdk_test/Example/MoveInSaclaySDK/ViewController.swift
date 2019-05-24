import UIKit
import MoveInSaclaySDK

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        let storyboard = UIStoryboard(name: "MoveInSaclay", bundle: MoveInSaclaySDK.resourcesBundle)
        let controller = storyboard.instantiateViewController(withIdentifier: "onboarding")
        self.navigationController?.pushViewController(controller, animated: true)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}

