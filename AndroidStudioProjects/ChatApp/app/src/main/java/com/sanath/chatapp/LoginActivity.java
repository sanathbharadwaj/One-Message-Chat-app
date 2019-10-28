package com.sanath.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

//This is the first activity called when the app is launched.
public class LoginActivity extends AppCompatActivity {


    private String phoneNumber;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) //Check if current user exists, meaning if user has already logged in
            moveToUserListActivity();// Then move the user to next activity, the list of users activity.
    }

    /**
     * This method is called when the Login button is pressed.
     */
    public void OnLogin(View view)
    {
        Toast.makeText(this, "Verifying SMS...", Toast.LENGTH_LONG).show();
        EditText phoneEditText = findViewById(R.id.phone_text);
        phoneNumber = "+91" + getInputText(phoneEditText);//Get text from the text field and append +91.
        EditText nameEditText = findViewById(R.id.name_text);
        name = getInputText(nameEditText); // Get the name from text field
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, // This function requests for SMS verification code
                60, //Timeout duration
                TimeUnit.SECONDS, //Timeout duration units
                this, //Context
                mCallBacks); // This is the callback variable written beneath. This has functions like onVerificationCompleted etc..
    }

    String getInputText(EditText editText)
    {
        return editText.getText().toString();
    }

    //This is the callback variable which captures the success and failure events of SMS verification.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        //This method is called when the verification is successful.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Toast.makeText(LoginActivity.this, "Phone number verified", Toast.LENGTH_SHORT).show();
            signInWithPhoneAuthCredential(credential); //Function called to sign in the user.
        }

        //This method is called when the verification is failed.
        @Override
        public void onVerificationFailed(FirebaseException e) {


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(LoginActivity.this, "SMS quota exceeded", Toast.LENGTH_SHORT).show();
            }

        }

        //This is called when the SMS is sent to the mobile.(Not relevant here).
        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

        }
    };

    //This function signins the user in the Firebase system.
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential) // This signs into the firebase system.
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    //This method is called when the signin is successful.
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Signed in sucessfully", Toast.LENGTH_SHORT).show();
                            User user = new User(name, phoneNumber);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                            reference.child(phoneNumber).setValue(user); // The user data is saved to the cloud.
                            moveToUserListActivity(); //To move to next activity.
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

    //This method takes the user to next activity.
    private void moveToUserListActivity() {
        Intent intent = new Intent(LoginActivity.this, UserListActivity.class);
        startActivity(intent);
        finish(); //Finish with kill this activity. Hence pressing back button will not come back to this activity.
    }

    ;
}
