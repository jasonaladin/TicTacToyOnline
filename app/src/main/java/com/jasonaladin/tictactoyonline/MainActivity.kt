package com.jasonaladin.tictactoyonline

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        loadGame()
        super.onStart()
    }

    fun buLogin(view: View) {
        firebaseLogin(etEmail.text.toString(), etPassword.text.toString())
    }

    private fun firebaseLogin(email:String, password:String){

        mAuth!!.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext,"Successful Login",Toast.LENGTH_SHORT).show()
                    val currentUser = mAuth!!.currentUser
                    if(currentUser!=null){
                        myRef.child("Users").child(splitEmail(currentUser.email.toString())).child("Request").setValue(true)
                    }
                    loadGame()
                }else{
                    Toast.makeText(applicationContext,"Failed Login",Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun loadGame(){
        val currentUser = mAuth!!.currentUser
        if(currentUser!=null){
            val intent = Intent(this,Game::class.java)
            intent.putExtra("email",currentUser.email)
            intent.putExtra("uid",currentUser.uid)
            startActivity(intent)
        }
    }
    fun splitEmail(str:String): String {
        val split = str.split("@")
        return split[0]
    }
}
