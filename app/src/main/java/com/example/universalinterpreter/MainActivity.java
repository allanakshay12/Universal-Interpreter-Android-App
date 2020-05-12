package com.example.universalinterpreter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String PREFERENCE = "Universal-Interpreter-Preference-File";
    private SharedPreferences preference;
    final String pref_option = "used_before";
    RecyclerView recyclerView;
    List<Homepage_ListItem> list = new ArrayList<>();
    HomePage_Adapter adapter;
    DatabaseReference databaseReferencenew, databaseReferenceread, databaseReference;
    Activity activity;
    int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    TextView leftarea, rightarea, contactname;
    int count = 0;
    Context context;
    Fundamental_Converter fundamental_converter;
    CardView cardView;
    ArrayList<String> dup_emails = new ArrayList<>();
    private String read_out_name = "", prev_read_out_name = "";
    private int prev_category = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;
        context = this;

        cardView = findViewById(R.id.main_page_contact_card);






       /* GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


       mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

       GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

       if(account==null) {
           Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } */

       leftarea = findViewById(R.id.left_area);
       rightarea = findViewById(R.id.right_area);
       contactname = findViewById(R.id.Contact_Name);

        preference = getApplicationContext().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        if(!preference.contains(pref_option) || preference.getBoolean(pref_option,false) == (false)){
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            this.finish();
        }
        String email = preference.getString("Email", "");

       // String android_id = android.provider.Settings.Secure.getString(this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(email.replace(".", "+")).child("Chats");
        databaseReferencenew = FirebaseDatabase.getInstance().getReference().child("Users").child(email.replace(".", "+")).child("Chats").child("New");
        databaseReferenceread = FirebaseDatabase.getInstance().getReference().child("Users").child(email.replace(".", "+")).child("Chats").child("Read");


        //Following Lines used to populate the recycler list
        //recyclerView = findViewById(R.id.homepage_recyclerview);
        //adapter = new HomePage_Adapter(list, this, this);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setAdapter(adapter);
        prepareHomePageData();




        rightarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    count = (count + 1) % list.size();
                    contactname.setText(list.get(count).getName());
                    if (list.get(count).getType() == 1) {
                        cardView.setCardBackgroundColor(Color.parseColor("White"));
                        contactname.setTextColor(Color.parseColor("Black"));
                    } else {
                        cardView.setCardBackgroundColor(Color.parseColor("Black"));
                        contactname.setTextColor(Color.parseColor("White"));
                    }
                    if(list.get(count).getType()==1) {
                        fundamental_converter = new Fundamental_Converter("New Message from " + list.get(count).getName(), preference.getString("output", "Text"), context);
                    } else {
                        fundamental_converter = new Fundamental_Converter(list.get(count).getName(), preference.getString("output", "Text"), context);
                    }
                    prev_read_out_name = list.get(count).getName();
                    prev_category = list.get(count).getType();
                } catch (Exception e) {}
            }
        });

        leftarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    count = (count - 1);
                    if (count == -1) {
                        count = list.size() - 1;
                    }
                    contactname.setText(list.get(count).getName());
                    if (list.get(count).getType() == 1) {
                        cardView.setCardBackgroundColor(Color.parseColor("White"));
                        contactname.setTextColor(Color.parseColor("Black"));
                    } else {
                        cardView.setCardBackgroundColor(Color.parseColor("Black"));
                        contactname.setTextColor(Color.parseColor("White"));
                    }
                    if(list.get(count).getType()==1) {
                        fundamental_converter = new Fundamental_Converter("New Message from " + list.get(count).getName(), preference.getString("output", "Text"), context);
                    } else {
                        fundamental_converter = new Fundamental_Converter(list.get(count).getName(), preference.getString("output", "Text"), context);
                    }
                    prev_read_out_name = list.get(count).getName();
                    prev_category = list.get(count).getType();
                } catch (Exception e) {}
            }
        });

        rightarea.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    Intent intent_chat = new Intent(getApplicationContext(), Chat.class);
                    intent_chat.putExtra("Client Email", list.get(count).getEmail());
                    intent_chat.putExtra("Client Name", list.get(count).getName());
                    intent_chat.putExtra("New_Chat", false);
                    startActivity(intent_chat);
                    return true;
                } catch (Exception e) { }
                return true;
            }
        });

        leftarea.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent_new_chat = new Intent(getApplicationContext(), Chat.class);
                intent_new_chat.putExtra("Client Email", "");
                intent_new_chat.putExtra("New_Chat", true);
                intent_new_chat.putExtra("Client Name", "");
                startActivity(intent_new_chat);
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Sign_In_Stuff", "signInResult:failed code=" + e.getStatusCode());

        }
    }

    //Function to populate the list
    void prepareHomePageData() {
        databaseReferencenew.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               try {
                   count = 0;
                   list.clear();
                   dup_emails.clear();
                   for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                       // Toast.makeText(activity, childsnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();

                       Homepage_ListItem item = new Homepage_ListItem(childsnapshot.child("Name").getValue().toString(), childsnapshot.child("Email").getValue().toString(), 1);
                       if(dup_emails.contains(childsnapshot.child("Email").getValue().toString())) {
                           databaseReferencenew.child(childsnapshot.getKey()).removeValue();
                       } else {
                           dup_emails.add(childsnapshot.child("Email").getValue().toString());
                           list.add(item);
                       }
                   }

                   try {
                       contactname.setText(list.get(count).getName());
                       if (list.get(count).getType() == 1) {
                           cardView.setCardBackgroundColor(Color.parseColor("White"));
                           contactname.setTextColor(Color.parseColor("Black"));
                       } else {
                           cardView.setCardBackgroundColor(Color.parseColor("Black"));
                           contactname.setTextColor(Color.parseColor("White"));
                       }
                       //fundamental_converter = new Fundamental_Converter(list.get(count).getName(), preference.getString("output", "Text"), context);
                   } catch (IndexOutOfBoundsException e) {

                   }
                   //adapter.notifyDataSetChanged();
                   //load_animation.smoothToHide();
                   //} catch (Exception e) {
                   //Toast.makeText(MainActivity.this, "Error in fetching details", Toast.LENGTH_SHORT).show();
                   //  list.clear();


                   databaseReferenceread.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           try {
                           for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                               //  Toast.makeText(activity, childsnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();

                               Homepage_ListItem item = new Homepage_ListItem(childsnapshot.child("Name").getValue().toString(), childsnapshot.child("Email").getValue().toString(), 0);

                               if(dup_emails.contains(childsnapshot.child("Email").getValue().toString())) {
                                   databaseReferenceread.child(childsnapshot.getKey()).removeValue();
                               } else {
                                   dup_emails.add(childsnapshot.child("Email").getValue().toString());
                                   list.add(item);
                               }

                           }

                           read_out_name = list.get(count).getName();


                           try {
                               contactname.setText(list.get(count).getName());
                               if (list.get(count).getType() == 1) {
                                   cardView.setCardBackgroundColor(Color.parseColor("White"));
                                   contactname.setTextColor(Color.parseColor("Black"));
                               } else {
                                   cardView.setCardBackgroundColor(Color.parseColor("Black"));
                                   contactname.setTextColor(Color.parseColor("White"));
                               }

                               if(!prev_read_out_name.equals(read_out_name)) {
                                   if (list.get(count).getType() == 1) {
                                       fundamental_converter = new Fundamental_Converter("New Message from " + list.get(count).getName(), preference.getString("output", "Text"), context);
                                   } else {
                                       fundamental_converter = new Fundamental_Converter(list.get(count).getName(), preference.getString("output", "Text"), context);
                                   }
                                   prev_read_out_name = list.get(count).getName();
                                   prev_category = list.get(count).getType();
                               } else {
                                   if(prev_category!=list.get(count).getType()) {
                                       if (list.get(count).getType() == 1) {
                                           fundamental_converter = new Fundamental_Converter("New Message from " + list.get(count).getName(), preference.getString("output", "Text"), context);
                                       } else {
                                           fundamental_converter = new Fundamental_Converter(list.get(count).getName(), preference.getString("output", "Text"), context);
                                       }
                                       prev_category = list.get(count).getType();
                                   }
                               }
                           } catch (IndexOutOfBoundsException e) {

                           }
                           //adapter.notifyDataSetChanged();
                           //load_animation.smoothToHide();
                           //} catch (Exception e) {
                           //Toast.makeText(MainActivity.this, "Error in fetching details", Toast.LENGTH_SHORT).show();
                           //  list.clear();

                           databaseReferenceread.removeEventListener(this);
                            } catch (Exception e) {

                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
                   //databaseReferencenew.removeEventListener(this);
               } catch (Exception e) {}
               // }
           }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
