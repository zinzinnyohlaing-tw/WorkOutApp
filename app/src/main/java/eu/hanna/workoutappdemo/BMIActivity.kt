package eu.hanna.workoutappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_bmiactivity.*
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {
    // Added variables for Metric and Us untis views and a variable for displaying the current selected view
    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNITS_VIEW = "US_UNIT_VIEW"
    }
    private var currentVisibleView: String = METRIC_UNITS_VIEW // A variable to hold a value to make a selected view visible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmiactivity)

        setSupportActionBar(toolbar_bmi_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "CALCULATE BMI"
        toolbar_bmi_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        makeVisibleMetricUnitsView()

        rgUnits.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.rbMetricUnits) {
                makeVisibleMetricUnitsView()
            } else {
                makeVisibleUsUnitsView()
            }
        }

        btnCalculateUnits.setOnClickListener {
            if (currentVisibleView == METRIC_UNITS_VIEW) {
                if (validateMetricUnits()) {

                    // The weight value is converted to Float value
                    val weightValue : Float = etMetricUnitWeight.text.toString().toFloat()

                    // The height value is converted to Float value and divided by 100 to convert it to meter
                    val heightValue: Float = etMetricUnitHeight.text.toString().toFloat()/100

                    val bmi = weightValue / (heightValue * heightValue)
                    displayBMIResult(bmi)
                } else {
                    Toast.makeText(this,"Please enter valid value",Toast.LENGTH_SHORT).show()
                }
            } else {
                if (validateUsUnits()) {
                    val usUnitHeightValueFeet : String = etUsUnitHeightFeet.text.toString()
                    val usUnitHeightValueInche: String = etUsUnitHeightInch.text.toString()
                    val usUnitWeightValue: Float = etUsUnitWeight.text.toString().toFloat()
                    val heightValue = usUnitHeightValueInche.toFloat() + usUnitHeightValueFeet.toFloat() * 12
                    val bmi = 703 * (usUnitWeightValue / (heightValue * heightValue))
                    displayBMIResult(bmi)
                } else {
                    Toast.makeText(this,"Please enter valid values",Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    // Function is used to validate the input values for METRIC Units
    private fun validateMetricUnits() : Boolean {
        var isValid = true
        if (etMetricUnitWeight.text.toString().isEmpty()) {
            isValid = false
        } else if (etMetricUnitWeight.text.toString().isEmpty()) {
            isValid = false
        }
        return isValid
    }

    // Function is used to validate the input value for US Units
    private fun validateUsUnits() : Boolean {
        var isValid = true
        if (etUsUnitWeight.text.toString().isEmpty()) {
            isValid = false
        } else if (etUsUnitHeightFeet.text.toString().isEmpty()) {
            isValid = false
        } else if (etUsUnitHeightInch.text.toString().isEmpty()) {
            isValid = false
        }
        return isValid
    }

    // Function is used to display the result of Metric units
    private fun displayBMIResult (bmi:Float) {
        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very serverly underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"

        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <=0 ){
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (java.lang.Float.compare(bmi,25f) > 0 && java.lang.Float.compare(bmi,30f) <=0) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        tvYourBMI.visibility = View.VISIBLE
        tvBMIValue.visibility = View.VISIBLE
        tvBMIType.visibility = View.VISIBLE
        tvBMIDescription.visibility = View.VISIBLE
        llDiplayBMIResult.visibility = View.VISIBLE

        // This is used to round the result value to 2 decimal values after "."
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()


        tvBMIValue.text = bmiValue
        tvBMIType.text = bmiLabel
        tvBMIDescription.text = bmiDescription
    }

    // Making a function to make the Metric Units view visible and hide the US Units View
    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW // Current View is updated here
        llMetricUnitsView.visibility = View.VISIBLE // Metric Unit view is visible
        llUsUnitsView.visibility = View.GONE // US Unit View is hidden

        etMetricUnitHeight.text!!.clear() // height value is cleared if it is added
        etMetricUnitWeight.text!!.clear() // weight value is cleared if it is added

        tvYourBMI.visibility = View.INVISIBLE
        tvBMIValue.visibility = View.INVISIBLE
        tvBMIType.visibility = View.INVISIBLE
        tvBMIDescription.visibility = View.INVISIBLE

        llDiplayBMIResult.visibility = View.GONE
    }

    // Making a function to make the US Units view visible
    private fun makeVisibleUsUnitsView() {
        currentVisibleView = US_UNITS_VIEW // Current View is updated here.
        llMetricUnitsView.visibility = View.GONE // METRIC UNITS VIEW is hidden
        llUsUnitsView.visibility = View.VISIBLE // US UNITS VIEW is Visible

        etUsUnitWeight.text!!.clear() // weight value is cleared.
        etUsUnitHeightFeet.text!!.clear() // height feet value is cleared.
        etUsUnitHeightInch.text!!.clear() // height inch is cleared.

        tvYourBMI.visibility = View.INVISIBLE // Result is cleared and the labels are hidden
        tvBMIValue.visibility = View.INVISIBLE // Result is cleared and the labels are hidden
        tvBMIType.visibility = View.INVISIBLE // Result is cleared and the labels are hidden
        tvBMIDescription.visibility = View.INVISIBLE // Result is cleared and the labels are hidden

        llDiplayBMIResult.visibility = View.GONE
    }

}