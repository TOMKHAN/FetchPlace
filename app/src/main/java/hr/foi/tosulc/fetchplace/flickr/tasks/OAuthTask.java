package hr.foi.tosulc.fetchplace.flickr.tasks;

import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import hr.foi.tosulc.fetchplace.R;
import hr.foi.tosulc.fetchplace.flickr.FlickrjActivity;
import hr.foi.tosulc.fetchplace.flickr.helpers.FlickrHelper;

/**
 * Created by tosulc on 16.10.2014..
 */
public class OAuthTask extends AsyncTask<Void, Integer, String> {

    private static final Uri OAUTH_CALLBACK_URI = Uri
            .parse(FlickrjActivity.CALLBACK_SCHEME + "://oauth");

    private Context mContext;
    private ProgressDialog mProgressDialog;

    public OAuthTask(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContext,
                mContext.getString(R.string.contacting_flickr), mContext.getString(R.string.authorizate_flickr));
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                OAuthTask.this.cancel(true);
            }
        });
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Flickr f = FlickrHelper.getInstance().getFlickr();
            OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
                    OAUTH_CALLBACK_URI.toString());
            saveTokenSecrent(oauthToken.getOauthTokenSecret());
            URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
                    Permission.WRITE, oauthToken);
            return oauthUrl.toString();
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    /**
     * Saves the oauth token secret.
     *
     * @param tokenSecret
     */
    private void saveTokenSecrent(String tokenSecret) {
        FlickrjActivity act = (FlickrjActivity) mContext;
        act.saveOAuthToken(null, null, null, tokenSecret);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (result != null && !result.startsWith("error")) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse(result)));
        } else {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }

}
