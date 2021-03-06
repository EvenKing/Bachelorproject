package no.nc_spectrum.hendelseapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class SettingsActivity extends AppCompatActivity {

    static boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        isRunning = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Adds a "back" button to the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    protected void onResume(){
        super.onResume();
        UpdateCheck.nullify(getApplicationContext());
        isRunning = true;
    }

    protected void onStop(){
        super.onStop();
        isRunning = false;
    }

    protected void onDestroy(){
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public boolean onSupportNavigateUp() { //Needed for the toolbar's back button
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_logout){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            UpdateCheck.loggedIn = false;
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickWebBrowser (View view) { //Listens to website button
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nc-spectrum.no/"));
        startActivity(i);
    }

    public void onClickMakeCalls (View view) { //listens to call button
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+4735203000"));
        startActivity(i);
    }

}
