package com.example.root.myapplication.user_sign;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.myapplication.MainActivity;
import com.example.root.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button btnReg;
    private TextInputLayout inEmail,inPass;

    private FirebaseAuth fAuth;
    private DatabaseReference fUsersDatabase;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnReg = (Button) findViewById(R.id.signup_btn);
        inEmail = (TextInputLayout) findViewById(R.id.input_reg_email);
        inPass =  (TextInputLayout) findViewById(R.id.input_reg_pass);

        fAuth = FirebaseAuth.getInstance();
        fUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uemail = inEmail.getEditText().getText().toString().trim();
                String upass = inPass.getEditText().getText().toString().trim();

                registerUser(uemail,upass);
            }
        });
    }

    private void registerUser(final String email, String password){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Proccesing your request, please wait...");

        progressDialog.show();

        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                   fUsersDatabase.child(fAuth.getCurrentUser().getUid())
                           .child("basic").child("email").setValue(email)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){

                               progressDialog.dismiss();

                               Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                               startActivity(mainIntent);
                               finish();
                               Toast.makeText(RegisterActivity.this, "User created:", Toast.LENGTH_SHORT).show();

                           }else{
                               progressDialog.dismiss();
                               Toast.makeText(RegisterActivity.this, "EROOR: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       }
                   });


                }else{

                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "EROOR: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


}
