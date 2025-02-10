package com.example.kotlinflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.kotlinflow.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: UserViewModel by viewModels() // Initialize ViewModel
    private lateinit var userAdapter: UserAdapter
    private lateinit var channelFlowAdapter: ChannelFlowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Setup RecyclerView
        userAdapter = UserAdapter(emptyList()) // Initially empty
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = userAdapter


        // 🔹 1. Collecting Flow Data (Fetching User List)
        lifecycleScope.launch {
            viewModel.fetchUsers().collect { users ->
                userAdapter.updateData(users) // Update RecyclerView with users
            }
        }

        // 🔹 2. Collecting StateFlow Data (Observing Counter)
        lifecycleScope.launch {
            viewModel.count.collect { value ->
                binding.txtCounter.text = "Count: $value"
            }
        }

        // 🔹 3. Increment Count on Button Click
        binding.btnIncrement.setOnClickListener {
            viewModel.incrementCount()
            viewModel.triggerEvent("Count increased to ${viewModel.count.value}")
        }

        // 🔹 4. Collecting SharedFlow Data (One-time UI Events)
        lifecycleScope.launch {
            viewModel.eventFlow.collect { message ->
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        // 🔹 Collecting ChannelFlow Data

        channelFlowAdapter = ChannelFlowAdapter(emptyList()) // Initially empty
        binding.rvChannelFlow.layoutManager = LinearLayoutManager(this)
        binding.rvChannelFlow.adapter = channelFlowAdapter

        lifecycleScope.launch {
            viewModel.userChannelFlow.collect { updatedUsers ->
                channelFlowAdapter.updateData(updatedUsers)
                Toast.makeText(this@MainActivity, "User list updated!", Toast.LENGTH_SHORT).show()
            }
        }

    }

}


data class User(val id: Int, val name: String)


class UserAdapter(private var userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUserName: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.txtUserName.text = userList[position].name
    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newUsers: List<User>) {
        userList = newUsers
        notifyDataSetChanged()
    }
}


class ChannelFlowAdapter(private var userList: List<User>) : RecyclerView.Adapter<ChannelFlowAdapter.ChannelFlowAdapter>() {

    class ChannelFlowAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUserName: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelFlowAdapter {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ChannelFlowAdapter(view)
    }

    override fun onBindViewHolder(holder: ChannelFlowAdapter, position: Int) {
        holder.txtUserName.text = userList[position].name
    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newUsers: List<User>) {
        userList = newUsers
        notifyDataSetChanged()
    }
}
