package com.example.universalinterpreter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

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
    String input_mode;
    String morse_input;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String email, message;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

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


        input_mode = preference.getString("input", "Text");

        if(getIntent().getBooleanExtra("New_Chat", true)==true) {
            inputText.setHint("Enter the Receiver's Email");
            outputText.setText("Enter the Receiver's Email");
            fundamental_converter = new Fundamental_Converter(outputText.getText().toString(), preference.getString("output", "Text"), this);
            centerarea.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    email = inputText.getText().toString().trim().replace(".", "+");
                    if(input_mode.equals("Morse")) {
                        email = MorsetoText(email);
                    }
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


            //Input Code
            if(input_mode.equals("Morse")) {
                inputText.setKeyListener(null);
                morse_input = inputText.getText().toString();
                leftarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            morse_input = morse_input.substring(0, morse_input.length() - 1);
                            inputText.setText(morse_input);
                        } catch (Exception e) {

                        }

                    }
                });
                rightarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        morse_input = morse_input + ".";
                        inputText.setText(morse_input);
                        // Vibrate for 1000 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(1000);
                        }
                    }
                });
                rightarea.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        morse_input = morse_input + "_";
                        inputText.setText(morse_input);
                        // Vibrate for 2000 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(2000);
                        }
                        return true;
                    }
                });
                leftarea.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        morse_input = morse_input + "\n";
                        inputText.setText(morse_input);
                        return true;
                    }
                });
                centerarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        morse_input = morse_input + " ";
                        inputText.setText(morse_input);
                    }
                });
            }

            if(input_mode.equals("Speech")) {

                leftarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            inputText.setText(inputText.getText().toString().substring(0, inputText.getText().toString().length() - 1));
                        } catch (Exception e) {

                        }
                    }
                });
                centerarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        promptSpeechInput();
                    }
                });
                rightarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        promptSpeechInput();
                    }
                });

            }


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
                    message = inputText.getText().toString().trim();
                    if(input_mode.equals("Morse")) {
                        message = MorsetoText(message);
                    }
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

            //Input Code
            if(input_mode.equals("Morse")) {
                inputText.setKeyListener(null);
                morse_input = inputText.getText().toString();
                leftarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            morse_input = morse_input.substring(0, morse_input.length() - 1);
                            inputText.setText(morse_input);
                        } catch (Exception e) {

                        }

                    }
                });
                rightarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        morse_input = morse_input + ".";
                        inputText.setText(morse_input);
                        // Vibrate for 1000 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(1000);
                        }
                    }
                });
                rightarea.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        morse_input = morse_input + "_";
                        inputText.setText(morse_input);
                        // Vibrate for 2000 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(2000);
                        }
                        return true;
                    }
                });
                leftarea.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        morse_input = morse_input + "\n";
                        inputText.setText(morse_input);
                        return true;
                    }
                });
                centerarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        morse_input = morse_input + " ";
                        inputText.setText(morse_input);
                    }
                });
            }

            if(input_mode.equals("Speech")) {

                inputText.setKeyListener(null);


                centerarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        promptSpeechInput();
                    }
                });
                rightarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        promptSpeechInput();
                    }
                });
                leftarea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            inputText.setText(inputText.getText().toString().substring(0, inputText.getText().toString().length() - 1));
                        } catch (Exception e) {

                        }
                    }
                });

            }

        }


    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Kindly Speak!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry, Speech not supported.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    inputText.setText(result.get(0));
                }
                break;
            }

        }
    }

    String MorsetoText(String morse) {
        String text = "";
        String textArray[] = morse.split("       ");
        for(int i = 0; i<textArray.length; i++) {
            String signArray[] = textArray[i].split("   ");
            for(int j = 0; j<signArray.length; j++) {
                switch (signArray[j]) {

                           case ". _"           : text = text + "A"; break;
                           case "_ . . ."       : text = text + "B"; break;
                           case "_ . _ ."       : text = text + "C"; break;
                           case "_ . ."         : text = text + "D"; break;
                           case "."             : text = text + "E"; break;
                           case ". . _ ."       : text = text + "F"; break;
                           case "_ _ ."         : text = text + "G"; break;
                           case ". . . ."       : text = text + "H"; break;
                           case ". ."           : text = text + "I"; break;
                           case ". _ _ _"       : text = text + "J"; break;
                           case "_ . _"         : text = text + "K"; break;
                           case ". _ . ."       : text = text + "L"; break;
                           case "_ _"           : text = text + "M"; break;
                           case "_ ."           : text = text + "N"; break;
                           case "_ _ _"         : text = text + "O"; break;
                           case ". _ _ ."       : text = text + "P"; break;
                           case "_ _ . _"       : text = text + "Q"; break;
                           case ". _ ."         : text = text + "R"; break;
                           case ". . ."         : text = text + "S"; break;
                           case "_"             : text = text + "T"; break;
                           case ". . _"         : text = text + "U"; break;
                           case ". . . _"       : text = text + "V"; break;
                           case ". _ _"         : text = text + "W"; break;
                           case "_ . . _"       : text = text + "X"; break;
                           case "_ . _ _"       : text = text + "Y"; break;
                           case "_ _ . ."       : text = text + "Z"; break;
                           case ". _ _ . _"     : text = text + "Å"; break;
                           case ". _ . _"       : text = text + "Ä"; break;
                           case "_ _ _ ."       : text = text + "Ö"; break;
                           case ". . _ . ."     : text = text + "É"; break;
                           case "_ _ . _ _"     : text = text + "Ñ"; break;
                           case ". . _ _"       : text = text + "Ü"; break;
                           case "_ _ _ _"       : text = text + "Š"; break;
                           case ". . . _ _ . ." : text = text + "ß"; break;
                           case "_ . _ . ."     : text = text + "Ç"; break;
                           case ". _ _ _ _"     : text = text + "1"; break;
                           case ". . _ _ _"     : text = text + "2"; break;
                           case ". . . _ _"     : text = text + "3"; break;
                           case ". . . . _"     : text = text + "4"; break;
                           case ". . . . ."     : text = text + "5"; break;
                           case "_ . . . ."     : text = text + "6"; break;
                           case "_ _ . . ."     : text = text + "7"; break;
                           case "_ _ _ . ."     : text = text + "8"; break;
                           case "_ _ _ _ ."     : text = text + "9"; break;
                           case "_ _ _ _ _"     : text = text + "0"; break;
                           case ". . _ _ . ."   : text = text + "?"; break;
                           case ". . _ _ ."     : text = text + "!"; break;
                           case "_ _ . . _ _"   : text = text + ","; break;
                           case ". _ . _ . _"   : text = text + "."; break;
                           case "_ . . . _"     : text = text + "="; break;
                           case "_ . . . . _"   : text = text + "-"; break;
                           case "_ . _ _ ."     : text = text + "("; break;
                           case "_ . _ _ . _"   : text = text + ")"; break;
                           case ". . . . . . . .": text = text + "¤"; break;
                           case ". _ . _ ."     : text = text + "+"; break;
                           case ". . . _ . _"   : text = text + "Æ"; break;
                           case ". _ _ . _ ."   : text = text + "@"; break;
                           case "_ . . _ ."     : text = text + "/"; break;
                           case ". _ _ . ."     : text = text + "%"; break;
                           case ". _ . . _ ."   : text = text + "\""; break;
                           case "_ . _ . _ ."   : text = text + ";"; break;
                           case "_ _ _ . . ."   : text = text + ":"; break;
                           case ". . . _ ."     : text = text + "§"; break;
                           case ". . _ . _"     : text = text + "¿"; break;
                           case "_ . _ . _"     : text = text + "~"; break;
                           case ". _ _ _ _ ."   : text = text + "\'"; break;
                           case ". _ . . _"     : text = text + "#"; break;
                           case ". _ . . ."     : text = text + "&"; break;
                           case ". . . _ . . _" : text = text + "$"; break;
                           case ". .   . ."     : text = text + "*"; break;

                }
            }
            text = text + " ";
        }
        return text.trim();
    }

}
