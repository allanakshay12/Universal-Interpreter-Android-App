package com.example.universalinterpreter;

import android.content.Context;
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

import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private final String PREFERENCE = "Universal-Interpreter-Preference-File";
    private SharedPreferences preference;
    final String pref_option = "used_before";
    final String input_option = "input";
    final String output_option = "output";
    private Button button;
    private RadioGroup input_rg, output_rg;
    private RadioButton input_asl, input_speech, input_morse, input_text, output_text, output_morse, output_speech;
    private EditText email, name;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preference = getApplicationContext().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);

        input_rg = findViewById(R.id.Input_Radio_Group);
        output_rg = findViewById(R.id.Output_Radio_Group);
        input_asl = findViewById(R.id.Input_ASL_Radio_Button);
        input_morse = findViewById(R.id.Input_Morse_Radio_Button);
        input_speech = findViewById(R.id.Input_Speech_Radio_Button);
        input_text = findViewById(R.id.Input_Text_Radio_Button);
        output_morse = findViewById(R.id.Output_Morse_Radio_Button);
        output_speech = findViewById(R.id.Output_Speech_Radio_Button);
        output_text = findViewById(R.id.Output_Text_Radio_Button);
        button = findViewById(R.id.Save_Preferences_Button);
        email = findViewById(R.id.Email);
        name = findViewById(R.id.Name);

        input_text.toggle();
        output_text.toggle();

        if(preference.contains(input_option)) {
            switch(preference.getString(input_option, "Text")) {
                case "Text":
                    input_text.toggle();
                    break;

                case "ASL":
                    input_asl.toggle();
                    break;

                case "Morse":
                    input_morse.toggle();
                    break;

                case "Speech":
                    input_speech.toggle();
                    break;
            }
        }

        if(preference.contains(output_option)) {
            switch(preference.getString(output_option, "Text")) {
                case "Text":
                    output_text.toggle();
                    break;

                case "Morse":
                    output_morse.toggle();
                    break;

                case "Speech":
                    output_speech.toggle();
                    break;
            }
        }

        if(preference.contains("Email")) {
            email.setText(preference.getString("Email", ""));
            email.setKeyListener(null);
        }

        if(preference.contains("Name")) {
            name.setText(preference.getString("Name", ""));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor = preference.edit();
                switch (input_rg.getCheckedRadioButtonId()) {
                    case R.id.Input_ASL_Radio_Button :
                        editor.putString(input_option, "ASL");
                        break;
                    case R.id.Input_Morse_Radio_Button :
                        editor.putString(input_option, "Morse");
                        break;
                    case R.id.Input_Speech_Radio_Button :
                        editor.putString(input_option, "Speech");
                        break;
                    case R.id.Input_Text_Radio_Button :
                        editor.putString(input_option, "Text");
                        break;
                }
                switch (output_rg.getCheckedRadioButtonId()) {
                    case R.id.Output_Morse_Radio_Button :
                        editor.putString(output_option, "Morse");
                        break;
                    case R.id.Output_Speech_Radio_Button :
                        editor.putString(output_option, "Speech");
                        break;
                    case R.id.Output_Text_Radio_Button :
                        editor.putString(output_option, "Text");
                        break;
                }

                editor.putBoolean(pref_option, true);
                if(email.getText().toString().trim().equals("") || name.getText().toString().trim().equals("")){
                    Toast.makeText(Settings.this, "Enter a valid email id / name", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString("Email", email.getText().toString().trim());
                    editor.putString("Name", name.getText().toString().trim());
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                    databaseReference.child(email.getText().toString().trim().replace(".", "+")).child("Name").setValue(name.getText().toString().trim());
                    databaseReference.child(email.getText().toString().trim().replace(".", "+")).child("Chats").child("Key Values").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null) {
                                //Toast.makeText(Settings.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                databaseReference.child(email.getText().toString().trim().replace(".", "+")).child("Chats").child("Key Values").child("New").setValue(0);
                                databaseReference.child(email.getText().toString().trim().replace(".", "+")).child("Chats").child("Key Values").child("Read").setValue(0);
                                databaseReference.child(email.getText().toString().trim().replace(".", "+")).child("Chats").child("Key Values").child("Sent").setValue(0);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    editor.apply();
                    finish();
                }

            }
        });

    }

}
