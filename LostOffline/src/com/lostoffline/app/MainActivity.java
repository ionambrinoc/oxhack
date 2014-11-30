package com.lostoffline.app;

import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.Settings;



public class MainActivity extends ActionBarActivity {
	
	private SendSMS mSender = new SendSMS();
	private double latitude;
	private double longitude;
	private boolean exceptionRaised;
	
	private void exitApp() {
		Intent intent = new Intent(Intent.ACTION_MAIN); //exit.
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
	}
	
	private void _getLocation() {
	    // Get the location manager
	    LocationManager locationManager = (LocationManager) 
	            getSystemService(LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    String bestProvider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(bestProvider);
	    LocationListener loc_listener = new LocationListener() {

	        public void onLocationChanged(Location l) {}

	        public void onProviderEnabled(String p) {}

	        public void onProviderDisabled(String p) {}

	        public void onStatusChanged(String p, int status, Bundle extras) {}
	    };
	    locationManager.requestLocationUpdates(bestProvider, 0, 0, loc_listener);
	    location = locationManager.getLastKnownLocation(bestProvider);
	    try {
	        latitude = location.getLatitude();
	        longitude = location.getLongitude();
	    } catch (NullPointerException e) {
	    	exceptionRaised = true;
	        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("You have no location services activated. Please activate or we can't direct you :(");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Location Settings",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    exceptionRaised = false;
                }
                
            });
            builder1.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    exitApp();
                }

            });

            AlertDialog alert11 = builder1.create();
            alert11.show();
	    }
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	exceptionRaised = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().commit();
        }
        
        Button mButton = (Button)findViewById(R.id.button1);
        mButton.setOnClickListener(new View.OnClickListener() {
            	public void onClick(View view) {
            		EditText mEdit   = (EditText)findViewById(R.id.editText1);
            		String destination = mEdit.getText().toString();
            		sendIt(view, destination);
              	}
        });        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void sendIt(View arg0, String destination) throws NullPointerException {
    	_getLocation();    	
    	String language = Locale.getDefault().getLanguage();
    	
    	if (!exceptionRaised) {
	    	boolean success = mSender.sendSMSMessage("+441727260228",
	    		"{\"current\":"+'"'+longitude+","+latitude+"\""+",\"desired\":" +
                        '"'+destination+"\",\"lang\":\""+language+"\"}");
	    	Toast.makeText(this, "Message " + (
	    		success ? "sent successfully" : "not sent - error"), 
	    		Toast.LENGTH_SHORT).show();
    	}
    }
}
