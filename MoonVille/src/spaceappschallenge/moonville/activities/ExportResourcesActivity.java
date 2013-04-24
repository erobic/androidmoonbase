package spaceappschallenge.moonville.activities;

import java.util.ArrayList;

import spaceappschallenge.moonville.GameActivity;
import spaceappschallenge.moonville.R;
import spaceappschallenge.moonville.businessmodels.Resource;
import spaceappschallenge.moonville.factories.Resources;
import spaceappschallenge.moonville.listadapters.ExportResourceListAdapter;
import spaceappschallenge.moonville.managers.MoonBaseManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ExportResourcesActivity extends GameActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export_resources);

		ExportResourceListAdapter resourceListAdapter = new ExportResourceListAdapter();
		Log.i("ResourcesActivity", "showing export resources screen..");
		ListView resourceListView = (ListView) this
				.findViewById(R.id.demandList);
		resourceListView.setAdapter(resourceListAdapter);
		updateUI();
	}

	public void updateUI()
	{
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_export_resources, menu);
		return true;
	}

	// methods called by onClick property of button in xml
	public void showBaseOverviewScreen(View view) {
		view.getContext().startActivity(
				new Intent(this, BaseOverviewActivity.class));
		this.finish();
	}

	public void showBuildingScreen(View view) {
		view.getContext().startActivity(
				new Intent(this, BaseOverviewActivity.class));
		this.finish();
	}

	public void showResourcesScreen(View view) {
		view.getContext().startActivity(
				new Intent(this, ResourcesActivity.class));
		this.finish();
	}

	public void showExportScreen(View view) {
		view.getContext().startActivity(
				new Intent(this, ExportResourcesActivity.class));
		this.finish();
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK))
	    {
			startActivity(
					new Intent(ExportResourcesActivity.this, BaseOverviewActivity.class));
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	


}
