package li.grains;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	static boolean is_opencv_loaded;

	static {
		if (!OpenCVLoader.initDebug()) {
			//Log.d("MAIN", "OpenCV not loaded");
			is_opencv_loaded = false;
		} else {
			//Log.d("MAIN", "OpenCV loaded");
			is_opencv_loaded = true;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// check if CV is loaded
		if (!is_opencv_loaded) {
			Toast.makeText(MainActivity.this, "OpenCV native-lib load error", Toast.LENGTH_LONG).show();
		}

		// check for permissons
		(new CheckPermissons(MainActivity.this)).check();

		// set up all my view
		set_my_view();
	}

	private void set_my_view() {
		final Button btn_perform_download_features;
		final Button btn_update_features;
		final Button btn_update_program;
		final Button btn_run_svm;
		final Button btn_reset_edittext;
		final Button btn_save_edittext;
		final LinearLayout layout_update_features;
		final EditText edittext_features_url = ((EditText) findViewById(R.id.edittext_main_features_url));
		final EditText edittext_params_url = ((EditText) findViewById(R.id.edittext_main_params_url));
		final TextView textview1 = (TextView) findViewById(R.id.textview_main_text1);

		// run svm: pick up a photo
		btn_run_svm = (Button) findViewById(R.id.button_main_run_svm);
		btn_run_svm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check_if_csv_exist()) {
					Intent intent = new Intent(MainActivity.this, RunSVMActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			}
		});

		// check if had saved urls to share pref
		ParseSharePref psp = new ParseSharePref(getString(R.string.share_pref_features_urls), MainActivity.this);
		if (psp.contains(getString(R.string.update_features_url))) {
			edittext_features_url.setText(psp.getString(getString(R.string.update_features_url)));
			edittext_params_url.setText(psp.getString(getString(R.string.update_params_url)));
		}

		// layout hide or show
		layout_update_features = (LinearLayout) findViewById(R.id.layout_main_update);
		btn_update_features = (Button) findViewById(R.id.button_main_update_features);
		btn_update_features.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int is_visible = layout_update_features.getVisibility();
				if (is_visible == View.VISIBLE) {
					layout_update_features.setVisibility(View.GONE);
				} else {
					layout_update_features.setVisibility(View.VISIBLE);
				}
			}
		});

		// download click
		btn_perform_download_features = (Button) findViewById(R.id.button_main_perform_update);
		btn_perform_download_features.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> urls = new ArrayList<String>();
				List<String> filenames = new ArrayList<String>();
				List<Integer> percents = new ArrayList<Integer>();

				urls.add(edittext_features_url.getText().toString());
				urls.add(edittext_params_url.getText().toString());

				filenames.add(getString(R.string.update_features_filename));
				filenames.add(getString(R.string.update_params_filename));

				percents.add(new Integer(95));
				percents.add(new Integer(5));

				DownloadFile dl = new DownloadFile(MainActivity.this, urls, filenames, percents);
				dl.execute();
			}
		});

		// save update urls
		btn_save_edittext = (Button) findViewById(R.id.button_main_update_url_save);
		btn_save_edittext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ParseSharePref psp = new ParseSharePref(getString(R.string.share_pref_features_urls), MainActivity.this);
				psp.setString(getString(R.string.update_features_url), ((EditText) findViewById(R.id.edittext_main_features_url)).getText().toString());
				psp.setString(getString(R.string.update_params_url), ((EditText) findViewById(R.id.edittext_main_params_url)).getText().toString());
			}
		});

		// reset update urls
		btn_reset_edittext = (Button) findViewById(R.id.button_main_update_url_reset);
		btn_reset_edittext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ParseSharePref psp = new ParseSharePref(getString(R.string.share_pref_features_urls), MainActivity.this);
				String original_url1 = getString(R.string.update_features_url);
				String original_url2 = getString(R.string.update_params_url);
				psp.setString(getString(R.string.update_features_url), original_url1);
				psp.setString(getString(R.string.update_params_url), original_url2);
				edittext_features_url.setText(original_url1);
				edittext_params_url.setText(original_url2);
			}
		});

		// update program
		btn_update_program = (Button) findViewById(R.id.button_main_update_program);
		btn_update_program.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								Uri uri = Uri.parse(getString(R.string.update_program_url));
								Intent intent = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
								break;
							default:
								break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage("Update?").setPositiveButton("Yes", dialogClickListener)
						.setNegativeButton("No", dialogClickListener).show();
			}
		});

		// version code
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			int version_code = pInfo.versionCode;
			textview1.setText("Build: " + Integer.toString(version_code));
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

	}

	private boolean check_if_csv_exist() {
		StoragePath storagePath = new StoragePath();
		File file = new File(storagePath.getPath() + getString(R.string.update_features_filename));
		if (!file.exists()) {
			Toast.makeText(getApplicationContext(), "Please update features", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

}
