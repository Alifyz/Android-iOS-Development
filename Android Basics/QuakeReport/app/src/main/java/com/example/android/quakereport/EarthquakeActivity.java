/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    public static final String URL_SERVER =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    public static String RAW_JSON = "";
    public static EarthquakeAdapter adapter;
    public static ListView earthquakeListView = null;

    private TextView mEmpyTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        getLoaderManager().initLoader(1, savedInstanceState, this).forceLoad();
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected == true) {
            mEmpyTextView.setText("There is no Internet Connection");
        }

        Log.i("MainActivity", "Initializing the Loader");
        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = (ListView) findViewById(R.id.list);

        //new EarthquakeAsync().execute(URL_SERVER);

        //Open an Intent Service to Browse for certain EarthQuake
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView offsetText = (TextView) view.findViewById(R.id.location_offset);
                TextView location = (TextView) view.findViewById(R.id.primary_location);

                StringBuilder keyword = new StringBuilder();
                keyword.append(offsetText.getText());
                keyword.append(location.getText());

                Intent goBroswer = new Intent(Intent.ACTION_WEB_SEARCH);
                goBroswer.putExtra(SearchManager.QUERY, keyword.toString());
                startActivity(goBroswer);
            }
        });

        mEmpyTextView = (TextView)findViewById(R.id.empty_view);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        earthquakeListView.setEmptyView(mEmpyTextView);
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        Log.i("MainActivity", "Creating the Loader");
        return new EarthquakeLoader(EarthquakeActivity.this);

    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new EarthquakeAdapter(getApplicationContext(), earthquakes);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
        Log.i("MainActivity", "Loader is finished");
        mEmpyTextView.setText(R.string.no_eathquake);
        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

    }

    /*private class EarthquakeAsync extends AsyncTask<String, Void, List<Earthquake>> {
        @Override
        protected List<Earthquake> doInBackground(String... strings) {
            URL parsedURL = EarthQuakeUtils.parseURL(strings[0]);
            try {
                RAW_JSON = EarthQuakeUtils.makeHTTPRequest(parsedURL);
            } catch (IOException e) {
                return null;
            }
            List<Earthquake> earthquakes = EarthQuakeUtils.extractEarthquakes(RAW_JSON);
            return earthquakes;
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            // Create a new {@link ArrayAdapter} of earthquakes
            adapter = new EarthquakeAdapter(getApplicationContext(), earthquakes);
            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(adapter);
        }
    }*/
}