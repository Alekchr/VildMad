package systems.mobile.vildmad;

import com.google.android.gms.maps.model.MarkerOptions;

public class CustomMarker {

    private MarkerOptions marker;
    private boolean isPublic;
    private String id;
    private String pictureUrl;
    private String description;
    private String title;
    private double lng;



    public CustomMarker(String id, double lng, double lat, boolean isPublic, String pictureUrl, String description, String titel) {
        this.id = id;
        this.lng = lng;
        this.lat = lat;
        this.isPublic = isPublic;
        this.pictureUrl = pictureUrl;
        this.description = description;
        this.title = titel;
    }

    public CustomMarker() {

    }



    public CustomMarker(double lng, double lat, boolean isPublic, String pictureUrl, String description, String titel) {
        this.lng = lng;
        this.lat = lat;
        this.isPublic = isPublic;
        this.pictureUrl = pictureUrl;
        this.description = description;
        this.title = titel;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }


}
