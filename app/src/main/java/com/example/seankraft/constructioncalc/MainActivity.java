package com.example.seankraft.constructioncalc;

import java.util.ArrayList;
import java.math.*;
import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

//TODO oder of operations : multiply / divide  and  add / subtract should run left to right

public class MainActivity extends Activity {

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // store all values
        outState.putStringArrayList("opList", opList);
        outState.putString("inputString", inputString);
        outState.putString("numeratorInput", numeratorInput);
        outState.putString("inputNumber", inputNumber.toString());
        outState.putString("output", output.toString());
        outState.putBoolean("feetAdded", feetAdded);
        outState.putBoolean("inchAdded", inchAdded);
        outState.putBoolean("addingDenominator", addingDenominator);
        outState.putBoolean("addingNumerator", addingNumerator);
        outState.putString("units", units);
        TextView outputText = (TextView) findViewById(R.id.outputText);
        outState.putString("uiOutputText", outputText.getText().toString());
        TextView debugText = (TextView) findViewById(R.id.oplist);
        outState.putString("uiDebugText", debugText.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            // the application is being reloaded
            // Toast.makeText(this, savedInstanceState.getString("uiOutputText"), Toast.LENGTH_LONG).show();
            opList = savedInstanceState.getStringArrayList("opList");
            inputString = savedInstanceState.getString("inputString");
            numeratorInput = savedInstanceState.getString("numeratorInput");
            inputNumber = new BigDecimal(savedInstanceState.getString("inputNumber"));
            output = new BigDecimal(savedInstanceState.getString("output"));
            feetAdded = savedInstanceState.getBoolean("feetAdded");
            inchAdded = savedInstanceState.getBoolean("inchAdded");
            addingDenominator = savedInstanceState.getBoolean("addingDenominator");
            addingNumerator = savedInstanceState.getBoolean("addingNumerator");
            units = savedInstanceState.getString("units");
            TextView outputText = (TextView) findViewById(R.id.outputText);
            outputText.setText(savedInstanceState.getString("uiOutputText"));
            TextView debugText = (TextView) findViewById(R.id.oplist);
            debugText.setText(savedInstanceState.getString("uiDebugText"));
        }
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    ArrayList<String> opList = new ArrayList<>();
    String inputString = "";
    String numeratorInput = "";

    Boolean feetAdded = false;
    Boolean inchAdded = false;
    Boolean addingNumerator = false;  // tracks if a fraction numerator is being added
    Boolean addingDenominator = false;  // tracks if a fraction denonicator is being added
    String units = "decimal";

    BigDecimal inputNumber = new BigDecimal("0");  // for calculating feet / inches
    BigDecimal output = new BigDecimal("0");  // the output of the calc function

