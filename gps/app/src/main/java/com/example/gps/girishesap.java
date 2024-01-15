package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class girishesap extends AppCompatActivity {
    private EditText editTextEmail,editTextPassword;
    private String txtEmail, txtSifre;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girishesap);
        editTextEmail = (EditText)findViewById(R.id.giriseditemail);
        editTextPassword=(EditText)findViewById(R.id.giriseditsifre);
        mAuth = FirebaseAuth.getInstance();
        Button btnkayit = findViewById(R.id.btnkayit);
        btnkayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tıklama işlemlerini buraya ekleyin
                Intent intent = new Intent(girishesap.this, giris.class);
                startActivity(intent);
            }
        });
    }

    public void GirisYap(View view)
    {
        txtEmail = editTextEmail.getText().toString();
        txtSifre = editTextPassword.getText().toString();
        if(!TextUtils.isEmpty(txtEmail) && ! TextUtils.isEmpty(txtSifre))
        {
            mAuth.signInWithEmailAndPassword(txtEmail,txtSifre)
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            mUser = mAuth.getCurrentUser();
                            Toast.makeText(girishesap.this,"Giriş Başarılı",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(girishesap.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(girishesap.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
            Toast.makeText(this,"Email ve Şifre Boş Olamaz",Toast.LENGTH_SHORT).show();
    }
}