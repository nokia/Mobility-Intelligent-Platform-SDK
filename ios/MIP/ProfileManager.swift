import Foundation
import AWSAuthCore
import AWSCore
import AWSAPIGateway
import AWSMobileClient
import AWSAuthCore
import AWSAuthUI
import MapKit
import Mapbox

import Charts

class ProfileManager {
    static var mShape = [MGLShape]()
    static var distance = 0.0
    static var transportmode = ""
    static func doInvokeProfilAPI(type : String, resultField: UILabel, mobilityCost:UILabel, carbonFootprint:UILabel) {
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "POST"
        // change to any valid path you configured in the API
        let URLString = "/profil"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        let httpBody = [
            "type" : type,
            ] as [String : Any]
        
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
        
        AWSAPI_V3DZAX3WY3_MobilityProfilMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        
        // Fetch the Cloud Logic client to be used for invocation
        let invocationClient =
            AWSAPI_V3DZAX3WY3_MobilityProfilMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            
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
                if (!(responseString?.contains("Internal server error"))! && !((responseString?.isEmpty)!)){
                    DispatchQueue.main.async {
                        if (type == "speed") {
                            resultField.text = String(format: "%.0f Km/h", Double(responseString!)!)
                        } else if (type == "duree") {
                            resultField.text = String(format: "%.0fh de mobilité", Double(responseString!)!)
                        } else if (type == "transport") {
                            transportmode = responseString!
                            switch (responseString){
                                case "car":
                                    resultField.text = "Voiture"
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.135/7.2)
                                    break;
                                case "automotive":
                                    resultField.text = "Automotive (Voiture/Bus/Train)"
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.09/7.2)
                                    mobilityCost.text = "75,20 € Navigo + frais voiture"
                                    break;
                                case "walking":
                                    resultField.text = "Marche"
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", 0)
                                    mobilityCost.text = "75,20 € Navigo + frais voiture"
                                    break;
                                case "train":
                                    resultField.text = "Train/Metro"
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.042/7.2)
                                    mobilityCost.text = "75,20 € Navigo"
                                    break;
                                case "bus":
                                    resultField.text = "Bus"
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.042/7.2)
                                    mobilityCost.text = "75,20 € Navigo"
                                    break;
                                case "train/bus":
                                    resultField.text = "TC Metro/Bus"
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.042/7.2)
                                    mobilityCost.text = "75,20 € Navigo"
                                    break;
                                default :
                                    resultField.text = responseString
                                    mobilityCost.text = "75,20 € Navigo + frais voiture"
                            }
                        } else if (type == "distance") {
                            distance = Double(responseString!)!/1000
                            resultField.text = String.init(format: "%.0f Km Parcourus avec MoveInSaclay", Double(responseString!)!/1000)
                            switch (transportmode){
                                case "car":
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.135/7.2)
                                    mobilityCost.text = String.init(format: "%.2f€ incluant %.2f€ carburant", distance*0.22, distance*0.09)
                                    break;
                                case "automotive":
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.09/7.2)
                                    mobilityCost.text = "75,20 € Navigo + frais voiture"
                                    break;
                                case "walking":
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", 0)
                                    mobilityCost.text = "75,20 € Navigo + frais voiture"
                                    break;
                                case "train":
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.042/7.2)
                                    mobilityCost.text = "75,20 € Navigo"
                                    break;
                                case "bus":
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.042/7.2)
                                    mobilityCost.text = "75,20 € Navigo"
                                    break;
                                case "train/bus":
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.042/7.2)
                                    mobilityCost.text = "75,20 € Navigo"
                                    break;
                                default :
                                    carbonFootprint.text = String.init(format: "%.0f Arbres", distance*0.09/7.2)
                                    mobilityCost.text = String.init(format: "%.2f€ incluant %.2f€ carburant", distance*0.22, distance*0.09)
                            }
                        }
                    }
                    print(result.statusCode)
                }
            }
            
            return nil
        }
    }
    
    static func doInvokeProfilAPI_distance(httpBody : [String : Any]) {
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "POST"
        // change to any valid path you configured in the API
        let URLString = "/profil"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
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
        
        AWSAPI_V3DZAX3WY3_MobilityProfilMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        
        // Fetch the Cloud Logic client to be used for invocation
        let invocationClient =
            AWSAPI_V3DZAX3WY3_MobilityProfilMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            
            if let error = task.error {
                print("Error occurred: \(error)")
                // Handle error here
                return nil
            }
            return nil
        }
    }
    
    static func doInvokeProfilAPI_record(map: MGLMapView) {
        var shape = [MGLShape]()
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "POST"
        // change to any valid path you configured in the API
        let URLString = "/profil"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        let httpBody = [
            "type" : "record",
            ] as [String : Any]
        
        let apiRequest = AWSAPIGatewayRequest(httpMethod: httpMethodName,
                                              urlString: URLString,
                                              queryParameters: queryStringParameters,
                                              headerParameters: headerParameters,
                                              httpBody: httpBody)
        
        let serviceConfiguration = AWSServiceConfiguration(
            region: AWSRegionType.EUWest1,
            credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        
        AWSAPI_V3DZAX3WY3_MobilityProfilMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        let invocationClient =
            AWSAPI_V3DZAX3WY3_MobilityProfilMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            if let error = task.error {
                print("Error occurred: \(error)")
                return nil
            }
            
            let result = task.result!
            let responseString =
                String(data: result.responseData!, encoding: .utf8)
            let records_array = responseString?.components(separatedBy: ";")
            for records in records_array! {
                let records_splitted = records.components(separatedBy: ",")
                if (records_splitted.count >= 2){
                    let latitude = Double(records_splitted[0])
                    let longitude = Double(records_splitted[1])
                    let point = MGLPointFeature()
                    point.coordinate = CLLocationCoordinate2D(latitude: latitude!, longitude: longitude!)
                    shape.append(point)
                }
            }
            DispatchQueue.main.async {
                MapTools.addRecordstoMap(map: map, shape: shape)
            }
            print(result.statusCode)
            return nil
        }
    }
    
    static func doInvokeProfilAPI_plus(modeUser: PieChartView) {
        var dataset = [ChartDataEntry]()
        // change the method name, or path or the query string parameters here as desired
        let httpMethodName = "POST"
        // change to any valid path you configured in the API
        let URLString = "/profil"
        let queryStringParameters = ["key":"{value}"]
        let headerParameters = [
            "Content-Type": "application/json",
            "Accept": "application/json"
        ]
        
        let httpBody = [
            "type" : "transportPlus",
            ] as [String : Any]
        
        let apiRequest = AWSAPIGatewayRequest(httpMethod: httpMethodName,
                                              urlString: URLString,
                                              queryParameters: queryStringParameters,
                                              headerParameters: headerParameters,
                                              httpBody: httpBody)
        
        let serviceConfiguration = AWSServiceConfiguration(
            region: AWSRegionType.EUWest1,
            credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        
        AWSAPI_V3DZAX3WY3_MobilityProfilMobileHubClient.register(with: serviceConfiguration!, forKey: "CloudLogicAPIKey")
        let invocationClient =
            AWSAPI_V3DZAX3WY3_MobilityProfilMobileHubClient(forKey: "CloudLogicAPIKey")
        
        invocationClient.invoke(apiRequest).continueWith { (
            task: AWSTask) -> Any? in
            if let error = task.error {
                print("Error occurred: \(error)")
                return nil
            }
            
            let result = task.result!
            let responseString =
                String(data: result.responseData!, encoding: .utf8)
            let records_array = responseString?.components(separatedBy: ";")
            for records in records_array! {
                if (records != "") {
                    let records_splitted = records.components(separatedBy: ",")
                    if (records_splitted.count >= 1){
                        let transportmode = "H de "+String.getFrenchTranslationOf(word: records_splitted[0])
                        let count = Double(records_splitted[1])!*5/3600
                        let entry = PieChartDataEntry(value: count, label: transportmode)
                        dataset.append(entry)
                    }
                }
            }
            DispatchQueue.main.async {
                let dataSet = PieChartDataSet(entries: dataset, label: "")
                dataSet.colors = ChartColorTemplates.material()
                let data = PieChartData(dataSet: dataSet)
                modeUser.data = data
                modeUser.notifyDataSetChanged()
            }
            print(result.statusCode)
            return nil
        }
    }
}
