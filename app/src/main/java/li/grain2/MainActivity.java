package li.grain2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	final public static int REQUEST_CODE_ASK_FOR_PERMISSONS = 0;
	final private String[] all_permissions=new String[]{
			Manifest.permission.CAMERA,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.INTERNET
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ask_for_all_permissions();
		if(check_if_permissions_got()==false){
			Toast.makeText(getApplicationContext(),"Could not get permissons",Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(getApplicationContext(),"Good permissions",Toast.LENGTH_SHORT).show();
		}
	}

	public void ask_for_all_permissions(){
		if (Build.VERSION.SDK_INT >= 23) {
			if(check_if_permissions_got()==false){
				ActivityCompat.requestPermissions(this,all_permissions, REQUEST_CODE_ASK_FOR_PERMISSONS);
			}else{
				// already permit
			}
		} else {
			// API was lower than 23
			Toast.makeText(getApplicationContext(),"API not need to ask for permission",Toast.LENGTH_SHORT).show();
		}
	}

	private boolean check_if_permissions_got(){
		for(String i:all_permissions){
			if(ContextCompat.checkSelfPermission(getApplicationContext(),i) == PackageManager.PERMISSION_DENIED)
				return false;
		}
		return true;
	}
}
