package com.example.aditi.todo_list;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class PermissionSettings extends AppCompatActivity {
     Switch c,d;
     static int reminderFlag=0;
     SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_settings);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarPermission);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.whiteback);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     finish();
                                                 }
                                             }

        );
        c = findViewById(R.id.switch1);
        d = findViewById(R.id.switch2);



        sharedPreferences =getSharedPreferences(FILENAME,MODE_PRIVATE);
        if(sharedPreferences.getInt("ReminderFlag",0)==1){
            d.setChecked(true);
            reminderFlag=1;
        }
        else {
            d.setChecked(false);
            reminderFlag=0;
        }


        if (ActivityCompat.checkSelfPermission(PermissionSettings.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)
        {     c.setChecked(true);
              c.setEnabled(false);

        }
        else{
            c.setChecked(false);

        }

        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){

                    if( !(ActivityCompat.checkSelfPermission(PermissionSettings.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(PermissionSettings.this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) ){

                        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS};
                        ActivityCompat.requestPermissions(PermissionSettings.this,permissions,1000);
                    }
                }
            }
        });
        d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    reminderFlag=1;

                }
                else{
                    reminderFlag=0;
                }

                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putInt("ReminderFlag",reminderFlag);
                editor.commit();

            }
        });



     }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1000){

            int smsReadPermission = grantResults[0];
            int smsReceivePermission = grantResults[1];

            if(smsReadPermission == PackageManager.PERMISSION_GRANTED && smsReceivePermission == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show();
            }else{

                Toast.makeText(this, "Grant Permissions", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
