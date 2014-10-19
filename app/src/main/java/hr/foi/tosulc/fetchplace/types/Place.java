package hr.foi.tosulc.fetchplace.types;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by TOMKHAN on 22.9.2014..
 */
public class Place {
    private int id;
    private String name;
    private String address;
    private LatLng location;

    public Place(int id, String name, String address, LatLng location) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
