package hr.foi.tosulc.fetchplace;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import hr.foi.tosulc.fetchplace.adapters.PlaceAdapter;
import hr.foi.tosulc.fetchplace.helpers.ConnectionDetector;
import hr.foi.tosulc.fetchplace.json.JSONPlaceGetterFromKeyword;
import hr.foi.tosulc.fetchplace.types.Place;

/**
 * Created by tosulc on 15.10.2014..
 */
public class DatabaseFragment extends Fragment {

    public static EditText etLocationName, etLocationAddress, etRadiusChange, etDummy;
    public static TextView tvLocationLatitude, tvLocationLongitude;
    public static int keywordValueChange;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_database, container, false);

        etLocationName = (EditText) rootView.findViewById(R.id.et_location_name);
        etLocationAddress = (EditText) rootView.findViewById(R.id.et_location_address);
        etRadiusChange = (EditText) rootView.findViewById(R.id.et_radius);
        tvLocationLatitude = (TextView) rootView.findViewById(R.id.tv_latitude_value);
        tvLocationLongitude = (TextView) rootView.findViewById(R.id.tv_longitude_value);
        etDummy = (EditText) rootView.findViewById(R.id.et_dummy_for_focus);
        final ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());

        etLocationName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (cd.isConnectedToInternet()) {
                        keywordValueChange = 1;
                        new GetLocationByKeyword().execute(etLocationName.getText().toString());
                        hideKeyboard(etLocationName);
                        etDummy.requestFocus();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.need_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        etLocationAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (cd.isConnectedToInternet()) {
                        keywordValueChange = 2;
                        new GetLocationByKeyword().execute(etLocationAddress.getText().toString());
                        hideKeyboard(etLocationAddress);
                        etDummy.requestFocus();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.need_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        etRadiusChange.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etRadiusChange.getText().toString().length() > 0) {
                        int radius = Double.valueOf(String.valueOf(etRadiusChange.getText().toString())).intValue();
                        if (radius > 0 && radius <= 50000) {
                            saveRadiusInSharedPreferences(Float.parseFloat(v.getText().toString()));
                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.radius_changed_success), Toast.LENGTH_SHORT).show();
                            hideKeyboard(etRadiusChange);
                            etDummy.requestFocus();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.radius_too_big), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.radius_not_specified), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        ImageButton btnLocationNameChange = (ImageButton) rootView.findViewById(R.id.ib_location_name_change);
        btnLocationNameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectedToInternet()) {
                    keywordValueChange = 1;
                    new GetLocationByKeyword().execute(etLocationName.getText().toString());
                    hideKeyboard(etLocationName);
                    etDummy.requestFocus();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.need_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton btnLocationAddressChange = (ImageButton) rootView.findViewById(R.id.ib_location_address_change);
        btnLocationAddressChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectedToInternet()) {
                    keywordValueChange = 2;
                    new GetLocationByKeyword().execute(etLocationAddress.getText().toString());
                    hideKeyboard(etLocationAddress);
                    etDummy.requestFocus();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.need_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton btnRadiusChange = (ImageButton) rootView.findViewById(R.id.ib_radius_change);
        btnRadiusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etRadiusChange.getText().toString().length() > 0) {
                    int radius = Double.valueOf(String.valueOf(etRadiusChange.getText().toString())).intValue();
                    if (radius > 0 && radius <= 50000) {
                        saveRadiusInSharedPreferences(Float.parseFloat(etRadiusChange.getText().toString()));
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.radius_changed_success), Toast.LENGTH_SHORT).show();
                        hideKeyboard(etRadiusChange);
                        etDummy.requestFocus();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.radius_too_big), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.radius_not_specified), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        etRadiusChange.setText(String.valueOf(getRadiusFromSharedPreferences()));
    }

    private class GetLocationByKeyword extends AsyncTask<String, Void, Place> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            if (keywordValueChange == 1) {
                progressDialog.setMessage(getActivity().getString(R.string.fetching_location_name));
            } else if (keywordValueChange == 2) {
                progressDialog.setMessage(getActivity().getString(R.string.fetching_location_address));
            }
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Place doInBackground(String... keyword) {
            Place place = new JSONPlaceGetterFromKeyword().getPlaceFromKeyword(keyword[0]);
            return place;
        }

        @Override
        protected void onPostExecute(Place place) {

            if (place != null) {
                updateLocationInfoOnDatabaseFragment(place);
                //save in DB
                PlaceAdapter pa = new PlaceAdapter(getActivity().getApplicationContext());
                pa.openToWrite();
                pa.updatePlace(place);
                pa.close();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.no_such_address), Toast.LENGTH_SHORT).show();
            }

            // Dismiss the progress dialog
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }

    }

    public static void updateLocationInfoOnDatabaseFragment(Place place) {
        etLocationName.setText(place.getName());
        etLocationAddress.setText(place.getAddress());
        LatLng latlng = place.getLocation();
        tvLocationLatitude.setText(String.valueOf(latlng.latitude));
        tvLocationLongitude.setText(String.valueOf(latlng.longitude));

    }

    public boolean saveRadiusInSharedPreferences(float radius) {
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences(MapFragment.ARG_PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("radius", radius);
        return editor.commit();
    }

    public int getRadiusFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences(MapFragment.ARG_PREFS_NAME, 0);
        float radiusValue = sharedPreferences
                .getFloat("radius", 200);
        return Double.valueOf(String.valueOf(radiusValue)).intValue();
    }

    public void hideKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
