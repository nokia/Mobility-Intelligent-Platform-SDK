//  Created by Ayoub Benyahya
//  Copyright © 2019 Ayoub Benyahya. All rights reserved.

import Foundation
import AWSAuthCore
import AWSCore
import AWSAPIGateway
import AWSMobileClient
import AWSAuthCore
import AWSAuthUI
import MapKit
import LocoKit
import Mapbox

class WalletManager {
    static var token_number: Double!
    static var mShape = [MGLShape]()
    static func doInvokeWalletAPI(resultField: UILabel) {
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "GET"
        // change to any valid path you configured in the API
        let URLString = "/token"
        
        // Construct the request object
        let apiRequest = AWSAPIGatewayRequest(httpMethod: httpMethodName,
                                              urlString: URLString,
                                              queryParameters: nil,
                                              headerParameters: nil,
                                              httpBody: nil)
        
        // Create a service configuration object for the region your AWS API was created in
        let serviceConfiguration = AWSServiceConfiguration(
            region: AWSRegionType.EUWest1,
            credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        
        AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        
        // Fetch the Cloud Logic client to be used for invocation
        let invocationClient =
            AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Double? in
            
            if let error = task.error {
                print("Error occurred: \(error)")
                // Handle error here
                return nil
            }
            
            // Handle successful result here
            let result = task.result!
            if let data = result.responseData {
                let responseString =
                    String(data: data, encoding: .utf8)
                if (!(responseString?.contains("Internal server error"))!){
                    token_number = (responseString! as NSString).doubleValue
                    DispatchQueue.main.async {
                        resultField.text = String(format: "%.0f MIPS", token_number)
                    }
                }
            }
            
            return nil
        }
    }
    
    static func getCurrentToken() -> Double {
        if (token_number != nil) {
            return token_number
        } else {
            return -1
        }
    }
    
    static func setNewTokenValue(token:Double, tokenUIfield: UILabel){
        token_number = token
        tokenUIfield.text = String(format: "%.0f MIPS", self.token_number)
    }
    
    static func doInvokeRewardsAPI(httpBody : [String : Any]) {
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "POST"
        // change to any valid path you configured in the API
        let URLString = "/token"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        let apiRequest = AWSAPIGatewayRequest(httpMethod: httpMethodName,
                                              urlString: URLString,
                                              queryParameters: queryStringParameters,
                                              headerParameters: headerParameters,
                                              httpBody: httpBody)
        
        let serviceConfiguration = AWSServiceConfiguration(
            region: AWSRegionType.EUWest1,
            credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        
        AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        
        let invocationClient =
            AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            
            if let error = task.error {
                print("Error occurred: \(error)")
                return nil
            }
            
            let result = task.result!
            let responseString =
                String(data: result.responseData!, encoding: .utf8)
            print (responseString)
            
            return nil
        }
    }
    
    static func doInvokeRewardsAPI_Rules(map: MGLMapView, locomotionManager: LocomotionManager) {
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "GET"
        // change to any valid path you configured in the API
        let URLString = "/token/rules"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        let apiRequest = AWSAPIGatewayRequest(httpMethod: httpMethodName,
                                              urlString: URLString,
                                              queryParameters: queryStringParameters,
                                              headerParameters: headerParameters,
                                              httpBody: nil)
        
        let serviceConfiguration = AWSServiceConfiguration(
            region: AWSRegionType.EUWest1,
            credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        
        AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        
        let invocationClient =
            AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            
            if let error = task.error {
                print("Error occurred: \(error)")
                return nil
            }
            
            let result = task.result!
            let responseString = String(data: result.responseData!, encoding: .utf8)
            let records_array = responseString?.components(separatedBy: ";")
            for records in records_array! {
                let records_splitted = records.components(separatedBy: ",")
                if (records_splitted.count >= 2){
                    let latitude = Double(records_splitted[0])
                    let longitude = Double(records_splitted[1])
                    let radius = Double(records_splitted[2])
                    let id = String(records_splitted[3])
                    setupMonitoredArea(lat: latitude!, lng: longitude!, radius: radius!, id: id, locomotionManager: locomotionManager)
                    let point = MGLPointFeature()
                    point.coordinate = CLLocationCoordinate2D(latitude: latitude!, longitude: longitude!)
                    mShape.append(point)
                }
            }
            DispatchQueue.main.async {
                MapTools.addNudgeAreastoMap(map: map, shape: mShape)
            }
            print(result.statusCode)
            
            return nil
        }
    }
    
