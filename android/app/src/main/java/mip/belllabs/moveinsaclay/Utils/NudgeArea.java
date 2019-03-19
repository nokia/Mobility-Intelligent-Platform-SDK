package mip.belllabs.moveinsaclay.Utils;

import com.mapbox.geojson.Point;

public class NudgeArea {
    private  String identifier;
    private int raduis;
    private Point center;

    //public NudgeArea(){} // constructeur vide
    public NudgeArea(String identifier, int raduis, Point center){
        this.center=center;
        this.raduis=raduis;
        this.identifier=identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setRaduis(int raduis) {
        this.raduis = raduis;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getRaduis() {
        return raduis;
    }

    public Point getCenter() {
        return center;
    }

}
