package li.grains;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;

public class MainActivity extends AppCompatActivity {

	static boolean is_opencv_loaded;
	static {
		if(!OpenCVLoader.initDebug()){
			Log.d("MAIN", "OpenCV not loaded");
			is_opencv_loaded=false;
		} else {
			Log.d("MAIN", "OpenCV loaded");
			is_opencv_loaded=true;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// check if CV is loaded
		if(!is_opencv_loaded){
			Toast.makeText(MainActivity.this,"OpenCV native-lib load error",Toast.LENGTH_LONG).show();
		}

		// check for permissons
		(new CheckPermissons(MainActivity.this)).check();

		// set up all my view
		set_my_view();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1 && resultCode==RESULT_OK){
			TextView text_params=(TextView)findViewById(R.id.textview_main_text2);
			String C=data.getExtras().getString("C");
			String gamma=data.getExtras().getString("gamma");
			text_params.setText(C+", "+gamma);
		}
	}

	private void set_my_view(){
		Button btn_perform_download_features;
		Button btn_update_features;
		Button btn_change_svm_params;
		Button btn_run_svm;
		final LinearLayout layout_update_features;

		// run svm: pick up a photo
		btn_run_svm=(Button)findViewById(R.id.button_main_run_svm);
		btn_run_svm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(check_if_csv_exist()){
					Intent intent=new Intent(MainActivity.this,RunSVMActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			}
		});

		// layout hide or show
		layout_update_features=(LinearLayout)findViewById(R.id.layout_main_update);
		btn_update_features =(Button)findViewById(R.id.button_main_update_features);
		btn_update_features.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int is_visible=layout_update_features.getVisibility();
				if(is_visible==View.VISIBLE){
					layout_update_features.setVisibility(View.GONE);
				}else{
					layout_update_features.setVisibility(View.VISIBLE);
				}
			}
		});

		// download click
		btn_perform_download_features=(Button)findViewById(R.id.button_main_perform_update);
		btn_perform_download_features.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String update_url=((EditText)findViewById(R.id.edittext_main_url)).getText().toString();
				DownloadFile dl=new DownloadFile(MainActivity.this,getString(R.string.update_filename));
				dl.execute(update_url);
			}
		});

		// load last svm params
		ParseSharePref parseSharePref=new ParseSharePref(getString(R.string.share_pref_svm_param),getApplicationContext());
		TextView text_params=(TextView)findViewById(R.id.textview_main_text2);
		if(parseSharePref.contains(getString(R.string.share_pref_is_set_param))){
			String C=parseSharePref.getString("C");
			String gamma=parseSharePref.getString("gamma");
			text_params.setText(C+", "+gamma);
		}

		// change svm params
		btn_change_svm_params=(Button)findViewById(R.id.button_main_change_svm_param);
		btn_change_svm_params.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(MainActivity.this, SVMParamsActivity.class);
				startActivityForResult(myIntent,1);
			}
		});
	}

	private boolean check_if_csv_exist(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		String filename=filepath+"/"+getString(R.string.update_filename);
		File file = new File(filename);
		if(!file.exists()){
			Toast.makeText(getApplicationContext(),"Please update features",Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

}
