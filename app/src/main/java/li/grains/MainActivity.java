package li.grains;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class MainActivity extends AppCompatActivity {

	static {
		if(!OpenCVLoader.initDebug()){
			Log.d("MAIN", "OpenCV not loaded");
		} else {
			Log.d("MAIN", "OpenCV loaded");
		}
	}


	private TextView text1=null;
	private Button btn_run_svm;
	private Button btn_perform_download_features;
	private Button btn_update_features;
	private Button btn_change_svm_params;
	private LinearLayout layout_update_features;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// check for permissons
		CheckPermissons checkPermissons=new CheckPermissons(MainActivity.this);
		checkPermissons.check();

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
//				Toast.makeText(MainActivity.this,update_url,Toast.LENGTH_SHORT).show();
				DownloadFile dl=new DownloadFile(MainActivity.this,getString(R.string.update_filename));
				dl.execute(update_url);
			}
		});


		Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
		Mat mr1 = m.row(1);
		mr1.setTo(new Scalar(1));
		Mat mc5 = m.col(5);
		mc5.setTo(new Scalar(5));

		text1=(TextView)findViewById(R.id.textview_main_text1);
		text1.setText(m.dump());
	}


}
