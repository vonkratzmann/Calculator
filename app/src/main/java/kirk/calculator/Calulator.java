package kirk.calculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.common.math.LongMath;

import java.math.RoundingMode;

public class Calulator extends AppCompatActivity {

    TextView textView;
    Display display;
    ProcessKeys processKeys;
    SharedPreferences sharedPreferences;

    Long accumulator;
    Long result;
    StringBuilder numberAsString;
    Boolean previousCalculation;
    Boolean accumulatorUsed;
    Boolean negativeNumber;
    Boolean deleteFlag;
    char operatorChar;
    char lastChar;

    String TAG = "Calculate";
    // get number of characters for the largest long number, ie (2 to the power of 63) -1
    final long l = Long.MAX_VALUE;
    // Set buffer size to take this number of characters minus 1 so we don't enter too large a number
    final int NUMBUFSIZE = String.valueOf(l).length()-1;
    final char NULLCHAR = '\0';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);
        //       Log.i(TAG, "onCreate: ");
        // Find the toolbar view inside the activit layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this activity
        // Check the toolbar exits in this activity
        if (toolbar != null)
            setSupportActionBar(toolbar);

        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        processKeys = new ProcessKeys();
        display = new Display();
        numberAsString = new StringBuilder(NUMBUFSIZE);

         // Check if instance has been saved
        if  (savedInstanceState != null) {
            // Yes restore state members from saved instance
            display.addString(savedInstanceState.getString(getString(   R.string.textView_Key)));
            accumulator         = savedInstanceState.getLong(getString( R.string.accumulator_Key));
            result              = savedInstanceState.getLong(getString( R.string.result_Key));
            numberAsString.delete(0, numberAsString.length());                                      // Clear string
            numberAsString.append(savedInstanceState.getString(getString( R.string.numberAsString_Key)));
            previousCalculation = savedInstanceState.getBoolean(getString(R.string.previousCalculation_Key));
            accumulatorUsed     = savedInstanceState.getBoolean(getString(R.string.accumulatorUsed_Key));
            negativeNumber      = savedInstanceState.getBoolean(getString(R.string.negativeNumber_Key));
            deleteFlag          = savedInstanceState.getBoolean(getString(R.string.deleteFlag_Key));
            operatorChar        = savedInstanceState.getChar(getString(   R.string.operatorChar_Key));
            lastChar            = savedInstanceState.getChar(getString(   R.string.lastChar_Key));
            
        // Check if values have been saved in preference file    
        } else if (sharedPreferences.getString(getString( R.string.textView_Key), null) != null) {
            // Yes restore the values
            display.addString(sharedPreferences.getString(getString( R.string.textView_Key), null));
            accumulator         = sharedPreferences.getLong(getString(R.string.accumulator_Key),0);
            result              = sharedPreferences.getLong(getString(   R.string.result_Key), 0);
            numberAsString.delete(0, numberAsString.length());                                      // Clear string
            numberAsString.append(sharedPreferences.getString(getString( R.string.numberAsString_Key), null));
            previousCalculation = sharedPreferences.getBoolean(getString(R.string.previousCalculation_Key), false);
            accumulatorUsed     = sharedPreferences.getBoolean(getString(R.string.accumulatorUsed_Key), false);
            negativeNumber      = sharedPreferences.getBoolean(getString(R.string.negativeNumber_Key), false);
            deleteFlag          = sharedPreferences.getBoolean(getString(R.string.deleteFlag_Key), false);
            operatorChar        = sharedPreferences.getString(getString( R.string.operatorChar_Key),null).charAt(0);
            lastChar            = sharedPreferences.getString(getString( R.string.lastChar_Key), null).charAt(0);
        } else {
            // No clear all the variables
            resetRegisters();
        }
    }

    @Override
    // Save values
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
//        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putString(getString( R.string.textView_Key), display.getString());
        savedInstanceState.putLong(getString(   R.string.accumulator_Key), accumulator);
        savedInstanceState.putLong(getString(   R.string.result_Key), result);
        savedInstanceState.putString(getString( R.string.numberAsString_Key), numberAsString.toString());
        savedInstanceState.putBoolean(getString(R.string.previousCalculation_Key), previousCalculation);
        savedInstanceState.putBoolean(getString(R.string.accumulatorUsed_Key), accumulatorUsed);
        savedInstanceState.putBoolean(getString(R.string.negativeNumber_Key), negativeNumber);
        savedInstanceState.putBoolean(getString(R.string.deleteFlag_Key), deleteFlag);
        savedInstanceState.putChar(getString(   R.string.operatorChar_Key), (char) operatorChar);
        savedInstanceState.putChar(getString(   R.string.lastChar_Key), (char) lastChar);
    }

    public void resetRegisters() {
        // Clear the variables
        accumulator         = 0L;
        result              = 0L;
        numberAsString.delete(0, numberAsString.length());      // Clear string
        previousCalculation = false;
        accumulatorUsed     = false;
        negativeNumber      = false;
        deleteFlag          = false;
        operatorChar        = NULLCHAR;
        lastChar            = NULLCHAR;
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; thi adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_calulator,menu);
        return true;
    }

    // Save values in preferences file so user can resume if application destroyed
    public void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(getString( R.string.textView_Key), display.getString());
        editor.putLong(getString(   R.string.accumulator_Key), accumulator);
        editor.putLong(getString(   R.string.result_Key), result);
        editor.putString(getString( R.string.numberAsString_Key), numberAsString.toString());
        editor.putBoolean(getString(R.string.previousCalculation_Key), previousCalculation);
        editor.putBoolean(getString(R.string.accumulatorUsed_Key), accumulatorUsed);
        editor.putBoolean(getString(R.string.negativeNumber_Key), negativeNumber);
        editor.putBoolean(getString(R.string.deleteFlag_Key), deleteFlag);
        editor.putString(getString( R.string.operatorChar_Key), Character.toString(operatorChar));
        editor.putString(getString( R.string.lastChar_Key), Character.toString(lastChar));
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.help:
                Intent helpScreen = new Intent(this, ShowHelp.class);
                startActivity(helpScreen);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendNumber0(View view) {
        // Called when user clicks the zero button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('0');
    }

    public void sendNumber1(View view) {
        // Called when user clicks the one button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('1');
    }

    public void sendNumber2(View view) {
        // Called when user clicks the two button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('2');
    }

    public void sendNumber3(View view) {
        // Called when user clicks the three button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('3');
    }

    public void sendNumber4(View view) {
        // Called when user clicks the four button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('4');
    }

    public void sendNumber5(View view) {
        // Called when user clicks the five button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('5');
    }

    public void sendNumber6(View view) {
        // Called when user clicks the six button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('6');
    }

    public void sendNumber7(View view) {
        // Called when user clicks the seven button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('7');
    }

    public void sendNumber8(View view) {
        // Called when user clicks the eight button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('8');
    }

    public void sendNumber9(View view) {
        // Called when user clicks the nine button
        // Check we have capacity in the number string buffer
        if (numberAsString.length() < NUMBUFSIZE)
            // Yes process the character, else ignore
            processKeys.number('9');
    }

    public void sendPlus(View view) {
        // Called when user clicks + operator
        processKeys.operator('+');
    }

    public void sendMinus(View view) {
        // Called when user clicks the - operator
        processKeys.operator('-');
    }

    public void sendMultiply(View view) {
        // Called when user clicks * operator
        processKeys.operator('*');
    }

    public void sendDivide(View view) {
        // Called when user clicks the / operator
        processKeys.operator('/');
    }

    public void sendEquals(View view) {
        // Called when user clicks the equals button
        processKeys.equals('=');
    }

    public void sendBack(View view) {
        // Called when user clicks the back button
        display.deleteLastChar();
    }

    public void sendClear(View view) {
        // Called when user clicks the clear button
        display.clear();
    }

    public class ProcessKeys {
        // process the keystrokes for the calculator

        public ProcessKeys() {
        }

        public void number(char c) {
            // handles if a number key is pressed
            // For each character that is not a delete character reset deleteFlag
            if (previousCalculation) {
                display.clear();                        // Clear as do not want to use the previous result
                resetRegisters();
            }
            deleteFlag = false;                          // Flag it is not a delete char
            display.addChar(c);  {                       // Display new character
                numberAsString.append(c);                   // Add to the current numbers entered
                lastChar = c;                               // Keep last char in case we have to backspac
            }
        }

        public void operator(char c) {
            // handles if an operator key is pressed
            // For each character that is not a delete character reset deleteFlag
            deleteFlag = false;                              // Flag it is not a delete char

            // Special case for a '-'; 2 scenarios, before check for normal processing

            // A user is about to enter a number on a blank screen, the'-' makes the next number negative
            // That is no number has been entered, and no previous calculation, and no previous numbered entered
            if (c == '-' && numberAsString.length() == 0 && !previousCalculation &&  !accumulatorUsed) {
                negativeNumber = true;                      // Make next number negative
                display.addChar(c);                         // Display new operator character
                // Do not update operatorChar
                lastChar = c;                               // Keep last char in case we have to backspace
                return;
            }
            // Operation is to be on a negative number about to be entered
            // Require a previous operator, no number being entered in numberAsString and can only do this once
            if ( c == '-' && operatorChar != NULLCHAR && numberAsString.length() == 0 && !negativeNumber)  {
                negativeNumber = true;                      // Make next number negative
                display.addChar(c);                         // Display new operator character
                // Do not update operatorChar
                lastChar = c;                                    // Keep last char in case we have to backspace
                return;
            }

            // Only process if no previous operator has been entered
            if (operatorChar != NULLCHAR)
                // Previous character was an operator, so ignore this character by returning
                return;
            if (numberAsString.length() != 0) {             // If a number has been entered
                operatorChar = c;                           // Store the operator for later use
                display.addChar(c);                         // Display new operator character
                accumulator = Long.valueOf(numberAsString.toString()); // Store completed number in accumulator for later processing
                if (negativeNumber) {
                    accumulator *= -1;                      // Make it negative
                    negativeNumber = false;
                }
                accumulatorUsed = true;                     // Say accumulator in use
                numberAsString.delete(0, numberAsString.length()); // Clear number, ready for the next number

            } else {                                        // No numbered entered
                if (previousCalculation) {                  // If previous calculation has been just carried out,
                    operatorChar = c;                       // Store the operator for operation on the previous result
                    display.addChar(c);                     // Display new operator character
                    previousCalculation = false;
                }
            }
            lastChar = c;                                    // Keep last char in case we have to backspace
        }

        public void equals(char c) {
            // handles if equal  key is pressed

            // Only process if two numbers and a valid operator present?
            // That is accumulator in use, number in numberAsString, and operatorChar has an operator
            if (!accumulatorUsed || (numberAsString.length() == 0) || (operatorChar == NULLCHAR))
                return;
            display.addChar(c);                                       // yes, Display the equals Character
            Long i = Long.valueOf(numberAsString.toString());         // Get the number entered by the user
            // Check if a '-' was entered before this number
            if (negativeNumber) {
                i *= -1;                                              // Make it negative
                negativeNumber = false;
            }
            numberAsString.delete(0, numberAsString.length());        // Clear ready for the next number to be entered
            try {
                switch (operatorChar) {
                    case '+':
                        result = LongMath.checkedAdd(accumulator, i);
                        display.addString(result.toString());           // Display the result
                        previousCalculation = true;                     // Set in case we have to an operation on this result
                        break;
                    case '-':
                        result = LongMath.checkedSubtract(accumulator, i);
                        display.addString(result.toString());           // Display the result
                        previousCalculation = true;                     // Set in case we have to an operation on this result
                        break;
                    case '*':
                        result = LongMath.checkedMultiply(accumulator, i);
                        display.addString(result.toString());           // Display the result
                        previousCalculation = true;                     // Set in case we have to an operation on this result
                        break;
                    case '/':
                        result = LongMath.divide(accumulator, i, RoundingMode.HALF_DOWN);
                        display.addString(result.toString());       // Display the result
                        previousCalculation = true;                 // Set in case we have to an operation on this result
                        break;
                }
                accumulator = result;                               // Save in case want to do next operation previous result
                operatorChar = NULLCHAR;                            // Clear the operator
            }
            catch(ArithmeticException e) {
                display.addString(e.getMessage() + " error" + " ");                  // Put up the error message
                resetRegisters();
            }
        }
    }

    public class Display {
        // manages the calculator display

        public Display() {
        // ensure display is cleared at start
            textView = (TextView) findViewById(R.id.textView);
            textView.setText("");
        }

        public void addChar (char c) {
            // add a new character to the display
            textView.setText(textView.getText() + Character.toString(c));
        }

        public void clear () {
            // clears the display and variables
            textView.setText("");
            resetRegisters();
        }

        public void deleteLastChar() {
            // delete last character on the display
            // ignore if the string is null
            // Only allow deleting of one character

            if (previousCalculation || deleteFlag)
                // Don't backspace over a previous result, do not want to change a previous calculation
                return;
            String s = textView.getText().toString();
            deleteFlag = true;                                          // Only allow one delete, so set flag to prevent two deletes
            // Check there is something to delete in the display
            if (s.length() <= 0)
                // Nothing on the dipslay to delete, so return
                return;
             // Delete last char from the display
            textView.setText(s.substring(0, s.length() - 1));

            // Check if negative number set, if so last char was a minus
            if (negativeNumber) {
                negativeNumber = false;
            } else if (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/')
                operatorChar = NULLCHAR;                                    // As this was an operator clear the operator
            else
                numberAsString.deleteCharAt(numberAsString.length() - 1); // Must be a number remove last char from number
        }

        public void displayString(String s) {
            // Display the new string
            textView.setText(s);
        }

        public void addString (String s) {
            // Adds new string and displays the result
            textView.setText(textView.getText() + s);
        }

        public String getString () {
            // Get string that is displayed
            return (String) textView.getText();
        }
    }
}
