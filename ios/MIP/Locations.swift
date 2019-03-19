import Foundation
import UIKit
import AWSDynamoDB

@objcMembers
class Locations: AWSDynamoDBObjectModel, AWSDynamoDBModeling {
    
    var _userId: String?
    var _itemId: String?
    var _baseMotionMode: String?
    var _latitude: NSNumber?
    var _longitude: NSNumber?
    var _modeAccuracy: String?
    var _timestamp: NSNumber?
    var _transportMotionMode: String?
    var _xyAcceleration: NSNumber?
    var _zAcceleration: NSNumber?
    var _speed: NSNumber?
    var _usertag: String?
    
    class func dynamoDBTableName() -> String {
        return "mip-mobilehub-1184013560-Locations"
    }
    
    class func hashKeyAttribute() -> String {

        return "_userId"
    }
    
    class func rangeKeyAttribute() -> String {
        return "_itemId"
    }
    
    override class func jsonKeyPathsByPropertyKey() -> [AnyHashable: Any] {
        return [
               "_userId" : "userId",
               "_itemId" : "itemId",
               "_baseMotionMode" : "baseMotionMode",
               "_latitude" : "latitude",
               "_longitude" : "longitude",
               "_modeAccuracy" : "modeAccuracy",
               "_timestamp" : "timestamp",
               "_transportMotionMode" : "transportMotionMode",
               "_xyAcceleration" : "xyAcceleration",
               "_zAcceleration" : "zAcceleration",
               "_speed" : "speed",
               "_usertag" : "usertag",
        ]
    }
}
