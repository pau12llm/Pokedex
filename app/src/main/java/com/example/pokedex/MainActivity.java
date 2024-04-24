package com.example.pokedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//NOMBRE APPI --> pokeapi.co
public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);

        Button signInButton = findViewById(R.id.buttonSignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signIn(email,password);
            }
        });

        TextView textView = findViewById(R.id.registerNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí inicia la actividad EmailPasswordActivity para iniciar sesión
                startActivity(new Intent(MainActivity.this, EmailPasswordActivity.class));

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, TabBarActivity.class));
            finish(); // Finaliza la actividad actual para que el usuario no pueda volver atrás con el botón de retroceso
        }
    }



    private void reload() { }
    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            startActivity(new Intent(MainActivity.this, TabBarActivity.class));
                            Toast.makeText(MainActivity.this, "Authentication ok.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
}