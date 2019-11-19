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
import android.widget.Toast;

import com.example.mrrobot.concurrent.Firebase.Auth;
import com.example.mrrobot.concurrent.Services.ConcurrentService.ConcurrentApiClient;
import com.example.mrrobot.concurrent.Services.SocketIO;

import com.example.mrrobot.concurrent.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class loginActivity extends AppCompatActivity
        implements View.OnClickListener, ISaveUserTask,CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]



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

        this.radioButton =(Switch) findViewById(R.id.swIslogIn);
        this.progressBar= (ProgressBar) findViewById(R.id.progressBarLogin);

        this.radioButton.setOnCheckedChangeListener(this);
        btnSignIn=(Button) findViewById(R.id.sign_in);
        this.btnSignIn.setOnClickListener(this);





        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        // [END initialize_auth]
    }

    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            loginActivity.this.btnSignIn.setText("Crear Cuenta");
            loginActivity.this.radioButton.setText("Iniciar sesion");
        }
        else{
            loginActivity.this.btnSignIn.setText("Iniciar sesion");
            loginActivity.this.radioButton.setText("¿No tienes Una Cuenta? CREAR");
        }
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
    private void showMessage(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
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

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
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

                            FirebaseUser user = mAuth.getCurrentUser();

                            SaveUserTask saveUserTask = new SaveUserTask(loginActivity.this);
                            saveUserTask.execute(false);

                        } else {
                            showProgress(false);

                            loginActivity.this.emailLayout.setError("Email Incorrecto");
                            loginActivity.this.passLayout.setError("Contraseña Incorrecto");
                        }

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
    @Override
    public void onError(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // SHOW MESSAGE
                showMessage(message);
                showProgress(false);
            }
        });
    }

    protected static class SaveUserTask extends AsyncTask<Boolean,Object,Boolean>{

        private ISaveUserTask iSaveUserTask;
        private boolean isComplete=false;
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
            User.getCurrentUser().save().addOnSuccessListener(onSuccessInFirebase);
            while(!isComplete){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            this.iSaveUserTask.onComplete();
        }

        OnSuccessListener onSuccessInFirebase=new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                User user =User.getCurrentUser();
                ConcurrentApiClient
                        .getConcurrentApiService()
                        .registerUser(user.getIdGoogle(),
                                user.getName(),
                                user.getName(),
                                "hola").enqueue(saveInServer);

            }
        };



        Callback<String> saveInServer=  new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code()==200){
                    isComplete=true;
                }
                Timber.d(response.toString());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                iSaveUserTask.onError(t.getMessage());
            }
        };

    }


}

 interface ISaveUserTask{
    void onPreExecute();
    void onComplete();
    void onError(String message);
}