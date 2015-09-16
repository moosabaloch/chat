package pana.com.chat;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    private Button loginButton, createAccountButton;
    private EditText emailToLogin, passwordToLogin;
    private Firebase firebaseUrl;
    Boolean alreadyLoggedIn=false;
    Firebase.AuthStateListener ASL;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        loginButton = (Button) view.findViewById(R.id.buttonLoginFragmentLogin);
        createAccountButton = (Button) view.findViewById(R.id.buttonLoginFragmentCreateAccount);

        emailToLogin = (EditText) view.findViewById(R.id.editTextLoginFragmentEmailAddress);
        passwordToLogin = (EditText) view.findViewById(R.id.editTextLoginFragmentPassword);

        firebaseUrl = new Firebase("https://pcchatapp.firebaseio.com/");

        loginButton.setEnabled(false);
        createAccountButton.setEnabled(false);

        firebaseUrl.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                ASL=this;
                if (authData != null) {
                    Log.d("Login Fragment......", "User Already Logged in with ID:"+authData.getUid());
                    alreadyLoggedIn=true;
                    getUserData(authData);
                } else {
                    Log.d("Login Fragment......", "User Not Logged in...");
                    alreadyLoggedIn=false;
                    loginButton.setEnabled(true);
                    createAccountButton.setEnabled(true);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!alreadyLoggedIn)
                    firebaseUrl.removeAuthStateListener(ASL);

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
                            if (authData != null) {
                                getUserData(authData);
                            }
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            loginButton.setEnabled(true);
                            emailToLogin.setEnabled(true);
                            passwordToLogin.setEnabled(true);
                            createAccountButton.setEnabled(true);
                            Toast.makeText(getActivity(),firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new CreateAccountFragment())
                        .commit();
            }
        });

        return view;
    }

    private void getUserData(final AuthData authData) {

        Log.d("Login Fragment......", "Getting user Data of ID:"+authData.getUid());

        firebaseUrl.child("users").child(authData.getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("Login Fragment......", "Getting user Data.... OnDataChanged");

                DataModelUser dataModelUser=new DataModelUser();
                dataModelUser=dataSnapshot.getValue(DataModelUser.class);

                Log.d("User Data",dataModelUser.toString());

                DataModelMeSingleton.getInstance().setId(authData.getUid());
                DataModelMeSingleton.getInstance().setImageUrl(dataModelUser.getImage_url());
                DataModelMeSingleton.getInstance().setName(dataModelUser.getName());
                DataModelMeSingleton.getInstance().setPhone(dataModelUser.getPhone());

                switchToHomeFrag();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getActivity(),firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
                firebaseUrl.unauth();
                loginButton.setEnabled(true);
                createAccountButton.setEnabled(true);
            }
        });
    }

    private void switchToHomeFrag() {
        Log.d("Login Fragment......", "Switching to Home Fragment");
        getFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ASL!=null){
            Log.d("Login Fragment......", "Removing Authentication State Listener");
            firebaseUrl.removeAuthStateListener(ASL);
        }
    }
}
