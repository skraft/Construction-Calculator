package com.example.seankraft.constructioncalc;

import java.util.ArrayList;
import java.util.regex.Pattern;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import java.math.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void clickBackspace(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        // remove the last character in the numInputString
        if (numInputString.length() > 0) {
            numInputString = numInputString.substring(0, numInputString.length() - 1);
        }
        else {
            numInputString = "";
        }
        outputText.setText(numInputString);
    }

    public void clickNegative(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        // add a negative if positive, remove the negative if negative
        if (numInputString.startsWith("-")) {
            numInputString = numInputString.substring(1, numInputString.length());
        }
        else {
            numInputString = "-" + numInputString;
        }
        outputText.setText(numInputString);
    }

    // function to receive operation button clicks
    public void clickOperation(View view) {
        // add the current concatenated number to operation list
        if (!numInputString.equals("") && !numInputString.equals("-")) {
            opList.add(numInputString);
        }
        // get the button text
        Button opButton = (Button) view;
        buttonString = opButton.getText().toString();
        if (opList.size() > 0) { // don't add operators if no number has been entered
            // don't allow two operators to be entered back to back : replace the last operator
            String lastEntry = opList.get(opList.size() - 1);
            if (lastEntry.contains("x") || lastEntry.contains("÷") || lastEntry.contains("+") || lastEntry.contains("−")) {
                opList.remove(opList.size() - 1); // replace the last entry with a new operator
                opList.add(buttonString);
            } else {
                opList.add(buttonString);
            }
        }
        // update the DEBUG window
        TextView debugText = (TextView) findViewById(R.id.debug);
        debugText.setText(opList.toString());
        // clear the numInputString for the next number entry
        numInputString = "";
    }

    //TODO typing '0' '=' crashes in an infinite loop
    //TODO very long numbers crash the app (probably a float resolution problem)\
    //TODO entering a new number after calculating should clear - right now it creates bad data
    //TODO oder of operations : multiply / divide  and  add / subtract should run left to right
    public void clickCalculate(View view) {
        // add the current concatenated number to operation list
        if (!numInputString.equals("")) {
            opList.add(numInputString);
        }
        // update the DEBUG window
        TextView debugText = (TextView) findViewById(R.id.debug);
        debugText.setText(opList.toString());
        // check for an invalid opList
        cleanOpList();
        // start calculate function
        calcualte();
        // clear the last number input
        numInputString = "";
    }

    public void clear(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView debugText = (TextView) findViewById(R.id.debug);
        outputText.setText("0");
        debugText.setText("");
        // clear variables
        opList.clear();
        numInputString = "";
    }

    public void cleanOpList() {
        String lastEntry = opList.get(opList.size() - 1);
        // if the last entry is an operator : remove it
        if (lastEntry.contains("x") || lastEntry.contains("÷") || lastEntry.contains("+") || lastEntry.contains("−")) {
            opList.remove(opList.size() - 1);
        }
    }

    public void calcualte() {
        BigDecimal output = new BigDecimal("0");
        if (opList.size() == 1) {
            output = new BigDecimal(opList.get(0)); // set
        }

        while (opList.size() > 1) {
            if (opList.contains("x")) {
                int i = opList.indexOf("x");
                BigDecimal val1 = new BigDecimal(opList.get(i - 1));
                BigDecimal val2 = new BigDecimal(opList.get(i + 1));
                output = val1.multiply(val2); // do multiply
                opList.remove(i + 1);
                opList.remove(i);
                opList.add(i, output.toString());
                opList.remove(i - 1);
            }
            else if (opList.contains("÷")) {
                int i = opList.indexOf("÷");
                BigDecimal val1 = new BigDecimal(opList.get(i - 1));
                BigDecimal val2 = new BigDecimal(opList.get(i + 1));
                output = val1.divide(val2, 9, BigDecimal.ROUND_HALF_UP).stripTrailingZeros(); // do divide
                opList.remove(i + 1);
                opList.remove(i);
                opList.add(i, output.toString());
                opList.remove(i - 1);
            }
            else if (opList.contains("+")) {
                int i = opList.indexOf("+");
                BigDecimal val1 = new BigDecimal(opList.get(i - 1));
                BigDecimal val2 = new BigDecimal(opList.get(i + 1));
                output = val1.add(val2); // do add
                opList.remove(i + 1);
                opList.remove(i);
                opList.add(i, output.toString());
                opList.remove(i - 1);
                }
            else if (opList.contains("−")) {
                int i = opList.indexOf("−");
                BigDecimal val1 = new BigDecimal(opList.get(i - 1));
                BigDecimal val2 = new BigDecimal(opList.get(i + 1));
                output = val1.subtract(val2); // do subtract
                opList.remove(i + 1);
                opList.remove(i);
                opList.add(i, output.toString());
                opList.remove(i - 1);
            }
        }

        // update the DEBUG window
        TextView debugText = (TextView) findViewById(R.id.debug);
        debugText.setText(opList.toString());
        // update the output text
        TextView outputText = (TextView) findViewById(R.id.outputText);

        // remove zero value decimals. Example (4.0 becomes 4)
        String outputString = output.toString();
        if (outputString.contains(".")) {
            String[] parts = outputString.split(Pattern.quote("."));
            if (new BigDecimal(parts[1]).compareTo(BigDecimal.ZERO) == 0) { // after decimal == 0
                outputString = parts[0];
            }
        }
        outputText.setText(outputString);
    }
}