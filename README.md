# Deprecation notice

The MoveInSaclay project was discontinued. There will no further active development, bug fix or maintenance.

# MoveInSaclay SDK Docs based on Android version

###### This SDK is a part of MoveInSaclay Plateform tools, it helps Partners for a quick integration. The main features of the SDK :
    - Users Account Management (Account creation, Auth) managed by AWS Cognito
    - Viz the differents postions in cluster mode on the Map
    - Fetching Smartphone Mobility Data and send it to the platforme in realtime when the user accept to share (GPS, Speed, Acceleration, Transportation Mode, Timestamp and Distance)
    - Manage the User Mobility Profile (Most used transport mode, Average Speed, Mobility Cost, Carbon footprint)
    - Survey Connector
    - Nudge / m-Wallet Management

###### In this SDK we include the code in order to call different APIs (Motion API, Profile API, Wallet API) in the folder APIGateway. And for each feature we got a Class :

    - Users Account Management : Managed by AuthManager & AuthManager class
    - Smartphone Mobility Data : Managed by MobilityDataManager class
    - User Mobility Profile    : Managed by ProfileManager class
    - Survey Connector         : Managed by SurveyManager class
    - m-Wallet Management      : Managed by WalletManager class

## SDK Functions

* AuthManager methods :

    - method init_auth() : init AuthManager
    - Instance of SignInUI class: verify AWSMobileClient credentials and load OnboardingActivity UI


* MobilityDataManager methods :

    - method doInvokeMotionAPI(httpBody,apiClient) : Send Mobility Datas on the httpBody using Motion API .
    - method doGetDistance(lat1 ,lng1,lat2,lng2) : get the distance between two gps positions .
    - method doCurrentGetDate() : get current date in UTC format.

* ProfileManager functions :

    - method doInvokeProfilAPI(type,resultTextView,costTextView,carbonTextView,apiClient): fetch speed ,transport mode  and distance values using profile API and update the values in the UI.
    - method doInvokeProfilAPI_distance(httpBody,apiClient) : post the trip description including the distance.
    - method doInvokeProfilAPI_record(context,mapboxMap,apiClient) : fetch all user records and Viz in a Map View on UI context.
    - method manageEndOfTrip(lat_start,lng_start,lat_stop,lng_stop,distance,duration,start_date,apiClient) : post the trip descriptions between start and stop postions when user stop data sharing

* SurveyManager method :

    - method loadSurvey (webView)  : load the survey and put it in a WebView

* WalletManager methods :

    - method doInvokeWalletAPI(tokenTextView,apiClient) : Fetch user tokens numbers and update the value of the wallet in the UI
    - method getCurrentToken() -> Double : Get current user token numbers
    - method setNewTokenValue(tokenValue) : Set new token number
    - method doInvokeRewardsAPI(httpBody, apiClient) : Send rewards based on Nudge Configuration
    - method manageNudgeEndOfTrip(lat,lng,token,apiClient) : Send rewards when user share data

* MapTool methods
    - method addSourceOnMapBoxMap(context,featureCollection,mapboxMap) : run on UI Thread and add the recording postions in geojsonSource to make clustering on Map View
    - method addIsochroneDataToMap(mapboxMap,isochroneSource) : Use the orsay_isochrone geojson file adding on isochronesource to display the isochrone area on map View according to the time.

# UI Activity management
    - OnboardingActivity: Use the activity_onboarding.xml file -> Onboarding & Authentification
    - MainActivity: Use the activity_main.xml file -> in this view we will be using all functions from other classes in order to share mobility data, get mobility profile information, manage wallet and surveys.

## Permission :
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
```

## SDK Testing
1. `git clone`
2. `Open project in Android Studio & Sync Gradle`
3. `Enjoy !`


# MoveInSaclay SDK Docs based on iOS version

###### This SDK is a part of MoveInSaclay Plateform tools, it helps Partners for a quick integration. The main features of the SDK :
    - Users Account Management (Account creation, Auth) managed by AWS Cognito
    - Fetching Smartphone Mobility Data and send it to the platforme in realtime when the user accept to share (GPS, Speed, Acceleration, Transportation Mode, Timestamp)
    - Manage the User Mobility Profile (Most used transport mode, Average Speed, Mobility Cost, Carbon footprint)
    - Survey Connector
    - Nudge / m-Wallet Management

###### In this SDK we include the code in order to call different APIs (Motion API, Profile API, Wallet API) in the folder APIGateway. And for each feature we got a Class :
    - Users Account Management : Managed by AuthManager & AWSSignInManager class
    - Smartphone Mobility Data : Managed by MobilityDataManager class
    - User Mobility Profile    : Managed by ProfileManager class
    - Survey Connector         : Managed by SurveyManager class
    - m-Wallet Management      : Managed by WalletManager class

## SDK Functions
* AuthManager functions
    - func init_AppDelegate() : init AWSSignInManager
    - func signIn(navigationController: UINavigationController, completionHandler:@escaping AWSAuthUICompletionHandler)

* MobilityDataManager functions
    - func doInvokeMotionAPI(locationRecord) : Send Mobility Data using Motion API

* ProfileManager functions
    - func doInvokeProfilAPI(type : String, resultField: UILabel) : fetch speed and transport mode value using profile API and update the value in the UI
    - func doInvokeProfilAPI_distance(httpBody : [String : Any]) : post the trip description including the distance
    - func doInvokeProfilAPI_record(map: MKMapView) : fetch all user records and Viz in a Map View

* SurveyManager functions :
    - func loadSurvey (surveyWeb : UIWebView)  : load the survey a put it in a WebView

* WalletManager functions
    - func doInvokeWalletAPI(resultField: UILabel) : Fetch user tokens numbers and update the value of the wallet in the UI
    - func getCurrentToken() -> Double : Get current user token numbers
    - func setNewTokenValue(token:Double, tokenUIfield: UILabel) : Set new token number and update the Wallet UI
    - func doInvokeRewardsAPI(httpBody : [String : Any]) : Send rewards based on Nudge Configuration
    - func manageNudgeZone(location: CLLocationCoordinate2D, lastSample : LocomotionSample, tokenUIfield: UILabel) : Send rewards when user detected in a specific area
    - func manageNudgeEndOfTrip( firstSample : LocomotionSample, lastSample : LocomotionSample, tokenUIfield: UILabel, tripDistance : Double, tripDuration : Double) : Send rewards when user share data

## UI management
* OnboardingViewController : For Onboarding and Authentification
* MainViewController : In this view we will be using all functions from other classes in order to share mobility data, get mobility profile information, manage wallet and surveys.

## Capabilities
* Background Mode : Location update and audiod
* Push Notification

## Dependencies
*  LocoKit : Activity Recognition
*  SwiftNotes : NotificationCenter Wrapper, events management
*  AWSAuthCore, AWSAuthUI, AWSUserPoolsSignIn : AWS Authentificating
*  AWSAPIGateway, AWSMobileClient :
*  AWSDynamoDB : AWS DB
*  Mapbox-iOS-SDK, MapboxNavigation, MapboxGeocoder.swift : Maps & Navigation
*  AppCenter : Crash & Analytics (HockeyApp)

## SDK Testing
1. `git clone`
2. `pod install`
3. `Enjoy !`
