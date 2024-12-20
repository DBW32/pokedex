package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class PokemonActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private TextView descriptionTextView;
    private Button catchButton;
    private String url;
    private String imageUrl;
    private String descriptionUrl;
    private RequestQueue requestQueue;

    private Boolean isCaught;
    private SharedPreferences caughtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        url = getIntent().getStringExtra("url");
        imageView = findViewById(R.id.pokemon_sprite);
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        descriptionTextView = findViewById(R.id.pokemon_description);
        catchButton = findViewById(R.id.catch_button);

        // Initialize SharedPreferences to track if Pokemon has been caught or not
        caughtStatus = getApplicationContext().getSharedPreferences("caughtStatus", MODE_PRIVATE);

        // Check if Pokemon has been caught or not and load correct 'catchButton' text
        isCaught = caughtStatus.getBoolean(url, false);

        if (isCaught) {
            catchButton.setText("Release");
        }
        else {
            catchButton.setText("Catch");
        }

        load();
    }

    public void load() {
        type1TextView.setText("");
        type2TextView.setText("");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String name = response.getString("name");
                    nameTextView.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));

                    JSONArray typeEntries = response.getJSONArray("types");
                    for (int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1) {
                            type1TextView.setText(type.substring(0, 1).toUpperCase() + type.substring(1));
                        }
                        else if (slot == 2) {
                            type2TextView.setText(type.substring(0, 1).toUpperCase() + type.substring(1));
                        }
                    }

                    // Retrieve URL for bitmap and call DownloadSpriteTask to download image in background
                    JSONObject imageEntries = response.getJSONObject("sprites");
                    imageUrl = imageEntries.getString("front_default");
                    new DownloadSpriteTask().execute(imageUrl);

                    // Retrieve URL for description and call separate method to load description
                    JSONObject speciesEntries = response.getJSONObject("species");
                    descriptionUrl = speciesEntries.getString("url");
                    loadDescription();

                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
            }
        });

        requestQueue.add(request);
    }

    // Separate method to call second endpoint and nested in 'load' to ensure 'descriptionUrl' is captured beforehand
    public void loadDescription() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, descriptionUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray descriptionEntries = response.getJSONArray("flavor_text_entries");

                    for (int i = 0; i < descriptionEntries.length(); i++) {
                        JSONObject descriptionEntry = descriptionEntries.getJSONObject(i);
                        String language = descriptionEntry.getJSONObject("language").getString("name");

                        // Check if language is English before loading description
                        if (language.equals("en")) {
                            String description = descriptionEntry.getString("flavor_text").replace("\n"," ");
                            descriptionTextView.setText(description);
                            break;
                        }
                        else {
                            continue;
                        }
                    }

                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon species json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon description error", error);
            }
        });

        requestQueue.add(request);
    }

    // Method to download Pokemon sprite in the background
    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            }
            catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }

        // Automatically called after doInBackground and passed return argument from that function
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);

        }
    }

    public void toggleCatch(View view) {
        // Check if Pokemon has been caught or not
        isCaught = caughtStatus.getBoolean(url, false);

        // Change 'catchButton' text and status in 'caughtStatus' based on 'isCaught'
        if (isCaught) {
            catchButton.setText("Catch");
            caughtStatus.edit().putBoolean(url, false).commit();
        }
        else {
            catchButton.setText("Release");
            caughtStatus.edit().putBoolean(url, true).commit();
        }
    }
}
