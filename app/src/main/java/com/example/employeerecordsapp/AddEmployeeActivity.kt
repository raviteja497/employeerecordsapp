package com.example.employeerecordsapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEmployeeActivity : AppCompatActivity() {

    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_employee)

        // Initialize FirebaseRepository instance
        firebaseRepository = FirebaseRepository()

        // Link UI elements
        val etName = findViewById<EditText>(R.id.etName)
        val etDesignation = findViewById<EditText>(R.id.etDesignation)
        val etSalary = findViewById<EditText>(R.id.etSalary)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val designation = etDesignation.text.toString().trim()
            val salary = etSalary.text.toString().toDoubleOrNull()

            // Add logging to confirm the button was clicked
            Log.d("AddEmployeeActivity", "Save button clicked")

            if (name.isNotEmpty() && designation.isNotEmpty() && salary != null) {
                val newEmployee = Employee(name = name, designation = designation, salary = salary)

                firebaseRepository.addEmployee(
                    employee = newEmployee,
                    onSuccess = {
                        Log.d("AddEmployeeActivity", "Employee added successfully to Firebase")
                        Toast.makeText(this, "Employee Added to Firebase", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onFailure = { e ->
                        Log.e("AddEmployeeActivity", "Error adding employee: ${e.message}", e)
                        Toast.makeText(this, "Failed to Add Employee: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Log.e("AddEmployeeActivity", "Validation failed - fields are empty or invalid")
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
