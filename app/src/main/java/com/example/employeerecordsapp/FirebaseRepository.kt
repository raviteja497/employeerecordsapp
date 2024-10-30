package com.example.employeerecordsapp

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseRepository {

    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("employees")

    // Add an employee
    fun addEmployee(
        employee: Employee,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("FirebaseRepository", "Attempting to add employee to Realtime Database")

        val newEmployeeRef = db.push() // Create a unique ID for each employee
        employee.id = newEmployeeRef.key ?: "" // Assign generated ID to the employee if your Employee class has an `id` field

        newEmployeeRef.setValue(employee)
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "Employee added successfully to Realtime Database: ${employee.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error adding employee to Realtime Database: ${e.message}", e)
                onFailure(e)
            }
    }

    // Get all employees
    fun getAllEmployees(
        onSuccess: (List<Employee>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.get()
            .addOnSuccessListener { snapshot ->
                val employees = snapshot.children.mapNotNull { it.getValue(Employee::class.java) }
                onSuccess(employees)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error fetching employees from Realtime Database", e)
                onFailure(e)
            }
    }

    // Update an employee
    fun updateEmployee(
        employee: Employee,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (employee.id.isNotEmpty()) {
            db.child(employee.id).setValue(employee)
                .addOnSuccessListener {
                    Log.d("FirebaseRepository", "Employee updated successfully in Realtime Database: ${employee.id}")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseRepository", "Error updating employee in Realtime Database", e)
                    onFailure(e)
                }
        } else {
            Log.e("FirebaseRepository", "Invalid employee ID for update")
            onFailure(Exception("Invalid employee ID"))
        }
    }

    // Delete an employee
    fun deleteEmployee(
        employeeId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.child(employeeId).removeValue()
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "Employee deleted successfully from Realtime Database: $employeeId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error deleting employee from Realtime Database", e)
                onFailure(e)
            }
    }
}
