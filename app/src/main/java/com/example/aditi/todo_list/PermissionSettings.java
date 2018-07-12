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
import android.widget.Toast;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class PermissionSettings extends AppCompatActivity {
    CheckBox c;

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
        c = findViewById(R.id.checkbox);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)
        {     c.setChecked(true);
              c.setEnabled(false);

        }
        else{
            c.setChecked(false);

        }



     }

    public void onCheckboxClicked(View view) {
            String[] permissions = {Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            int callGrantResult = grantResults[0];
            if (callGrantResult == PackageManager.PERMISSION_GRANTED) {
                c.setChecked(true);
            } else {
               c.setChecked(false);
            }


        }


    }

}
