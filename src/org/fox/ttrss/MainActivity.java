package org.fox.ttrss;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();

	private SharedPreferences m_prefs;
	private String m_themeName = "";
	private String m_sessionId;
	private boolean m_feedsOpened = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		m_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());       
		
		if (m_prefs.getString("theme", "THEME_DARK").equals("THEME_DARK")) {
			setTheme(R.style.DarkTheme);
		} else {
			setTheme(R.style.LightTheme);
		}

		m_themeName = m_prefs.getString("theme", "THEME_DARK");
        
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			m_sessionId = extras.getString("sessionId");
		} 
		
		if (savedInstanceState != null) {
			m_sessionId = savedInstanceState.getString("sessionId");
			m_feedsOpened = savedInstanceState.getBoolean("feedsOpened");
			Log.d(TAG, "FU: " + m_feedsOpened);
		}
		
        setContentView(R.layout.main);
        
        if (!m_feedsOpened) {
        	Log.d(TAG, "Opening feeds fragment...");
        	
        	FragmentTransaction ft = getFragmentManager().beginTransaction();			
        	FeedsFragment frag = new FeedsFragment();
		
        	frag.initialize(m_sessionId);
		
        	ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        	ft.replace(R.id.feeds_container, frag);
        	ft.commit();
        	
        	m_feedsOpened = true;
        }

    }
    
	@Override
	public void onSaveInstanceState (Bundle out) {
		super.onSaveInstanceState(out);

		out.putString("sessionId", m_sessionId);
		out.putBoolean("feedsOpened", m_feedsOpened);
	}
    
	@Override
	public void onResume() {
		super.onResume();

		if (!m_prefs.getString("theme", "THEME_DARK").equals(m_themeName)) {
			Intent refresh = new Intent(this, LoginActivity.class);
			startActivity(refresh);
			finish();
		}			
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preferences:
			Intent intent = new Intent(this, PreferencesActivity.class);
			startActivityForResult(intent, 0);
			return true;
		case R.id.logout:
			logout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void logout() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivityForResult(intent, 0);
		finish();
	}

}