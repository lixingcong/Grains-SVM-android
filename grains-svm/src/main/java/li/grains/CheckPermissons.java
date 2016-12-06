package li.grains;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by li on 16-12-3.
 */

public class CheckPermissons {

	private Activity activity;

	final private static int REQUEST_CODE_ASK_FOR_PERMISSONS = 0;
	final private String[] all_permissions = new String[]{
			Manifest.permission.CAMERA,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.INTERNET
	};

	public CheckPermissons(Activity activity) {
		this.activity = activity;
	}

	public void check() {
		ask_for_all_permissions();
		if (check_if_permissions_got() == false) {
			Toast.makeText(activity, "Could not get permissons", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(activity, "Good permissions", Toast.LENGTH_SHORT).show();
		}
	}

	private void ask_for_all_permissions() {
		if (Build.VERSION.SDK_INT >= 23) {
			if (check_if_permissions_got() == false) {
				ActivityCompat.requestPermissions(activity, all_permissions, REQUEST_CODE_ASK_FOR_PERMISSONS);
			} else {
				// already permit
			}
		} else {
			// API was lower than 23
			Toast.makeText(activity, "API not need to ask for permission", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean check_if_permissions_got() {
		for (String i : all_permissions) {
			if (ContextCompat.checkSelfPermission(activity, i) == PackageManager.PERMISSION_DENIED)
				return false;
		}
		return true;
	}
}
