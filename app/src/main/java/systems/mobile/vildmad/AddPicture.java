package systems.mobile.vildmad;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddPicture.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddPicture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPicture extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static final int REQUEST_IMAGE_CAPTURE = 11111;


    private String mParam1;
    private String mParam2;

    private String mCurrentPhotoPath;
    private ImageView mImageView;
    private OnFragmentInteractionListener mListener;

    public AddPicture() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPicture.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPicture newInstance(String param1, String param2) {
        AddPicture fragment = new AddPicture();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * OnCreateView fragment override
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_find, container, false);

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

    protected void takePicture(){
        Context context = getActivity();
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
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            addPhotoToGallery();
            // Show the full sized image.
            setFullImageFromFilePath(mCurrentPhotoPath, mImageView);
        } else {
            Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    protected File createImageFile() throws IOException {
        Context context = getActivity();
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

    protected void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest
                    .permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(getActivity().getApplicationContext(), "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission
                        .WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
