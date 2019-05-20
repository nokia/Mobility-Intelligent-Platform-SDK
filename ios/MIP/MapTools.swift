//
//  MapTools.swift
//  MIP
//
//  Created by Ayoub Benyahya on 12/5/18.
//  Copyright © 2018 Ayoub Benyahya. All rights reserved.
//

import Foundation
import MapKit
import Mapbox
import MapboxCoreNavigation
import MapboxNavigation
import MapboxDirections

class PathPolyline: MKPolyline {
    
    var color: UIColor?
    
    var renderer: MKPolylineRenderer {
        let renderer = MKPolylineRenderer(polyline: self)
        renderer.strokeColor = color
        renderer.lineWidth = 3
        return renderer
    }
}

class customPin: NSObject, MKAnnotation {
    var coordinate: CLLocationCoordinate2D
    var title: String?
    var subtitle: String?
    var pinColor: UIColor?
    
    init(pinTitle:String, pinSubTitle:String, location:CLLocationCoordinate2D, pColor:UIColor) {
        self.title = pinTitle
        self.subtitle = pinSubTitle
        self.coordinate = location
        self.pinColor = pColor
    }
}

class MyPointAnnotation: MGLPointAnnotation {
    var flagImage: UIImage?
}


class CustomDayStyle: DayStyle {
    
    private let backgroundColor = #colorLiteral(red: 0.02352941176, green: 0.1176470588, blue: 0.3294117647, alpha: 1)
    private let darkBackgroundColor = #colorLiteral(red: 0.02745098039, green: 0.1333333333, blue: 0.3725490196, alpha: 1)
    private let secondaryBackgroundColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
    private let blueColor = #colorLiteral(red: 0.26683864, green: 0.5903761983, blue: 1, alpha: 1)
    private let lightGrayColor = #colorLiteral(red: 0.501960814, green: 0.501960814, blue: 0.501960814, alpha: 1)
    private let darkGrayColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)
    private let primaryLabelColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    private let secondaryLabelColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0.9)
    
    required init() {
        super.init()
        mapStyleURL = URL(string: "mapbox://styles/abenyahya/cjpv5v4pl0zio2smwsv6s5b1j")!
        styleType = .day
    }
    
    override func apply() {
        super.apply()
        ArrivalTimeLabel.appearance().textColor = lightGrayColor
        BottomBannerView.appearance().backgroundColor = secondaryBackgroundColor
        BottomBannerContainerView.appearance().backgroundColor = secondaryBackgroundColor
        Button.appearance().textColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        CancelButton.appearance().tintColor = lightGrayColor
        DistanceLabel.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).unitTextColor = secondaryLabelColor
        DistanceLabel.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).valueTextColor = primaryLabelColor
        DistanceLabel.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).unitTextColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        DistanceLabel.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).valueTextColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        DistanceRemainingLabel.appearance().textColor = lightGrayColor
        DismissButton.appearance().textColor = darkGrayColor
        FloatingButton.appearance().backgroundColor = #colorLiteral(red: 0.9999960065, green: 1, blue: 1, alpha: 1)
        FloatingButton.appearance().tintColor = blueColor
        InstructionsBannerView.appearance().backgroundColor = backgroundColor
        InstructionsBannerContentView.appearance().backgroundColor = backgroundColor
        LanesView.appearance().backgroundColor = darkBackgroundColor
        LaneView.appearance().primaryColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        ManeuverView.appearance().backgroundColor = backgroundColor
        ManeuverView.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).primaryColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        ManeuverView.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).secondaryColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0.5)
        ManeuverView.appearance(whenContainedInInstancesOf: [NextBannerView.self]).primaryColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        ManeuverView.appearance(whenContainedInInstancesOf: [NextBannerView.self]).secondaryColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0.5)
        ManeuverView.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).primaryColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        ManeuverView.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).secondaryColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        MarkerView.appearance().pinColor = blueColor
        NextBannerView.appearance().backgroundColor = backgroundColor
        NextInstructionLabel.appearance().textColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        NavigationMapView.appearance().tintColor = blueColor
        NavigationMapView.appearance().routeCasingColor = #colorLiteral(red: 0.1968861222, green: 0.4148176908, blue: 0.8596113324, alpha: 1)
        NavigationMapView.appearance().trafficHeavyColor = #colorLiteral(red: 0.9995597005, green: 0, blue: 0, alpha: 1)
        NavigationMapView.appearance().trafficLowColor = #colorLiteral(red: 0, green: 1, blue: 0, alpha: 1)
        NavigationMapView.appearance().trafficModerateColor = #colorLiteral(red: 1, green: 0.6184511781, blue: 0, alpha: 1)
        NavigationMapView.appearance().trafficSevereColor = #colorLiteral(red: 0.7458544374, green: 0.0006075350102, blue: 0, alpha: 1)
        NavigationMapView.appearance().trafficUnknownColor = blueColor
        PrimaryLabel.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).normalTextColor = primaryLabelColor
        PrimaryLabel.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).normalTextColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        ResumeButton.appearance().backgroundColor = secondaryBackgroundColor
        ResumeButton.appearance().tintColor = blueColor
        SecondaryLabel.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).normalTextColor = secondaryLabelColor
        SecondaryLabel.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).normalTextColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        TimeRemainingLabel.appearance().textColor = lightGrayColor
        TimeRemainingLabel.appearance().trafficLowColor = #colorLiteral(red: 0, green: 0.6272783279, blue: 0, alpha: 1)
        TimeRemainingLabel.appearance().trafficHeavyColor = #colorLiteral(red: 0.9995597005, green: 0, blue: 0, alpha: 1)
        TimeRemainingLabel.appearance().trafficModerateColor = #colorLiteral(red: 1, green: 0.6184511781, blue: 0, alpha: 1)
        TimeRemainingLabel.appearance().trafficUnknownColor = #colorLiteral(red: 0.1968861222, green: 0.4148176908, blue: 0.8596113324, alpha: 1)
        WayNameLabel.appearance().normalTextColor = blueColor
        WayNameView.appearance().backgroundColor = secondaryBackgroundColor
    }
}

