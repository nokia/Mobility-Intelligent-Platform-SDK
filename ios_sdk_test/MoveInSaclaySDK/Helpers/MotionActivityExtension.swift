//
//  MotionActivityExtension.swift
//  MIP
//
//  Created by Ayoub Benyahya on 6/28/18.
//  Copyright © 2018 Ayoub Benyahya. All rights reserved.
//

import Foundation
import CoreMotion

extension CMMotionActivity {
    
    var motionTypes: [String] {
        var motionTypes = [String]()
        
        if self.stationary {
            motionTypes.append("Stationary")
        }
        
        if self.walking {
            motionTypes.append("Walking")
        }
        
        if self.running {
            motionTypes.append("Running")
        }
        
        if self.automotive {
            motionTypes.append("Transport")
        }
        
        if self.cycling {
            motionTypes.append("Cycling")
        }
        
        if self.unknown {
            motionTypes.append("Unknown")
        }
        
        if motionTypes.isEmpty {
            motionTypes.append("unidentified motion type")
        }
        
        return motionTypes
    }
    
    var printableDate: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = DateFormatter.Style.short
        dateFormatter.timeStyle = DateFormatter.Style.short
        
        return "Start date: \(dateFormatter.string(from: self.startDate))"
    }
    
    var printableConfidence: String {
        var printableConfidence = "Confidence: "
        
        switch self.confidence {
        case .low:
            printableConfidence = "Low"
            
        case .medium:
            printableConfidence = "Medium"
            
        case .high:
            printableConfidence = "High"
        }
        
        return printableConfidence
    }
}
