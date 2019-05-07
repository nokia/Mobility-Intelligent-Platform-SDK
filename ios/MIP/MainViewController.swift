import Foundation
import AWSAuthCore
import AWSAuthUI
import LocoKit
import CoreLocation
import SwiftNotes
import CoreMotion
import MapKit

import AWSAuthCore
import AWSCore
import AWSAPIGateway
import AWSMobileClient
import AWSDynamoDB

import Mapbox
import MapboxCoreNavigation
import MapboxNavigation
import MapboxDirections
import MapboxGeocoder

class MainViewController: UITableViewController, MKMapViewDelegate, MGLMapViewDelegate, CLLocationManagerDelegate, UISearchBarDelegate {
    @IBOutlet weak var surveyWeb: UIWebView!
    @IBOutlet weak var dataSharing: UISwitch!
    @IBOutlet weak var gpsVal: UILabel!
    @IBOutlet weak var modeVal: UILabel!
    @IBOutlet weak var speedVal: UILabel!
    @IBOutlet weak var avgspeedUser: UILabel!
    @IBOutlet weak var timestamp: UILabel!
    @IBOutlet weak var tokenNumber: UILabel!
    @IBOutlet weak var transportModeDash: UILabel!
    @IBOutlet weak var transportModePredicition: UILabel!
    let store = TimelineStore()
    var timeline: TimelineRecorder!
    var baseClassifier: ActivityTypeClassifier?
    var transportClassifier: ActivityTypeClassifier?
    var loco = LocomotionManager.highlander
    var locomotionObserver : NSObjectProtocol!
    var locotionObserver : NSObjectProtocol!
    var mobilityObserver : NSObjectProtocol!
    var currentLocation: CLLocationCoordinate2D?
    var currentSpeed: Double?
    var firstSample : LocomotionSample!
    var lastSample : LocomotionSample!
    var currentTimestamp = 0
    var distance_course = 0.0
    var distance_token = 0.0
    var duration_course = 0.0
    var token_number = 0.0
    var firstShare = false
    var lastShare = false
    var did_enter_nudgeZone = false
    var source: MGLShapeSource!
    var timer = Timer()
    
    @IBOutlet weak var zoneNudge: UILabel!
    @IBOutlet weak var distance: UILabel!
    @IBOutlet weak var carboFootpring: UILabel!
    @IBOutlet weak var mobilityCost: UILabel!
    @IBOutlet weak var tripDistance: UILabel!
    @IBOutlet weak var duree: UILabel!
    @IBOutlet weak var mapboxView: MGLMapView!
    var directionsRoute: Route?
    var directionsRouteCycling: Route?
    var destinationAnnotation: MGLPointAnnotation?
    var routeLayer: MGLLineStyleLayer?
    var source_route: MGLShapeSource?
    var geocoder: Geocoder?
    var userDefaults:UserDefaults!
    var searchActive : Bool = false
    var isTrain : Bool = true
    var isCar : Bool = true
    var isBus : Bool = true
    var lastLocation : CLLocation!
    
    @IBOutlet weak var newSurveyButton: UIButton!
    @IBOutlet weak var nudgeMap: Map!
    
