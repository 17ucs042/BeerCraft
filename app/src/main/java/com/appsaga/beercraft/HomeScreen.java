package com.appsaga.beercraft;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class HomeScreen extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = HomeScreen.class.getSimpleName();

    /** URL to query the USGS dataset for earthquake information */

    private static final String USGS_REQUEST_URL =
            "http://starlord.hackerearth.com/beercraft";

    ArrayList<Beer> beers = new ArrayList<>();
    ArrayList<Beer> sortBeers = new ArrayList<>();
    ArrayList<String> bearName = new ArrayList<>();
    ListView beerListView;
    AutoCompleteTextView searchText;
    BeerAdapter beerAdapter;
    ArrayAdapter<String> searchAdapter;
    ImageView searchIcon;
    ArrayList<Beer> searchedBeer = new ArrayList<>();
    ProgressDialog progressDialog;
    Button sort;
    Button filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        progressDialog = ProgressDialog.show(HomeScreen.this,"Fetching Beers....","Please wait");

        BeerAsyncTask task = new BeerAsyncTask();
        task.execute();

        searchText= findViewById(R.id.search_text);
        searchIcon=findViewById(R.id.search_icon);

        sort=findViewById(R.id.sort);
        filter=findViewById(R.id.filter);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!searchText.getText().toString().equalsIgnoreCase("")&&beerListView.getCount()!=0)
                {
                    searchedBeer.clear();
                    for(int i=0;i<beers.size();i++)
                    {
                        Beer beer = beers.get(i);

                        if(beer.getName().toLowerCase().contains(searchText.getText().toString().toLowerCase()))
                        {
                            searchedBeer.add(beer);
                        }
                    }

                    beerAdapter = new BeerAdapter(HomeScreen.this,searchedBeer);
                    beerListView.setAdapter(null);
                    beerListView.setAdapter(beerAdapter);
                }
                else
                {
                    beerAdapter = new BeerAdapter(HomeScreen.this,beers);
                    beerListView.setAdapter(null);
                    beerListView.setAdapter(beerAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final Dialog sortDialog = new Dialog(HomeScreen.this);
        sortDialog.setContentView(R.layout.sort_dialog);

        final CheckBox increasing = sortDialog.findViewById(R.id.increasing);
        final CheckBox decreasing = sortDialog.findViewById(R.id.decreasing);
        Button done = sortDialog.findViewById(R.id.done);

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sortBeers.clear();
                sortBeers.addAll(beers);
                sortDialog.show();
            }
        });

        increasing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    decreasing.setChecked(false);
                }
            }
        });

        decreasing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    increasing.setChecked(false);
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(increasing.isChecked())
                {
                    Collections.sort(sortBeers, new Comparator<Beer>() {
                        @Override
                        public int compare(Beer o1, Beer o2) {

                            return (o1.getAbv().compareTo(o2.getAbv()));

                        }
                    });

                    beerAdapter=new BeerAdapter(HomeScreen.this,sortBeers);
                    beerListView.setAdapter(null);
                    beerListView.setAdapter(beerAdapter);
                }
                else if(decreasing.isChecked())
                {
                    Collections.sort(sortBeers, new Comparator<Beer>() {
                        @Override
                        public int compare(Beer o1, Beer o2) {

                            return (o1.getAbv().compareTo(o2.getAbv()))*(-1);

                        }
                    });

                    beerAdapter=new BeerAdapter(HomeScreen.this,sortBeers);
                    beerListView.setAdapter(null);
                    beerListView.setAdapter(beerAdapter);
                }
                else
                {
                    beerAdapter=new BeerAdapter(HomeScreen.this,beers);
                    beerListView.setAdapter(null);
                    beerListView.setAdapter(beerAdapter);
                }

                sortDialog.dismiss();
            }
        });
    }

    private class BeerAsyncTask extends AsyncTask<URL, Void, Beer> {

        @Override
        protected Beer doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(USGS_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back

           // Log.d("test...",url+"");
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {

               // Log.d("test...",e.getMessage());
              //  Toast.makeText(HomeScreen.this,"An error occurred",Toast.LENGTH_SHORT).show();
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return null;
        }

        /**
         * Update the screen with the given earthquake (which was the result of the
         */
        @Override
        protected void onPostExecute(Beer beer) {
            if (beers.size() == 0) {

                Toast.makeText(HomeScreen.this,"No beers found",Toast.LENGTH_SHORT).show();
            }
            else
            {
                beerAdapter = new BeerAdapter(HomeScreen.this,beers);
                beerListView = findViewById(R.id.beer_list_view);
                beerListView.setAdapter(beerAdapter);

                searchAdapter =  new ArrayAdapter<>(HomeScreen.this,android.R.layout.simple_list_item_1, bearName);
                searchText.setAdapter(searchAdapter);
            }
            progressDialog.dismiss();
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
               // Log.d("test...",urlConnection+"");
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }

            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }

           // Log.d("test...","yes3");
            return output.toString();
        }

        private void extractFeatureFromJson(String beerJSON) {

           // ProgressDialog progressDialog = ProgressDialog.show(HomeScreen.this,"Fetching Beers....","Please wait");

            if (TextUtils.isEmpty(beerJSON)) {
                Log.d("test...","yes2");
                return ;
            }

            try {
                JSONArray beerArray = new JSONArray(beerJSON);

                Log.d("size",beerArray.length()+"");
                // If there are results in the beer array
                for(int i=0;i<beerArray.length();i++) {

                    JSONObject beer = beerArray.getJSONObject(i);

                    String abv = beer.getString("abv");
                    String ibu = beer.getString("ibu");
                    String id = beer.getString("id");
                    String name = beer.getString("name");
                    String style = beer.getString("style");
                    String ounces = beer.getString("ounces");

                    beers.add(new Beer(abv,ibu,id,name,style,ounces));
                    bearName.add(name);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the beer JSON results", e);
            }
            return ;

        }
    }
}
