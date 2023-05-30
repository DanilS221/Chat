package com.example.tailorqvest

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import com.example.tailorqvest.databinding.ActivitySetupProfileBinding
import com.example.tailorqvest.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class SetupProfileActivity : AppCompatActivity() {

    var binding: ActivitySetupProfileBinding? = null
    var auth:FirebaseAuth? =null
    var database: FirebaseDatabase? = null
    var storage : FirebaseStorage? = null
    var selectedImage: Uri? = null
    var dialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Загрузка...")
        dialog!!.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        binding!!.imageView.setOnClickListener {
            val intent = Intent()
            intent.action =Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)

        }
        binding!!.continueBtn02.setOnClickListener {
            val name : String = binding!!.nameBox.text.toString()
            if (name.isEmpty())
            {
                binding!!.nameBox.setError("Вы не ввели имя")

            }
            dialog!!.show()
            if (selectedImage!=null)
            {
                val reference = storage!!.reference.child("Profile")
                    .child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        reference.downloadUrl.addOnCompleteListener {url->
                            val imageUrl = url.toString()
                            val uid = auth!!.uid
                            val phone = auth!!.currentUser!!.phoneNumber
                            val name :String = binding!!.nameBox.text.toString()
                            val user = User(uid,name,phone,imageUrl)
                            database!!.reference
                                .child("users")
                                .child(uid!!)
                                .setValue(user)
                                .addOnCompleteListener {
                                    dialog!!.dismiss()
                                    val  intent = Intent(this@SetupProfileActivity,MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                        }

                    }
                    else{
                        val uid = auth!!.uid
                        val  phone = auth!!.currentUser!!.phoneNumber
                        val name :String = binding!!.nameBox.text.toString()
                        val  user = User(uid,name,phone,"No Image")
                        database!!.reference
                            .child("users")
                            .child(uid!!)
                            .setValue(user)
                            .addOnCanceledListener {
                                dialog!!.dismiss()
                                val intent = Intent(this@SetupProfileActivity,MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                    }

                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null){
            if (data.data !=null){
                val uri = data.data //filePath
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference
                    .child("Profile")
                    .child(time.toString()+"")
                reference.putFile(uri!!).addOnCompleteListener { task->
                    reference.downloadUrl.addOnCompleteListener {uri->
                        val filePath = uri.toString()
                        val obj = HashMap<String,Any>()
                        obj ["image"] = filePath
                        database!!.reference
                            .child("users")
                            .child(FirebaseAuth.getInstance().uid!!)
                            .updateChildren(obj).addOnSuccessListener {  }

                    }

                }

            }
            binding!!.imageView.setImageURI(data.data)
            selectedImage = data.data

        }
    }
}