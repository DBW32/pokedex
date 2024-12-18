package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private PokedexAdapter adapter;  // Type PokedexAdapter to call methods unique to that class
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new PokedexAdapter(getApplicationContext());
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        // Pass 'null' to getFilter upon launch to show full list initially
        adapter.getFilter().filter(null);
    }

    // Method called when creating a menu
    // Enables SearchView to automatically call methods on MainActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);  // Search code is specified in MainActivity class

        return true;
    }

    // Method called where argument 'newText' is String representing current text of SearchView
    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);  // Pass 'newText' to PokermonFilter
        return false;
    }

    // Method called when users presses "Submit" on a keyboard
    @Override
    public boolean onQueryTextSubmit(String newText) {
        adapter.getFilter().filter(newText);  // Pass 'newText' to PokermonFilter
        return false;
    }
}
