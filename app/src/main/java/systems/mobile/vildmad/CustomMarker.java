package systems.mobile.vildmad;

import com.google.android.gms.maps.model.MarkerOptions;

public class CustomMarker {

    private MarkerOptions marker;
    private boolean isPublic;
    private int id;
    private String pictureUrl;
    private String description;
    private String title;

    public CustomMarker (MarkerOptions marker, boolean isPublic, String pictureUrl, String description, String titel){
        this.marker = marker;
        this.isPublic = isPublic;
        this.pictureUrl = pictureUrl;
        this.description = description;
        this.title = titel;
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
