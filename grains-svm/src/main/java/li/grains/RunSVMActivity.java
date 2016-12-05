package li.grains;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
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

		// check if C and gamma was set
		if(check_if_SVM_params_set()==true)
			initSVM();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				// save cropped picture to local
				save_Uri_to_local(result.getUri(),getString(R.string.img_filename_cropped));

				((ImageView) findViewById(R.id.imageview_runsvm_inputimg)).setImageURI(result.getUri());
				set_add_img_visible(false);
				is_cropped=true;
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
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
		btn_trained_pic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String all_trained_sample=features_train.get_all_chinese();
				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RunSVMActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
				alertDialog.setTitle("All trained samples");
				alertDialog.setMessage(all_trained_sample);
				alertDialog.show();
			}
		});

		// run svm
		btn_run.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(is_svm_has_been_intialized==false)
					initSVM();

				if(is_cropped && is_svm_has_been_intialized) {
					PredictProgress predictProgress=new PredictProgress(RunSVMActivity.this,my_svm,features_train,getString(R.string.img_filename_cropped));
					predictProgress.execute();
				}
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
				.setAutoZoomEnabled(true)
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

				// load features
				if(is_got_train_features==false)
					loadTrainFeatures();

				// train
				my_svm.train(train_x,train_y);

				// dismiss it
				progress.dismiss();
				is_svm_has_been_intialized=true;
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
		features_train =new My_Features("",getString(R.string.update_features_filename));
		features_train.load_saved_features();
		train_y=features_train.get_features_y();
		train_x=features_train.get_features_x();
		is_got_train_features=true;
	}

	private void save_Uri_to_local(Uri imgUri, String filename){
		StoragePath storagePath=new StoragePath();
		final int chunkSize = 1024;  // We'll read in one kB at a time
		byte[] imageData = new byte[chunkSize];

		try {
			InputStream in = getContentResolver().openInputStream(imgUri);
			OutputStream out = new FileOutputStream(storagePath.getPath()+filename);
			int bytesRead;
			while ((bytesRead = in.read(imageData)) > 0) {
				out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
			}
			in.close();
			out.close();
			is_cropped=true;
		} catch (Exception ex) {
			Log.e("URI", ex.toString());
			ex.printStackTrace();
			is_cropped=false;
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
	}
}
