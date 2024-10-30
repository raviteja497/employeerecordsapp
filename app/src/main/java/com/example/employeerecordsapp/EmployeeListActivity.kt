package com.example.employeerecordsapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmployeeListActivity : AppCompatActivity() {

    private lateinit var rvEmployeeList: RecyclerView
    private lateinit var employeeAdapter: EmployeeAdapter
    private var employeeList: MutableList<Employee> = mutableListOf()
    private val firebaseRepository = FirebaseRepository() // Initialize FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_list)

        rvEmployeeList = findViewById(R.id.rvEmployeeList)
        rvEmployeeList.layoutManager = LinearLayoutManager(this)

        // Initialize EmployeeAdapter with the correct parameters
        employeeAdapter = EmployeeAdapter(this, employeeList, firebaseRepository) {
            loadEmployeesFromFirebase() // Callback to refresh data if needed
        }
        rvEmployeeList.adapter = employeeAdapter

        // Load employees from Firebase
        loadEmployeesFromFirebase()
    }

    private fun loadEmployeesFromFirebase() {
        val employeeRef = FirebaseDatabase.getInstance().getReference("employees")

        employeeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                employeeList.clear()
                for (employeeSnapshot in snapshot.children) {
                    val employee = employeeSnapshot.getValue(Employee::class.java)
                    employee?.let { employeeList.add(it) }
                }
                employeeAdapter.updateList(employeeList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EmployeeListActivity", "Failed to load employees: ${error.message}")
                Toast.makeText(this@EmployeeListActivity, "Failed to load employees", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
