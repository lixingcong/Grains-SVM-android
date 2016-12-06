package li.grains;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import li.grains.ml.My_CSV;

public class SVMParamsActivity extends AppCompatActivity {

	private ListView mlistView;
	private List<String> svm_params_array = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_svmparams);

		setTitle("SVM params list");

		mlistView = (ListView) findViewById(R.id.listview_svmparams_params);

		read_params_from_file();

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, svm_params_array.toArray(new String[0]));
		mlistView.setAdapter(adapter);
		mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = mlistView.getItemAtPosition(position).toString();

				// save to share-prefref
				String[] item_splited = item.split(",");
				ParseSharePref ps = new ParseSharePref(getString(R.string.share_pref_svm_param), getApplicationContext());
				ps.setString("C", item_splited[0]);
				ps.setString("gamma", item_splited[1]);

				// mark as set
				if (ps.contains(getString(R.string.share_pref_is_set_param)) == false) {
					ps.setString(getString(R.string.share_pref_is_set_param), "true");
				}

				// return Activity result
				getIntent().putExtra("C", item_splited[0]);
				getIntent().putExtra("gamma", item_splited[1]);
				setResult(RESULT_OK, getIntent());
				finish();
			}
		});
	}

	private void read_params_from_file() {
		My_CSV my_csv = new My_CSV(getString(R.string.update_params_filename));
		svm_params_array = my_csv.read();
	}
}
