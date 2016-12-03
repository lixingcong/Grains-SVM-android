package li.grains;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by li on 16-12-3.
 */

public class ParseSharePref {

	private Context context;
	private SharedPreferences sp=null;
	private SharedPreferences.Editor editor=null;

	public ParseSharePref(String sharePrefName, Context context) {
		this.context=context;
		this.sp=context.getSharedPreferences(sharePrefName,Context.MODE_PRIVATE);
		this.editor=this.sp.edit();
	}

	public void setDouble(String key,double val){
		String val_=Double.toString(val);
		setString(key,val_);
	}

	public void setString(String key,String val){
		editor.putString(key,val);
		editor.commit();
	}

	public double getDouble(String key){
		String res=getString(key);
		return Double.parseDouble(res);
	}

	public String getString(String key){
		return sp.getString(key,"");
	}

	public boolean contains(String key){
		return sp.contains(key);
	}
}
