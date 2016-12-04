package li.grains;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.util.List;

import li.grains.ml.My_Features;
import li.grains.ml.My_SVM;

/**
 * Created by li on 16-12-4.
 */

public class PredictProgress extends AsyncTask<Void, Void, Void> {

	private Context context;
	private My_SVM my_svm=null;
	private My_Features my_features=null;
	private String filename=null;
	private ProgressDialog progress=null;
	private boolean is_finish;
	private String result=null;

	public PredictProgress(Context context,My_SVM my_svm, My_Features my_features, String filename){
		this.my_svm=my_svm;
		this.filename=filename;
		this.context=context;
		this.my_features=my_features;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progress=new ProgressDialog(context);
		// please wait
		progress.setTitle("Calculating features...");
		progress.setMessage("Please wait for a while");
		progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.setButton(ProgressDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progress.dismiss();
			}
		});
		is_finish=false;
		progress.show();
		progress.getButton(ProgressDialog.BUTTON_POSITIVE).setEnabled(false);
	}

	@Override
	protected Void doInBackground(Void... params) {
		if(!is_finish) {
			String filename_save = Environment.getExternalStorageDirectory().getPath();
			filename_save += ("/" + filename);
			Log.v("SVM", filename_save);
			My_Features features_test = new My_Features(filename_save);
			List<List<Double>> test_x = features_test.get_features_x();
			List<Double> predict_y = my_svm.predict(test_x);
			result = my_features.get_chinese_from_category(new Double(predict_y.get(0)).intValue());
			publishProgress();
			// announce that it finished
			is_finish = true;
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		if(is_finish){
			progress.getButton(ProgressDialog.BUTTON_POSITIVE).setEnabled(true);
			progress.setTitle("Result: "+result);
			progress.setMessage("Done");
		}
	}
}
