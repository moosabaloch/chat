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

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {
    private EditText name, email, password, phone;
    private Firebase firebaseUrl;
    private Button createMyAccount;

    public CreateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        firebaseUrl = new Firebase("https://pcchatapp.firebaseio.com/");
        name = (EditText) view.findViewById(R.id.editTextCreateAccountFragmentFullName);
        email = (EditText) view.findViewById(R.id.editTextCreateAccountFragmentEmailAddress);
        password = (EditText) view.findViewById(R.id.editTextCreateAccountFragmentPassword);
        phone = (EditText) view.findViewById(R.id.editTextCreateAccountFragmentPhone);
        createMyAccount = (Button) view.findViewById(R.id.buttonCreateAccountFragmentCreateAccount);
        createMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();

            }
        });

        return view;
    }

    private void createAccount() {
        final String nameIs = name.getText().toString();
        final String emailIs = email.getText().toString();
        String passIs = password.getText().toString();
        final String phoneIs = phone.getText().toString();
        //Disable fields to interrupt
        name.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);
        phone.setEnabled(false);
        createMyAccount.setEnabled(false);
        //Check blank fields
        if (nameIs.equals("") || emailIs.equals("") || passIs.equals("") || phoneIs.equals("")) {
            Toast.makeText(getActivity(), "Fill Complete Details", Toast.LENGTH_LONG).show();
        } else {
            firebaseUrl.createUser(emailIs, passIs, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> stringObjectMap) {
                    HashMap<String, String> userDetails = new HashMap<>();
                    userDetails.put("email_id", emailIs);
                    userDetails.put("image_url", "");
                    userDetails.put("name", nameIs);
                    userDetails.put("phone", phoneIs);
                    firebaseUrl.child("users").child(stringObjectMap.get("uid").toString()).setValue(userDetails);
                    Toast.makeText(getActivity(), "AccountCreated Successfully", Toast.LENGTH_LONG).show();
                    FragmentManager fragmentManager2 = getFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    LoginFragment loginFragment = new LoginFragment();
                    fragmentTransaction2.addToBackStack("");
                    fragmentTransaction2.hide(CreateAccountFragment.this);
                    fragmentTransaction2.add(android.R.id.content, loginFragment);
                    fragmentTransaction2.commit();

                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Toast.makeText(getActivity(), "Cannot Create Account due to " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    //Enable fields to Retry
                    name.setEnabled(true);
                    email.setEnabled(true);
                    password.setEnabled(true);
                    phone.setEnabled(true);
                    createMyAccount.setEnabled(true);

                }
            });
        }


    }


}
