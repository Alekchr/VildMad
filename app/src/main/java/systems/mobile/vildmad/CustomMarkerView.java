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

        TextView type = view.findViewById(R.id.mTypeText);
        TextView kind = view.findViewById(R.id.mKindText);
        TextView descr = view.findViewById(R.id.mNoteTextView);

        CustomMarker cm = (CustomMarker) marker.getTag();
        type.setText(cm.getTitle());
        kind.setText(cm.getPictureUrl()); // CHANGE THIS TO WHAT KIND IT IS LATER
        descr.setText(cm.getDescription());

        return view;
    }
}
