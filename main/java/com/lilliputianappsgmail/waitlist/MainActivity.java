package com.lilliputianappsgmail.waitlist;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private int day;
    private int pmShiftStart;
    private String globalSnark;
    private TypedArray amMult;
    private TypedArray pmMult;
    private TypedArray amMod;
    private TypedArray pmMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, globalSnark, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Resources res = getResources();

        globalSnark = "Pffft. Quit screwing with your phone and get back to work.";

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        pmShiftStart = editTextToInt(getString(R.string.PM_shift_start));

        amMult = res.obtainTypedArray(R.array.am_mult_array);
        pmMult = res.obtainTypedArray(R.array.pm_mult_array);
        amMod = res.obtainTypedArray(R.array.am_mod_array);
        pmMod = res.obtainTypedArray(R.array.pm_mod_array);

        EditText etParties = (EditText) findViewById(R.id.waitingParties);
        EditText etOpenTables = (EditText) findViewById(R.id.openTables);
        EditText etClosedTables = (EditText) findViewById(R.id.closedTables);
        etParties.setText("0");
        etOpenTables.setText("0");
        etClosedTables.setText("0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getWaitTime(View view) {
        // handle the button shit
        String answer = new String();
        int waitingParties, openTables, closedTables;

        hideKeyboard(this);

        Resources res = getResources();
        TextView tvWait= (TextView) findViewById(R.id.textWait);
        EditText etParties = (EditText) findViewById(R.id.waitingParties);
        EditText etOpenTables = (EditText) findViewById(R.id.openTables);
        EditText etClosedTables = (EditText) findViewById(R.id.closedTables);

        waitingParties = editTextToInt(etParties.getText().toString());
        openTables = editTextToInt(etOpenTables.getText().toString());
        closedTables = editTextToInt(etClosedTables.getText().toString());

        float mult;
        float mod;

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(hour<pmShiftStart) {
            mult = amMult.getFloat(day, 0);
            mod = amMod.getFloat(day,0);
        }
        else {
            mult = pmMult.getFloat(day, 0);
            mod = pmMod.getFloat(day,0);
        }

        // Waiting Parties - Open/Bus Tables x (Base Modifier + ((Closed Tables * Closed Modifier)) = Wait Time
        mult = mult + mod * closedTables;
        double wait = mult * (waitingParties - openTables);

        tvWait.setText("Wait is " + String.format("%.0f", wait) + " minutes.");

        getSnark();
    }
    public int editTextToInt(String textIn) {
        int intOut;
        try {
            intOut = Integer.parseInt(textIn);
            return intOut;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public void getSnark(){
        Resources res = getResources();
        String [] snark = res.getStringArray(R.array.snark_array) ;
        int random = (int )(Math.random() * snark.length);
        globalSnark = snark[random];
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
