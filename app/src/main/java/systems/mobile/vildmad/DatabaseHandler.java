package systems.mobile.vildmad;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private static DatabaseHandler databaseHandler;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mDatabase.getReference("Marker");
    List<CustomMarker> list = new ArrayList();
    List<CustomMarker> selectedList = new ArrayList();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    UploadTask uploadTask;

    private DatabaseHandler(){

    }

    public static synchronized DatabaseHandler getInstance() {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler();
        }
        return databaseHandler;
    }


    public void writeNewMarker(CustomMarker cm) {

        //UPLOAD THE IMAGE TO STORAGE
        Uri file = cm.getPictureUrl();
        if (file != null) {
            StorageReference locationPath = storageRef.child("images/" + file.getLastPathSegment());
            uploadTask = locationPath.putFile(file);
        }


        myRef.push().setValue(cm);
    }


    public void readAllMarkers(){
        list.clear();
        myRef.addValueEventListener(new ValueEventListener() {
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
        });

    }

    public List returnMarkerList() {
        return list;
    }

    public List<CustomMarker> returnMarkerByPlant(final String plantName) {
        list.clear();
        myRef.orderByChild("title").equalTo(plantName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot customMarkerSnapshot : dataSnapshot.getChildren()){
                            try {
                                CustomMarker marker = customMarkerSnapshot.getValue(CustomMarker.class);
                                list.add(marker);
                                Log.d("Custom markers", list.toString());
                            }
                            catch (Exception e) {
                                System.out.println("Error " + e.getMessage());
                            }
                        }}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });

        return list;
    }

/*            @Override
            public void onDataChange(DataSnapshot titleSnapshot) {
                String markerTitle = titleSnapshot.getValue(String.class);
                Query query = myRef.orderByChild("title").equalTo(plantName);
                query.addValueEventListener(new ValueEventListener() {*/

/*    public CustomMarker returnMarkerByID(int id) {
        readAllMarkers();
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getId() == id)
                return list.get(i);
        return null;

    }*/
}
