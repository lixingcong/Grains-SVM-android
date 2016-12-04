package li.grains;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import li.grains.ml.My_Features;
import li.grains.ml.My_SVM;

public class RunSVMActivity extends AppCompatActivity {

	private My_Features features_train =null;
	private My_SVM my_svm=null;
	private boolean is_svm_has_been_intialized;
	private boolean is_got_train_features;
	private boolean is_cropped;
	private List<Double> train_y=null;
	private List<List<Double>> train_x=null;
	private TextView textview1=null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_svm);
		set_my_view();
		is_svm_has_been_intialized=false;
		is_got_train_features=false;
		is_cropped=false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				save_bitmap_to_local(result.getBitmap(),getString(R.string.img_filename_cropped));
				((ImageView) findViewById(R.id.imageview_runsvm_inputimg)).setImageURI(result.getUri());
				Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
				set_add_img_visible(false);
				is_cropped=true;
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
				set_add_img_visible(true);
				is_cropped=false;
			}
		}
	}

	private void set_my_view(){
		final Button btn_get_pic=(Button)findViewById(R.id.button_runsvm_getpic);
		final Button btn_trained_pic=(Button)findViewById(R.id.button_runsvm_trainedpic);
		final Button btn_run=(Button)findViewById(R.id.button_runsvm_run);
		final ImageView imageview_add_logo=(ImageView)findViewById(R.id.imageview_runsvm_addlogo);

		textview1=(TextView)findViewById(R.id.textview_runsvm_text1);

		// set title
		setTitle("Run SVM");

		// get pic
		btn_get_pic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startCropImageActivity(null);
			}
		});
		imageview_add_logo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startCropImageActivity(null);
			}
		});

		// show trained images

		// run svm
		btn_run.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// we should check is params were set before
				if(check_if_SVM_params_set()==false)
					return;

				if(is_svm_has_been_intialized==false)
					initSVM();

				if(is_cropped)
					predict_this_picture();
			}
		});
	}

	private void set_add_img_visible(boolean is_display){
		final ImageView imageview_add_img=(ImageView)findViewById(R.id.imageview_runsvm_addlogo);
		if(is_display){
			imageview_add_img.setVisibility(View.VISIBLE);
		}else{
			imageview_add_img.setVisibility(View.GONE);
		}
	}

	private void startCropImageActivity(Uri imageUri) {
		CropImage.activity(imageUri)
				.setGuidelines(CropImageView.Guidelines.ON)
				.setMultiTouchEnabled(true)
				.setAllowRotation(true)
				.setAspectRatio(1,1)
				.setFixAspectRatio(true)
				.setAllowCounterRotation(true)
				.setAutoZoomEnabled(false)
				.start(this);
	}

	private void initSVM(){
		final ProgressDialog progress=new ProgressDialog(this);
		// please wait
		progress.setTitle("Initializing SVM...");
		progress.setMessage("Please wait for a while");
		progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.show();

		Thread mThread = new Thread() {
			@Override
			public void run() {
				// init svm
				ParseSharePref parseSharePref=new ParseSharePref(getString(R.string.share_pref_svm_param),getApplicationContext());
				double C=parseSharePref.getDouble("C");
				double gamma=parseSharePref.getDouble("gamma");
				my_svm=new My_SVM(C,gamma);
				is_svm_has_been_intialized=true;

				// load features
				if(is_got_train_features==false)
					loadTrainFeatures();

				// train
				my_svm.train(train_x,train_y);

				// dismiss it
				progress.dismiss();
			}
		};
		mThread.start();
	}

	private boolean check_if_SVM_params_set(){
		ParseSharePref parseSharePref=new ParseSharePref(getString(R.string.share_pref_svm_param),getApplicationContext());
		if(parseSharePref.contains(getString(R.string.share_pref_is_set_param))==false){
			Intent intent=new Intent(getApplicationContext(),SVMParamsActivity.class);
			startActivity(intent);
			Toast.makeText(getApplicationContext(),"Please set params first",Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void loadTrainFeatures(){
		features_train =new My_Features("",getString(R.string.update_filename));
		features_train.load_saved_features();
		train_y=features_train.get_features_y();
		train_x=features_train.get_features_x();
		is_got_train_features=true;
	}

	private void save_bitmap_to_local(Bitmap bitmap,String filename){
		FileOutputStream out = null;
		try {
			// Locate storage location
			String filepath = Environment.getExternalStorageDirectory().getPath();
			out = new FileOutputStream(filepath+"/"+filename);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void predict_this_picture(){
		String filename = Environment.getExternalStorageDirectory().getPath();
		filename+=("/"+getString(R.string.img_filename_cropped));
		Log.v("SVM",filename);
		My_Features features_test=new My_Features(filename);
		List<List<Double>> test_x=features_test.get_features_x();
		List<Double> predict_y= my_svm.predict(test_x);
		String result=features_train.get_chinese_from_category(new Double(predict_y.get(0)).intValue());
		textview1.setText(result);
	}
}
