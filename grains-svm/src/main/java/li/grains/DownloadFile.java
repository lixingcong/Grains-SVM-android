package li.grains;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by li on 16-12-3.
 */

public class DownloadFile extends AsyncTask<String, Integer, String> {

	private ProgressDialog mProgressDialog;
	private Context context;
	private String save_filename;
	private boolean running;

	public DownloadFile(Context context,String save_filename){
		super();
		this.context=context;
		this.save_filename =save_filename;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Create progress dialog
		mProgressDialog = new ProgressDialog(context);
		// Set your progress dialog Title
		mProgressDialog.setTitle("filename: "+save_filename);
		// Set your progress dialog Message
		mProgressDialog.setMessage("Downloading, Please Wait!");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// Show progress dialog
		mProgressDialog.show();

		// download could be cancelled
		running=true;
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				cancel(true);
				running=false;
			}
		});
	}

	@Override
	protected String doInBackground(String... params) {
		if(running)
			try {
				URL url = new URL(params[0]);
				URLConnection connection = url.openConnection();
				connection.connect();

				// Detect the file lenghth
				int fileLength = connection.getContentLength();

				// Locate storage location
				String filepath = Environment.getExternalStorageDirectory()
						.getPath();

				// Download the file
				InputStream input = new BufferedInputStream(url.openStream());

				// Save the downloaded file
				OutputStream output = new FileOutputStream(filepath + "/"
						+ save_filename);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					// Publish the progress
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				// Close connection
				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
				// Error Log
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}

		running=false;
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		// Update the progress dialog
		mProgressDialog.setProgress(values[0]);
		// Dismiss the progress dialog
		// mProgressDialog.dismiss();
		if(values[0]==100){
			Toast.makeText(context, "Update Done", Toast.LENGTH_SHORT).show();
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onCancelled() {
		running = false;
		Toast.makeText(context, "Update was cancelled", Toast.LENGTH_SHORT).show();
	}

}
