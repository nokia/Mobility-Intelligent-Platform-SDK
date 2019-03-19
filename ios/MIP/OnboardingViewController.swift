import UIKit
import AWSAuthCore
import AWSAuthUI
import CoreLocation
import SwiftNotes
import LocoKit
import UserNotifications

class OnboardingViewController: UIViewController  {
    
    var userDefaults:UserDefaults!
    @IBOutlet weak var userType: UIPickerView!
    var pickerData: [String] = [String]()
    var userTypeSelection = "#TeamEPAPS"
    var locationObserver : NSObjectProtocol!
    var notificationGranted = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        userDefaults = UserDefaults.standard
        addObservers()
        manageUserTagPicker()
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
        let firstOnboarding = userDefaults.bool(forKey: "OnboardingAgree_V2.4.3_21")
        if (firstOnboarding){
            actionOnAgree()
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func manageUserTagPicker(){
        pickerData = ["#TeamEPAPS", "#TeamCPS", "#TeamSQY", "#TeamVGP", "#TeamUPSaclay", "#TeamVedecom", "#TeamNokia", "#TeamTransdev", "#TeamEDF", "#TeamOuiShare", "#Resident", "#Etudiant", "#Salarié"]
        self.userType.delegate = self
        self.userType.dataSource = self
        let usertag = self.userDefaults.string(forKey: "UserTag")
        if (usertag != nil){
            var rowIndex = pickerData.index(of: usertag!)
            if(rowIndex == nil) { rowIndex = 0 }
            userType.selectRow(rowIndex!, inComponent: 0, animated: false)
            self.userDefaults.set(userTypeSelection, forKey: "UserTag")
        }
    }
    
    func addObservers(){
        when(Notification.Name("locationOK")) { _ in
            self.locationObserver = when(LocomotionManager.highlander, does: .didChangeAuthorizationStatus) {_ in
                switch(CLLocationManager.authorizationStatus()) {
                case .notDetermined, .restricted, .denied:
                    self.createSettingsAlertController(title: "Activation GPS", message: "Nous aurions besoin de votre position GPS pour le partage de vos données de mobilité et la navigation. \n Settings > Privacy > Location Services")
                case .authorizedAlways, .authorizedWhenInUse:
                    print("Access")
                    self.signIn()
                }
                print(".didChangeAuthorizationStatus")
            }
        }
    
        when(Notification.Name("AuthOK")) { _ in
            self.onSignIn ()
        }
    }
    
    @IBAction func onAgree(_ sender: Any) {
        actionOnAgree()
        userDefaults.set(true, forKey: "OnboardingAgree_V2.4.3_21")
    }
    
    func actionOnAgree(){
        if !CLLocationManager.locationServicesEnabled() {
            self.createSettingsAlertController(title: "Activation GPS", message: "Nous aurions besoin de votre position GPS pour le partage de vos données de mobilité et la navigation. \n Settings > Privacy > Location Services")
        } else {
            self.signIn()
        }
    }
    
    func signIn() {
        if(AWSSignInManager.sharedInstance().isLoggedIn){
            onSignIn()
        } else {
            AuthManager.signIn(navigationController: self.navigationController!)
        }
    }
    
    func onSignIn () {
        gotoMain()
    }
    
    
    func gotoMain(){
        NotificationCenter.default.removeObserver(locationObserver)
        DispatchQueue.main.async {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let controller = storyboard.instantiateViewController(withIdentifier: "MainViewController")
            self.present(controller, animated: true, completion: nil)
        }
    }
    
    
    func addReminder() {
        UNUserNotificationCenter.current().requestAuthorization(
            options: [.alert,.sound])
        {
            (granted, error) in
            self.notificationGranted = granted
            if let error = error {
                print("granted, but Error in notification permission:\(error.localizedDescription)")
            }
        }
        
        addReminderAt(hour: 8, identifier: "mis.reminder.am")
        addReminderAt(hour: 17, identifier: "mis.reminder.pm")
    }
    
    func addReminderAt(hour:Int, identifier:String){
        var dateComponents = DateComponents()
        dateComponents.hour = hour
        
        let content = UNMutableNotificationContent()
        content.title = "Gagnez des MIPS ..."
        content.body = "N'oubliez pas d'activer le partage de vos données aujourd'hui !"
        content.categoryIdentifier = "mis.reminder.category"
        let trigger = UNCalendarNotificationTrigger(
            dateMatching: dateComponents,
            repeats: true)
        let request = UNNotificationRequest(identifier: identifier, content: content, trigger: trigger)
        
        UNUserNotificationCenter.current().add(request) { (error) in
            if let error = error {
                print("error in reminder: \(error.localizedDescription)")
            }
        }
    }
    
}

extension OnboardingViewController: UIPickerViewDelegate, UIPickerViewDataSource {
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return pickerData.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return pickerData[row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        userTypeSelection = pickerData[row]
        self.userDefaults.set(userTypeSelection, forKey: "UserTag")
    }
}