    @IBOutlet weak var trainSwitch: UISwitch!
    @IBOutlet weak var carSwitch: UISwitch!
    @IBOutlet weak var busSwitch: UISwitch!
    var lastMotionClassifier: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.setHidesBackButton(true, animated:true)
        manageUserPreferences()
        setupLocoKit()
        setupMap()
        identityVerification()
        initSurvey()
    }
    
    func setupMap(){
        mapboxView.delegate = self
        let coordinate = CLLocationCoordinate2D(latitude: 48.66, longitude: 2.24)
        let camera = MGLMapCamera(lookingAtCenter: coordinate,
                              fromDistance: 2000,
                              pitch: 50,
                              heading: 0.0)
        mapboxView.camera = camera
        mapboxView.showsUserLocation = true
        mapboxView.setUserTrackingMode(.follow, animated: true)
        let longPress = UILongPressGestureRecognizer(target: self, action: #selector(didLongPress(_:)))
        mapboxView.addGestureRecognizer(longPress)
        geocoder = Geocoder.shared
    }
    
    func setupLocoKit () {
        LocoKitService.apiKey = "b78df95038a941de96cb3d7b47d814e1"
        ActivityTypesCache.highlander.store = store
        timeline = TimelineRecorder(store: store, classifier: TimelineClassifier.highlander)
        loco.locationManager.allowsBackgroundLocationUpdates = true
        loco.maximumDesiredLocationAccuracy = kCLLocationAccuracyNearestTenMeters
        loco.recordPedometerEvents = true
        loco.recordAccelerometerEvents = true
        loco.recordCoreMotionActivityTypeEvents = true
        loco.useLowPowerSleepModeWhileStationary = true
        loco.requestLocationPermission(background: true)
        locotionObserver = when(loco, does: .didChangeAuthorizationStatus) {_ in
            switch(CLLocationManager.authorizationStatus()) {
                case .notDetermined, .restricted, .denied:
                    self.createSettingsAlertController(title: "Activation GPS", message: "Nous aurions besoin de votre position GPS pour le partage de vos données de mobilité et la navigation. \n Settings > Privacy > Location Services")
                case .authorizedAlways, .authorizedWhenInUse:
                    print("Access")
            }
            print(".didChangeAuthorizationStatus")
            self.setupMap()
        }
        
        initMonitoringRegions()
    }
    
    func initMonitoringRegions(){
        
        when(loco, does: Notification.Name("didRegionEnter")) { notification in
            self.zoneNudge.text = "Dans une Zone Nudge"
            if (self.lastMotionClassifier != nil){
                WalletManager.onEnterNudgeArea(areaId: notification.userInfo!["identifier"] as! String, areaLat: notification.userInfo!["centerLat"] as! Double, areaLng: notification.userInfo!["centerLng"] as! Double, transportMode: self.lastMotionClassifier, userid: AWSIdentityManager.default().identityId!, timestamp: NSDate().timeIntervalSince1970)
            }
            print(".didRegionEnter")
        }
        
        when(loco, does: Notification.Name("didExitRegion")) { notification in
            self.zoneNudge.text = "En Dehors des Zones"
            print(".didRegionExit")
        }
    }
    
    func identityVerification() {
        if(AWSSignInManager.sharedInstance().isLoggedIn){
             self.onIdentityVerified()
        }
    }
    
    func initSurvey () {
        let isSurveyRevealed = userDefaults.bool(forKey: "survey_V2.4.3_17")
        if (!isSurveyRevealed){
            newSurveyButton.setTitle("1 Nouvelle Enquete Disponible", for: .normal)
            SurveyManager.loadSurvey(surveyWeb: surveyWeb)
        } else {
            newSurveyButton.setTitle("0 Enquete Disponible", for: .normal)
        }
    }
    
    func onIdentityVerified () {
        DispatchQueue.global().async {
            ProfileManager.doInvokeProfilAPI(type: "distance", resultField: self.distance, mobilityCost: self.mobilityCost, carbonFootprint: self.carboFootpring)
            ProfileManager.doInvokeProfilAPI(type: "transport", resultField: self.transportModeDash, mobilityCost: self.mobilityCost, carbonFootprint: self.carboFootpring)
            ProfileManager.doInvokeProfilAPI(type: "speed", resultField: self.avgspeedUser, mobilityCost: self.mobilityCost, carbonFootprint: self.carboFootpring)
            ProfileManager.doInvokeProfilAPI(type: "duree", resultField: self.duree, mobilityCost: self.mobilityCost, carbonFootprint: self.carboFootpring)
            ProfileManager.doInvokeProfilAPI_record(map: self.mapboxView)
            WalletManager.doInvokeWalletAPI(resultField: self.tokenNumber)
            WalletManager.doInvokeRewardsAPI_Rules(map: self.nudgeMap, locomotionManager: self.loco)
            let ondataSharing = self.userDefaults.bool(forKey: "OnDataSharing")
            if (ondataSharing) {
                onMain {
                    self.dataSharing.setOn(true, animated: true)
                }
                self.firstShare = true
                self.dataSharingStatus ("En attente")
                self.startRecordingMobilityData ()
            }
        }
        if (dataSharing.isOn) {
            startRecordingMobilityData ()
        }
    }
    
    func manageUserPreferences (){
        userDefaults = UserDefaults.standard
        isTrain = userDefaults.bool(forKey: "train_preferences")
        isCar = userDefaults.bool(forKey: "car_preferences")
        isBus = userDefaults.bool(forKey: "bus_preferences")
        trainSwitch.setOn(isTrain, animated: true)
        carSwitch.setOn(isCar, animated: true)
        busSwitch.setOn(isBus, animated: true)
    }
    
    @IBAction func onCar(_ sender: UISwitch) {
        if (sender.isOn){
            isCar = true
        } else {
            isCar = false
        }
        userDefaults.set(isCar, forKey: "car_preferences")
    }
    @IBAction func onBus(_ sender: UISwitch) {
        if (sender.isOn){
            isBus = true
        } else {
            isBus = false
        }
        userDefaults.set(isBus, forKey: "bus_preferences")
    }
    @IBAction func onTrain(_ sender: UISwitch) {
        if (sender.isOn){
            isTrain = true
        } else {
            isTrain = false
        }
        userDefaults.set(isTrain, forKey: "train_preferences")
    }
    
    @IBAction func onSwitchValueChanged(_ sender: UISwitch) {
        var lat = -1.0, lng = -1.0
        if (sender.isOn){
            firstShare = true
            dataSharingStatus ("En attente")
            startRecordingMobilityData ()
        } else {
            dataSharingStatus ("Off")
            stopRecordingMobilityData ()
            WalletManager.manageTrips(firstSample: firstSample, lastSample: lastSample, tokenUIfield: tokenNumber, tripDistance: distance_token, tripDuration: duration_course)
            
            if (lastLocation != nil){
                lat = lastLocation.coordinate.latitude
                lng = lastLocation.coordinate.longitude
            }
            WalletManager.onStopDataSharing(lat: lat, lng: lng, userid: AWSIdentityManager.default().identityId!, timestamp: NSDate().timeIntervalSince1970)
            
            distance_course = 0.0
            distance_token = 0.0
            duration_course = 0.0 
            lastSample = nil
            firstSample = nil
        }
        userDefaults.set(sender.isOn, forKey: "OnDataSharing")
        print (sender.isOn)
    }
    
    func dataSharingStatus(_ state: String) {
        onMain {
            self.gpsVal.text = state
            self.zoneNudge.text = state
            self.speedVal.text = state
            //self.motionVal.text = state
            self.modeVal.text = state
            self.timestamp.text = state
            self.tripDistance.text = state
            self.transportModePredicition.text = state
        }
    }
    
    //Function to Start Recording Mobility Data and register events observer
    func startRecordingMobilityData(){
        if !CLLocationManager.locationServicesEnabled() {
            self.createSettingsAlertController(title: "Activation GPS", message: "Nous aurions besoin de votre position GPS pour le partage de vos données de mobilité et la navigation. \n Settings > Privacy > Location Services")
        }
        loco.startRecording()
        locomotionObserver = when(loco, does: .locomotionSampleUpdated) { _ in
            print(".locomotionSampleUpdated")
            self.motionTriggerAnalysis()
        }
        
        mobilityObserver = when(Notification.Name("motionUpdated")) { _ in
            print(".motionUpdated")
            self.motionUpdated()
        }
    }
    
    //Function to Stop Recording Mobility Data
    func stopRecordingMobilityData(){
        loco.stopRecording()
        self.removeMotionObservers()
    }
    
    func removeMotionObservers() {
        NotificationCenter.default.removeObserver(locotionObserver)
        NotificationCenter.default.removeObserver(locomotionObserver)
        NotificationCenter.default.removeObserver(mobilityObserver)
    }
    
    //Data sharing frenquency analysis => one records each 5s when speed changes
    func motionTriggerAnalysis() {
        let sample = loco.locomotionSample()
        if !sample.isNolo {
            let lastSpeed = sample.filteredLocations?.last?.speed
            let lastTimestamp = Int((sample.filteredLocations?.last?.timestamp.timeIntervalSince1970)!)
            if (lastSpeed != currentSpeed && (lastTimestamp - currentTimestamp>=5)) {
                currentSpeed = lastSpeed
                currentTimestamp = lastTimestamp
                if (firstShare){
                    firstSample = sample
                    firstShare = false
                }
                if (lastSample != nil) {
                    let deltaDist = sample.distance(from: lastSample)
                    let deltaTime = (sample.filteredLocations?.last?.timestamp.timeIntervalSince1970)! - (lastSample.filteredLocations?.last?.timestamp.timeIntervalSince1970)!
                    
                    distance_course = distance_course + Double(String(format: "%f", deltaDist!))!
                    duration_course = duration_course + deltaTime
                    
                    distance_token = distance_token + Double(String(format: "%f", deltaDist!))!
                    if (distance_token>=1000) {
                        WalletManager.manageTrips(firstSample: firstSample, lastSample: lastSample, tokenUIfield: tokenNumber, tripDistance: Double(distance_token), tripDuration: duration_course)
                        WalletManager.doInvokeWalletAPI(resultField: self.tokenNumber)
                        ProfileManager.doInvokeProfilAPI(type: "distance", resultField: self.distance, mobilityCost: self.mobilityCost, carbonFootprint: self.carboFootpring)
                        ProfileManager.doInvokeProfilAPI(type: "transport", resultField: self.transportModeDash, mobilityCost: self.mobilityCost, carbonFootprint: self.carboFootpring)
                        ProfileManager.doInvokeProfilAPI(type: "speed", resultField: self.avgspeedUser, mobilityCost: self.mobilityCost, carbonFootprint: self.carboFootpring)
                        ProfileManager.doInvokeProfilAPI(type: "duree", resultField: self.duree, mobilityCost: self.mobilityCost, carbonFootprint: self.carboFootpring)
                        distance_token=0;
                    }
                    
                    print (distance_course)
                    print (duration_course)
                }
                lastSample = sample
                trigger(Notification.Name("motionUpdated"))
            }
        }
    }
    
    //Update UI and send mobility data using motion API
    func motionUpdated() {
        let sample = loco.locomotionSample()
        if !sample.isNolo {
            lastLocation = sample.filteredLocations?.last
            let lat = lastLocation?.coordinate.latitude
            let lng = lastLocation?.coordinate.longitude
            let accelerationXy = sample.xyAcceleration
            let accelerationZ = sample.zAcceleration
            let timestamp = lastLocation?.timestamp.timeIntervalSince1970
            let speed = lastLocation?.speed
            var motionType = sample.coreMotionActivityType
            var motionAccuracy = sample.movingState
            var motionClassifier = ""
            var speedkmh = speed! * 3600/1000
            if (speedkmh < 0) {
                speedkmh = 0
            }
            //updateTheTransportClassifier()
            
            if (motionType != nil && accelerationXy != nil && accelerationZ != nil){
                if (speedkmh == 0) {
                    motionType = .stationary
                    motionAccuracy = .stationary
                }
                if (speedkmh > 3 && motionType?.rawValue == "stationary") {
                    motionType = .walking
                    motionAccuracy = .moving
                }
                if (speedkmh > 10 && motionType?.rawValue == "walking") {
                    motionType = .automotive
                    motionAccuracy = .moving
                }
                
                if #available(iOS 11.0, *) {
                    if (motionType?.rawValue == "automotive"){
                        motionClassifier = MobilityDataManager.predictTransportMode(basemotionmode:0, lat: lat as! NSNumber, lng: lng as! NSNumber, speed: speedkmh as! NSNumber, predictionTextView: modeVal)
                    } else {
                        modeVal.text = String.getFrenchTranslationOf(word: motionType!.rawValue)
                        motionClassifier = motionType!.rawValue
                    }
                }
                
                /*if motionType?.rawValue == "automotive" {
                    let results = TimelineClassifier.highlander.classify(sample)
                    let bestMatch = results?.first
                    if (bestMatch != nil){
                        motionClassifier = (bestMatch?.name).map { $0.rawValue }!*/
                        if (motionType?.rawValue == "automotive"){
                            if (isBus && !isTrain && !isCar){
                                motionClassifier = "bus"
                            } else if (isTrain && !isBus && !isCar){
                                motionClassifier = "train"
                            } else if (isCar && !isTrain && !isBus) {
                                motionClassifier = "car"
                            } else if (isTrain && isBus && !isCar) {
                                motionClassifier = "train/bus"
                            } else if (isCar && isBus && !isTrain) {
                                motionClassifier = "bus/car"
                            } else if (isTrain && isCar && !isCar) {
                                motionClassifier = "train/car"
                            }
                        }
                    /*} else {
                        motionClassifier = "automotive"
                    }
                } else {
                    motionClassifier = motionType!.rawValue
                }*/
                
                lastMotionClassifier = motionClassifier
                
                self.gpsVal.text = String(format: "%.4f ; %.4f", lat!, lng!)
                self.speedVal.text = String (format: "%.2f Km/h", speedkmh)
                //self.motionVal.text = String.getFrenchTranslationOf(word: motionType.map { $0.rawValue }!)
                //self.modeVal.text = String.getFrenchTranslationOf(word: motionClassifier)
                self.timestamp.text = Date.getFormattedDate(date: sample.date)
                self.tripDistance.text = String(format: "%.2f KM", distance_course/1000)
                
                self.createLocation(timestamp: timestamp!, latitude: lat!, longitude: lng!, baseMotionMode: motionType!.rawValue, transportMotionMode: lastMotionClassifier, modeAccuracy: motionAccuracy.rawValue, xyAcceleration: accelerationXy!, zAcceleration: accelerationZ!, speed: speedkmh)
            }
        }
    }
    
    func createLocation(timestamp : TimeInterval, latitude : Double, longitude : Double, baseMotionMode : String, transportMotionMode : String, modeAccuracy : String, xyAcceleration : Double, zAcceleration : Double, speed : Double) {
        
        let locationItem: Locations = Locations()
        locationItem._userId = AWSIdentityManager.default().identityId
        locationItem._itemId = UUID().uuidString
        locationItem._timestamp = timestamp as NSNumber
        locationItem._latitude = latitude as NSNumber
        locationItem._longitude = longitude as NSNumber
        locationItem._baseMotionMode = baseMotionMode
        locationItem._transportMotionMode = transportMotionMode
        locationItem._modeAccuracy = modeAccuracy
        locationItem._xyAcceleration = xyAcceleration as NSNumber
        locationItem._zAcceleration = zAcceleration as NSNumber
        locationItem._speed = speed as NSNumber
        locationItem._usertag = userDefaults.string(forKey: "UserTag")
        let usertag = self.userDefaults.string(forKey: "UserTag")
        print (locationItem)
        
        if (transportMotionMode.contains("stationary") || transportMotionMode.contains("nil")){
            print ("Event will not be sent to MIS Observatory")
        } else {
            MobilityDataManager.doInvokeMotionAPI(locationItem)
        }
    }

    func updateTheTransportClassifier() {
        guard let coordinate = LocomotionManager.highlander.filteredLocation?.coordinate else {
            return
        }
        
        if let classifier = transportClassifier, classifier.contains(coordinate: coordinate), !classifier.isStale {
            return
        }
        
        transportClassifier = ActivityTypeClassifier(requestedTypes: ActivityTypeName.extendedTypes, coordinate: coordinate)
    }
    
    
    @IBOutlet weak var surveyHideView: UIView!
    @IBAction func onSurveyClicked(_ sender: Any) {
        var lat = -1.0, lng = -1.0
        let isSurveyRevealed = userDefaults.bool(forKey: "survey_V2.4.3_17")
        if (!isSurveyRevealed){
            surveyHideView.isHidden = true
            if (lastLocation != nil){
                lat = lastLocation.coordinate.latitude
                lng = lastLocation.coordinate.longitude
            }
            
            WalletManager.onSurveyReveled(lat: lat, lng: lng, userid: AWSIdentityManager.default().identityId!, timestamp: NSDate().timeIntervalSince1970)
            userDefaults.set(true, forKey: "survey_V2.4.3_17")
        }
    }
    
    func mapView(_ mapView: MGLMapView, annotationCanShowCallout annotation: MGLAnnotation) -> Bool {
        return true
    }
    
    func mapView(_ mapView: MGLMapView, tapOnCalloutFor annotation: MGLAnnotation) {
        startNavigation()
    }
    
    func startNavigation() {
        mapboxView.removeAnnotation(destinationAnnotation!)
        if (routeLayer != nil){
            mapboxView.style?.removeLayer(routeLayer!)
            mapboxView.style?.removeSource(source_route!)
        }
        let navigationViewController = NavigationViewController(for: directionsRoute!, styles: [CustomDayStyle(), CustomNightStyle()])
        self.present(navigationViewController, animated: true, completion: nil)
    }
    
    func calculateRoute(from origin: CLLocationCoordinate2D,
                        to destination: CLLocationCoordinate2D,
                        completion: @escaping (Route?, Error?) -> ()) {
        
        let origin = Waypoint(coordinate: origin, coordinateAccuracy: -1, name: "Votre Depart")
        let destination = Waypoint(coordinate: destination, coordinateAccuracy: -1, name: "Votre Destination")
        
        let options = NavigationRouteOptions(waypoints: [origin, destination], profileIdentifier: .automobileAvoidingTraffic)
        options.locale =  Locale(identifier: "fr")
        
        _ = Directions.shared.calculate(options) { [unowned self] (waypoints, routes, error) in
            self.directionsRoute = routes?.first
            self.drawRoute(route: self.directionsRoute!)
        }
    }
    
    func drawRoute(route: Route) {
        guard route.coordinateCount > 0 else { return }
        var routeCoordinates = route.coordinates!
        let polyline = MGLPolylineFeature(coordinates: &routeCoordinates, count: route.coordinateCount)
        
        if let source = mapboxView.style?.source(withIdentifier: "route-source") as? MGLShapeSource {
            source.shape = polyline
        } else {
            source_route = MGLShapeSource(identifier: "route-source", features: [polyline], options: nil)
            
            routeLayer = MGLLineStyleLayer(identifier: "route-style", source: source_route!)
            routeLayer?.lineColor = NSExpression(forConstantValue: #colorLiteral(red: 0, green: 1, blue: 0, alpha: 1))
            routeLayer?.lineWidth = NSExpression(forConstantValue: 5)
            
            mapboxView.style?.addSource(source_route!)
            mapboxView.style?.addLayer(routeLayer!)
        }
    }
    
    @objc func didLongPress(_ sender: UILongPressGestureRecognizer) {
        guard sender.state == .began else { return }
        
        // Converts point where user did a long press to map coordinates
        let point = sender.location(in: mapboxView)
        let coordinate = mapboxView.convert(point, toCoordinateFrom: mapboxView)
        
        if (destinationAnnotation != nil){
            mapboxView.removeAnnotation(destinationAnnotation!)
        }
        if (routeLayer != nil){
            mapboxView.style?.removeLayer(routeLayer!)
            mapboxView.style?.removeSource(source_route!)
        }
        
        // Create a basic point annotation and add it to the map
        destinationAnnotation = MGLPointAnnotation()
        destinationAnnotation?.coordinate = coordinate
        destinationAnnotation?.title = "Mode Navigation"
        destinationAnnotation?.subtitle = "Avec #MoveInSaclay !"
        mapboxView.addAnnotation(destinationAnnotation!)
        
        if !CLLocationManager.locationServicesEnabled() {
            self.createSettingsAlertController(title: "Activation GPS", message: "Nous aurions besoin de votre position GPS pour le partage de vos données de mobilité et la navigation. \n Settings > Privacy > Location Services")
        } else {
            // Calculate the route from the user's location to the set destination
            calculateRoute(from: (mapboxView.userLocation!.coordinate), to: coordinate) { (route, error) in
                if error != nil {
                    print("Error calculating route")
                }
            }
        }
    }
    
    @IBAction func onDeleteData(_ sender: Any) {
        createDeleteDataController();
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
    }
}
