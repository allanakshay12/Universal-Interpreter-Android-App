package com.example.universalinterpreter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.util.List;

public class HomePage_Adapter extends RecyclerView.Adapter<HomePage_Adapter.MyViewHolder> {

    private List<Homepage_ListItem> homepagelist;
    private Typeface mycustomfont;
    private Activity activity;
    private Context context;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Main_Page");
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference photoRef;
    private StorageReference FileRef;
    private String docid = "", fileid = "";
    StorageReference fileref;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, content, timesignature,link;
        public ImageView image, logo;
        public CardView card;
        //public HackyPagerViewer pagerViewer;


        public MyViewHolder(View view)
        {
            super(view);
            name = view.findViewById(R.id.listitem_name);

            //pagerViewer=view.findViewById(R.id.viewpagerforimage);

            card = view.findViewById(R.id.listitem_card);

        }

    }

    public HomePage_Adapter(List<Homepage_ListItem> homepagelist, Activity activity, Context context)
    {
        this.homepagelist = homepagelist;

        this.activity = activity;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homepage_listitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Homepage_ListItem listitem = homepagelist.get(position);

        holder.name.setText(listitem.getName());

        if(listitem.getType()==1) {
            holder.card.setCardBackgroundColor(Color.parseColor("#1565C0"));
            holder.name.setTextColor(Color.parseColor("White"));


        }
        else {
            holder.card.setCardBackgroundColor(Color.parseColor("White"));
            holder.name.setTextColor(Color.parseColor("Black"));

        }

    }

    @Override
    public int getItemCount() {
        return homepagelist.size();
    }

    /*public void downloadDirect(StorageReference imageRef, ImageView imageView, int type) {
        //type : 0-Image, 1-Logo
        try {
                // Download directly from StorageReference using Glide
                // (See HomepageGlideModule for Loader registration)
            if(type==0) {
                GlideApp.with(context)
                        .load(imageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            } else {
                GlideApp.with(context)
                        .load(imageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }
        }catch (Exception ex){
            Toast.makeText(activity, "Error in downloading image", Toast.LENGTH_SHORT).show();;
        }
    }*/






    //download method

    private void startdownload() {
        /*try {
            String url="https://docs.google.com/uc?id=[FILE_ID]&export=download";
            String id=getID(docid);
            url=url.replace("[FILE_ID]",id);
            Download download=new Download(context, fileid ,url);
            download.start();
        }
        catch (SecurityException e){
            Toast.makeText(context,"Please grant storage permissions to download",Toast.LENGTH_LONG).show();
        } catch (Exception e) {

            Toast.makeText(activity, "An Error has occured in downloading the file", Toast.LENGTH_SHORT).show();

        }*/

        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    File directory = new File(Environment.getExternalStorageDirectory() + "/UVCE-Connect");

                    if (!directory.exists()) {
                        directory.mkdirs();
                    }


                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    String fileName = fileid;
                    request.setDescription("File")
                            .setTitle(fileName)
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalPublicDir("/UVCE-Connect/Files", fileName)
                            .allowScanningByMediaScanner();

                    DownloadManager manager = (DownloadManager)
                            context.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(context, "Downloading.....", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(context, "Permission not granted to read External storage. Please grant permission and try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }






    private String getID(String url) {
        String ID = "";

        for (int i = url.length() - 1; i >= 0; --i) {
            if (url.charAt(i) != '=') {
                ID = url.charAt(i) + ID;
            } else {
                break;
            }
        }

        return ID;
    }


}
