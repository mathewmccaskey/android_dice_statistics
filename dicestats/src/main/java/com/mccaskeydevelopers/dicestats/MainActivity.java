package com.mccaskeydevelopers.dicestats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


public class MainActivity extends Activity {

    Button calculate;
    Button clear;
    Spinner spinner_d4, spinner_d6, spinner_d8, spinner_d10, spinner_d12, spinner_d20;
    EditText modifier, goal;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the button to get the statistics results
        calculate = (Button) findViewById(R.id.calc_button);
        calculate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num_d4, num_d6, num_d8, num_d10, num_d12, num_d20;
                int total_dice, high_roll;
                double mod, want, average, std_dev, prob;
                boolean calc_prob;
                String final_statement;

                //Gather up all the numbers from the dice spinners
                num_d4 = (Integer) spinner_d4.getSelectedItem();
                num_d6 = (Integer) spinner_d6.getSelectedItem();
                num_d8 = (Integer) spinner_d8.getSelectedItem();
                num_d10 = (Integer) spinner_d10.getSelectedItem();
                num_d12 = (Integer) spinner_d12.getSelectedItem();
                num_d20 = (Integer) spinner_d20.getSelectedItem();

                total_dice = num_d4 + num_d6 + num_d8 + num_d10 + num_d12 + num_d20;
                high_roll = num_d4*4 + num_d6*6 + num_d8*8 + num_d10*10 + num_d12*12 + num_d20*20;

                //Get the modifier and goal (if applicable) from the last two inputs
                if (modifier.getText().toString().length() > 0){
                    mod = Integer.parseInt(modifier.getText().toString());}
                else{
                    mod = 0.0;}

                if (goal.getText().toString().length() > 0){
                    want = Integer.parseInt(goal.getText().toString());
                    calc_prob = true;}
                else{
                    want = 0.0;
                    calc_prob = false;}

                //Calculate the average and standard deviation
                average = num_d4*2.5 + num_d6*3.5 + num_d8*4.5 + num_d10*5.5 + num_d12*6.5 + num_d20*10.5 + mod;
                String ave_string = Double.toString(average);

                std_dev = Math.pow((((num_d4*Math.pow(4.0,2.0) + num_d6*Math.pow(6.0,2.0) + num_d8*Math.pow(8.0,2.0)
                 + num_d10*Math.pow(10.0,2.0) + num_d12*Math.pow(12.0,2.0) + num_d20*Math.pow(20.0,2.0))
                 - total_dice)/12.0),0.5);
                String std_string = String.format("%.3f",std_dev);

