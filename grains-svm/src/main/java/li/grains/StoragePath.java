package li.grains;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by li on 16-12-6.
 */

public final class StoragePath {
	public static String prefix;
	private String subfolder = "grains-svm";
	private static boolean is_initialized = false;
	private static boolean is_folder_exist = false;

	public StoragePath() {
		if (!is_initialized) {
			StoragePath.prefix = Environment.getExternalStorageDirectory().getPath() + "/" + subfolder + "/";
			check_folder_if_exist();
			if (is_folder_exist)
				StoragePath.is_initialized = true;
		}
	}

	public String getPath() {
		return prefix;
	}

	private void check_folder_if_exist() {
		File folder = new File(StoragePath.prefix);
		if (!folder.exists()) {
			//Log.v("xxxx","Not Exist");
			is_folder_exist = folder.mkdirs();
		} else {
			//Log.v("xxxx", "already Exist");
		}
	}
}
