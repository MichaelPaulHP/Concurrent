package com.example.mrrobot.concurrent;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity
        implements View.OnClickListener{

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]


    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private TextInputEditText editTextEmailAddress;
    private TextInputEditText editTextPassword;
    TextInputLayout emailLayout;
    TextInputLayout passLayout;
    private RadioButton radioButton;
    private Button btnSignIn;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLayout =(TextInputLayout) findViewById(R.id.email_input_layout);
        passLayout =(TextInputLayout) findViewById(R.id.pass_input_layout);
        this.editTextEmailAddress = (TextInputEditText)findViewById(R.id.inputEmail);
        this.editTextPassword = (TextInputEditText)findViewById(R.id.inputPass);
        this.radioButton =(RadioButton) findViewById(R.id.radioIslogIn);
        this.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    loginActivity.this.btnSignIn.setText("Crear Cuenta");
                    loginActivity.this.radioButton.setText("Iniciar sesion");
                }
                else{
                    loginActivity.this.btnSignIn.setText("Iniciar sesion");
                    loginActivity.this.radioButton.setText("¿No tienes Una Cuenta? CREAR");
                }
            }
        });
        btnSignIn=(Button) findViewById(R.id.sign_in);
        this.btnSignIn.setOnClickListener(this);




        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        // [END initialize_auth]
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);
    }
    private void signInEmailPassOrCreate() {
        if(this.radioButton.isChecked()){
            createWithEmailAndPass();
        }
        else {
            signInEmailPass();
        }
    }


    // [START onactivityresult]

    // [END onactivityresult]

    // [START auth_with_google]
    // [END auth_with_google]


    // [START signin]
   /* private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }*/

    private boolean isFormCorrect(){

        String email=this.emailLayout.getEditText().getText().toString();
        String pass = this.passLayout.getEditText().getText().toString();

        if( email.isEmpty() && pass.isEmpty()){
            this.emailLayout.setError("Ingrese Email");
            this.passLayout.setError("ingrese Contraseña");
            return false;

        }
        else{
            return true;
        }
        //return !(email==""  && pass=="");
    }
    private void signInEmailPass() {

        if (!isFormCorrect()) {
            //showAlert("Error","Ingrese Sus  Datos");
            return;
        }
        String email=this.emailLayout.getEditText().getText().toString();
        String password=this.passLayout.getEditText().getText().toString();
        //showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            /*.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();*/
                            //showAlert("Datos Incorrectos","Email o Password Incorrecto");
                            //updateUI(null);
                            loginActivity.this.emailLayout.setError("Email Incorrecto");
                            loginActivity.this.emailLayout.setError("Contrasela Incorecto");
                        }

                        // [START_EXCLUDE]
                        /*if (!task.isSuccessful()) {
                            mStatusTextView.setText("R.string.auth_failed");
                        }*/
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }
    // [END signin]
    private void createWithEmailAndPass(){
        if (!isFormCorrect()) {
            //showAlert("Error","Ingrese Sus  Datos");
            return;
        }
        String email=this.emailLayout.getEditText().getText().toString();
        String password=this.passLayout.getEditText().getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUserInDB();
                            createUserInServer();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            loginActivity.this.emailLayout.setError("Email Incorrecto");
                            loginActivity.this.emailLayout.setError("Contraseña Incorrecto");
                        }

                        // ...
                    }
                });
    }
    /*private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }*/
    private void createUserInDB(){
        User.getCurrentUser().save();
    }
    private void createUserInServer(){
        SocketIO.saveThisUser(User.getCurrentUser().getId());
    }

    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {
            // iniciar MainActivity

            Intent intent   = new Intent(loginActivity.this,MainActivity.class);
            startActivity(intent);

            /*mStatusTextView.setText(user.getEmail());
            mDetailTextView.setText(user.getUid());

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);*/
        } else {
            // error
            //this.statusTextView.setText("Algo esta Mal");
            /*mStatusTextView.setText("R.string.signed_out");
            mDetailTextView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);*/
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in) {
            signInEmailPassOrCreate();
        }
    }


}
