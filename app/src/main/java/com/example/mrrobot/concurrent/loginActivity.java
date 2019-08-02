package com.example.mrrobot.concurrent;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mrrobot.concurrent.Firebase.Auth;
import com.example.mrrobot.concurrent.Services.SocketIO;

import com.example.mrrobot.concurrent.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.socket.client.Socket;

public class loginActivity extends AppCompatActivity
        implements View.OnClickListener, ISaveUserTask {

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
    private Switch radioButton;
    private Button btnSignIn;
    private TextView statusTextView;
    private  ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLayout =(TextInputLayout) findViewById(R.id.email_input_layout);
        passLayout =(TextInputLayout) findViewById(R.id.pass_input_layout);
        this.editTextEmailAddress = (TextInputEditText)findViewById(R.id.inputEmail);
        this.editTextPassword = (TextInputEditText)findViewById(R.id.inputPass);
        this.radioButton =(Switch) findViewById(R.id.swIslogIn);
        this.progressBar= (ProgressBar) findViewById(R.id.progressBarLogin);
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
        String email=this.emailLayout.getEditText().getText().toString();
        String password=this.passLayout.getEditText().getText().toString();
        if(isFormCorrect(email,password)){

            showProgress(true);
            if(this.radioButton.isChecked()){

                createWithEmailAndPass(email,password);
            }
            else {
                signInEmailPass(email,password);
            }
        }

    }

    private boolean isFormCorrect(String email,String password){

        if( email.isEmpty() && password.isEmpty()){
            this.emailLayout.setError("Ingrese Email");
            this.passLayout.setError("ingrese Contraseña");
            return false;

        }
        else{
            return true;
        }
        //return !(email==""  && pass=="");
    }
    private void signInEmailPass(String email,String password) {

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

                            showProgress(false);
                            loginActivity.this.emailLayout.setError("Email Incorrecto");
                            loginActivity.this.emailLayout.setError("Contrasela Incorecto");
                        }

                    }
                });
        // [END sign_in_with_email]
    }
    // [END signin]
    private void createWithEmailAndPass(String email,String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            SaveUserTask saveUserTask = new SaveUserTask(loginActivity.this);
                            saveUserTask.execute(false);

                        } else {
                            showProgress(false);
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
    private static void createUserInDB(){
        User.getCurrentUser().save();
    }


    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {
            // iniciar MainActivity

            Intent intent   = new Intent(loginActivity.this,MainActivity.class);
            startActivity(intent);


        } else {

        }
    }
    private void startMainActivity(){
        Intent intent   = new Intent(loginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void showProgress(Boolean show){

        loginActivity.this.progressBar.setVisibility(show?View.VISIBLE:View.GONE);
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in) {
            signInEmailPassOrCreate();
        }
    }

    @Override
    public void onPreExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgress(true);
            }
        });
    }

    @Override
    public void onComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        });
    }

    protected static class SaveUserTask extends AsyncTask<Boolean,Object,Boolean>{
        private ISaveUserTask iSaveUserTask;
        public SaveUserTask(ISaveUserTask iSaveUserTask) {
            this.iSaveUserTask = iSaveUserTask;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            this.iSaveUserTask.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {

            FirebaseAuth mAuth;
            while(Auth.getInstance()==null){

            }
            Socket socket = SocketIO.getSocket();
            while (!socket.connected()){

            }
            createUserInDB();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            this.iSaveUserTask.onComplete();
        }


    }

}
 interface ISaveUserTask{
    void onPreExecute();
    void onComplete();
}