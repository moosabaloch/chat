package pana.com.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
        createNewAccount();
        loginButtonAction();
        return view;
    }

    private void loginButtonAction() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailToLogin.getText().toString();
                String passIs = passwordToLogin.getText().toString();

                if (email.equals("") || passIs.equals("")) {
                    Toast.makeText(getActivity(), "Email Or Password Field is Empty", Toast.LENGTH_LONG).show();
                } else {
                    loginButton.setEnabled(false);
                    emailToLogin.setEnabled(false);
                    passwordToLogin.setEnabled(false);
                    firebaseUrl.authWithPassword(email, passIs, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(final AuthData authData) {
                            firebaseUrl.child("users").child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    DataModelMeSingleton.getInstance().setId(authData.getUid());
                                    DataModelMeSingleton.getInstance().setImageUrl(dataSnapshot.child("image_url").getValue().toString());
                                    DataModelMeSingleton.getInstance().setName(dataSnapshot.child("name").getValue().toString());
                                    DataModelMeSingleton.getInstance().setPhone(dataSnapshot.child("phone").getValue().toString());
                                    FragmentManager fragmentManager2 = getFragmentManager();
                                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                                    FriendsFragment friendsFragment = new FriendsFragment();
                                    fragmentTransaction2.addToBackStack("");
                                    fragmentTransaction2.hide(LoginFragment.this);
                                    fragmentTransaction2.add(android.R.id.content, friendsFragment);
                                    fragmentTransaction2.commit();
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            loginButton.setEnabled(true);
                            emailToLogin.setEnabled(true);
                            passwordToLogin.setEnabled(true);

                            Toast.makeText(getActivity(), "Error Login " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });
    }

    private void createNewAccount() {
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                CreateAccountFragment createAccountFragment = new CreateAccountFragment();
                fragmentTransaction2.addToBackStack("");
                fragmentTransaction2.hide(LoginFragment.this);
                fragmentTransaction2.add(android.R.id.content, createAccountFragment);
                fragmentTransaction2.commit();
            }
        });
    }
}
