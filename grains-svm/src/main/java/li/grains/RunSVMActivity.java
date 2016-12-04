package li.grains;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class RunSVMActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_svm);
		set_my_view();
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
}
