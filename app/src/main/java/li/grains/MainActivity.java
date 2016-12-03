package li.grains;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// check for permissons
		(new CheckPermissons(MainActivity.this)).check();

		// set up all my view
		set_my_view();

		// OpenCV demo
		cv_demo();
	}

	private void set_my_view(){
		Button btn_perform_download_features;
		Button btn_update_features;
		final LinearLayout layout_update_features;

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
	}

	private void cv_demo(){
		TextView text1=null;
		Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
		Mat mr1 = m.row(1);
		mr1.setTo(new Scalar(1));
		Mat mc5 = m.col(5);
		mc5.setTo(new Scalar(5));

		text1=(TextView)findViewById(R.id.textview_main_text1);
		text1.setText(m.dump());
	}
}
