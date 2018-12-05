package systems.mobile.vildmad;

import com.google.android.gms.maps.model.MarkerOptions;

public class Marker {

    private MarkerOptions marker;
    private boolean isPublic;
    private int id;
    private String pictureUrl;

    public Marker (MarkerOptions marker, boolean isPublic, String pictureUrl){
        this.marker = marker;
        this.isPublic = isPublic;
        this.pictureUrl = pictureUrl;
    }


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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

}
