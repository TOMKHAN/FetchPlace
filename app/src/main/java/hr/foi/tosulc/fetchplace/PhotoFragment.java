package hr.foi.tosulc.fetchplace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import hr.foi.tosulc.fetchplace.flickr.FlickrjActivity;
import hr.foi.tosulc.fetchplace.helpers.ConnectionDetector;

import static hr.foi.tosulc.fetchplace.helpers.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static hr.foi.tosulc.fetchplace.helpers.CommonUtilities.EXTRA_MESSAGE;

/**
 * Created by tosulc on 15.10.2014..
 */
public class PhotoFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static ImageButton btnUploadPicture;
    public static ImageButton btnViewPictureInBrowser;
    public static ImageView ivPictureTaken;
    public static String currentPhotoPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_foto, container, false);
        final ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
        ImageButton btnTakePicture = (ImageButton) rootView.findViewById(R.id.btn_take_picture);
        btnUploadPicture = (ImageButton) rootView.findViewById(R.id.btn_upload_picture);
        btnViewPictureInBrowser = (ImageButton) rootView.findViewById(R.id.btn_goto_website);

        getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPictureTaken = (ImageView) getActivity().findViewById(R.id.iv_picture_taken);
                if (cd.isConnectedToInternet()) {
                    if (ivPictureTaken.getDrawable() != null) {
                        File sdCardDirectory = Environment
                                .getExternalStorageDirectory();
                        Intent photoUploadIntent = new Intent(getActivity().getApplicationContext(), FlickrjActivity.class);
                        photoUploadIntent.putExtra("flickImagePath", sdCardDirectory + "/FetchPlace/awesome_picture.png");
                        startActivity(photoUploadIntent);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.no_picture_selected), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.need_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            ivPictureTaken = (ImageView) getActivity().findViewById(R.id.iv_picture_taken);
            scaleAndShowPhotoImage();
            btnUploadPicture.setVisibility(View.VISIBLE);
            btnViewPictureInBrowser.setVisibility(View.INVISIBLE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File sdCardDirectory = Environment
                    .getExternalStorageDirectory();
            new File(sdCardDirectory + "/FetchPlace/").mkdir();
            currentPhotoPath = sdCardDirectory + "/FetchPlace/awesome_picture.png";
            File photoFile = new File(currentPhotoPath);
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void scaleAndShowPhotoImage() {
        // Get the dimensions of the View
        int targetW = ivPictureTaken.getWidth();
        int targetH = ivPictureTaken.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        Bitmap rotatedBitmap = rotatePicture(bitmap,currentPhotoPath);
        ivPictureTaken.setImageBitmap(rotatedBitmap);
    }

    /**
     * BroadcastReceiver for receiving responsePictureID after uploading picture asynctask
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String responsePictureID = intent.getExtras().getString(EXTRA_MESSAGE);
            btnUploadPicture.setVisibility(View.INVISIBLE);
            final String userFlickrID = getUserIDFromSharedPreferences();
            btnViewPictureInBrowser.setVisibility(View.VISIBLE);
            btnViewPictureInBrowser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://www.flickr.com/photos/" + userFlickrID + "/" + responsePictureID + "/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public String getUserIDFromSharedPreferences() {
        String PREFS_NAME = "flickrj-android-sample-pref";
        String KEY_USER_ID = "flickrj-android-userId";
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getString(KEY_USER_ID, null);
    }

    public Bitmap rotatePicture(Bitmap bitmap, String imagePath) {
        Bitmap rotatedBitmap = null;
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }
}
