package org.haobtc.onekey.dfu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.haobtc.onekey.activities.settings.UpgradeBixinKEYActivity;

public class NotificationActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// If this activity is the root activity of the task, the app is not running
		if (isTaskRoot()) {
			/*Intent intent = new Intent(this, UpgradeBixinKEYActivity.class);
			intent.putExtra("tag", 2);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);*/
		}
		// Now finish, which will drop the user in to the activity that was at the top
		//  of the task stack
		finish();
	}
}