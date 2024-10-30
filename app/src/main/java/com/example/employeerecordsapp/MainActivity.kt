

package com.example.employeerecordsapp


import com.google.firebase.FirebaseApp // Import FirebaseApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var rvEmployees: RecyclerView
    private lateinit var employeeAdapter: EmployeeAdapter
    private var employeeList: MutableList<Employee> = mutableListOf()
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        FirebaseApp.initializeApp(this)
        // Initialize FirebaseRepository and link UI elements
        firebaseRepository = FirebaseRepository() // FirebaseRepository instance
        rvEmployees = findViewById(R.id.rvEmployees)
        searchView = findViewById(R.id.searchView)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnView = findViewById<Button>(R.id.btnView)

        // Set up RecyclerView with adapter
        rvEmployees.layoutManager = LinearLayoutManager(this)
        employeeAdapter = EmployeeAdapter(this, employeeList, firebaseRepository) { loadEmployees() } // Pass firebaseRepository here
        rvEmployees.adapter = employeeAdapter

        // Set listeners for buttons
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddEmployeeActivity::class.java))
        }
        btnView.setOnClickListener { loadEmployees() }

        // Initialize search functionality
        setupSearchView()

        // Test Firebase connection
        testFirebaseConnection()
    }

    private fun testFirebaseConnection() {
        FirebaseDatabase.getInstance().reference.child("test_linked").setValue("Linked to Firebase")
            .addOnSuccessListener {
                Log.d("FirebaseTest", "Successfully connected to Firebase.")
                Toast.makeText(this, "Connected to Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseTest", "Firebase connection failed: ${e.message}", e)
                Toast.makeText(this, "Failed to connect to Firebase", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadEmployees() {
        firebaseRepository.getAllEmployees(
            onSuccess = { employees ->
                employeeList.clear()
                employeeList.addAll(employees)
                employeeAdapter.updateList(employeeList)
                if (employeeList.isEmpty()) {
                    Toast.makeText(this, "No employees found", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { e ->
                Toast.makeText(this, "Failed to load employees: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchEmployees(query)
                } else {
                    loadEmployees()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    searchEmployees(newText)
                } else {
                    loadEmployees()
                }
                return true
            }
        })
    }

    private fun searchEmployees(query: String) {
        val filteredList = employeeList.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.designation.contains(query, ignoreCase = true)
        }
        employeeAdapter.updateList(filteredList)
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No employees match the search criteria", Toast.LENGTH_SHORT).show()
        }
    }
}
