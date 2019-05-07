//
//  UITools.swift
//  MIP
//
//  Created by Ayoub Benyahya on 1/2/19.
//  Copyright © 2019 Ayoub Benyahya. All rights reserved.
//

import Foundation
import SwiftNotes
import AWSAuthCore
import AWSAuthUI
import AWSMobileClient

extension UIViewController {
    func createSettingsAlertController(title: String, message: String) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let settingsAction = UIAlertAction(title: NSLocalizedString("Settings", comment: ""), style: .default) { (UIAlertAction) in
            UIApplication.shared.open(URL(string: "App-Prefs:root=LOCATION_SERVICES")! as URL, options: [:], completionHandler: nil)
            
            trigger(Notification.Name("locationOK"))
        }
        
        alertController.addAction(settingsAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func createDeleteDataController() {
        let alertController = UIAlertController(title: "Confirmation", message: "Etes-vous sur de vouloir vous deconnecter de l'application MoveInSaclay ?", preferredStyle: .alert)
        let settingsAction = UIAlertAction(title: NSLocalizedString("Oui", comment: ""), style: .default) { (UIAlertAction) in
            trigger(Notification.Name("DeleteDATAOK"))
            print("DeleteDATAOK")
            AWSMobileClient.sharedInstance().signOut(completionHandler: { (error) in
                print("Logout")
            })
            exit(0)
        }
        
        let cancelAction = UIAlertAction(title: NSLocalizedString("Non", comment: ""), style: .destructive) { (UIAlertAction) in
            trigger(Notification.Name("DeleteDATANO"))
            print("DeleteDATANO")
        }
        
        alertController.addAction(settingsAction)
        alertController.addAction(cancelAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func createRegularTripController() {
        let alertController = UIAlertController(title: "Pas Encore identifié", message: "Nous sommes pas encore en mesure de vous presenter vos trajets récurrents.", preferredStyle: .alert)
        let okAction = UIAlertAction(title: NSLocalizedString("OK", comment: ""), style: .default) { (UIAlertAction) in
            print("OK")
        }
        alertController.addAction(okAction)
        self.present(alertController, animated: true, completion: nil)
    }
}


import UIKit

@IBDesignable public class RoundedView: UIView {
    
    @IBInspectable var borderColor: UIColor = UIColor.white {
        didSet {
            layer.borderColor = borderColor.cgColor
        }
    }
    
    @IBInspectable var borderWidth: CGFloat = 2.0 {
        didSet {
            layer.borderWidth = borderWidth
        }
    }
    
    @IBInspectable var cornerRadius: CGFloat = 10.0 {
        didSet {
            layer.cornerRadius = cornerRadius
        }
    }
    
}

extension Date {
    static func getFormattedDate(date: Date) -> String{
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd/MM/yyyy HH:mm:ss"
        return dateFormatter.string(from: date)
    }
}

extension String {
    static func getFrenchTranslationOf (word: String) -> String{
        var translation = ""
        
        switch (word){
            case "car":
                translation = "Voiture"
                break;
            case "automotive":
                translation = "Automotive"
                break;
            case "walking":
                translation = "Marche"
                break;
            case "running":
                translation = "Course"
                break;
            case "stationary":
                translation = "Arret"
                break;
            case "cycling":
                translation = "Vélo"
                break;
            case "motorcylce":
                translation = "Moto"
                break;
            case "train":
                translation = "Train/Metro"
                break;
            case "bus":
                translation = "Bus"
                break;
            case "train/bus":
                translation = "Train/Bus"
                break;
            case "train/car":
                translation = "Voiture/Train"
                break;
            case "bus/car":
                translation = "Voiture/Bus"
                break;
            case "airplane":
                translation = "Avion"
                break;
            case "unknown":
                translation = "-"
                break;
            default :
                translation = word
        }
        
        return translation
    }
    
}

