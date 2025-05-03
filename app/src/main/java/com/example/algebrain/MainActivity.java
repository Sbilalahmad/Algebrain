package com.example.algebrain;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
// Use TextView for display

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Import ViewBinding class
import com.example.algebrain.databinding.ActivityMainBinding;

// Import Math functions
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Declare ViewBinding variable
    private ActivityMainBinding binding;

    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '×';
    private static final char DIVISION = '÷';
    private static final char PERCENT = '%';
    private static final char EQU = '=';
    // Constants for other functions are handled directly in onClick

    private char currentAction = ' '; // Represents the pending operation

    private double valueOne = Double.NaN; // First operand or intermediate result
    private double valueTwo; // Second operand

    private DecimalFormat decimalFormat;

    // Define error string in strings.xml: <string name="calculator_error">Error</string>
    private String errorString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup decimal format for display
        decimalFormat = new DecimalFormat("#.##########");

        // Get error string resource
        errorString = getString(R.string.calculator_error);

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set onClick listeners for all buttons
        setupButtonClickListeners();

        // Initialize displays
        binding.calculator.setText("");
        binding.result.setText("0");
    }

    private void setupButtonClickListeners() {
        // Numbers
        binding.zero.setOnClickListener(this);
        binding.one.setOnClickListener(this);
        binding.two.setOnClickListener(this);
        binding.three.setOnClickListener(this);
        binding.four.setOnClickListener(this);
        binding.five.setOnClickListener(this);
        binding.six.setOnClickListener(this);
        binding.seven.setOnClickListener(this);
        binding.right.setOnClickListener(this); // Button 8
        binding.nine.setOnClickListener(this);
        binding.decimal.setOnClickListener(this);

        // Operators
        binding.add.setOnClickListener(this);
        binding.subtract.setOnClickListener(this);
        binding.multiply.setOnClickListener(this);
        binding.divide.setOnClickListener(this);
        binding.buttonPercentt.setOnClickListener(this);
        binding.equls.setOnClickListener(this);

        // Functions & Controls
        binding.sqrootX.setOnClickListener(this);
        binding.buttonPi.setOnClickListener(this);
        binding.buttonE.setOnClickListener(this);
        binding.buttonFactorial.setOnClickListener(this);
        binding.buttonAc.setOnClickListener(this);
        binding.buttonParentheses.setOnClickListener(this);
        binding.buttonDeleteOne.setOnClickListener(this);
    }

    // Performs the pending calculation
    private void computeCalculation() {
        // Ensure we have a first value and an action to perform
        if (Double.isNaN(valueOne) || currentAction == ' ' || currentAction == EQU) {
            // Nothing to compute if valueOne isn't set or no action is pending
            return;
        }

        String currentInput = binding.result.getText().toString();
        // Try to parse the second value from the current display
        try {
            // Avoid parsing if input is empty, just "-", or already an error
            if (currentInput.isEmpty() || currentInput.equals("-") || currentInput.equals(errorString)) {
                 // If the input is already an error, reset state but don't proceed
                 if (currentInput.equals(errorString)) {
                     clearAll();
                 }
                 return; // Don't compute if input is invalid/incomplete
            }
            valueTwo = Double.parseDouble(currentInput);
        } catch (NumberFormatException e) {
            // Failed to parse the second number
            binding.result.setText(errorString);
            binding.calculator.setText(""); // Clear top display on error
            valueOne = Double.NaN; // Reset state on error
            currentAction = ' ';
            valueTwo = Double.NaN;
            return;
        }

        // Perform the calculation based on currentAction
        double result = valueOne;
        boolean errorOccurred = false;

        switch (currentAction) {
            case ADDITION:
                result = valueOne + valueTwo;
                break;
            case SUBTRACTION:
                result = valueOne - valueTwo;
                break;
            case MULTIPLICATION:
                result = valueOne * valueTwo;
                break;
            case DIVISION:
                if (valueTwo == 0) {
                    errorOccurred = true; // Division by zero
                } else {
                    result = valueOne / valueTwo;
                }
                break;
            case PERCENT:
                // Assumes "X % Y" means "X * (Y / 100)"
                result = valueOne * (valueTwo / 100.0);
                break;
        }

        // Update displays and state
        if (errorOccurred || Double.isNaN(result) || Double.isInfinite(result)) {
            binding.result.setText(errorString);
            binding.calculator.setText("");
            valueOne = Double.NaN;
            currentAction = ' ';
        } else {
            valueOne = result; // Store result for chaining
            binding.result.setText(decimalFormat.format(valueOne));
            // Calculator display is updated elsewhere (e.g., on operator press or equals)
        }

        // Reset valueTwo after calculation
        valueTwo = Double.NaN;
        // currentAction is handled by the calling context (e.g., set to EQU or next operator)
    }

     // Clears the current entry display (like CE)
    private void clearEntry() {
        binding.result.setText("0");
        // Does not reset valueOne or currentAction
    }

    // Clears everything (like AC)
    private void clearAll() {
        valueOne = Double.NaN;
        valueTwo = Double.NaN;
        binding.result.setText("0");
        binding.calculator.setText("");
        currentAction = ' ';
    }

    // Deletes the last character from the result display
    private void deleteLastChar() {
        String currentText = binding.result.getText().toString();
        if (currentText.length() > 0 && !currentText.equals("0")) {
             // Handle case where text is just a single digit or "-"
             if (currentText.length() == 1 || (currentText.length() == 2 && currentText.startsWith("-"))) {
                 binding.result.setText("0");
             } else {
                 binding.result.setText(currentText.substring(0, currentText.length() - 1));
             }
        } else {
             binding.result.setText("0"); // Ensure it resets to 0 if empty or already 0
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        String currentInput = binding.result.getText().toString();
        String currentCalculation = binding.calculator.getText().toString();

        // Prevent input if result shows error, unless AC is pressed
        if (currentInput.equals(errorString) && id != R.id.button_ac) {
            return;
        }

        // --- Input Handling ---

        // Number Buttons (0-9)
        if (id == R.id.zero || id == R.id.one || id == R.id.two || id == R.id.three || id == R.id.four ||
            id == R.id.five || id == R.id.six || id == R.id.seven || id == R.id.right || id == R.id.nine) {
            String digit = ((Button) view).getText().toString();

            // Start new input if:
            // 1. Last action was '='
            // 2. Current display is "0"
            // 3. An operator is pending, and we haven't started typing the second number yet
            if (currentAction == EQU || currentInput.equals("0") || (!Double.isNaN(valueOne) && Double.isNaN(valueTwo) && currentAction != ' ')) {
                 binding.result.setText(digit);
                 if (currentAction == EQU) { // Reset state if starting new calculation after '='
                     binding.calculator.setText("");
                     valueOne = Double.NaN;
                     currentAction = ' ';
                 }
            } else {
                 // Append digit
                 binding.result.append(digit);
            }

        // Decimal Button
        } else if (id == R.id.decimal) {
            // Start fresh if last action was equals
            if (currentAction == EQU) {
                binding.result.setText("0.");
                binding.calculator.setText("");
                valueOne = Double.NaN;
                currentAction = ' ';
                return;
            }
            // Start fresh if operator pending and second number not started
            if (!Double.isNaN(valueOne) && Double.isNaN(valueTwo) && currentAction != ' ') {
                 binding.result.setText("0.");
                 return;
            }
             // Append decimal only if not already present
            if (!currentInput.contains(".")) {
                 if (currentInput.isEmpty() || currentInput.equals("0")) {
                     binding.result.setText("0.");
                 } else {
                     binding.result.append(".");
                 }
            }

        // --- Operation Handling ---

        // Operator Buttons (+, -, ×, ÷, %)
        } else if (id == R.id.add || id == R.id.subtract || id == R.id.multiply || id == R.id.divide || id == R.id.button_percentt) {
             // Chain calculation: If an operation is already pending, compute it first
             if (currentAction != ' ' && currentAction != EQU && !Double.isNaN(valueOne)) {
                 computeCalculation();
                 // Stop if the chained calculation resulted in an error
                 if (binding.result.getText().toString().equals(errorString)) {
                     return;
                 }
             }

             // Store the current number from result display as valueOne
             if (!binding.result.getText().toString().equals(errorString)) {
                 try {
                     valueOne = Double.parseDouble(binding.result.getText().toString());
                 } catch (NumberFormatException e) {
                     clearAll();
                     binding.result.setText(errorString);
                     return;
                 }
             } else {
                 // If display already shows error, just reset
                 clearAll();
                 return;
             }

            // Set the new action and update calculator display if valueOne is valid
            if (!Double.isNaN(valueOne)) {
                 currentAction = ((Button) view).getText().toString().charAt(0);
                 binding.calculator.setText(decimalFormat.format(valueOne) + " " + currentAction);
                 // Reset valueTwo, ready for next input
                 valueTwo = Double.NaN;
                 // Result display waits for the next number input
            } else {
                 // Fallback error case
                 clearAll();
                 binding.result.setText(errorString);
            }

        // Equals Button
        } else if (id == R.id.equls) {
            // Compute only if an action is pending and valueOne is valid
            if (!Double.isNaN(valueOne) && currentAction != ' ' && currentAction != EQU) {
                 String val2Str = binding.result.getText().toString();
                 // Update calculator display to show the full calculation before computing
                 if (!val2Str.isEmpty() && !val2Str.equals("-") && !val2Str.equals(errorString)) {
                     // Ensure the existing text doesn't already end with an operator space
                     String calcText = binding.calculator.getText().toString();
                     if (calcText.length() > 0 && !Character.isDigit(calcText.charAt(calcText.length()-1)) && calcText.charAt(calcText.length()-1) != ' ') {
                         binding.calculator.setText(calcText + " " + val2Str + " =");
                     } else {
                         // Handle cases where calcText might be empty or already formatted
                         binding.calculator.setText(decimalFormat.format(valueOne) + " " + currentAction + " " + val2Str + " =");
                     }
                 }
                 computeCalculation();
                 currentAction = EQU; // Mark equals as the last action
            }

        // --- Function & Control Handling ---

        // AC (All Clear) Button
        } else if (id == R.id.button_ac) {
            clearAll();

        // Delete Button (DEL)
        } else if (id == R.id.button_deleteOne) {
            // Allow deleting from the result even after '='
            if (currentAction == EQU) {
                currentAction = ' '; // Change state to allow editing
            }
            deleteLastChar();

        // Square Root Button (√)
        } else if (id == R.id.sqroot_x) {
             if (!currentInput.isEmpty() && !currentInput.equals(errorString)) {
                 try {
                     double currentValue = Double.parseDouble(currentInput);
                     String originalInputFormatted = decimalFormat.format(currentValue);
                     if (currentValue < 0) {
                         binding.result.setText(errorString);
                         binding.calculator.setText("sqrt(" + originalInputFormatted + ")");
                         valueOne = Double.NaN;
                     } else {
                         double result = Math.sqrt(currentValue);
                         binding.result.setText(decimalFormat.format(result));
                         valueOne = result; // Store result
                         binding.calculator.setText("sqrt(" + originalInputFormatted + ")");
                     }
                     currentAction = EQU; // Mark as calculation complete
                     valueTwo = Double.NaN;
                 } catch (NumberFormatException e) {
                     clearAll();
                     binding.result.setText(errorString);
                 }
             }

        // Pi Button (π)
        } else if (id == R.id.button_pi) {
            binding.result.setText(decimalFormat.format(Math.PI));
            binding.calculator.setText("π");
            valueOne = Math.PI;
            currentAction = EQU; // Treat as a completed value entry
            valueTwo = Double.NaN;

        // Euler's Number Button (e)
        } else if (id == R.id.button_e) {
            binding.result.setText(decimalFormat.format(Math.E));
            binding.calculator.setText("e");
            valueOne = Math.E;
            currentAction = EQU; // Treat as a completed value entry
            valueTwo = Double.NaN;

        // Factorial Button (!)
        } else if (id == R.id.button_factorial) {
            if (!currentInput.isEmpty() && !currentInput.equals(errorString)) {
                 try {
                     double currentValue = Double.parseDouble(currentInput);
                     String originalInputFormatted = decimalFormat.format(currentValue);
                     double result = factorial(currentValue); // Use helper

                     if (Double.isNaN(result) || Double.isInfinite(result)) {
                         binding.result.setText(errorString);
                         binding.calculator.setText(originalInputFormatted + "!");
                         valueOne = Double.NaN;
                     } else {
                         binding.result.setText(decimalFormat.format(result));
                         valueOne = result; // Store result
                         binding.calculator.setText(originalInputFormatted + "!");
                     }
                     currentAction = EQU; // Mark as calculation complete
                     valueTwo = Double.NaN;
                 } catch (NumberFormatException e) {
                     clearAll();
                     binding.result.setText(errorString);
                 }
             }
        // Removed the extra '}}' here

        // Parentheses Button (()) - Reverted to Placeholder
        } else if (id == R.id.button_parentheses) {
            // TODO: Implement parenthesis logic (requires expression parser)
            binding.calculator.setText("() Not Implemented");
            // Optionally clear state or show temporary message in result
            // clearAll(); // Example: could clear state if desired
            // binding.result.setText("Use AC"); // Example: could show message
        }
    }

    // Factorial helper function
    private double factorial(double num) {
        // ... existing code ...
        if (num < 0) {
            return Double.NaN; // Factorial not defined for negative numbers
        }
        if (num == 0 || num == 1) {
            return 1; // Base case
        }
        double result = 1;
        for (int i = 2; i <= num; i++) {
            result *= i;
        }
        return result;
    }
} // End of MainActivity class