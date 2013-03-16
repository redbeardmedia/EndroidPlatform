package nl.endroid.app.platform;

import android.os.Bundle;
import nl.endroid.framework.BaseActivity;
import nl.endroid.framework.Billing;
import nl.endroid.framework.Utils;

public class MainActivity extends BaseActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Billing.load(Utils.getString(R.string.licence_key), this);
	}
}
