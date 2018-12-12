package systems.mobile.vildmad;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMarkerView implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomMarkerView(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.marker_selected_layout, null);

        TextView name_tv = view.findViewById(R.id.mTypeText);
        CustomMarker cm = (CustomMarker) marker.getTag();
        name_tv.setText(cm.getDescription());

        return view;
    }
}
