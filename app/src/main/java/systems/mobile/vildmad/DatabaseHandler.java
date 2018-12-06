package systems.mobile.vildmad;

import android.provider.ContactsContract;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHandler {

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mDatabase.getReference("Marker");

    public DatabaseHandler(){

    }

    public void writeNewMarker(CustomMarker cm){

        String id = myRef.push().getKey();

        CustomMarker customMarker = new CustomMarker(id, cm.getLng(), cm.getLat(), cm.isPublic(), cm.getPictureUrl(), cm.getDescription(), cm.getTitle());

        myRef.push().setValue(customMarker);
    }
}
