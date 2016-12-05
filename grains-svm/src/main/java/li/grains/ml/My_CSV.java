package li.grains.ml;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import li.grains.StoragePath;

/**
 * Created by li on 16-12-4.
 */

public class My_CSV {
	String csv_file;
	String line = "";
	BufferedReader br = null;
	List<String> content_read = null;

	public My_CSV(String csv_file) {
		StoragePath storagePath = new StoragePath();
		this.csv_file =storagePath.getPath() + csv_file;
	}

	private void read_all() {
		if (content_read == null) {
			content_read = new ArrayList<String>();

			try (BufferedReader br = new BufferedReader(new FileReader(csv_file))) {
				while ((line = br.readLine()) != null) {
					if(line.length()!=0)
						content_read.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void write(List<String> data) {
		StringBuilder builder = new StringBuilder();

		for (String line : data) {
			builder.append(line + '\n');
		}

		try {
			PrintWriter writer = new PrintWriter(csv_file, "UTF-8");
			writer.write(builder.toString());
			writer.close();
		} catch (IOException e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

	}

	public List<String> read() {
		read_all();
		return content_read;
	}

	public int get_total_rows() {
		return content_read.size();
	}

}
