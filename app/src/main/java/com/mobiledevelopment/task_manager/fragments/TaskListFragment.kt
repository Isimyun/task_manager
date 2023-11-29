package com.mobiledevelopment.task_manager.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobiledevelopment.task_manager.databinding.FragmentTaskListBinding
import com.mobiledevelopment.task_manager.utils.adapter.TaskAdapter
import com.mobiledevelopment.task_manager.utils.model.TaskData

class TaskListFragment : Fragment(), AddTaskPopUpFragment.DialogNextBtnClickListener,
    TaskAdapter.TaskAdapterClicksInterface {
    private lateinit var auth : FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var navController : NavController
    private lateinit var binding : FragmentTaskListBinding
    private lateinit var popUpFragment : AddTaskPopUpFragment
    private lateinit var adapter : TaskAdapter
    private lateinit var list : MutableList<TaskData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun registerEvents() {
        binding.addTaskBtn.setOnClickListener {
            popUpFragment = AddTaskPopUpFragment()
            popUpFragment.setListener(this)
            popUpFragment.show(
                childFragmentManager,
                "AddTaskPopUpFragment"
            )
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks").child(auth.currentUser?.uid.toString())

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        list = mutableListOf()
        adapter = TaskAdapter(list)
        adapter.setListener(this)
        binding.mainRecyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.key?.let {
                        TaskData(it, taskSnapshot.value.toString())
                    }

                    if (task != null) {
                        list.add(task)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "An error occurred!", Toast.LENGTH_SHORT).show()
            }

        })
    }


    override fun onSaveTask(task: String, etAddTask: TextInputEditText) {
        databaseRef.push().setValue(task).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Task added successfully!", Toast.LENGTH_SHORT).show()
                etAddTask.text = null
            }
            else {
                Toast.makeText(context, "Failed to add task", Toast.LENGTH_SHORT).show()
            }

            popUpFragment.dismiss()
        }
    }

    override fun onDeleteTaskBtnClicked(taskData: TaskData) {
        databaseRef.child(taskData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "An error occurred!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(taskData: TaskData) {
        TODO("Not yet implemented")
    }
}