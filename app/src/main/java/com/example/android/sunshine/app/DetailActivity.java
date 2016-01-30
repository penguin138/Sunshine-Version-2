package com.example.android.sunshine.app;



import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        /*
        Intent intent = getIntent();
        String weatherData = intent.getStringExtra(Intent.EXTRA_TEXT);
        //Toast.makeText(this,weatherData, Toast.LENGTH_LONG).show();
        /*
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(weatherData);

        FrameLayout layout = (FrameLayout) findViewById(R.id.container);
        layout.addView(textView);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_show_on_map) {
            Uri geoLocation = null;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String address = prefs.getString(getString(R.string.pref_location), getString(R.string.default_location));
            FetchCoordinatesTask task = new FetchCoordinatesTask(this);
            task.execute(address);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        ShareActionProvider provider;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            String weatherData = intent.getStringExtra(Intent.EXTRA_TEXT);
            TextView textView = (TextView) rootView.findViewById(R.id.detail_text);
            textView.setText(weatherData);
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.detail_fragment,menu);
            MenuItem shareItem = menu.findItem(R.id.action_share);
            provider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            Intent intent = getActivity().getIntent();
            String weatherData = intent.getStringExtra(Intent.EXTRA_TEXT);
            shareIntent.putExtra(Intent.EXTRA_TEXT, weatherData + " #SunshineApp");
            provider.setShareIntent(shareIntent);
        }
    }
}


