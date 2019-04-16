import Foundation

import AWSAuthCore
import AWSCore
import AWSAPIGateway
import AWSMobileClient
import AWSAuthCore
import AWSAuthUI
import MapKit
import LocoKit
import CoreLocation
import SwiftNotes

import CoreML

class MobilityDataManager {
    
    static func doInvokeMotionAPI(_ locationItem: Locations) {
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "POST"
        // change to any valid path you configured in the API
        let URLString = "/traces"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        let httpBody = [
            "itemId" : locationItem._itemId,
            "baseMotionMode" : locationItem._baseMotionMode,
            "latitude" : locationItem._latitude?.stringValue,
            "longitude" : locationItem._longitude?.stringValue,
            "speed" : locationItem._speed?.stringValue,
            "timestamp" : locationItem._timestamp?.stringValue,
            "xyAcceleration" : locationItem._xyAcceleration?.stringValue,
            "zAcceleration" : locationItem._zAcceleration?.stringValue,
            "transportMotionMode": locationItem._transportMotionMode,
            "modeAccuracy": locationItem._modeAccuracy,
            "deviceType":  "0",
            "usertag":  locationItem._usertag,
            ] as [String : Any]
        
        print (httpBody)
        
        // Construct the request object
        let apiRequest = AWSAPIGatewayRequest(httpMethod: httpMethodName,
                                              urlString: URLString,
                                              queryParameters: queryStringParameters,
                                              headerParameters: headerParameters,
                                              httpBody: httpBody)
        
        // Create a service configuration object for the region your AWS API was created in
        let serviceConfiguration = AWSServiceConfiguration(
            region: AWSRegionType.EUWest1,
            credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        
        AWSAPI_5AJLQ8PC10_MotionMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        
        // Fetch the Cloud Logic client to be used for invocation
        let invocationClient =
            AWSAPI_5AJLQ8PC10_MotionMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            
            if let error = task.error {
                print("Error occurred: \(error)")
                // Handle error here
                return nil
            }
            
            // Handle successful result here
            let result = task.result!
            let responseString =
                String(data: result.responseData!, encoding: .utf8)
            
            print(responseString)
            print(result.statusCode)
            
            return nil
        }
    }
    
    @available(iOS 11.0, *)
    static func predictTransportMode(basemotionmode:NSNumber, lat:NSNumber, lng:NSNumber, speed:NSNumber, predictionTextView: UILabel){
    }
}

extension LocomotionManager {
    func monitorRegionAtLocation(center: CLLocationCoordinate2D, radius : CLLocationDistance, identifier: String ) {
        if CLLocationManager.authorizationStatus() == .authorizedAlways {
            if CLLocationManager.isMonitoringAvailable(for: CLCircularRegion.self) {
                let region = CLCircularRegion(center: center,
                                              radius: radius, identifier: identifier)
                region.notifyOnEntry = true
                region.notifyOnExit = true
                
                locationManager.startMonitoring(for: region)
            }
        }
    }
    
    func stopMonitorRegion(identifier: String ) {
        let regions = locationManager.monitoredRegions
        for region in regions {
            if region.identifier == identifier {
                locationManager.stopMonitoring(for: region)
            }
        }
    }
}