    public void clickConvert(View view) {
        final String[] convert_items = getResources().getStringArray(R.array.convert_types);
        // create pop up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.convert_title);
        builder.setItems(convert_items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                format_output(which);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void clear(View view) {
        TextView debugText = (TextView) findViewById(R.id.oplist);
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView units1 = (TextView) findViewById(R.id.outputUnits1);
        TextView outputInch = (TextView) findViewById(R.id.outputInch);
        TextView fractionNum = (TextView) findViewById(R.id.outputNumerator);
        TextView fractionDem = (TextView) findViewById(R.id.outputDenominator);
        TextView units2 = (TextView) findViewById(R.id.outputUnits2);

        // clear global variables
        opList.clear();
        inputString = "";
        numeratorInput = "";
        feetAdded = false;
        inchAdded = false;
        addingDenominator = false;
        addingNumerator = false;
        units = "decimal";
        inputNumber = BigDecimal.ZERO;
        output = BigDecimal.ZERO;

        // clear UI elements
        debugText.setText("");
        outputText.setText("0");
        units1.setText("");
        outputInch.setText("");
        fractionNum.setText("");
        fractionDem.setText("");
        units2.setText("");

        // reset colors
        fractionNum.setBackgroundResource(R.color.uiDefault);
        fractionDem.setBackgroundResource(R.color.uiDefault);
    }

    public void click_backspace(View view) {
        // remove the last character in the numInputString
        if (inputString.length() > 0) {
            inputString = inputString.substring(0, inputString.length() - 1);
        }
        else {
            inputString = "";
        }
        update_text_fields(view);
    }

    public void click_feet(View view) {
        TextView units1 = (TextView) findViewById(R.id.outputUnits1);
        if (!inputString.equals("") && !inputString.equals("-")) {
            feetAdded = true;
            units1.setText("ft");  // TODO string reference
            // multiply input times 12
            BigDecimal feet = new BigDecimal(inputString);
            inputNumber = feet.multiply(new BigDecimal("12")).add(inputNumber);
        }
        inputString = "";
    }

    public void click_inch(View view) {
        TextView units2 = (TextView) findViewById(R.id.outputUnits2);
        if (!inputString.equals("") && !inputString.equals("-")) {
            // get current number entry
            inchAdded = true;
            units2.setText("in");  // TODO string reference
            // add input value
            BigDecimal inch = new BigDecimal(inputString);
            inputNumber = inch.add(inputNumber);
        }
        inputString = "";
    }

    public void click_fraction(View view) {
        TextView fractionNum = (TextView) findViewById(R.id.outputNumerator);
        TextView fractionDem = (TextView) findViewById(R.id.outputDenominator);
        if (!addingNumerator && !addingDenominator) {
            inputString = "";
            // highlight the numerator input
            fractionNum.setBackgroundResource(R.color.uiHighlight);
            fractionNum.setText("  ");
            addingNumerator = true;
        }
        else if (addingNumerator && !addingDenominator) {
            // store the numerator string before clearing
            numeratorInput = inputString;
            inputString = "";
            // highlight the denominator for input
            fractionNum.setBackgroundResource(R.color.uiDefault);
            fractionDem.setBackgroundResource(R.color.uiHighlight);
            fractionDem.setText("  ");
            addingDenominator = true;
        }
    }

    public void click_number(View view) {
        Button numberButton = (Button) view;
        // add the number to the output string for the view
        inputString = inputString + numberButton.getText().toString();
        update_text_fields(view);
        // if the only entry in the opList is another number, clear: (starting a fresh calculation)
        if (opList.size() == 1) {
            String entry = opList.get(0);
            if (!entry.contains("x") && !entry.contains("÷") && !entry.contains("+") && !entry.contains("−")) {
                clear(view);
                update_text_fields(view);
            }
        }
    }

    public void click_negative(View view) {

        // add a negative if positive, remove the negative if negative
        if (inputString.startsWith("-")) {
            inputString = inputString.substring(1, inputString.length());
        }
        else {
            inputString = "-" + inputString;
        }
        update_text_fields(view);
    }

    public void click_operation(View view) {
        compile_input_number();
        // get the button text
        Button opButton = (Button) view;
        String buttonString = opButton.getText().toString();
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
        update_text_fields(view);
        // clear the variables for the next number entry
        clear_input(view);  // resets outputText and input values
    }

    public void clickCalculate(View view) {
        // add the current concatenated number to opList
        compile_input_number();
        // update the DEBUG window
        update_text_fields(view);
        // clear the last number input
        clear_input(view);
        // check for an invalid opList
        clean_op_list();
        // start calculate function
        calculate();
        // format the output
        int formatMode = -1;  // -1 means no predetermined format
        format_output(formatMode);
    }

    public void update_text_fields(View view) {
        TextView debugText = (TextView) findViewById(R.id.oplist);
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView outputInch = (TextView) findViewById(R.id.outputInch);
        TextView fractionNum = (TextView) findViewById(R.id.outputNumerator);
        TextView fractionDem = (TextView) findViewById(R.id.outputDenominator);

        if (!inputString.equals("") && !inputString.equals("-")) {
            if (addingDenominator) {
                fractionDem.setText(inputString);
            } else if (addingNumerator) {
                fractionNum.setText(inputString);
            } else if (inchAdded) {
                fractionNum.setText(inputString);
                addingNumerator = true;
            } else if (feetAdded) {
                outputInch.setText(inputString);
            } else {
                outputText.setText(inputString);
            }
        }

        // convert opList to a string and display in the debug textView
        String opListString = "";
        for (String op : opList) {
            opListString += op + " ";
        }
        debugText.setText(opListString);
    }

    public void compile_input_number() {
        // This function compiles any feet, inch, and fraction input into the opList
        if (addingDenominator) {
            BigDecimal numerator = new BigDecimal(numeratorInput);
            BigDecimal denominator = new BigDecimal(inputString);
            inputNumber = numerator.divide(denominator, 9, BigDecimal.ROUND_HALF_UP).add(inputNumber);
        }
        // if there is an inputNumber; add it to the opList
        if (inputNumber.compareTo(BigDecimal.ZERO) != 0) {
            opList.add(inputNumber.toString());
        }
        // otherwise add the current inputString to the opList
        else {
            if (!inputString.equals("") && !inputString.equals("-")) {
                opList.add(inputString);
            }
        }
    }

    public void clear_input(View view) {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView units1 = (TextView) findViewById(R.id.outputUnits1);
        TextView outputInch = (TextView) findViewById(R.id.outputInch);
        TextView fractionNum = (TextView) findViewById(R.id.outputNumerator);
        TextView fractionDem = (TextView) findViewById(R.id.outputDenominator);
        TextView units2 = (TextView) findViewById(R.id.outputUnits2);

        outputText.setText("");
        units1.setText("");
        outputInch.setText("");
        fractionNum.setText("");
        fractionDem.setText("");
        units2.setText("");

        inputString = "";
        numeratorInput = "";
        inputNumber = BigDecimal.ZERO;
        feetAdded = false;
        inchAdded = false;
        addingNumerator = false;
        addingDenominator = false;
    }

    public void clean_op_list() {
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
            // TODO handle divide by zero errors
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
    }

    public void format_output(int formatMode) {
        // This function checks the current 'units' value and writes the output to the UI
        // in the appropriate format. It also checks the current fraction resolution and
        // converts the decimal value to the correct fraction.

        // if no format mode is defined, check units to determine format mode
        if (formatMode == -1) {
            if (addingDenominator || feetAdded || inchAdded) {
                if (feetAdded || inchAdded) {
                    formatMode = 0;  // feet and inches mode
                }
                else {
                    formatMode = 3;  // fraction mode
                }
            }
            else {
                formatMode = 4;  // decimal mode
            }
        }

        if (formatMode == 0) {
            format_feet_and_inch();
        }
        else if (formatMode == 1) {
            format_feet_only();
        }
        else if (formatMode == 2) {
            format_inch_only();
        }
        else if (formatMode == 3) {
            format_fraction();
        }
        else if (formatMode == 4) {
            format_decimal();
        }
    }

    public String remove_trailing_zeros(BigDecimal input) {
        // TODO remove some trailing zeros (4.12030900000 becomes 4.120309)
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

    public String[] decimal_to_fraction(String decimalString) {
        // if the decimal part is > 0
        if (new BigDecimal(decimalString).compareTo(BigDecimal.ZERO) == 1) {  // after decimal > 0
            // create a denominator for the fraction
            BigDecimal numerator = new BigDecimal(decimalString);
            BigDecimal denominator = new BigDecimal("10").pow(decimalString.length());

            // find the fraction resolution from the app preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String fractionRes = sharedPref.getString("pref_fraction_res", "16");

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
                String[] outputString = {numerator.toString(), denominator.toString()};
                return outputString;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    public void format_feet_and_inch() {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView units1 = (TextView) findViewById(R.id.outputUnits1);
        TextView outputInch = (TextView) findViewById(R.id.outputInch);
        TextView fractionNum = (TextView) findViewById(R.id.outputNumerator);
        TextView fractionDem = (TextView) findViewById(R.id.outputDenominator);
        TextView units2 = (TextView) findViewById(R.id.outputUnits2);

        BigDecimal integerPart = output;
        String[] fractionPart = new String[2];

        // if there is a decimal point, break the output into parts
        String outputString = output.toString();
        if (outputString.contains(".")) {
            String[] parts = outputString.split(Pattern.quote("."));
            integerPart = new BigDecimal(parts[0]);
            fractionPart = decimal_to_fraction(parts[1]);
        }

        BigDecimal feetInch[] = integerPart.divideAndRemainder(new BigDecimal("12"));
        // add feet
        if (feetInch[0].compareTo(BigDecimal.ZERO) != 0) {
            outputText.setText(remove_trailing_zeros(feetInch[0]));
            units1.setText("ft");  // TODO string reference
        }
        // add inches
        if (feetInch[1].compareTo(BigDecimal.ZERO) != 0) {
            outputInch.setText(remove_trailing_zeros(feetInch[1]));
            units2.setText("in"); // TODO string reference
        }
        // add fractions
        if (fractionPart != null) {
            fractionNum.setText(fractionPart[0]);
            fractionDem.setText(fractionPart[1]);
            units2.setText("in"); // TODO string reference
        }
    }

    public void format_feet_only() {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        TextView fractionNum = (TextView) findViewById(R.id.outputNumerator);
        TextView fractionDem = (TextView) findViewById(R.id.outputDenominator);
        TextView units2 = (TextView) findViewById(R.id.outputUnits2);
        // find the decimal vs fraction boolean from the app preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean feetFractions = sharedPref.getBoolean("pref_feet_display", true);

        String feetOutput = "";
        String[] fractionPart = new String[2];
        BigDecimal feet = output.divide(new BigDecimal("12"), 9, BigDecimal.ROUND_HALF_UP);
        // if there is a decimal point, break the output into parts
        if (feetFractions) {
            String partsString = feet.toString();
            if (partsString.contains(".")) {
                String[] parts = partsString.split(Pattern.quote("."));
                feetOutput = parts[0];
                fractionPart = decimal_to_fraction(parts[1]);
            }
            else {
                feetOutput = feet.toString();
            }
            outputText.setText(feetOutput);
            // add fraction
            if (fractionPart != null) {
                fractionNum.setText(fractionPart[0]);
                fractionDem.setText(fractionPart[1]);
            }
            units2.setText("ft");  // TODO string reference
        }
        else {
            outputText.setText(remove_trailing_zeros(feet));
            units2.setText("ft");  // TODO string reference
        }
    }

    public void format_inch_only() {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        BigDecimal integerPart = output;
        String[] fractionPart = new String[2];
        // if there is a decimal point, break the output into parts
        String outputString = output.toString();
        if (outputString.contains(".")) {
            String[] parts = outputString.split(Pattern.quote("."));
            integerPart = new BigDecimal(parts[0]);
            fractionPart = decimal_to_fraction(parts[1]);
        }

        String inchOutput = "";
        // add inches
        if (integerPart.compareTo(BigDecimal.ZERO) != 0) {
            inchOutput += remove_trailing_zeros(integerPart);
        }
        // add fraction
        if (fractionPart != null) {
            inchOutput += " ";
            inchOutput += fractionPart;
        }
        inchOutput += '"';
        outputText.setText(inchOutput);
    }

    public void format_fraction() {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        BigDecimal integerPart = output;
        String[] fractionPart = new String[2];
        // if there is a decimal point, break the output into parts
        String outputString = output.toString();
        if (outputString.contains(".")) {
            String[] parts = outputString.split(Pattern.quote("."));
            integerPart = new BigDecimal(parts[0]);
            fractionPart = decimal_to_fraction(parts[1]);
        }

        String fractionOutput = "";
        if (integerPart.compareTo(BigDecimal.ZERO) != 0) {
            fractionOutput += integerPart.toString();
        }
        if (fractionPart != null) {
            fractionOutput += " " + fractionPart;
        }
        outputText.setText(fractionOutput);
    }

    public void format_decimal() {
        TextView outputText = (TextView) findViewById(R.id.outputText);
        String cleanedOutput = remove_trailing_zeros(output);
        outputText.setText(cleanedOutput);
    }
}