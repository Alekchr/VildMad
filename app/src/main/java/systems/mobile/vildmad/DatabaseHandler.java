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

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mDatabase.getReference("Marker");
    List<CustomMarker> list;

    public DatabaseHandler(){
    list = new ArrayList<>();
    }

    public void writeNewMarker(CustomMarker cm){

        String id = myRef.push().getKey();

        CustomMarker customMarker = new CustomMarker(cm.getId(), cm.getLng(), cm.getLat(), cm.isPublic(), cm.getPictureUrl(), cm.getDescription(), cm.getTitle());

        myRef.push().setValue(customMarker);
    }
    public void readAllMarkers(){



        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                list.clear();

                for(DataSnapshot customMarkerSnapshot : dataSnapshot.getChildren()){
                    CustomMarker marker = customMarkerSnapshot.getValue(CustomMarker.class);

                    list.add(marker);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public CustomMarker returnMarkerByID(int id) {
        readAllMarkers();
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getId() == id)
                return list.get(i);
        return null;

    }
}
