package net.softglobe.firebaserealtimedbtutorial

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.softglobe.firebaserealtimedbtutorial.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var databaseReference : DatabaseReference
    val usersList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_main)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        binding.btnAdd.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()

            val userId = databaseReference.push().key

            if (userId != null) {
                val user = User(userId, name, email)
                databaseReference.child(userId).setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    user?.let { usersList.add(it) }
                }

                binding.rvUsers.apply {
                    adapter = UserAdapter(databaseReference)
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    (adapter as UserAdapter).submitList(usersList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })


    }
}