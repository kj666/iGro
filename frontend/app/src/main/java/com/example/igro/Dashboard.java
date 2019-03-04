package com.example.b_ngh.igrobaltej;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {
    private Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        logout =  (Button) findViewById(R.id.logout);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(Dashboard.this, MainActivity.class);
                startActivity(i);
                finish();
                Toast.makeText(Dashboard.this,"Log Out Successfull", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
