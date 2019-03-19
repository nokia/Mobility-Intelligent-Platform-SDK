package mip.belllabs.moveinsaclay.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "mip-mobilehub-1184013560-Locations")

public class LocationsDO {
    private String _userId;
    private String _itemId;
    private String _baseMotionMode;
    private Double _latitude;
    private Double _longitude;
    private String _modeAccuracy;
    private Double _speed;
    private Double _timestamp;
    private String _transportMotionMode;
    private Double _xyAcceleration;
    private Double _zAcceleration;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "itemId")
    @DynamoDBAttribute(attributeName = "itemId")
    public String getItemId() {
        return _itemId;
    }

    public void setItemId(final String _itemId) {
        this._itemId = _itemId;
    }
    @DynamoDBAttribute(attributeName = "baseMotionMode")
    public String getBaseMotionMode() {
        return _baseMotionMode;
    }

    public void setBaseMotionMode(final String _baseMotionMode) {
        this._baseMotionMode = _baseMotionMode;
    }
    @DynamoDBAttribute(attributeName = "latitude")
    public Double getLatitude() {
        return _latitude;
    }

    public void setLatitude(final Double _latitude) {
        this._latitude = _latitude;
    }
    @DynamoDBAttribute(attributeName = "longitude")
    public Double getLongitude() {
        return _longitude;
    }

    public void setLongitude(final Double _longitude) {
        this._longitude = _longitude;
    }
    @DynamoDBAttribute(attributeName = "modeAccuracy")
    public String getModeAccuracy() {
        return _modeAccuracy;
    }

    public void setModeAccuracy(final String _modeAccuracy) {
        this._modeAccuracy = _modeAccuracy;
    }
    @DynamoDBAttribute(attributeName = "speed")
    public Double getSpeed() {
        return _speed;
    }

    public void setSpeed(final Double _speed) {
        this._speed = _speed;
    }
    @DynamoDBAttribute(attributeName = "timestamp")
    public Double getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(final Double _timestamp) {
        this._timestamp = _timestamp;
    }
    @DynamoDBAttribute(attributeName = "transportMotionMode")
    public String getTransportMotionMode() {
        return _transportMotionMode;
    }

    public void setTransportMotionMode(final String _transportMotionMode) {
        this._transportMotionMode = _transportMotionMode;
    }
    @DynamoDBAttribute(attributeName = "xyAcceleration")
    public Double getXyAcceleration() {
        return _xyAcceleration;
    }

    public void setXyAcceleration(final Double _xyAcceleration) {
        this._xyAcceleration = _xyAcceleration;
    }
    @DynamoDBAttribute(attributeName = "zAcceleration")
    public Double getZAcceleration() {
        return _zAcceleration;
    }

    public void setZAcceleration(final Double _zAcceleration) {
        this._zAcceleration = _zAcceleration;
    }

}
