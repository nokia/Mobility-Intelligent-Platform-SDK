import UIKit

class PortailViewController: UIViewController {

    @IBOutlet weak var portailWebview: UIWebView!
    override func viewDidLoad() {
        super.viewDidLoad()
        loadPortail ()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func loadPortail () {
        let myURL = URL(string: "https://moveinsaclay.app")
        let myRequest = URLRequest(url: myURL!)
        portailWebview.loadRequest(myRequest)
    }

}
