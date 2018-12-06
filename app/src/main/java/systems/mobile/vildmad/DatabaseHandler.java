package systems.mobile.vildmad;

import android.provider.ContactsContract;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHandler {

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mDatabase.getReference("marker");

    public DatabaseHandler(){

    }

    public void writeNewMarker(CustomMarker cm){
        CustomMarker customMarker = cm;
        myRef.child("marker").child("hej").setValue(customMarker);
    }
}
