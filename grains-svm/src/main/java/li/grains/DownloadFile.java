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
import java.util.List;

/**
 * Created by li on 16-12-3.
 */

public class DownloadFile extends AsyncTask<Void, Integer, String> {

	private ProgressDialog mProgressDialog;
	private Context context;
	private boolean running;
	private List<String> urls,filenames;
	private List<Integer> percents;
	private int total_percent;
	StoragePath storagePath=null;

	public DownloadFile(Context context,List<String> urls,List<String> filenames,List<Integer> percents){
		super();
		this.context=context;
		this.urls=urls;
		this.filenames=filenames;
		this.percents=percents;
		this.total_percent=0;
		this.storagePath=new StoragePath();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Create progress dialog
		mProgressDialog = new ProgressDialog(context);
		// Set your progress dialog Title
		mProgressDialog.setTitle("Connecting...");
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
	protected String doInBackground(Void... params) {
		if(running){
			for(int i=0;i<urls.size();i++)
				download_a_file(i);
		}
		running=false;
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		// Update the progress dialog
		mProgressDialog.setProgress(values[0]);
		mProgressDialog.setTitle("Save to: "+filenames.get(values[1]));
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

	private void download_a_file(int misson_index){
		try {
			URL url = new URL(urls.get(misson_index));
			URLConnection connection = url.openConnection();
			connection.connect();

			// Detect the file lenghth
			int fileLength = connection.getContentLength();

			// Download the file
			InputStream input = new BufferedInputStream(url.openStream());

			// Save the downloaded file
			OutputStream output = new FileOutputStream(storagePath.getPath()+filenames.get(misson_index));

			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				// Publish the progress
				publishProgress(((int) (total * percents.get(misson_index) / fileLength)+total_percent),misson_index);
				output.write(data, 0, count);
			}

			total_percent+=percents.get(misson_index);

			// Close connection
			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {
			// Error Log
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
	}
}