                if (calc_prob) {
                    prob = calculate_prob(total_dice, high_roll, num_d4, num_d6, num_d8, num_d10, num_d12, num_d20, mod, want);
                    String prob_string = String.format("%.4f", (prob * 100.0));
                    if ((total_dice + mod) >= want) {
                        final_statement = "Auto Success!";
                    } else if ((high_roll + mod) <= want) {
                        final_statement = "Auto Fail!";
                    } else if ((total_dice + mod) >= (want - 1)) {
                        final_statement = "Don\'t roll 1\'s on everything!";
                    } else {
                        final_statement = "";
                    }

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Results!");
                    alert.setMessage("Average = "+ave_string+"\nStandard Deviation = "+std_string+
                        "\nProbability to Succeed = "+prob_string+"%\n"+final_statement);

                    // Make an "OK" button that dismisses the alert
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {}
                    });
                    alert.show();
                }
                else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Results!");
                    alert.setMessage("Average = "+ave_string+"\nStandard Deviation = "+std_string);

                    // Make an "OK" button that dismisses the alert
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {}
                    });
                    alert.show();
                }
            }
        });

        // Set up the button to clear the results
        clear = (Button) findViewById(R.id.clear_button);
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // reset the spinners
                spinner_d4.setSelection(0);
                spinner_d6.setSelection(0);
                spinner_d8.setSelection(0);
                spinner_d10.setSelection(0);
                spinner_d12.setSelection(0);
                spinner_d20.setSelection(0);

                // reset the modifier and the goal
                modifier.setText(null);
                goal.setText(null);
            }
        });

        //Set up all the stuff for the dice spinners
        Integer[] num_dice = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, num_dice);
        spinner_d4 = (Spinner) findViewById(R.id.d4_spinner);
        spinner_d4.setAdapter(adapter);

        spinner_d6 = (Spinner) findViewById(R.id.d6_spinner);
        spinner_d6.setAdapter(adapter);

        spinner_d8 = (Spinner) findViewById(R.id.d8_spinner);
        spinner_d8.setAdapter(adapter);

        spinner_d10 = (Spinner) findViewById(R.id.d10_spinner);
        spinner_d10.setAdapter(adapter);

        spinner_d12 = (Spinner) findViewById(R.id.d12_spinner);
        spinner_d12.setAdapter(adapter);

        spinner_d20 = (Spinner) findViewById(R.id.d20_spinner);
        spinner_d20.setAdapter(adapter);

        //Set up the text edits for the modifier and goal
        modifier = (EditText) findViewById(R.id.main_modifier);
        goal = (EditText) findViewById(R.id.main_goal);
    }

    // function that calculates the probability to make a successful roll
    public double calculate_prob(int total_dice, int high_roll, int num_d4, int num_d6, int num_d8,
                                 int num_d10, int num_d12,int num_d20, double mod, double want){
        //parameters used in this function only
        double[][] probs = new double[total_dice+1][high_roll+1];
        double result;
        int i, j, k, dice_counter, max_tally;

        dice_counter = 0;
        max_tally = 0;
        probs[0][0] = 1.0;

        // loop over the d4's
        for(i=1; i<=num_d4; i++){
            dice_counter++;
            max_tally = max_tally + 4;
            for(j=dice_counter; j<=max_tally; j++){
                probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-1]/4.0;
                for (k=2; k<=4; k++){
                    if (j>=k){
                        probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-k]/4.0;
                    }
                }
            }
        }

        // loop over the d6's
        for(i=1; i<=num_d6; i++){
            dice_counter++;
            max_tally = max_tally + 6;
            for(j=dice_counter; j<=max_tally; j++){
                probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-1]/6.0;
                for (k=2; k<=6; k++){
                    if (j>=k){
                        probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-k]/6.0;
                    }
                }
            }
        }

        // loop over the d8's
        for(i=1; i<=num_d8; i++){
            dice_counter++;
            max_tally = max_tally + 8;
            for(j=dice_counter; j<=max_tally; j++){
                probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-1]/8.0;
                for (k=2; k<=8; k++){
                    if (j>=k){
                        probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-k]/8.0;
                    }
                }
            }
        }

        // loop over the d10's
        for(i=1; i<=num_d10; i++){
            dice_counter++;
            max_tally = max_tally + 10;
            for(j=dice_counter; j<=max_tally; j++){
                probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-1]/10.0;
                for (k=2; k<=10; k++){
                    if (j>=k){
                        probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-k]/10.0;
                    }
                }
            }
        }

        // loop over the d12's
        for(i=1; i<=num_d12; i++){
            dice_counter++;
            max_tally = max_tally + 12;
            for(j=dice_counter; j<=max_tally; j++){
                probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-1]/12.0;
                for (k=2; k<=12; k++){
                    if (j>=k){
                        probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-k]/12.0;
                    }
                }
            }
        }

        // loop over the d20's
        for(i=1; i<=num_d20; i++){
            dice_counter++;
            max_tally = max_tally + 20;
            for(j=dice_counter; j<=max_tally; j++){
                probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-1]/20.0;
                for (k=2; k<=20; k++){
                    if (j>=k){
                        probs[dice_counter][j] = probs[dice_counter][j] + probs[dice_counter-1][j-k]/20.0;
                    }
                }
            }
        }

        //tabulate the result
        if (total_dice+mod >= want){
            result = 1.0;
        } else {
            result = 0.0;
            for (i = 1; i <= high_roll; i++) {
                if (i >= (want - mod)) {
                    result = result + probs[total_dice][i];
                }
            }
        }

        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        ImageView img_4, img_6, img_8, img_10, img_12, img_20;
        Drawable myDrawable_4, myDrawable_6, myDrawable_8, myDrawable_10, myDrawable_12, myDrawable_20;

        if (id == R.id.action_about){
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("About Dice Statistics:");
            alert.setMessage("This app is my first journey into the world of Android programming.\n\n"+
            "Simply enter in the number of each type of die to be rolled, a modifier (optional), and a goal (also optional).\n\n"+
            "Press calculate to get the average and standard deviation of the roll.\n\n"+
            "If a goal is entered it will also give the probability of a successful roll.\n\n"+
            "Helpful aid when playing Pathfinder Adventure Card Game! Enjoy!");

            // Make an "OK" button that dismisses the alert
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
            return true;
        } else{

            img_4 = (ImageView) findViewById(R.id.d4_image);
            img_6 = (ImageView) findViewById(R.id.d6_image);
            img_8 = (ImageView) findViewById(R.id.d8_image);
            img_10 = (ImageView) findViewById(R.id.d10_image);
            img_12 = (ImageView) findViewById(R.id.d12_image);
            img_20 = (ImageView) findViewById(R.id.d20_image);

            switch (id) {
                case R.id.dice_color_red:
                    myDrawable_4 = getResources().getDrawable(R.drawable.d4_red);
                    myDrawable_6 = getResources().getDrawable(R.drawable.d6_red);
                    myDrawable_8 = getResources().getDrawable(R.drawable.d8_red);
                    myDrawable_10 = getResources().getDrawable(R.drawable.d10_red);
                    myDrawable_12 = getResources().getDrawable(R.drawable.d12_red);
                    myDrawable_20 = getResources().getDrawable(R.drawable.d20_red);

                    img_4.setImageDrawable(myDrawable_4);
                    img_6.setImageDrawable(myDrawable_6);
                    img_8.setImageDrawable(myDrawable_8);
                    img_10.setImageDrawable(myDrawable_10);
                    img_12.setImageDrawable(myDrawable_12);
                    img_20.setImageDrawable(myDrawable_20);
                    return true;

                case R.id.dice_color_green:
                    myDrawable_4 = getResources().getDrawable(R.drawable.d4_green);
                    myDrawable_6 = getResources().getDrawable(R.drawable.d6_green);
                    myDrawable_8 = getResources().getDrawable(R.drawable.d8_green);
                    myDrawable_10 = getResources().getDrawable(R.drawable.d10_green);
                    myDrawable_12 = getResources().getDrawable(R.drawable.d12_green);
                    myDrawable_20 = getResources().getDrawable(R.drawable.d20_green);

                    img_4.setImageDrawable(myDrawable_4);
                    img_6.setImageDrawable(myDrawable_6);
                    img_8.setImageDrawable(myDrawable_8);
                    img_10.setImageDrawable(myDrawable_10);
                    img_12.setImageDrawable(myDrawable_12);
                    img_20.setImageDrawable(myDrawable_20);
                    return true;

                case R.id.dice_color_cyan:
                    myDrawable_4 = getResources().getDrawable(R.drawable.d4_cyan);
                    myDrawable_6 = getResources().getDrawable(R.drawable.d6_cyan);
                    myDrawable_8 = getResources().getDrawable(R.drawable.d8_cyan);
                    myDrawable_10 = getResources().getDrawable(R.drawable.d10_cyan);
                    myDrawable_12 = getResources().getDrawable(R.drawable.d12_cyan);
                    myDrawable_20 = getResources().getDrawable(R.drawable.d20_cyan);

                    img_4.setImageDrawable(myDrawable_4);
                    img_6.setImageDrawable(myDrawable_6);
                    img_8.setImageDrawable(myDrawable_8);
                    img_10.setImageDrawable(myDrawable_10);
                    img_12.setImageDrawable(myDrawable_12);
                    img_20.setImageDrawable(myDrawable_20);
                    return true;

                case R.id.dice_color_blue:
                    myDrawable_4 = getResources().getDrawable(R.drawable.d4_blue);
                    myDrawable_6 = getResources().getDrawable(R.drawable.d6_blue);
                    myDrawable_8 = getResources().getDrawable(R.drawable.d8_blue);
                    myDrawable_10 = getResources().getDrawable(R.drawable.d10_blue);
                    myDrawable_12 = getResources().getDrawable(R.drawable.d12_blue);
                    myDrawable_20 = getResources().getDrawable(R.drawable.d20_blue);

                    img_4.setImageDrawable(myDrawable_4);
                    img_6.setImageDrawable(myDrawable_6);
                    img_8.setImageDrawable(myDrawable_8);
                    img_10.setImageDrawable(myDrawable_10);
                    img_12.setImageDrawable(myDrawable_12);
                    img_20.setImageDrawable(myDrawable_20);
                    return true;

                case R.id.dice_color_purple:
                    myDrawable_4 = getResources().getDrawable(R.drawable.d4_purple);
                    myDrawable_6 = getResources().getDrawable(R.drawable.d6_purple);
                    myDrawable_8 = getResources().getDrawable(R.drawable.d8_purple);
                    myDrawable_10 = getResources().getDrawable(R.drawable.d10_purple);
                    myDrawable_12 = getResources().getDrawable(R.drawable.d12_purple);
                    myDrawable_20 = getResources().getDrawable(R.drawable.d20_purple);

                    img_4.setImageDrawable(myDrawable_4);
                    img_6.setImageDrawable(myDrawable_6);
                    img_8.setImageDrawable(myDrawable_8);
                    img_10.setImageDrawable(myDrawable_10);
                    img_12.setImageDrawable(myDrawable_12);
                    img_20.setImageDrawable(myDrawable_20);
                    return true;

                case R.id.dice_color_black:
                    myDrawable_4 = getResources().getDrawable(R.drawable.d4_black);
                    myDrawable_6 = getResources().getDrawable(R.drawable.d6_black);
                    myDrawable_8 = getResources().getDrawable(R.drawable.d8_black);
                    myDrawable_10 = getResources().getDrawable(R.drawable.d10_black);
                    myDrawable_12 = getResources().getDrawable(R.drawable.d12_black);
                    myDrawable_20 = getResources().getDrawable(R.drawable.d20_black);

                    img_4.setImageDrawable(myDrawable_4);
                    img_6.setImageDrawable(myDrawable_6);
                    img_8.setImageDrawable(myDrawable_8);
                    img_10.setImageDrawable(myDrawable_10);
                    img_12.setImageDrawable(myDrawable_12);
                    img_20.setImageDrawable(myDrawable_20);
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }
}