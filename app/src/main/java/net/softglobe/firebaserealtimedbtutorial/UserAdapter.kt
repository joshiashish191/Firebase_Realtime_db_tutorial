package net.softglobe.firebaserealtimedbtutorial

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import net.softglobe.firebaserealtimedbtutorial.databinding.UserLayoutBinding

class UserAdapter(val databaseReference: DatabaseReference) : ListAdapter<User, UserAdapter.MyViewHolder>(DiffUser()) {
    lateinit var binding : UserLayoutBinding
    lateinit var context: Context
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(user: User) {
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.user_layout, parent, false)
        context = parent.context
        return MyViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
        binding.btnEdit.setOnClickListener {
            val dialog = LayoutInflater.from(context).inflate(R.layout.user_edit_dialog, null)
            val editDialog = AlertDialog.Builder(context).setView(dialog).create()

            val name = dialog.findViewById<EditText>(R.id.et_edit_name)
            val email = dialog.findViewById<EditText>(R.id.et_edit_email)
            name.setText(user.name)
            email.setText(user.email)
            dialog.findViewById<Button>(R.id.btn_edit_submit).setOnClickListener {
                val userMap = mapOf(
                    "name" to name.text.toString(),
                    "email" to email.text.toString()
                )
                databaseReference.child(user.id).updateChildren(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                        editDialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to update User", Toast.LENGTH_SHORT).show()
                        editDialog.dismiss()
                    }
            }
            editDialog.show()


        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Do you really want to delete this user?")
                .setPositiveButton("Yes") { p0, p1 ->
                    databaseReference.child(user.id).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancel", ) { p0, p1 ->

                }
                    .show()
        }
    }

    class DiffUser : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

}