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

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    private Button loginButton, createAccountButton;
    private EditText emailToLogin, passwordToLogin;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        loginButton = (Button) view.findViewById(R.id.buttonLoginFragmentLogin);
        createAccountButton = (Button) view.findViewById(R.id.buttonLoginFragmentCreateAccount);
        emailToLogin = (EditText) view.findViewById(R.id.editTextLoginFragmentEmailAddress);
        passwordToLogin = (EditText) view.findViewById(R.id.editTextLoginFragmentPassword);
        createNewAccount();
        loginButtonAction();
        return view;
    }

    private void loginButtonAction() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                FriendsFragment friendsFragment = new FriendsFragment();
                fragmentTransaction2.addToBackStack("");
                fragmentTransaction2.hide(LoginFragment.this);
                fragmentTransaction2.add(android.R.id.content, friendsFragment);
                fragmentTransaction2.commit();
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
