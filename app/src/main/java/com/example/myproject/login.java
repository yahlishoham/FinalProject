package com.example.myproject;
import com.example.myproject.UserDetails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {
    EditText email;
    EditText password;
    Button Login;
    Button BackToSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Login = findViewById(R.id.Login);
        BackToSignup = findViewById(R.id.BackToSignup);

        UserDetails two = new UserDetails("yahli@email", "02937");

        Login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                two.setUserEmail(userEmail);
                two.setUserPwd(userPassword);
            }
        });
        BackToSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent third = new Intent(login.this , register.class);

                startActivity(third);

            }});
    }
        public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.main, menu);

            return true;

    }
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.retToIntro) {
            Intent intent = new Intent( login.this , MainActivity.class);

            startActivity(intent);

            return true;
        }
        if (id ==R.id.closeApp){
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }
}