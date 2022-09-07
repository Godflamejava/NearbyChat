package com.example.nearbychat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactActivity extends AppCompatActivity {
    Set<String> contactSet;
    ContactsAdapter adapter;
    RecyclerView  recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ImageButton add_contact=findViewById(R.id.add_contact);
         recyclerView= findViewById(R.id.recyclerView);
        contactSet = getData();
         adapter= new ContactsAdapter( new ArrayList<>(contactSet),this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBox();
            }
        });
    }
    public Set<String> getData(){
        SharedPreferences sharedPref = getSharedPreferences("contacts", Context.MODE_PRIVATE);
        Set<String> contactSet ;
        if(sharedPref.getStringSet("contacts",null)==null)
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putStringSet("contacts",new HashSet<>());
            editor.apply();
        }
        contactSet=sharedPref.getStringSet("contacts",null);
        return contactSet;
    }
    public void alertDialogBox()
    {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);
        final TextInputEditText name = (TextInputEditText) promptsView
                .findViewById(R.id.etname);
        final TextInputEditText uit = (TextInputEditText) promptsView
                .findViewById(R.id.etuit);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

//                                Toast.makeText(ContactActivity.this, (name.getText().toString())+" "+(uit.getText().toString()), Toast.LENGTH_SHORT).show();
                                contactSet.add(name.getText().toString()+(uit.getText().toString()));
                                adapter= new ContactsAdapter( new ArrayList<>(contactSet),recyclerView.getContext());
                                recyclerView.setAdapter(adapter);
                                SharedPreferences sharedPref = getSharedPreferences("contacts", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putStringSet("contacts",contactSet);
                                editor.apply();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();



    }
}