package com.roh44x.davinciapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createSignInIntent();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls("https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String name = user.getDisplayName();
                String email = user.getEmail();
                String id = user.getUid();

                User userLogin = new User(email, name, "");
                mDatabase.child(id).setValue(userLogin).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(MainActivity.this, DomainSelect.class));
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }
}