    static func doInvokeBehaviorEventAPI(httpBody : [String : Any]) {
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "POST"
        // change to any valid path you configured in the API
        let URLString = "/token/event/behavior"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        let apiRequest = AWSAPIGatewayRequest(httpMethod: httpMethodName,
                                              urlString: URLString,
                                              queryParameters: queryStringParameters,
                                              headerParameters: headerParameters,
                                              httpBody: httpBody)
        
        let serviceConfiguration = AWSServiceConfiguration(
            region: AWSRegionType.EUWest1,
            credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        
        AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        
        let invocationClient =
            AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            
            if let error = task.error {
                print("Error occurred: \(error)")
                return nil
            }
            
            let result = task.result!
            let responseString =
                String(data: result.responseData!, encoding: .utf8)
            print (responseString)
            
            return nil
        }
    }
    
    static func doInvokeSharingEventAPI(httpBody : [String : Any]) {
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "POST"
        // change to any valid path you configured in the API
        let URLString = "/token/event/datasharing"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        let apiRequest = AWSAPIGatewayRequest(httpMethod: httpMethodName,
                                              urlString: URLString,
                                              queryParameters: queryStringParameters,
                                              headerParameters: headerParameters,
                                              httpBody: httpBody)
        
        let serviceConfiguration = AWSServiceConfiguration(
            region: AWSRegionType.EUWest1,
            credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        
        AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        
        let invocationClient =
            AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            
            if let error = task.error {
                print("Error occurred: \(error)")
                return nil
            }
            
            let result = task.result!
            let responseString =
                String(data: result.responseData!, encoding: .utf8)
            print (responseString)
            
            return nil
        }
    }
    
    
    static func doInvokeSurveyEventAPI(httpBody : [String : Any]) {
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "POST"
        // change to any valid path you configured in the API
        let URLString = "/token/event/survey"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        let apiRequest = AWSAPIGatewayRequest(httpMethod: httpMethodName,
                                              urlString: URLString,
                                              queryParameters: queryStringParameters,
                                              headerParameters: headerParameters,
                                              httpBody: httpBody)
        
        let serviceConfiguration = AWSServiceConfiguration(
            region: AWSRegionType.EUWest1,
            credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        
        AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        
        let invocationClient =
            AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            
            if let error = task.error {
                print("Error occurred: \(error)")
                return nil
            }
            
            let result = task.result!
            let responseString =
                String(data: result.responseData!, encoding: .utf8)
            print (responseString)
            
            return nil
        }
    }
    
    static func setupMonitoredArea (lat: Double, lng: Double, radius: Double, id: String, locomotionManager: LocomotionManager){
        locomotionManager.stopMonitorRegion(identifier: id)
        locomotionManager.monitorRegionAtLocation(center: CLLocationCoordinate2D(latitude: lat, longitude: lng), radius: CLLocationDistance(radius), identifier: id)
    }
    
    static func onEnterNudgeArea(areaId: String, areaLat: Double, areaLng: Double, transportMode: String, userid: String, timestamp: Double){
        let eventBody = [
            "timestamp" : timestamp,
            "eventid" : UUID().uuidString,
            "userid" : userid,
            "transportmode" : transportMode,
            "arealat": areaLat,
            "arealng": areaLng,
            "areaid": areaId
            ] as [String : Any]
        
        WalletManager.doInvokeBehaviorEventAPI(httpBody : eventBody)
    }
    
    static func onStopDataSharing(lat: Double, lng: Double, userid: String, timestamp: Double){
        let eventBody = [
            "timestamp" : timestamp,
            "eventid" : UUID().uuidString,
            "userid" : userid,
            "lat": lat,
            "lng": lng
            ] as [String : Any]
        
        WalletManager.doInvokeSharingEventAPI(httpBody : eventBody)
    }
    
