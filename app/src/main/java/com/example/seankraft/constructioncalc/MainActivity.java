package com.example.seankraft.constructioncalc;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    ArrayList<String> opList = new ArrayList<>();
    String buttonString = "";
    String numInputString = "";

    // for rounding to a specific fraction resolution:
    //Math.round(myFloat*16) / 16f

    // function to receive number button clicks
    public void clickNumber(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        Button numberButton = (Button) view;
        buttonString = numberButton.getText().toString();
        // add the number to the output string for the view
        numInputString = numInputString + buttonString;
        outputText.setText(numInputString);
    }

    // function to receive operation button clicks
    public void clickOperation(View view) {
        // add the current concatenated number to operation list
        opList.add(numInputString);
        // add the clicked operator to the operation list
        Button numberButton = (Button) view;
        buttonString = numberButton.getText().toString();
        opList.add(buttonString);
        // update the DEBUG window
        TextView debugText = (TextView) findViewById(R.id.debug);
        debugText.setText(opList.toString());
        // clear the numInputString for the next number entry
        numInputString = "";
    }

    public void clickCalculate(View view) {
        // add the current concatenated number to operation list
        opList.add(numInputString);
        // update the DEBUG window
        TextView debugText = (TextView) findViewById(R.id.debug);
        debugText.setText(opList.toString());
        // start calculate function
        calcualte();
    }

    public void clear(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView debugText = (TextView) findViewById(R.id.debug);
        outputText.setText("0");
        debugText.setText("");
        // clear variables
        opList = new ArrayList<>();
        numInputString = "";
    }

    public void calcualte() {
        int output = 0;

        while (opList.size() > 1) {
            if (opList.contains("x")) {
                for (int i = 0; i < opList.size(); i++) {
                    if (opList.get(i).contains("x")) {
                        output = Integer.parseInt(opList.get(i - 1)) * Integer.parseInt(opList.get(i + 1));
                        opList.remove(i + 1);
                        opList.remove(i);
                        opList.add(i, Integer.toString(output));
                        opList.remove(i - 1);
                    }
                }
            }
            else if (opList.contains("÷")) {
                for (int i = 0; i < opList.size(); i++) {
                    if (opList.get(i).contains("÷")) {
                        output = Integer.parseInt(opList.get(i - 1)) / Integer.parseInt(opList.get(i + 1));
                        opList.remove(i + 1);
                        opList.remove(i);
                        opList.add(i, Integer.toString(output));
                        opList.remove(i - 1);
                    }
                }
            }
            else if (opList.contains("+")) {
                for (int i = 0; i < opList.size(); i++) {
                    if (opList.get(i).contains("+")) {
                        output = Integer.parseInt(opList.get(i - 1)) + Integer.parseInt(opList.get(i + 1));
                        opList.remove(i + 1);
                        opList.remove(i);
                        opList.add(i, Integer.toString(output));
                        opList.remove(i - 1);
                        }
                    }
                }
            else if (opList.contains("−")) {
                for (int i = 0; i < opList.size(); i++) {
                    if (opList.get(i).contains("−")) {
                        output = Integer.parseInt(opList.get(i - 1)) - Integer.parseInt(opList.get(i + 1));
                        opList.remove(i + 1);
                        opList.remove(i);
                        opList.add(i, Integer.toString(output));
                        opList.remove(i - 1);
                    }
                }
            }
        }

        // update the DEBUG window
        TextView debugText = (TextView) findViewById(R.id.debug);
        debugText.setText(opList.toString());
        // update the output text
        TextView outputText = (TextView) findViewById(R.id.outputText);
        outputText.setText(Integer.toString(output));
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
}