class CustomNightStyle: NightStyle {
    
    private let backgroundColor = #colorLiteral(red: 0.02352941176, green: 0.1176470588, blue: 0.3294117647, alpha: 1)
    private let darkBackgroundColor = #colorLiteral(red: 0.02745098039, green: 0.1333333333, blue: 0.3725490196, alpha: 1)
    private let secondaryBackgroundColor = #colorLiteral(red: 0.1122514829, green: 0.1219404861, blue: 0.1349411011, alpha: 1)
    private let blueColor = #colorLiteral(red: 0.26683864, green: 0.5903761983, blue: 1, alpha: 1)
    private let lightGrayColor = #colorLiteral(red: 0.501960814, green: 0.501960814, blue: 0.501960814, alpha: 1)
    private let darkGrayColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)
    private let primaryTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    private let secondaryTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0.9)
    private let primaryLabelColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    private let secondaryLabelColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0.9)
    
    required init() {
        super.init()
         mapStyleURL = URL(string: "mapbox://styles/abenyahya/cjpv5yqcg0rc82sp2667vvic3")!
        styleType = .night
    }
    
    override func apply() {
        super.apply()
        ArrivalTimeLabel.appearance().textColor = lightGrayColor
        BottomBannerView.appearance().backgroundColor = secondaryBackgroundColor
        BottomBannerContainerView.appearance().backgroundColor = secondaryBackgroundColor
        Button.appearance().textColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        CancelButton.appearance().tintColor = lightGrayColor
        DistanceLabel.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).unitTextColor = secondaryLabelColor
        DistanceLabel.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).valueTextColor = primaryLabelColor
        DistanceLabel.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).unitTextColor = lightGrayColor
        DistanceLabel.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).valueTextColor = darkGrayColor
        DistanceRemainingLabel.appearance().textColor = lightGrayColor
        DismissButton.appearance().textColor = primaryTextColor
        FloatingButton.appearance().tintColor = blueColor
        InstructionsBannerView.appearance().backgroundColor = backgroundColor
        InstructionsBannerContentView.appearance().backgroundColor = backgroundColor
        LanesView.appearance().backgroundColor = darkBackgroundColor
        LaneView.appearance().primaryColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        ManeuverView.appearance().backgroundColor = backgroundColor
        ManeuverView.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).primaryColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        ManeuverView.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).secondaryColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0.5)
        ManeuverView.appearance(whenContainedInInstancesOf: [NextBannerView.self]).primaryColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        ManeuverView.appearance(whenContainedInInstancesOf: [NextBannerView.self]).secondaryColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0.5)
        ManeuverView.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).primaryColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        ManeuverView.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).secondaryColor = lightGrayColor
        MarkerView.appearance().pinColor = blueColor
        NextBannerView.appearance().backgroundColor = backgroundColor
        NextInstructionLabel.appearance().textColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        NavigationMapView.appearance().tintColor = blueColor
        NavigationMapView.appearance().routeCasingColor = #colorLiteral(red: 0.1968861222, green: 0.4148176908, blue: 0.8596113324, alpha: 1)
        NavigationMapView.appearance().trafficHeavyColor = #colorLiteral(red: 0.9995597005, green: 0, blue: 0, alpha: 1)
        NavigationMapView.appearance().trafficLowColor = #colorLiteral(red: 0, green: 1, blue: 0, alpha: 1)
        NavigationMapView.appearance().trafficModerateColor = #colorLiteral(red: 1, green: 0.6184511781, blue: 0, alpha: 1)
        NavigationMapView.appearance().trafficSevereColor = #colorLiteral(red: 0.7458544374, green: 0.0006075350102, blue: 0, alpha: 1)
        NavigationMapView.appearance().trafficUnknownColor = blueColor
        PrimaryLabel.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).normalTextColor = primaryLabelColor
        PrimaryLabel.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).normalTextColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        SecondaryLabel.appearance(whenContainedInInstancesOf: [InstructionsBannerView.self]).normalTextColor = secondaryLabelColor
        SecondaryLabel.appearance(whenContainedInInstancesOf: [StepInstructionsView.self]).normalTextColor = #colorLiteral(red: 0.9842069745, green: 0.9843751788, blue: 0.9841964841, alpha: 1)
        TimeRemainingLabel.appearance().textColor = lightGrayColor
        TimeRemainingLabel.appearance().trafficLowColor = #colorLiteral(red: 0, green: 0.6272783279, blue: 0, alpha: 1)
        TimeRemainingLabel.appearance().trafficHeavyColor = #colorLiteral(red: 0.9995597005, green: 0, blue: 0, alpha: 1)
        TimeRemainingLabel.appearance().trafficModerateColor = #colorLiteral(red: 1, green: 0.6184511781, blue: 0, alpha: 1)
        TimeRemainingLabel.appearance().trafficUnknownColor = #colorLiteral(red: 0.1968861222, green: 0.4148176908, blue: 0.8596113324, alpha: 1)
        WayNameLabel.appearance().normalTextColor = blueColor
        WayNameView.appearance().backgroundColor = secondaryBackgroundColor
        
        DistanceRemainingLabel.appearance().normalTextColor = primaryTextColor
        BottomBannerView.appearance().backgroundColor = secondaryBackgroundColor
        BottomBannerContainerView.appearance().backgroundColor = secondaryBackgroundColor
        FloatingButton.appearance().backgroundColor = #colorLiteral(red: 0.1434620917, green: 0.1434366405, blue: 0.1819391251, alpha: 0.9037466989)
        ResumeButton.appearance().backgroundColor = #colorLiteral(red: 0.1434620917, green: 0.1434366405, blue: 0.1819391251, alpha: 0.9037466989)
        ResumeButton.appearance().tintColor = blueColor
    }
}

