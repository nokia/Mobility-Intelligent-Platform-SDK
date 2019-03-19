//
//  NudgeArea.swift
//  MIP
//
//  Created by Ayoub Benyahya on 2/7/19.
//  Copyright © 2019 Ayoub Benyahya. All rights reserved.
//

import Foundation
import CoreLocation

class NudgeArea {
    var _identifier: String?
    var _radius : CLLocationDistance?
    var _center : CLLocationCoordinate2D?
    
    func nudgeArea (identifier: String, radius: CLLocationDistance, center: CLLocationCoordinate2D) {
        _identifier = identifier
        _radius = radius
        _center = center
    }
    
    func getCenter() -> CLLocationCoordinate2D {
        return _center!
    }
    
    func getRadius() -> CLLocationDistance {
        return _radius!
    }
    
    func getIdentifier() -> String {
        return _identifier!
    }
}