    static func onSurveyReveled(lat: Double, lng: Double, userid: String, timestamp: Double){
        let eventBody = [
            "timestamp" : timestamp,
            "eventid" : UUID().uuidString,
            "userid" : userid,
            "lat": lat,
            "lng": lng
            ] as [String : Any]
        
        WalletManager.doInvokeSurveyEventAPI(httpBody : eventBody)
    }
    
    static func manageNudgeZone(location: CLLocationCoordinate2D, lastSample : LocomotionSample, tokenUIfield: UILabel){
        token_number = WalletManager.getCurrentToken()
        token_number = token_number + 1.0
        WalletManager.setNewTokenValue(token: token_number,tokenUIfield: tokenUIfield)
        let transactionBody = [
            "description" : "Nudge Zone",
            "date" : lastSample.date.debugDescription,
            "transactionid" : UUID().uuidString,
            "numbertoken" : token_number,
            "latitude": location.latitude,
            "longitude": location.longitude
            ] as [String : Any]
        print (token_number)
        WalletManager.doInvokeRewardsAPI(httpBody : transactionBody)
    }
    
    static func manageTrips( firstSample : LocomotionSample, lastSample : LocomotionSample, tokenUIfield: UILabel, tripDistance : Double, tripDuration : Double){
        if (verifyInputs(firstSample: firstSample, lastSample: lastSample)){
            let httpBody = [
                "type" : "trip",
                "lastdate" : lastSample.date.debugDescription,
                "firstdate" : firstSample.date.debugDescription,
                "distance": tripDistance,
                "duration": tripDuration,
                "latitude_start": firstSample.filteredLocations?.last?.coordinate.latitude,
                "longitude_start": firstSample.filteredLocations?.last?.coordinate.longitude,
                "latitude_stop": lastSample.filteredLocations?.last?.coordinate.latitude,
                "longitude_stop": lastSample.filteredLocations?.last?.coordinate.longitude
                ] as [String : Any]
            print (httpBody)
            ProfileManager.doInvokeProfilAPI_distance(httpBody: httpBody)
            /*let nudge_lat = lastSample.filteredLocations?.last?.coordinate.latitude
            let nudge_lng = lastSample.filteredLocations?.last?.coordinate.longitude
            if (isInParisSaclay(latitude: nudge_lat!, longitude: nudge_lng!)){
                token_number = WalletManager.getCurrentToken()
                if (token_number >= 0){
                    token_number = token_number + tripDistance * 0.001
                    WalletManager.setNewTokenValue(token: token_number, tokenUIfield: tokenUIfield)
                    print (token_number)
                    let transactionBody = [
                        "description" : "Nudge KM",
                        "date" : lastSample.date.debugDescription,
                        "transactionid" : UUID().uuidString,
                        "numbertoken" : token_number,
                        "latitude": lastSample.filteredLocations?.last?.coordinate.latitude,
                        "longitude": lastSample.filteredLocations?.last?.coordinate.longitude
                        ] as [String : Any]
                    WalletManager.doInvokeRewardsAPI(httpBody : transactionBody)
                }
            }*/
        }
    }
    
    static func verifyInputs(firstSample : LocomotionSample, lastSample : LocomotionSample) -> Bool {
        var isVerified = false
        if (firstSample != nil && lastSample != nil){
            isVerified = true
        }
        return isVerified
    }
    
    static func isInParisSaclay (latitude: Double, longitude: Double)-> Bool{
        var result = false
        let current_location = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        let nudge_location_cps = CLLocationCoordinate2D(latitude: 48.702, longitude: 2.231)
        let nudge_location_sqy = CLLocationCoordinate2D(latitude: 48.779, longitude: 2.061)
        let nudge_cllocation_cps = CLLocation(latitude: nudge_location_cps.latitude, longitude: nudge_location_cps.longitude)
        let nudge_cllocation_sqy = CLLocation(latitude: nudge_location_sqy.latitude, longitude: nudge_location_sqy.longitude)
        let distance_nudge_cps = CLLocation(latitude: current_location.latitude, longitude: current_location.longitude).distance(from: nudge_cllocation_cps)
        let distance_nudge_sqy = CLLocation(latitude: current_location.latitude, longitude: current_location.longitude).distance(from: nudge_cllocation_sqy)
        
        if (distance_nudge_cps<=10139 || distance_nudge_sqy <= 11748){
            result = true
        }
        return result
    }
}

