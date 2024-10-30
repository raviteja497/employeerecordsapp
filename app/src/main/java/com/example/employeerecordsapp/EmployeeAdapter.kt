package com.example.employeerecordsapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class EmployeeAdapter(
    private val context: Context,
    private var employeeList: MutableList<Employee>,
    private val firebaseRepository: FirebaseRepository, // Injected repository instance
    private val refreshData: () -> Unit // Callback for refreshing data after update or delete
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    // ViewHolder class for each Employee Item
    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDesignation: TextView = itemView.findViewById(R.id.tvDesignation)
        val tvSalary: TextView = itemView.findViewById(R.id.tvSalary)
        val btnUpdate: Button = itemView.findViewById(R.id.btnUpdate)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun getItemCount(): Int = employeeList.size

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.tvName.text = "Name: ${employee.name}"
        holder.tvDesignation.text = "Designation: ${employee.designation}"
        holder.tvSalary.text = "Salary: \$${employee.salary}"

        holder.btnUpdate.setOnClickListener {
            showUpdateDialog(employee)
        }

        holder.btnDelete.setOnClickListener {
            showDeleteConfirmation(employee)
        }
    }

    // Show dialog for updating an employee
    private fun showUpdateDialog(employee: Employee) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update Employee")

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_update_employee, null)
        builder.setView(view)

        val etName = view.findViewById<EditText>(R.id.etUpdateName)
        val etDesignation = view.findViewById<EditText>(R.id.etUpdateDesignation)
        val etSalary = view.findViewById<EditText>(R.id.etUpdateSalary)

        etName.setText(employee.name)
        etDesignation.setText(employee.designation)
        etSalary.setText(employee.salary.toString())

        builder.setPositiveButton("Update") { dialog, _ ->
            val updatedName = etName.text.toString()
            val updatedDesignation = etDesignation.text.toString()
            val updatedSalary = etSalary.text.toString().toDoubleOrNull()

            if (updatedName.isNotEmpty() && updatedDesignation.isNotEmpty() && updatedSalary != null) {
                val updatedEmployee = employee.copy(
                    name = updatedName,
                    designation = updatedDesignation,
                    salary = updatedSalary
                )
                // Update employee in Firebase
                firebaseRepository.updateEmployee(
                    updatedEmployee,
                    onSuccess = {
                        Toast.makeText(context, "Employee Updated", Toast.LENGTH_SHORT).show()
                        refreshData()
                    },
                    onFailure = {
                        Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    // Show confirmation dialog for deleting an employee
    private fun showDeleteConfirmation(employee: Employee) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Employee")
        builder.setMessage("Are you sure you want to delete ${employee.name}?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            employee.id?.let {
                firebaseRepository.deleteEmployee(
                    it,
                    onSuccess = {
                        Toast.makeText(context, "Employee Deleted", Toast.LENGTH_SHORT).show()
                        refreshData()
                    },
                    onFailure = {
                        Toast.makeText(context, "Deletion Failed", Toast.LENGTH_SHORT).show()
                    }
                )
            } ?: Toast.makeText(context, "Error: Employee ID is missing", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    // Function to update the list and refresh RecyclerView
    fun updateList(newList: List<Employee>) {
        employeeList = newList.toMutableList()
        notifyDataSetChanged()
    }
}
