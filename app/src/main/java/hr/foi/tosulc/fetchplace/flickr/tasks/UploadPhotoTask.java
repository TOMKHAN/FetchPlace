package hr.foi.tosulc.fetchplace.flickr.tasks;

import java.io.File;
import java.io.FileInputStream;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;

import hr.foi.tosulc.fetchplace.R;
import hr.foi.tosulc.fetchplace.flickr.FlickrjActivity;
import hr.foi.tosulc.fetchplace.flickr.helpers.FlickrHelper;
import hr.foi.tosulc.fetchplace.helpers.CommonUtilities;

/**
 * Created by tosulc on 16.10.2014..
 */
public class UploadPhotoTask extends AsyncTask<OAuth, Void, String> {
    private final FlickrjActivity flickrjAndroidSampleActivity;
    private File file;

    public UploadPhotoTask(FlickrjActivity flickrjAndroidSampleActivity,
                           File file) {
        this.flickrjAndroidSampleActivity = flickrjAndroidSampleActivity;
        this.file = file;
    }

    /**
     * The progress dialog before going to the browser.
     */
    private ProgressDialog mProgressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(flickrjAndroidSampleActivity,
                flickrjAndroidSampleActivity.getString(R.string.uploading_picture_title), flickrjAndroidSampleActivity.getString(R.string.uploading_picture));
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                UploadPhotoTask.this.cancel(true);
            }
        });
    }

    @Override
    protected String doInBackground(OAuth... params) {
        OAuth oauth = params[0];
        OAuthToken token = oauth.getToken();

        try {
            Flickr f = FlickrHelper.getInstance().getFlickrAuthed(
                    token.getOauthToken(), token.getOauthTokenSecret());

            UploadMetaData uploadMetaData = new UploadMetaData();
            uploadMetaData.setTitle("" + file.getName());
            return f.getUploader().upload(file.getName(),
                    new FileInputStream(file), uploadMetaData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        if (monUploadDone != null) {
            monUploadDone.onComplete();
        }

        Toast.makeText(flickrjAndroidSampleActivity.getApplicationContext(), flickrjAndroidSampleActivity.getString(R.string.upload_picture_success), Toast.LENGTH_SHORT).show();
        CommonUtilities.displayMessage(flickrjAndroidSampleActivity, response);

    }

    onUploadDone monUploadDone;

    public void setOnUploadDone(onUploadDone monUploadDone) {
        this.monUploadDone = monUploadDone;
    }

    public interface onUploadDone {
        void onComplete();
    }

}