class Map: MGLMapView{}

class MapTools {
    static var icon = UIImage(named: "dot")
    static func addRecordstoMap(map: MGLMapView, shape: [MGLShape]/*, coordinates: [CLLocationCoordinate2D]*/) {
        guard let style = map.style else { return }
        let source = MGLShapeSource(identifier: "clusteredPorts", features: shape as! [MGLShape & MGLFeature], options: [.clustered: true, .clusterRadius: 65])
        
        style.addSource(source)
        style.setImage((icon?.withRenderingMode(.alwaysTemplate))!, forName: "icon")
        
        let ports = MGLSymbolStyleLayer(identifier: "ports", source: source)
        ports.iconImageName = NSExpression(forConstantValue: "icon")
        ports.iconColor = NSExpression(forConstantValue: UIColor.darkGray.withAlphaComponent(0.9))
        ports.predicate = NSPredicate(format: "cluster != YES")
        style.addLayer(ports)
        
        let stops = [
            50: UIColor.blue,
            100: UIColor.orange,
            200: UIColor.red
        ]
        
        let circlesLayer = MGLCircleStyleLayer(identifier: "clusteredPorts", source: source)
        circlesLayer.circleRadius = NSExpression(forConstantValue: NSNumber(value: 25.0))
        circlesLayer.circleOpacity = NSExpression(forConstantValue: 0.75)
        circlesLayer.circleStrokeColor = NSExpression(forConstantValue: UIColor.white.withAlphaComponent(0.75))
        circlesLayer.circleStrokeWidth = NSExpression(forConstantValue: 2)
        circlesLayer.circleColor = NSExpression(format: "mgl_step:from:stops:(point_count, %@, %@)", UIColor.blue, stops)
        circlesLayer.predicate = NSPredicate(format: "cluster == YES")
        style.addLayer(circlesLayer)
        let numbersLayer = MGLSymbolStyleLayer(identifier: "clusteredPortsNumbers", source: source)
        numbersLayer.textColor = NSExpression(forConstantValue: UIColor.white)
        numbersLayer.textFontSize = NSExpression(forConstantValue: NSNumber(value: 20))
        numbersLayer.iconAllowsOverlap = NSExpression(forConstantValue: true)
        numbersLayer.text = NSExpression(format: "CAST(point_count, 'NSString')")
        
        numbersLayer.predicate = NSPredicate(format: "cluster == YES")
        style.addLayer(numbersLayer)
        /*let polyline = MGLPolyline(coordinates: coordinates, count: UInt(coordinates.count))
        map.addAnnotation(polyline)*/
    }
    
    static func addNudgeAreastoMap(map: MGLMapView, shape: [MGLShape]) {
        guard let style = map.style else { return }
        let source = MGLShapeSource(identifier: "nudgeRules", features: shape as! [MGLShape & MGLFeature], options: nil)
        
        style.addSource(source)
        
        let circlesLayer = MGLCircleStyleLayer(identifier: "nudgeRules", source: source)
        circlesLayer.circleRadius = NSExpression(forConstantValue: 25)
        circlesLayer.circleOpacity = NSExpression(forConstantValue: 0.25)
        circlesLayer.circleStrokeColor = NSExpression(forConstantValue: UIColor.white.withAlphaComponent(0.75))
        circlesLayer.circleStrokeWidth = NSExpression(forConstantValue: 1)
        circlesLayer.circleColor = NSExpression(forConstantValue: UIColor.red)
        style.addLayer(circlesLayer)
    }
}
