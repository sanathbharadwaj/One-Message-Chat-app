package com.sanath.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//This activity shows the list of users registered for this app(Excluding you).

public class UserListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        //Get the phone number of the current signed in user.
        final String currentPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users"); //point to the users node is FB database
        reference.addValueEventListener(new ValueEventListener() { //This method fetches all the data in the "users" node.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  //This method is called when the data is successfully fetched
                List<User> users = new ArrayList<>(); //Create an empty list of users.
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) //This for loop traverses through each element in the data fetched.
                { //Each user information is obtained in one iteration.
                    User user = userSnapshot.getValue(User.class);//Puts the snapshot data into the user class.
                    if(!user.getPhoneNumber().equals(currentPhoneNumber)) //If phone number is same as the
                        // current users phone number then do not add to the list.
                        users.add(user);
                }
                //Populate the list with the user names.
                populateListView(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    //This method pupulates the list view
    private void populateListView(final List<User> users) {
            ListView listView = findViewById(R.id.users_list); //Get the list view.

            List<String> names = new ArrayList<>(); // Create an empty list of user names.
            for(User user : users){
                names.add(user.getName()); //From each user get the name and add to this list.
            }
            //Create an array adapter.
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, names);
            listView.setAdapter(arrayAdapter); //Set the adapter to the list view
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                //This method is called when any item of the list is selected
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long ld) {
                    User user = users.get(position); //Get the user object for the position clicked.
                    moveToChatMainActivity(user); //Method to move to next activity.
                }
            });


    }
    //Method to move to next activity.
    private void moveToChatMainActivity(User user) {
        String name = user.getName(); //Get the name from the user.
        String phoneNumber = user.getPhoneNumber(); //Get the phone number from the user.

        Intent intent = new Intent(UserListActivity.this, MainActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("phoneNumber", phoneNumber); // Pass phone numbers to the intent extras
        startActivity(intent); //Move to next activity
    }
}
