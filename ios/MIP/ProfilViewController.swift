//  Created by Ayoub Benyahya
//  Copyright © 2019 Ayoub Benyahya. All rights reserved.

import UIKit
import Charts

class ProfilViewController: UIViewController {

    @IBOutlet weak var modeUser: PieChartView!
    override func viewDidLoad() {
        super.viewDidLoad()
        setupChart ()
        ProfileManager.doInvokeProfilAPI_plus(modeUser: modeUser)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    func setupChart (){
        modeUser.chartDescription?.text = "Pourcentage d'utilisation des modes de transport"
        modeUser.backgroundColor = UIColor(red:0.02, green:0.12, blue:0.33, alpha:1.0)
        modeUser.holeColor = UIColor(red:0.02, green:0.12, blue:0.33, alpha:1.0)
        modeUser.chartDescription?.textColor = UIColor.white
        modeUser.legend.textColor = UIColor.white
    }

}
