package com.example.pokedex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmailPasswordActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    String userID;

    EditText money, items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        ImageButton backButton = findViewById(R.id.buttonBack);

        backButton.setOnClickListener(v -> {
            // Manejar el clic en el botón de flecha
            Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Opcional, dependiendo de si quieres que la actividad actual permanezca en la pila de actividades
        });

        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        EditText nameEditText = findViewById(R.id.editTextName);

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        Button registerButton = findViewById(R.id.buttonConfirm);
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String name = nameEditText.getText().toString();

            createAccount(email, password, name);
        });
    }

    void saveData(String name, String email, FirebaseUser currentUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("nombre", name);
        user.put("email", email);
        user.put("money", 1000);
        user.put("items", new HashMap<String, Integer>()); // Cambiado a mapa para manejar cantidades
        user.put("pokemons", new ArrayList<>());

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("MainActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
                        updateProfile(currentUser, name);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();

        if (currentUser != null) {
            reload();
        }
    }

    private void createAccount(String email, String password, String name) {
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = fAuth.getCurrentUser();
                        userID = fAuth.getCurrentUser().getUid();
                        saveData(name, email, currentUser);


                    } else {
                        // Si el registro falla, muestra un mensaje al usuario
                        if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(EmailPasswordActivity.this, "La contraseña es demasiado débil.",
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(EmailPasswordActivity.this, "Las credenciales son inválidas.",
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(EmailPasswordActivity.this, "Ya existe una cuenta con este correo electrónico.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EmailPasswordActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void reload() {
    }

    private void updateProfile(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        Toast.makeText(EmailPasswordActivity.this, "Te has registrado correctamente.",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}
