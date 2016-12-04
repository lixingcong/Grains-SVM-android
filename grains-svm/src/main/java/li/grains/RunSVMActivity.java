package li.grains;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import li.grains.ml.My_Features;
import li.grains.ml.My_SVM;

public class RunSVMActivity extends AppCompatActivity {

	private My_Features my_features=null;
	private My_SVM my_svm=null;
	private boolean is_svm_has_been_intialized;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_svm);
		set_my_view();
		is_svm_has_been_intialized=false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				((ImageView) findViewById(R.id.imageview_runsvm_inputimg)).setImageURI(result.getUri());
				Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
				set_add_img_visible(false);
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
				set_add_img_visible(true);
			}
		}
	}

	private void set_my_view(){
		final Button btn_get_pic=(Button)findViewById(R.id.button_runsvm_getpic);
		final Button btn_trained_pic=(Button)findViewById(R.id.button_runsvm_trainedpic);
		final Button btn_run=(Button)findViewById(R.id.button_runsvm_run);
		final ImageView imageview_add_logo=(ImageView)findViewById(R.id.imageview_runsvm_addlogo);

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
		ParseSharePref parseSharePref=new ParseSharePref(getString(R.string.share_pref_svm_param),getApplicationContext());
		double C=parseSharePref.getDouble("C");
		double gamma=parseSharePref.getDouble("gamma");
		my_svm=new My_SVM(C,gamma);
		is_svm_has_been_intialized=true;
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
}
