package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class giris extends AppCompatActivity {

    private EditText editTextEmail,editTextPassword, editIsim;
    private String txtEmail, txtSifre,txtIsim;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private HashMap<String, String>mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editIsim=(EditText)findViewById(R.id.editTextisim);
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
    }

    public void kayitOl(View v)
    {
        txtEmail = editTextEmail.getText().toString();
        txtSifre = editTextPassword.getText().toString();
        txtIsim  = editIsim.getText().toString();

        if(!TextUtils.isEmpty(txtIsim) && !TextUtils.isEmpty(txtEmail)&& !TextUtils.isEmpty(txtSifre))
        {
            mAuth.createUserWithEmailAndPassword(txtEmail,txtSifre)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                mUser = mAuth.getCurrentUser();
                                mData = new HashMap<>();
                                mData.put("KullaniciAdi",txtIsim);
                                mData.put("KulaniciEmail",txtEmail);
                                mData.put("KulaniciSifresi",txtSifre);
                                mData.put("KullaniciId",mUser.getUid());

                                mReference.child("Kullanicilar").child(mUser.getUid())
                                                .setValue(mData)
                                                        .addOnCompleteListener(giris.this, new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                              if(task.isSuccessful())
                                                              {
                                                                  Toast.makeText(giris.this,"Kayıt İşlemı Başarılı",Toast.LENGTH_SHORT).show();
                                                                  Intent intent = new Intent(giris.this, girishesap.class);
                                                                  startActivity(intent);
                                                              }
                                                              else
                                                              {
                                                                  Toast.makeText(giris.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                              }
                                                            }
                                                        });
                            }
                            else
                                Toast.makeText(giris.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
            Toast.makeText(this,"Email ve Şifre Boş olamaz.",Toast.LENGTH_SHORT).show();
    }
}