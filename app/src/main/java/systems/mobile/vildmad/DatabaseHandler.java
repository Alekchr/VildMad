package systems.mobile.vildmad;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseHandler {

    private static DatabaseHandler databaseHandler;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mDatabase.getReference("Marker");
    CopyOnWriteArrayList<CustomMarker> list = new CopyOnWriteArrayList();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    UploadTask uploadTask;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private DatabaseHandler() {

    }

    public static synchronized DatabaseHandler getInstance() {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler();
        }
        return databaseHandler;
    }

    public void writeNewMarker(final CustomMarker cm) {


        if (cm.getPictureUrl() != null) {
            Uri file = Uri.parse(cm.getPictureUrl());
            if (file != null) {
                StorageReference locationPath = storageRef.child("images/" + file.getLastPathSegment());
                uploadTask = locationPath.putFile(file);
            }

            System.out.println(cm.getPictureUrl());
        }
        myRef.push().setValue(cm);
    }


    public void readAllMarkers() {

        list.clear();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot customMarkerSnapshot : dataSnapshot.getChildren()) {
                    try {
                        CustomMarker marker = customMarkerSnapshot.getValue(CustomMarker.class);
                        list.add(marker);
                    } catch (Exception e) {
                        System.out.println("Error " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public boolean checkIfPublicAndUser(CustomMarker cm) {
        if (cm.isPublic() == true)
            return true;
        Log.d("uid", auth.getUid());
        if (cm.getId().equals(auth.getUid()))
            return true;
        else

            return false;
    }

    public List returnMarkerList() {
        return list;
    }

    public void returnMarkerByPlant(final String plantName) {
        myRef.orderByChild("title").equalTo(plantName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot customMarkerSnapshot : dataSnapshot.getChildren()) {
                    try {
                        CustomMarker marker = customMarkerSnapshot.getValue(CustomMarker.class);
                        if (!list.contains(marker) && checkIfPublicAndUser(marker) == true) {
                            list.add(marker);
                        }

                        Log.d("Custom markers", list.toString());
                    } catch (Exception e) {
                        System.out.println("Error " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    public void removeMarkerByPlant(String plantName) {
        for (Iterator<CustomMarker> itr = list.iterator(); ((Iterator) itr).hasNext(); ) {
            CustomMarker marker = itr.next();
            if (marker.getTitle().equals(plantName)) {
                list.remove(marker);
            }

        }

    }

}
