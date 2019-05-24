//
//  PortailViewController.swift
//  MIP
//
//  Created by Ayoub Benyahya on 3/6/19.
//  Copyright © 2019 Ayoub Benyahya. All rights reserved.
//

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
        let myURL = URL(string: "https://moveinsaclay.app/citoyen")
        let myRequest = URLRequest(url: myURL!)
        portailWebview.loadRequest(myRequest)
    }

}
