package hr.foi.tosulc.fetchplace.flickr.tasks;

import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;

import hr.foi.tosulc.fetchplace.flickr.FlickrjActivity;
import hr.foi.tosulc.fetchplace.flickr.helpers.FlickrHelper;


/**
 * Created by tosulc on 16.10.2014..
 */
public class GetOAuthTokenTask extends AsyncTask<String, Integer, OAuth> {

    private FlickrjActivity activity;

    public GetOAuthTokenTask(FlickrjActivity context) {
        this.activity = context;
    }

    @Override
    protected OAuth doInBackground(String... params) {
        String oauthToken = params[0];
        String oauthTokenSecret = params[1];
        String verifier = params[2];

        Flickr f = FlickrHelper.getInstance().getFlickr();
        OAuthInterface oauthApi = f.getOAuthInterface();
        try {
            return oauthApi.getAccessToken(oauthToken, oauthTokenSecret,
                    verifier);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    protected void onPostExecute(OAuth result) {
        if (activity != null) {
            activity.onOAuthDone(result);
        }
    }

}
