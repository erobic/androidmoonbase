/*
 * Starts the game. Initializes managers based on user choices.
 */
package spaceappschallenge.moonville.activities;

import spaceappschallenge.moonville.R;
import spaceappschallenge.moonville.domain.Difficulty;
import spaceappschallenge.moonville.domain.GameDetails;
import spaceappschallenge.moonville.factories.MoonBaseManager;
import spaceappschallenge.moonville.factories.TransportCompanies;
import spaceappschallenge.moonville.listadapters.TransportCompanySpinnerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class NewGameActivity extends GameActivity {
	protected GameDetails gameDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		
		Spinner spinner = (Spinner) this.findViewById(R.id.choosespacetransport);
		spinner.setAdapter( new TransportCompanySpinnerAdapter( TransportCompanies.getInstance().getCompanies() ) );
		
	}

	public void startNewGame(View view) {
		gameDetails = GameDetails.getInstance();
		retrieveUserInputs();
		initModel();
		showBaseOverviewScreen(view);
	}

	// Retrieve user name and difficulty level
	protected void retrieveUserInputs() {
		EditText userNameEditText = (EditText) findViewById(R.id.usercompanyname);
		String userName = userNameEditText.getText().toString();
		gameDetails.setUsername(userName);

		RadioGroup difficultyRadioGroup = (RadioGroup) findViewById(R.id.choosedifficulty);
		int radioButtonId = difficultyRadioGroup.getCheckedRadioButtonId();
		RadioButton selectedButton = (RadioButton) difficultyRadioGroup
				.findViewById(radioButtonId);
		int difficulty = difficultyRadioGroup.indexOfChild(selectedButton);

		gameDetails.setDifficultyLevel(difficulty);
		Log.i("Difficulty:", "" + gameDetails.getDifficultyLevel());
	}

	protected void initModel() {
		// Dummy models

		Difficulty diff = new Difficulty(gameDetails.getDifficultyLevel());

		MoonBaseManager.createNewMoonBase(diff, this);
		// MoonBase should become a singleton I think, best way to make it
		// easily accessible -Jos
	}

	// I changed the init method to this one, which is triggered by the onClick
	// property in the xml of the button
	// since we are using separate activities, this is a very easy way to
	// implement navigation IMO.
	public void showBaseOverviewScreen(View view) {
		view.getContext().startActivity(
				new Intent(this, BaseOverviewActivity.class));
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_game, menu);
		return true;
	}
	


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK))
	    {
			startActivity(
					new Intent(this, MainMenuActivity.class));
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}

}
