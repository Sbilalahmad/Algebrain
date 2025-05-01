package com.example.algebrain;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Use TextView for display

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
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';
    private static final char PERCENT = '%';
    private static final char EQU = '=';

    private char currentAction;

    private double valueOne = Double.NaN;
    private double valueTwo;

    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the root view from binding

        // Setup decimal format
        decimalFormat = new DecimalFormat("#.##########"); // Format to avoid trailing zeros

        // Apply window insets using the root view from binding (assuming 'main' is the ID of the root ConstraintLayout)
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
        binding.right.setOnClickListener(this); // Assuming 'right' is button 8
        binding.nine.setOnClickListener(this);
        binding.decimal.setOnClickListener(this);

        // Operators
        binding.add.setOnClickListener(this);
        binding.subtract.setOnClickListener(this);
        binding.multiply.setOnClickListener(this);
        binding.divide.setOnClickListener(this);
        binding.equls.setOnClickListener(this); // Equals button

        // Functions
        binding.buttonPercentt.setOnClickListener(this); // Percent button
        binding.buttonClearAll.setOnClickListener(this); // CE button
        binding.buttonClear.setOnClickListener(this);    // C button
        binding.buttonDeleteOne.setOnClickListener(this); // DEL button
        binding.oneByX.setOnClickListener(this);         // 1/x button
        binding.sqOfX.setOnClickListener(this);          // x^2 button
        binding.sqrootX.setOnClickListener(this);        // sqrt(x) button
        binding.addSubtract.setOnClickListener(this);    // +/- button
    }

    private void computeCalculation() {
        if (!Double.isNaN(valueOne)) {
            boolean errorOccurred = false; // Flag to track errors during calculation
            try {
                String currentInput = binding.result.getText().toString();
                // Avoid calculation if input is empty, just "-", or already an error
                if (currentInput.isEmpty() || currentInput.equals("-") || currentInput.equals(getString(R.string.calculator_error))) {
                    if (currentInput.equals(getString(R.string.calculator_error))) {
                        // If the input is already an error, ensure state is reset
                        clearAll();
                    }
                    return; // Don't compute if input is invalid/incomplete
                }
                valueTwo = Double.parseDouble(currentInput);
            } catch (NumberFormatException e) {
                errorOccurred = true; // Mark error on parse failure
                valueOne = Double.NaN; // Ensure valueOne reflects error state
                // Let the block after the switch handle display
            }

            // Only proceed with calculation if no parse error occurred yet
            if (!errorOccurred) {
                switch (currentAction) {
                    case ADDITION:
                        valueOne = this.valueOne + valueTwo;
                        break;
                    case SUBTRACTION:
                        valueOne = this.valueOne - valueTwo;
                        break;
                    case MULTIPLICATION:
                        valueOne = this.valueOne * valueTwo;
                        break;
                    case DIVISION:
                        if (valueTwo == 0) {
                            valueOne = Double.NaN; // Indicate division by zero error
                            errorOccurred = true; // Mark error
                        } else {
                            valueOne = this.valueOne / valueTwo;
                        }
                        break;
                    case PERCENT:
                        // Assuming valueOne * (valueTwo / 100)
                        valueOne = this.valueOne * (valueTwo / 100.0);
                        break;
                    case EQU:
                        // This case shouldn't be reached if logic is correct
                        break;
                }
            }

            // Display result or error based on NaN or the error flag
            if (Double.isNaN(valueOne) || errorOccurred) {
                binding.result.setText(R.string.calculator_error);
                binding.calculator.setText(""); // Clear top display on error
                valueOne = Double.NaN; // Ensure valueOne is NaN in error state
                currentAction = EQU; // Reset action after error
            } else {
                binding.result.setText(decimalFormat.format(valueOne));
                // Optional: Update calculator display after successful calculation
                // binding.calculator.setText(decimalFormat.format(valueOne));
            }
            // Reset valueTwo after calculation attempt (success or failure)
            valueTwo = Double.NaN;

        } else {
            // Handle case where valueOne was initially NaN (first number entry)
            try {
                 String currentInput = binding.result.getText().toString();
                 // Only parse if input is valid
                 if (!currentInput.isEmpty() && !currentInput.equals("-") && !currentInput.equals(getString(R.string.calculator_error))) {
                    valueOne = Double.parseDouble(currentInput);
                 } else if (currentInput.equals(getString(R.string.calculator_error))) {
                     clearAll(); // Reset if starting from error
                 }
            } catch (NumberFormatException e) {
                 // Handle error if the first value is invalid
                 clearAll();
                 binding.result.setText(R.string.calculator_error);
            }
        }
    }

     // Clears the current entry (bottom display)
    private void clearEntry() {
        binding.result.setText("0");
        // Don't reset valueOne or currentAction here
    }

    // Clears everything (like C button)
    private void clearAll() {
        valueOne = Double.NaN;
        valueTwo = Double.NaN;
        binding.result.setText("0");
        binding.calculator.setText("");
        currentAction = EQU; // Reset action
    }

    // Deletes the last character (like DEL button)
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


    @Override
    public void onClick(View view) {
        int id = view.getId();
        String currentInput = binding.result.getText().toString();
        String currentCalculation = binding.calculator.getText().toString();

        // Number Buttons (0-9)
        if (id == R.id.zero || id == R.id.one || id == R.id.two || id == R.id.three || id == R.id.four ||
            id == R.id.five || id == R.id.six || id == R.id.seven || id == R.id.right || id == R.id.nine) {
            String digit = ((Button) view).getText().toString();

            // If current input is "0" or an error, replace it. Also replace after an operation.
            if (currentInput.equals("0") || currentInput.equals(getString(R.string.calculator_error)) || currentAction != EQU && Double.isNaN(valueTwo)) {
                 // Check if an operation is pending and we haven't started typing valueTwo
                 if (currentAction != EQU && Double.isNaN(valueTwo) && !Double.isNaN(valueOne)) {
                     binding.result.setText(digit); // Start new input for valueTwo
                     valueTwo = 0; // Mark that valueTwo input has started (use 0 temporarily)
                 } else {
                     binding.result.setText(digit); // Replace 0 or error
                 }

            } else {
                binding.result.append(digit);
            }
             // If equals was the last operation, pressing a number starts a new calculation
             if (currentAction == EQU && !Double.isNaN(valueOne)) {
                 //binding.calculator.setText(""); // Clear top display for new calculation
                 //valueOne = Double.NaN; // Reset valueOne to start fresh
                 //currentAction = ' '; // Reset action
             }


        // Decimal Button
        } else if (id == R.id.decimal) {
            if (!currentInput.contains(".")) {
                 // If starting fresh after operation or error
                 if (currentInput.equals("0") || currentInput.equals(getString(R.string.calculator_error)) || (currentAction != EQU && Double.isNaN(valueTwo) && !Double.isNaN(valueOne))) {
                     binding.result.setText("0.");
                     if (currentAction != EQU) valueTwo = 0; // Mark valueTwo input started
                 } else {
                     binding.result.append(".");
                 }
            }

        // Operator Buttons (+, -, *, /, %)
        } else if (id == R.id.add || id == R.id.subtract || id == R.id.multiply || id == R.id.divide || id == R.id.button_percentt) {
             boolean computed = false; // Flag if computation happened
             // If there's a pending operation and valueOne exists, try to compute first
             // Also ensure current input isn't empty/invalid for computation
             if (currentAction != EQU && !Double.isNaN(valueOne) && !currentInput.isEmpty() && !currentInput.equals("-") && !currentInput.equals(getString(R.string.calculator_error))) {
                 computeCalculation();
                 computed = true;
             }
             // If no computation happened (or it failed resulting in NaN valueOne),
             // try to store the current input as valueOne, unless it's an error.
             else if (!computed && !currentInput.equals(getString(R.string.calculator_error)) && !currentInput.isEmpty() && !currentInput.equals("-")) {
                 try {
                     valueOne = Double.parseDouble(currentInput);
                 } catch (NumberFormatException e) {
                     clearAll();
                     binding.result.setText(R.string.calculator_error);
                     return; // Exit onClick if parse fails
                 }
             }

            // Set the new action and update calculator display ONLY if valueOne is valid
            if (!Double.isNaN(valueOne)) {
                 currentAction = ((Button) view).getText().toString().charAt(0);
                 binding.calculator.setText(decimalFormat.format(valueOne) + " " + currentAction);
                 // Don't clear result here; let the next number press handle it
                 valueTwo = Double.NaN; // Reset valueTwo, ready for next input
            } else {
                 // If valueOne is still NaN (e.g., after an error in computeCalculation or parse error),
                 // ensure the calculator is in a clean error state.
                 if (!binding.result.getText().toString().equals(getString(R.string.calculator_error))) {
                    binding.result.setText(R.string.calculator_error); // Ensure error is shown
                 }
                 binding.calculator.setText(""); // Clear top display
                 currentAction = EQU; // Reset action
                 valueOne = Double.NaN; // Ensure valueOne is NaN
                 valueTwo = Double.NaN; // Ensure valueTwo is NaN
            }


        // Equals Button
        } else if (id == R.id.equls) {
            if (!Double.isNaN(valueOne) && currentAction != EQU) {
                 String val2Str = binding.result.getText().toString();
                 if (!val2Str.isEmpty() && !val2Str.equals("-")) {
                     // Update calculator display before computing
                     binding.calculator.setText(currentCalculation + " " + val2Str + " =");
                     computeCalculation();
                     currentAction = EQU; // Mark that equals was pressed
                     // valueOne now holds the result, ready for chaining
                 }
            }


        // Clear Entry Button (CE)
        } else if (id == R.id.button_clearAll) {
            clearEntry();

        // Clear All Button (C)
        } else if (id == R.id.button_clear) {
            clearAll();

        // Delete Button (DEL)
        } else if (id == R.id.button_deleteOne) {
            deleteLastChar();

        // +/- Button
        } else if (id == R.id.add_subtract) {
            if (!currentInput.equals("0") && !currentInput.isEmpty() && !currentInput.equals(getString(R.string.calculator_error))) {
                double currentValue = Double.parseDouble(currentInput);
                currentValue *= -1;
                binding.result.setText(decimalFormat.format(currentValue));
                 // If this is the first operand, update valueOne if it was set from this
                 // This logic might need refinement depending on when +/- is pressed
            } else if (currentInput.equals("0")) {
                 binding.result.setText("-"); // Allow starting input with minus
            } else if (currentInput.equals("-")) {
                 binding.result.setText("0"); // Toggle back from just minus
            }


        // 1/x Button
        } else if (id == R.id.one_by_x) {
            if (!currentInput.isEmpty() && !currentInput.equals("0") && !currentInput.equals(getString(R.string.calculator_error))) {
                try {
                    double currentValue = Double.parseDouble(currentInput);
                    if (currentValue == 0) {
                        binding.result.setText(R.string.calculator_error); // Division by zero
                        valueOne = Double.NaN; // Reset state on error
                        currentAction = EQU;
                    } else {
                        currentValue = 1 / currentValue;
                        binding.result.setText(decimalFormat.format(currentValue));
                        // Update valueOne if this was the result or the first operand
                        valueOne = currentValue;
                        binding.calculator.setText("1/(" + currentInput + ")"); // Show operation
                        currentAction = EQU; // Treat as calculation complete
                    }
                } catch (NumberFormatException e) {
                     binding.result.setText(R.string.calculator_error);
                     clearAll();
                }
            }

        // x^2 Button
        } else if (id == R.id.sq_of_x) {
             if (!currentInput.isEmpty() && !currentInput.equals(getString(R.string.calculator_error))) {
                 try {
                     double currentValue = Double.parseDouble(currentInput);
                     currentValue = currentValue * currentValue; // Or Math.pow(currentValue, 2)
                     binding.result.setText(decimalFormat.format(currentValue));
                     valueOne = currentValue; // Update valueOne
                     binding.calculator.setText("sqr(" + currentInput + ")"); // Show operation
                     currentAction = EQU; // Treat as calculation complete
                 } catch (NumberFormatException e) {
                     binding.result.setText(R.string.calculator_error);
                     clearAll();
                 }
             }

        // Square Root Button
        } else if (id == R.id.sqroot_x) {
             if (!currentInput.isEmpty() && !currentInput.equals(getString(R.string.calculator_error))) {
                 try {
                     double currentValue = Double.parseDouble(currentInput);
                     if (currentValue < 0) {
                         binding.result.setText(R.string.calculator_error); // Cannot sqrt negative
                         valueOne = Double.NaN;
                         currentAction = EQU;
                     } else {
                         currentValue = Math.sqrt(currentValue);
                         binding.result.setText(decimalFormat.format(currentValue));
                         valueOne = currentValue; // Update valueOne
                         binding.calculator.setText("sqrt(" + currentInput + ")"); // Show operation
                         currentAction = EQU; // Treat as calculation complete
                     }
                 } catch (NumberFormatException e) {
                     binding.result.setText(R.string.calculator_error);
                     clearAll();
                 }
             }
        }
    }
}