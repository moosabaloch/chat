package pana.com.chat.ui.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.DataModel.DataModelUser;
import pana.com.chat.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    Boolean alreadyLoggedIn = false;
    private Firebase.AuthStateListener ASL;
    private Button loginButton, createAccountButton;
    private EditText emailToLogin, passwordToLogin;
    private Firebase firebaseUrl= new Firebase("https://pcchatapp.firebaseio.com/");

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("LoginFrag","onCreateView");
       // firebaseUrl = new Firebase("https://pcchatapp.firebaseio.com/");

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        loginButton = (Button) view.findViewById(R.id.buttonLoginFragmentLogin);
        createAccountButton = (Button) view.findViewById(R.id.buttonLoginFragmentCreateAccount);

        emailToLogin = (EditText) view.findViewById(R.id.editTextLoginFragmentEmailAddress);
        passwordToLogin = (EditText) view.findViewById(R.id.editTextLoginFragmentPassword);



        loginButton.setEnabled(false);
        createAccountButton.setEnabled(false);

        clickListeners();
        return view;
    }

    private void addAuthStateListener() {
        firebaseUrl.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                ASL = this;
                authentication(authData);
            }
        });
    }

    private void clickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void authentication(AuthData authData) {
        if (authData != null) {
            Log.d("Login Fragment......", "User Logged in with ID:" + authData.getUid());
            alreadyLoggedIn = true;
            getUserData(authData);
        } else {
            Log.d("Login Fragment......", "User Not Logged in...");
            alreadyLoggedIn = false;
            loginButton.setEnabled(true);
            createAccountButton.setEnabled(true);

        }
    }

    private void login() {

        Log.d("Login Fragment......", "Login Button Clicked");

        String email = emailToLogin.getText().toString();
        String passIs = passwordToLogin.getText().toString();

        if (email.equals("") || passIs.equals("")) {
            Toast.makeText(getActivity(), "Email Or Password Field is Empty", Toast.LENGTH_LONG).show();
        } else {
            loginButton.setEnabled(false);
            emailToLogin.setEnabled(false);
            passwordToLogin.setEnabled(false);
            createAccountButton.setEnabled(false);

            firebaseUrl.authWithPassword(email, passIs, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {

                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    loginButton.setEnabled(true);
                    emailToLogin.setEnabled(true);
                    passwordToLogin.setEnabled(true);
                    createAccountButton.setEnabled(true);
                    Toast.makeText(getActivity(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void createAccount() {
        getFragmentManager().beginTransaction()
                .addToBackStack("")
                .replace(R.id.fragment, new CreateAccountFragment())
                .commit();
    }

    private void getUserData(final AuthData authData) {

        Log.d("Login Fragment......", "Getting user Data of ID:" + authData.getUid());

        firebaseUrl.child("users").child(authData.getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("Login Fragment......", "Getting user Data.... OnDataChanged");

                DataModelUser dataModelUser;//=new DataModelUser();
                dataModelUser = dataSnapshot.getValue(DataModelUser.class);

                Log.d("User Data", dataModelUser.toString());

                DataModelMeSingleton.getInstance().setId(authData.getUid());
                DataModelMeSingleton.getInstance().setImageUrl(dataModelUser.getImage_url());
                DataModelMeSingleton.getInstance().setName(dataModelUser.getName());
                DataModelMeSingleton.getInstance().setPhone(dataModelUser.getPhone());
                DataModelMeSingleton.getInstance().setEmail(dataModelUser.getEmail_id());

                switchToHomeFrag();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getActivity(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                firebaseUrl.unauth();
                loginButton.setEnabled(true);
                createAccountButton.setEnabled(true);
            }
        });
    }

    private void switchToHomeFrag() {
        Log.d("Login Fragment......", "Switching to Home Fragment");
        //  getFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();
        FragmentInterActionListener fragmentInterActionListener = (FragmentInterActionListener) getActivity();
        fragmentInterActionListener.switchToHome();
    }

    @Override
    public void onResume() {
        Log.d("LoginFrag","onResume-> Adding ASL");
        super.onResume();
        addAuthStateListener();
    }

    @Override
    public void onPause() {
        Log.d("LoginFrag","onPause-> Removing ASL");
        super.onPause();
        if (ASL != null) {
            Log.d("Login Fragment......", "Removing Authentication State Listener");
            firebaseUrl.removeAuthStateListener(ASL);
        }
    }

    @Override
    public void onDestroyView() {
        Log.d("LoginFrag","onDestroyView");

        super.onDestroyView();

    }

    public interface FragmentInterActionListener {
        void switchToHome();

    }
}
