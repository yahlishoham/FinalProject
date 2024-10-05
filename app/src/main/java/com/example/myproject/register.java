package com.example.myproject;
import com.example.myproject.UserDetails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class register extends AppCompatActivity {
    ImageView icon;
    EditText name;
    EditText email;
    EditText password;
    EditText password2;
    EditText phoneNumber;
    Button register;
    Button moveToLogin;
    Button backToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        phoneNumber = findViewById(R.id.phoneNumber);
        register = findViewById(R.id.register);
        moveToLogin = findViewById(R.id.moveToLogin);
        icon = findViewById(R.id.icon);
        backToMain = findViewById(R.id.backToMain);

        String massage = "Happy you started";
        Toast.makeText(register.this, massage, Toast.LENGTH_SHORT).show();

        UserDetails one = new UserDetails("yahli", "yahli@email", "yahli123", "0284277437");

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                String userPhoneNumber = phoneNumber.getText().toString();

                one.setUserName(userName);
                one.setUserEmail(userEmail);
                one.setUserPwd(userPassword);
                one.setUserPhone(userPhoneNumber);

            }});
        moveToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent second = new Intent(register.this , login.class);

                startActivity(second);

            }});
        backToMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent first = new Intent(register.this , MainActivity.class);

                startActivity(first);

            }});
    }
}