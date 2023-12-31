package com.mobiledevelopment.task_manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobiledevelopment.task_manager.R
import com.mobiledevelopment.task_manager.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        displayUserData()
        displayTaskCount()

        binding.numberOfTasks.setOnClickListener {
            navigateToTaskListFragment()
        }
    }

    // Get instance from database
    // Uses navigation controller
    private fun init(view: View) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Tasks")
        navController = Navigation.findNavController(view)
    }

    // This is just displaying the user's data
    private fun displayUserData() {
        val currentUser = auth.currentUser
        currentUser?.let {
            // Display user email
            binding.userName.text = it.email
        }
    }


    private fun displayTaskCount() {
        val currentUser = auth.currentUser
        currentUser?.let {
            // Get the user's UID
            val userId = it.uid

            // Query tasks for the current user
            database.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Get the count of tasks
                        val taskCount = snapshot.childrenCount.toInt()

                        // Display the task count
                        binding.numberOfTasks.text = "$taskCount TASKs PENDING"
                    }

                    // Display error message when faced errors
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Database Error", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    // Navigates to task list fragment
    fun navigateToTaskListFragment() {
        navController.navigate(R.id.action_profileFragment_to_taskListFragment)
    }


}
