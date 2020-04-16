package com.jasonaladin .tictactoyonline

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_game.*


class Game : AppCompatActivity() {

    private var database = FirebaseDatabase.getInstance()
    private var mRef = database.reference

    var player1 = arrayListOf<Int>()
    var player2 = arrayListOf<Int>()
    var emailP1 = ""
    var emailP2 = ""
    var deviceSymbol:String? = null

    var activePlayer = ""
    var myEmail:String? = null
    var sessionID:String? = null

    var notificationCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        var bundle:Bundle? = intent.extras
        myEmail = bundle!!.getString("email")
        tvMyEmail.text = "You are logged in as: "+myEmail.toString()
        tlGame.visibility = View.VISIBLE
        incomingCall()
    }

    fun buClick(view: View){//update to firebase database on player move
        val buttonSelected = view as Button
        var cellID = 0
        if(activePlayer == myEmail){
            when (buttonSelected.id) {
                R.id.bu1 -> cellID = 1
                R.id.bu2 -> cellID = 2
                R.id.bu3 -> cellID = 3

                R.id.bu4 -> cellID = 4
                R.id.bu5 -> cellID = 5
                R.id.bu6 -> cellID = 6

                R.id.bu7 -> cellID = 7
                R.id.bu8 -> cellID = 8
                R.id.bu9 -> cellID = 9
            }
            //mRef.child("PlayerOnline").child(sessionID.toString()).child(cellID.toString()).setValue(myEmail)
            val value = HashMap<String, String>()
            value.set(cellID.toString(), myEmail.toString())
            mRef.child("PlayerOnline").child(sessionID.toString()).push().setValue(value)
        }else{
            Toast.makeText(this,"Wait for ${splitEmail(activePlayer)} to make a move", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateGrid(cellID:Int, buSelected:Button){//change color of selected button and put X or O

        if(activePlayer == emailP1){
            buSelected.text = "X"
            buSelected.setBackgroundColor(Color.BLUE)
            player1.add(cellID)
            Log.i(emailP1,player1.toString())
            activePlayer = emailP2

        }
        else{
            buSelected.text = "O"
            buSelected.setBackgroundColor(Color.GREEN)
            player2.add(cellID)
            Log.i(emailP2,player2.toString())
            activePlayer = emailP1
        }
        buSelected.isEnabled = false
        CheckWinner()
    }

    fun moveListener(cellID: Int){
        val buSelected:Button?

        when(cellID){
            1 -> buSelected = bu1
            2 -> buSelected = bu2
            3 -> buSelected = bu3
            4 -> buSelected = bu4
            5 -> buSelected = bu5
            6 -> buSelected = bu6
            7 -> buSelected = bu7
            8 -> buSelected = bu8
            9 -> buSelected = bu9
            else -> {
                buSelected = bu1
            }
        }

        updateGrid(cellID,buSelected)
    }

    fun CheckWinner(){
        var winner = -1
        //row 1
        if(player1.contains(1) && player1.contains(2) && player1.contains(3)) winner = 1
        else if(player2.contains(1) && player2.contains(2) && player2.contains(3)) winner = 2

        //row 2
        else if(player1.contains(4) && player1.contains(5) && player1.contains(6)) winner = 1
        else if(player2.contains(4) && player2.contains(5) && player2.contains(6)) winner = 2

        //row 3
        else if(player1.contains(7) && player1.contains(8) && player1.contains(9)) winner = 1
        else if(player2.contains(7) && player2.contains(8) && player2.contains(9)) winner = 2

        //col 1
        else if(player1.contains(1) && player1.contains(4) && player1.contains(7)) winner = 1
        else if(player2.contains(1) && player2.contains(4) && player2.contains(7)) winner = 2

        //col 2
        else if(player1.contains(2) && player1.contains(5) && player1.contains(8)) winner = 1
        else if(player2.contains(2) && player2.contains(5) && player2.contains(8)) winner = 2

        //col 3
        else if(player1.contains(3) && player1.contains(6) && player1.contains(9)) winner = 1
        else if(player2.contains(3) && player2.contains(6) && player2.contains(9)) winner = 2

        //diagonal
        else if(player1.contains(1) && player1.contains(5) && player1.contains(9)) winner = 1
        else if(player1.contains(3) && player1.contains(5) && player1.contains(7)) winner = 1
        else if(player2.contains(1) && player2.contains(5) && player2.contains(9)) winner = 2
        else if(player2.contains(3) && player2.contains(5) && player2.contains(7)) winner = 2

        if(winner == 1){
            postWinner(emailP1,this)
        }else if (winner == 2) {
            postWinner(emailP2, this)
        }
    }

    fun resetGame(view:View){//button reset
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }

    fun buRequest(view: View) {//button request
        val userEmail = etEmailGame.text.toString()
        mRef.child("Users").child(splitEmail(userEmail)).child("Request").push().setValue(myEmail)
        deviceSymbol = "X" //set device symbol
        emailP1 = myEmail.toString()
        emailP2 = userEmail
        activePlayer = emailP1
        Toast.makeText(this,"You are playing as $deviceSymbol",Toast.LENGTH_SHORT).show()
        playerOnline(splitEmail(myEmail!!)+splitEmail(userEmail))
    }

    fun buAccept(view: View) {//button accept
        val userEmail = etEmailGame.text.toString()
        mRef.child("Users").child(splitEmail(userEmail)).child("Request").push().setValue(myEmail)
        deviceSymbol = "O" //set device symbol
        emailP2 = myEmail.toString()
        emailP1 = userEmail
        activePlayer = emailP1
        Toast.makeText(this,"You are playing as $deviceSymbol",Toast.LENGTH_SHORT).show()
        playerOnline(splitEmail(userEmail)+splitEmail(myEmail!!)) //make a sessionID in database

    }

    fun playerOnline(sessionID:String){//listener for PlayerOnline.SessionID to update the tictacktoy grid

        this.sessionID = sessionID
        mRef.child("PlayerOnline").removeValue()

        mRef.child("PlayerOnline").child(sessionID).orderByKey().limitToLast(1)
            .addValueEventListener(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try{
                        //player1.clear()
                        //player2.clear()
                        for(key in dataSnapshot.children){
                            val keyValue = key.key.toString()
                            val nextKeyValue = dataSnapshot.child(keyValue).children
                            for(keyNext in nextKeyValue){
                                val cellID = keyNext.key!!.toInt()
                                val email = keyNext.value.toString()
                                moveListener(cellID)
                            }
                        }
                    }catch (ex:Exception){}
                }
            })
    }

    fun incomingCall(){//listener for Users.Email.Request
        val splitMyEmail = splitEmail(myEmail.toString())
        mRef.child("Users").child(splitMyEmail).child("Request")
            .addValueEventListener(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        val td = p0.value as HashMap<String, Any>
                        var value:String
                        for(key in td.keys){
                            value = td[key] as String
                            etEmailGame.setText(value)
                            val notifyMe = Notification()
                            notifyMe.notify(applicationContext,value+" wants to play",notificationCount)
                            notificationCount++
                            mRef.child("Users").child(splitEmail(myEmail.toString())).setValue(true)
                            break
                        }
                    }catch (ex:Exception){}
                }
            })
    }

    fun splitEmail(str:String): String {//Split email @ then return first part
        val split = str.split("@")
        return split[0]
    }

    fun postWinner(email:String,context: Context){
        activePlayer = ""
        player1.clear()
        player2.clear()
        emailP1 = ""
        emailP2 = ""

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Congratulations!")
            .setMessage(email+" you are the winner.")
            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                // User clicked OK button
                Toast.makeText(this,"Dialog success",Toast.LENGTH_SHORT).show()
            }).show()
    }
}


