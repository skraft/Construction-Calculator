package com.example.seankraft.constructioncalc;

import java.util.ArrayList;
import java.util.regex.Pattern;
import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
//        Button convertButton = (Button) findViewById(R.id.button_convert);
//        convertButton.setOnClickListener(this);
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

    /*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.convert_title)
                .setItems(R.array.convert_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }
    */


    // from settings
    String fractionRes = "16";

    ArrayList<String> opList = new ArrayList<>();
    String buttonString = "";
    String numInputString = "";
    String numeratorInput = "";
    BigDecimal inputNumber = new BigDecimal("0");  // for calculating feet / inches
    BigDecimal output = new BigDecimal("0");  // the output of the calc function

    Boolean feetAdded = false;
    Boolean inchAdded = false;
    Boolean fractionAdded = false;  // tracks fraction input until clear or calculate is pushed
    Boolean addingFraction = false;  // tracks if a fraction is currently being typed

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

    final String[] items = {
            R.string.convert_feet,
            R.string.convert_inch,
            R.string.convert_feetinch,
            R.string.convert_fraction,
            R.string.convert_decimal
    };

    public void clickConvert(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.convert_title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do something with the selection
                TextView debugText = (TextView) findViewById(R.id.oplist);
                String tempText = items[which];
                debugText.setText(tempText);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void clickNumber(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView debugText = (TextView) findViewById(R.id.oplist);
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
        TextView debugText = (TextView) findViewById(R.id.oplist);
        if (!numInputString.equals("") && !numInputString.equals("-")) {
            // get current number entry
            String outText = numInputString + "' ";
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
        TextView debugText = (TextView) findViewById(R.id.oplist);
        if (!numInputString.equals("") && !numInputString.equals("-")) {
            // get current number entry
            String outText = numInputString + '"';
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
        if (!numInputString.equals("") && !numInputString.equals("-")) {
            fractionAdded = true;
            addingFraction = true;
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
        TextView debugText = (TextView) findViewById(R.id.oplist);
        debugText.setText(opList.toString());
        // clear the variables for the next number entry
        numInputString = "";
        numeratorInput = "";
        inputNumber = BigDecimal.ZERO;
        addingFraction = false;
    }

    public void clickCalculate(View view) {
        // add the current concatenated number to operation list
        compile_input_number();
        // update the DEBUG window
        TextView debugText = (TextView) findViewById(R.id.oplist);
        debugText.setText(opList.toString());
        // check for an invalid opList
        cleanOpList();
        // start calculate function
        calculate();
        // clear the last number input
        numInputString = "";
        inputNumber = BigDecimal.ZERO;
    }

    public void clear(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView debugText = (TextView) findViewById(R.id.oplist);
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
        addingFraction = false;
        units = "decimal";
    }

    public void compile_input_number() {
        // This function compiles any feet, inch, and fraction input into the opList
        if (addingFraction) {
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
        addingFraction = false;  // hitting "=" concludes fraction entry
        format_output();
    }

    public String remove_trailing_zeros(BigDecimal input) {
        // remove zero value decimals. Example (4.0 becomes 4)
        String outputString = input.toString();
        if (outputString.contains(".")) {
            String[] parts = outputString.split(Pattern.quote("."));
            if (new BigDecimal(parts[1]).compareTo(BigDecimal.ZERO) == 0) { // after decimal == 0
                outputString = parts[0];
            }
        }
        return outputString;
    }

    public String decimal_to_fraction(String decimalString) {
        // if the decimal part is > 0
        if (new BigDecimal(decimalString).compareTo(BigDecimal.ZERO) == 1) {  // after decimal > 0
            // create a denominator for the fraction
            BigDecimal numerator = new BigDecimal(decimalString);
            BigDecimal denominator = new BigDecimal("10").pow(decimalString.length());

            // convert to fraction to resolution from settings
            BigDecimal reducer = denominator.divide(new BigDecimal(fractionRes), 9, BigDecimal.ROUND_HALF_UP);
            numerator = numerator.divide(reducer, 0, BigDecimal.ROUND_HALF_UP);
            denominator = new BigDecimal(fractionRes);

            BigDecimal two = new BigDecimal("2");
            // reduce fraction
            while (numerator.compareTo(BigDecimal.ONE) == 1 &&
                    denominator.compareTo(BigDecimal.ONE) == 1) {
                BigDecimal numRemain = numerator.remainder(two);
                BigDecimal denRemain = denominator.remainder(two);
                // if numerator and denominator can be split in half evenly
                if (numRemain.compareTo(BigDecimal.ZERO) == 0 && denRemain.compareTo(BigDecimal.ZERO) == 0) {
                    numerator = numerator.divide(two, 0, BigDecimal.ROUND_HALF_UP);
                    denominator = denominator.divide(two, 0, BigDecimal.ROUND_HALF_UP);
                }
                else {
                    break;
                }
            }
            // only return a fraction if the numerator is greater then 0
            if (numerator.compareTo(BigDecimal.ZERO) == 1) {
                String outputString = numerator.toString() + "/" + denominator.toString();
                return outputString;
            }
            else {
                return null;
            }
        }
        else {
            return "";
        }
    }

    public void format_output() {
        // This function checks the current 'units' value and writes the output to the UI
        // in the appropriate format. It also checks the current fraction resolution and
        // converts the decimal value to the correct fraction.

        TextView debugText = (TextView) findViewById(R.id.oplist);
        TextView outputText = (TextView) findViewById(R.id.outputText);
        debugText.setText(output.toString());

        if (fractionAdded || feetAdded || inchAdded) {
            BigDecimal integerPart = output;
            String fractionPart = "";

            // convert decimal to fraction
            String outputString = output.toString();
            if (outputString.contains(".")) {
                String[] parts = outputString.split(Pattern.quote("."));
                integerPart = new BigDecimal(parts[0]);
                fractionPart = decimal_to_fraction(parts[1]);
            }

            // check what type of data was entered and format the output to match
            if (feetAdded || inchAdded) {
                BigDecimal feetInch[] = integerPart.divideAndRemainder(new BigDecimal("12"));
                String feetInchOutput = "";
                // add feet
                if (feetInch[0].compareTo(BigDecimal.ZERO) != 0) {
                    feetInchOutput += remove_trailing_zeros(feetInch[0]);
                    feetInchOutput += "'";
                }
                // add inches
                if (feetInch[1].compareTo(BigDecimal.ZERO) != 0) {
                    feetInchOutput += " " + remove_trailing_zeros(feetInch[1]);
                    feetInchOutput += '"';
                }
                // add fractions
                if (fractionPart != null) {
                    feetInchOutput += fractionPart;
                }
                outputText.setText(feetInchOutput);
            }
            else {
                // output integer with fraction : no feet or inches
                String fractionOutput = "";
                if (integerPart.compareTo(BigDecimal.ZERO) != 0) {
                    fractionOutput += integerPart.toString();
                }
                if (fractionPart != null) {
                    fractionOutput += " " + fractionPart;
                }
                outputText.setText(fractionOutput);
            }
        }
        else {
            // output decimal number : no feet, inches, or fractions
            String outputString = remove_trailing_zeros(output);
            outputText.setText(outputString);
        }
    }
}