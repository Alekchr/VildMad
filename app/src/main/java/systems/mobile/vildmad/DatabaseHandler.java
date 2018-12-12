package systems.mobile.vildmad;

import android.provider.ContactsContract;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mDatabase.getReference("Marker");
    List<CustomMarker> list = new ArrayList();
    //List<CustomMarker> list;

    public DatabaseHandler(){

    }


    public void writeNewMarker(CustomMarker cm){

        String id = myRef.push().getKey();

        CustomMarker customMarker = new CustomMarker(cm.getId(), cm.getLng(), cm.getLat(), cm.isPublic(), cm.getPictureUrl(), cm.getDescription(), cm.getTitle(), cm.getType());

        myRef.push().setValue(customMarker);
    }
    public void readAllMarkers(){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot customMarkerSnapshot : dataSnapshot.getChildren()){
                    try {
                        CustomMarker marker = customMarkerSnapshot.getValue(CustomMarker.class);
                        list.add(marker);
                    }
                    catch (Exception e) {
                        System.out.println("Error " + e.getMessage());
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        myRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public List returnAllMarkers() {
        readAllMarkers();
        return list;
    }
/*    public CustomMarker returnMarkerByID(int id) {
        readAllMarkers();
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getId() == id)
                return list.get(i);
        return null;

    }*/
}
