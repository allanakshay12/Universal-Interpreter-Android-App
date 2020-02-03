package com.example.universalinterpreter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Chat extends AppCompatActivity {

    EditText inputText;
    TextView leftarea, rightarea, centerarea, outputText;
    Fundamental_Converter fundamental_converter;
    private final String PREFERENCE = "Universal-Interpreter-Preference-File";
    private SharedPreferences preference;
    DatabaseReference dbref;
    Activity activity;
    Context context;
    int key = 0, read_key = 0, sent_key = 0;
    String prev_key;
    String prev_cat;
    Boolean found = false;
    String client_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client_email = getIntent().getStringExtra("Client Email").replace(".", "+");

        inputText = findViewById(R.id.Chat_Input_Text);
        leftarea = findViewById(R.id.Chat_Left_Area);
        rightarea = findViewById(R.id.Chat_Right_Area);
        centerarea = findViewById(R.id.Chat_Bottom_Area);
        outputText = findViewById(R.id.Chat_Output_Text);

        activity = this;
        context = this;

        dbref = FirebaseDatabase.getInstance().getReference().child("Users");

        preference = getApplicationContext().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);

        if(getIntent().getBooleanExtra("New_Chat", true)==true) {
            inputText.setHint("Enter the Receiver's Email");
            outputText.setText("Enter the Receiver's Email");
            fundamental_converter = new Fundamental_Converter(outputText.getText().toString(), preference.getString("output", "Text"), this);
            centerarea.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final String email = inputText.getText().toString().trim().replace(".", "+");
                    if(email.equals("")) {
                        Toast.makeText(Chat.this, "Kindly Enter an Email ID", Toast.LENGTH_SHORT).show();
                    } else {
                        dbref.child(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //Toast.makeText(activity, dataSnapshot.getChildren().toString(), Toast.LENGTH_SHORT).show();

                                    try {
                                        Intent intent_chat = new Intent(context, Chat.class);
                                        intent_chat.putExtra("Client Email", email);
                                        intent_chat.putExtra("Client Name", dataSnapshot.child("Name").getValue().toString());
                                        intent_chat.putExtra("New_Chat", false);
                                        startActivity(intent_chat);
                                        finish();
                                    } catch(Exception e){
                                        Toast.makeText(activity, "User does not exist", Toast.LENGTH_SHORT).show();
                                    }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    return true;
                }
            });
        } else {
            dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //try {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.getKey().toString().equals("Key Values") || snapshot.getKey().toString().equals("Sent")) {
                                continue;
                            }
                            for (DataSnapshot newsnap : snapshot.getChildren()) {
                                //Toast.makeText(activity, newsnap.child("Email").getValue().toString(), Toast.LENGTH_SHORT).show();
                                if (newsnap.child("Email").getValue().toString().equals(client_email)) {
                                    outputText.setText(newsnap.child("Message").getValue().toString());
                                    prev_key = newsnap.getKey();
                                    prev_cat = snapshot.getKey();
                                    found = true;
                                    break;
                                }
                            }

                        }
                    fundamental_converter = new Fundamental_Converter(outputText.getText().toString(), preference.getString("output", "Text"), context);

                    // } catch (Exception e) {

                    //}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            centerarea.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final String message = inputText.getText().toString().trim();
                    if(message.equals("")) {
                        Toast.makeText(activity, "Enter a valid message", Toast.LENGTH_SHORT).show();
                    } else {
                        dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Key Values").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() != null) {
                                    read_key = Integer.parseInt(dataSnapshot.child("Read").getValue().toString());
                                    read_key--;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Key Values").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() != null) {
                                    sent_key = Integer.parseInt(dataSnapshot.child("Sent").getValue().toString());
                                    sent_key--;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        dbref.child(client_email).child("Chats").child("Key Values").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //Toast.makeText(activity, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                if(dataSnapshot.getValue() != null) {
                                    key = Integer.parseInt(dataSnapshot.child("New").getValue().toString());

                                    key--;

                                }
                                if(found) {
                                    dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child(prev_cat).child(prev_key).removeValue();
                                }
                                dbref.child(getIntent().getStringExtra("Client Email").replace(".", "+")).child("Chats").child("New").child(String.valueOf(key)).child("Name").setValue(preference.getString("Name", ""));
                                dbref.child(getIntent().getStringExtra("Client Email").replace(".", "+")).child("Chats").child("New").child(String.valueOf(key)).child("Email").setValue(preference.getString("Email", "").replace(".", "+"));
                                dbref.child(getIntent().getStringExtra("Client Email").replace(".", "+")).child("Chats").child("New").child(String.valueOf(key)).child("Message").setValue(message);
                                dbref.child(getIntent().getStringExtra("Client Email").replace(".", "+")).child("Chats").child("Key Values").child("New").setValue(String.valueOf(key));
                                dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Read").child(String.valueOf(read_key)).child("Name").setValue(getIntent().getStringExtra("Client Name"));
                                dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Read").child(String.valueOf(read_key)).child("Email").setValue(getIntent().getStringExtra("Client Email"));
                                dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Read").child(String.valueOf(read_key)).child("Message").setValue(outputText.getText().toString());
                                dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Key Values").child("Read").setValue(String.valueOf(read_key));
                                dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Sent").child(String.valueOf(sent_key)).child("Name").setValue(getIntent().getStringExtra("Client Name"));
                                dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Sent").child(String.valueOf(sent_key)).child("Email").setValue(getIntent().getStringExtra("Client Email"));
                                dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Sent").child(String.valueOf(sent_key)).child("Message").setValue(message);
                                dbref.child(preference.getString("Email", "").replace(".", "+")).child("Chats").child("Key Values").child("Sent").setValue(String.valueOf(sent_key));
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    return true;
                }
            });

        }


    }

}
