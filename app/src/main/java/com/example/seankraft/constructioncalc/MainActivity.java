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

//TODO oder of operations : multiply / divide  and  add / subtract should run left to right

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
    String numeratorInput = "";
    BigDecimal inputNumber = new BigDecimal("0");

    Boolean feetAdded = false;
    Boolean inchAdded = false;
    Boolean fractionAdded = false;

    String units = "decimal";

    // If feetAdded and inchAdded units = feetInch
    // If feetAdded but not inch, units = feet
    // If inchAdded but not feet, units = inch
    // If !feetAdded && !inchAdded, units = decimal

    //untyped units are inches by default
    //'feet' pushed, just multiply current value by 12 and add it to numInputString
    //'inch' pushed, enter current value
    //fraction entered, convert to decimal and add to numInputString

    // for rounding to a specific fraction resolution:
    //Math.round(myFloat*16) / 16f

    public void clickNumber(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView debugText = (TextView) findViewById(R.id.debug);
        Button numberButton = (Button) view;
        buttonString = numberButton.getText().toString();
        // add the number to the output string for the view
        numInputString = numInputString + buttonString;
        outputText.setText(numInputString);
        // if the only entry in the opList is another number, clear: (starting a fresh calculation)
        if (opList.size() == 1) {
            String entry = opList.get(0);
            if (!entry.contains("x") && !entry.contains("÷") && !entry.contains("+") && !entry.contains("−")) {
                opList.clear();
                debugText.setText("");
            }
        }
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

    public void clickFeet(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView debugText = (TextView) findViewById(R.id.debug);
        if (!numInputString.equals("") && !numInputString.equals("-")) {
            // get current number entry
            String outText = numInputString + " feet";
            outputText.setText(outText);
            feetAdded = true;
            // multiply input times 12
            BigDecimal feet = new BigDecimal(numInputString);
            inputNumber = feet.multiply(new BigDecimal("12")).add(inputNumber);
            // update debug
            debugText.setText(inputNumber.toString());
        }
        numInputString = "";
    }

    public void clickInch(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView debugText = (TextView) findViewById(R.id.debug);
        if (!numInputString.equals("") && !numInputString.equals("-")) {
            // get current number entry
            String outText = numInputString + " inch";
            outputText.setText(outText);
            inchAdded = true;
            // add input value
            BigDecimal inch = new BigDecimal(numInputString);
            inputNumber = inch.add(inputNumber);
            // update debug
            debugText.setText(inputNumber.toString());
        }
        numInputString = "";
    }

    public void clickFraction(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView debugText = (TextView) findViewById(R.id.debug);
        if (!numInputString.equals("") && !numInputString.equals("-")) {
            fractionAdded = true;
            // define numerator
            numeratorInput = numInputString;
            String outText = numInputString + "/";
            outputText.setText(outText);
        }
        numInputString = "";
    }

    public void clickOperation(View view) {
        compile_input_number();
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
        // clear the variables for the next number entry
        numInputString = "";
        numeratorInput = "";
        inputNumber = BigDecimal.ZERO;
        fractionAdded = false;
    }

    public void clickCalculate(View view) {
        // add the current concatenated number to operation list
        compile_input_number();
        // update the DEBUG window
        TextView debugText = (TextView) findViewById(R.id.debug);
        debugText.setText(opList.toString());
        // check for an invalid opList
        cleanOpList();
        // start calculate function
        calculate();
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
        numeratorInput = "";
        inputNumber = BigDecimal.ZERO;
        feetAdded = false;
        inchAdded = false;
        fractionAdded = false;
        units = "decimal";
    }

    public void compile_input_number() {
        // This function compiles any feet, inch, and fraction input into the opList
        if (fractionAdded) {
            BigDecimal numerator = new BigDecimal(numeratorInput);
            BigDecimal denominator = new BigDecimal(numInputString);
            inputNumber = numerator.divide(denominator, 9, BigDecimal.ROUND_HALF_UP).add(inputNumber);
        }
        // if there is an inputNumber; add it to the opList
        if (inputNumber.compareTo(BigDecimal.ZERO) != 0) {
            opList.add(inputNumber.toString());
        }
        else {
            if (!numInputString.equals("") && !numInputString.equals("-")) {
                opList.add(numInputString);
            }
        }
    }

    public void cleanOpList() {
        String lastEntry = opList.get(opList.size() - 1);
        // if the last entry is an operator : remove it
        if (lastEntry.contains("x") || lastEntry.contains("÷") || lastEntry.contains("+") || lastEntry.contains("−")) {
            opList.remove(opList.size() - 1);
        }
    }

    public void calculate() {
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