package systems.mobile.vildmad;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPicture{

    static final int REQUEST_IMAGE_CAPTURE = 11111;

    Activity activity;

    private String mCurrentPhotoPath;
    private OnactivityInteractionListener mListener;

    public AddPicture(Activity activity) {
        this.activity = activity;
    }

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     *
     * @return A new instance of activity AddPicture.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPicture newInstance(Activity activity) {
        AddPicture addPicture = new AddPicture(activity);
        return addPicture;
    }

    /*
        /**
         * OnCreateView activity override
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        takePicture();

        View view;
        view = inflater.inflate(R.layout.activity_add_picture, container, false);

        // Set the image view
        mImageView = (ImageView)view.findViewById(R.id.cameraImageView);
        Button cameraButton = (Button)view.findViewById(R.id.cameraButton);

        // Set OnItemClickListener so we can be notified on button clicks
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        return view;
    }
*/
    protected void takePicture(){
        Context context = activity;
        PackageManager pm = context.getPackageManager();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(pm) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                Toast toast = Toast.makeText(context, "There was a problem saving the photo...",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = FileProvider.getUriForFile(context,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        fileUri);
                activity.startActivityForResult(takePictureIntent,
                        REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    protected File createImageFile() throws IOException {
        Context context = activity;
        warnNoWritePermission(context);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Uri addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
        return contentUri;
    }



    /**
     * Scale the photo down and fit it to our image views.
     *
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        Log.d("CUrrent path", mCurrentPhotoPath);
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private void warnNoWritePermission(Context context) {
        int result = ActivityCompat.checkSelfPermission(context, Manifest.permission
                .WRITE_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest
                    .permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(activity.getApplicationContext(), "External Storage " +
                        "permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(activity,new String[]{Manifest
                        .permission
                        .WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onactivityInteraction(uri);
        }
    }


       /**
     * This interface must be implemented by activities that contain this
     * activity to allow an interaction in this activity to be communicated
     * to the activity and potentially other activitys contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/activitys/communicating.html"
     * >Communicating with Other activitys</a> for more information.
     */
    public interface OnactivityInteractionListener {
        // TODO: Update argument type and name
        void onactivityInteraction(Uri uri);
    }
}
