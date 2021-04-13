package com.example.erbroker.Activites;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.erbroker.Logic.Customer;
import com.example.erbroker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // getting details information from Ui -> Checking syntax information -> Creating new User
    public void registerUser(View view)
    {
        EditText userText = findViewById(R.id.user_register);
        EditText emailText = findViewById(R.id.email_register);
        EditText passText = findViewById(R.id.pass_register);
        EditText addressText = findViewById(R.id.address_register);
        EditText phoneText = findViewById(R.id.phone_register);

        String name = userText.getText().toString();
        String email = emailText.getText().toString();
        String password = passText.getText().toString();
        String addressName = addressText.getText().toString();
        String phoneNumber = phoneText.getText().toString();
        String brokerName = getIntent().getStringExtra("broker_name");


        if (!name.equals("") && !email.equals("") && !password.equals("") && !addressName.equals("") && !phoneNumber.equals(""))
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                HashMap<String, Customer> usersTable = new HashMap<>();
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();
                                Customer c = new Customer(name, email, addressName, phoneNumber,brokerName);
                                db.collection("Users").document(uid).set(c).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this, "Register success.",
                                                Toast.LENGTH_LONG).show();
                                        MainActivity.startFadingAnimation(RegisterActivity.this, view);
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    }
                                });
                            } else
                                {
                                    if(password.length()<6)
                                    {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, "Register failed - Password must contain at least 6 chars .",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(RegisterActivity.this, "Register failed.",
                                                Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "Wrong Values.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}