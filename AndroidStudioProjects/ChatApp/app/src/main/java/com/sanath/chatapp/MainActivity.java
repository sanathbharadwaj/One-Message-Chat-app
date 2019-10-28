package com.sanath.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//This is the last activity where the users chat.
public class MainActivity extends AppCompatActivity {

    private String currentPhoneNumber;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages");//DB ref pointing to the messages node.

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentPhoneNumber = currentUser.getPhoneNumber(); //Get current users phone number.


        Intent intent = getIntent();
        String userName = intent.getStringExtra("name"); //Get the name from the intent extras
        phoneNumber = intent.getStringExtra("phoneNumber");//Get the phone number from the intent extras


        TextView nameTextView = findViewById(R.id.recepient_name);
        nameTextView.setText(userName);//Set the text to the top TextView which is the name of the other participant.

        reference.child(currentPhoneNumber).child(phoneNumber).addValueEventListener(new ValueEventListener() {//Fetches the message from the node.
            //messages --> myPhoneNumber --> Other participant ph no --> Text message
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //This method is called when the text is obtained.
                String message = dataSnapshot.getValue(String.class); //Get the obtained message
                setMessageText(message); //Set the text to textview.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show(); //This is called if there is an error fetching data.
            }
        });
    }

    //A simple method to set text to the textView.
    void setMessageText(String message)
    {
        TextView textView = findViewById(R.id.message_text_view);
        textView.setText(message);
    }

    //Method called when the send button is pressed.
    public void onSend(View view)
    {
        final EditText editText = findViewById(R.id.text_input);
        String text = editText.getText().toString(); //Get the message text user has entered
        if(text.length() == 0) //Do nothing if user presses send button without any text. Return from this function.
            return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages"); //DB ref pointing to messages node.
        reference.child(currentPhoneNumber).child(phoneNumber).setValue(text, new DatabaseReference.CompletionListener() {
            //messages --> myPhoneNumber --> Other participant ph no --> Save the text message
            @Override
            //This method is called when save is complete
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError != null) // This variable will be non-null if there is an error.
                {
                    Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    return; // Toast the error and get out the function.
                }
                editText.setText(""); //Once successful set the edittext text to blank.
            }
        });


        //Repeat the same saving procedure for messages --> Other ph no --> my phone number --> Save text message.
        reference.child(phoneNumber).child(currentPhoneNumber).setValue(text); //The callbacks here is not that necessary.
    }
}
