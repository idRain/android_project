package com.example.jump_game;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView result_object, result_label;
    private RelativeLayout main_wrapper, registration_wrapper;
    private EditText email, password;
    private Button authentication_btn;
    private int current_result_value;

    //Authentication
    private FirebaseAuth mAuth;

    //Firebase DB
    DatabaseReference myRef;

    //local DB
    SQLiteDatabase localDB;

    private void changeAppState() {
        Cursor query;
        if (mAuth.getCurrentUser() == null) {
            authentication_btn.setText(R.string.signing_in);
            query = localDB.rawQuery("SELECT result FROM users where id= 'unknown'", null);
        } else {
            authentication_btn.setText(R.string.change_account);
            query = localDB.rawQuery("SELECT result FROM users where id= '" + mAuth.getCurrentUser().getUid() + "'", null);
        }
        query.moveToFirst();
        current_result_value = query.getInt(0);
        query.close();
        result_label.setText(R.string.current_best_result_label);
        result_object.setText(String.valueOf(current_result_value));
        result_label.setTextColor(getResources().getColor(R.color.main_text_color));
        result_object.setTextColor(getResources().getColor(R.color.main_text_color));
    }

    private void closeRegistrationForm() {
        main_wrapper.setVisibility(View.VISIBLE);
        registration_wrapper.setVisibility(View.INVISIBLE);
    }

    private void openRegistrationForm() {
        main_wrapper.setVisibility(View.INVISIBLE);
        registration_wrapper.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //field for email and password
        this.email = findViewById(R.id.input_email);
        this.password = findViewById(R.id.input_password);
        this.authentication_btn = (Button) findViewById(R.id.authentication);

        //result
        current_result_value = 0;
        result_label = findViewById(R.id.result_label);
        result_object = findViewById(R.id.result_value);

        //Authentication
        mAuth = FirebaseAuth.getInstance();

        //Firebase DB
        myRef = FirebaseDatabase.getInstance().getReference().child("users");

        //local DB
        localDB = getBaseContext().openOrCreateDatabase("users_results.db", MODE_PRIVATE, null);
        localDB.execSQL("CREATE TABLE IF NOT EXISTS users (id TEXT, result INTEGER)");
        Cursor query = localDB.rawQuery("SELECT * FROM users where id= 'unknown'", null);
        if (!query.moveToFirst())
            localDB.execSQL("INSERT INTO users (id, result) VALUES ('unknown', 0)");

        changeAppState();

        //get registration and main screens
        main_wrapper = findViewById(R.id.main_wrapper);
        registration_wrapper = findViewById(R.id.registration_wrapper);

        registration_wrapper.setVisibility(View.INVISIBLE);

        //get arguments from another activities
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            int new_result = arguments.getInt("result_value");
            //current best result
            if (mAuth.getCurrentUser() == null) {
                query = localDB.rawQuery("SELECT result FROM users where id= 'unknown'", null);
            } else {
                query = localDB.rawQuery("SELECT result FROM users where id= '" + mAuth.getCurrentUser().getUid() + "'", null);
            }
            query.moveToFirst();
            if (new_result == -1) {
                changeAppState();
            }else if (new_result > query.getInt(0)) {
                current_result_value = new_result;
                //local DB
                if (mAuth.getCurrentUser() == null) {
                    localDB.execSQL("UPDATE users SET result= " + String.valueOf(new_result) + " WHERE id= 'unknown'");
                } else {
                    localDB.execSQL("UPDATE users SET result= " + String.valueOf(new_result) + " WHERE id= '" + mAuth.getCurrentUser().getUid() + "'");
                }
                result_label.setText(R.string.new_best_result_label);
                result_label.setTextColor(getResources().getColor(R.color.best_result_text_color));
                result_object.setTextColor(getResources().getColor(R.color.best_result_text_color));

                //cloud DB
                if (mAuth.getCurrentUser() != null) {
                    myRef.child(mAuth.getCurrentUser().getUid()).setValue(new_result);
                }
            } else {
                current_result_value = new_result;
                result_label.setText(R.string.current_result_label);
                result_label.setTextColor(getResources().getColor(R.color.current_result_text_color));
                result_object.setTextColor(getResources().getColor(R.color.current_result_text_color));
            }
        }

        //Cursor close
        query.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        result_object.setText(String.valueOf(current_result_value));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refuse: {
                closeRegistrationForm();
            }
            break;
            case R.id.send_form: {
                String email_text = email.getText().toString();
                String password_text = password.getText().toString();
                signing(email_text, password_text);
            }
            break;
            case R.id.start: {
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.authentication: {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    mAuth.signOut();
                    changeAppState();
                }
                openRegistrationForm();
                Toast.makeText(MainActivity.this, "Войдите или зарегистрируйтесь", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.exit: {
                finish();
            }
        }
    }

    //Authentication
    private void signing(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    myRef.child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                //get data from cloud
                                int result_from_cloud = task.getResult().getValue(Integer.class);

                                //local BD
                                Cursor query = localDB.rawQuery("SELECT result FROM users where id= '" + mAuth.getCurrentUser().getUid() + "'", null);
                                if (!query.moveToFirst()) {
                                    localDB.execSQL("INSERT INTO users (id, result) VALUES ('" + mAuth.getCurrentUser().getUid() + "', " + String.valueOf(result_from_cloud) + ")");
                                } else {
                                    int result_from_localDB = query.getInt(0);
                                    if (result_from_localDB > result_from_cloud) {
                                        myRef.child(mAuth.getCurrentUser().getUid()).setValue(result_from_localDB);
                                    } else if (result_from_localDB < result_from_cloud) {
                                        localDB.execSQL("UPDATE users SET result= " + result_from_cloud + " WHERE id= '" + mAuth.getCurrentUser().getUid() + "'");
                                    }
                                }

                                changeAppState();

                                //cursor close
                                query.close();
                                Toast.makeText(MainActivity.this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                                closeRegistrationForm();
                            }
                        }
                    });
                } else {
                    registration(email, password);
                }
            }
        });
    }

    private void registration(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //local BD
                    localDB.execSQL("INSERT INTO users (id, result) VALUES ('" + mAuth.getCurrentUser().getUid() +"', 0)");
                    myRef.child(mAuth.getCurrentUser().getUid()).setValue(0);
                    changeAppState();
                    Toast.makeText(MainActivity.this, "Регистрация выполнена успешно", Toast.LENGTH_SHORT).show();
                    closeRegistrationForm();
                } else {
                    Toast.makeText(MainActivity.this, "Некорректный email или пароль", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}