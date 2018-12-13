package systems.mobile.vildmad;

import android.net.Uri;

import com.google.android.gms.maps.model.MarkerOptions;

public class CustomMarker {

    private MarkerOptions marker;
    private boolean isPublic;
    private int id;
    private Uri pictureUrl;
    private String description;
    private String title;
    private String type;
    private double lng;



    public CustomMarker() {
        this.id = this.id;
        this.lng = this.lng;
        this.lat = this.lat;
        this.isPublic = isPublic;
        this.pictureUrl = this.pictureUrl;
        this.description = this.description;
        this.title = this.title;
        this.type = this.type;
    }

    public CustomMarker(double lng, double lat, boolean isPublic, Uri pictureUrl, String description, String title, String type) {
        this.lng = lng;
        this.lat = lat;
        this.isPublic = isPublic;
        this.pictureUrl = pictureUrl;
        this.description = description;
        this.title = title;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    private double lat;


    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(Uri pictureUrl) {
        this.pictureUrl = pictureUrl;
    }


}
