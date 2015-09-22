package pana.com.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE = 1;
    private static final int RESULT_CROP = 2;
    DataModelMeSingleton ME;
    Firebase pcchatapp;
    ArrayList friendsID, conversationID;
    ArrayList<DataModelUser> friendsData;
    ListView listView;
    Button btn_profile, btn_groups, btn_friends, btn_requests, btn_logout;
    TextView tv;
    String TAG = "HOME FRAGMENT.....";
    int count;
    Uri selectedImageUri = null;
    String selectedImagePath;
    ImageView imageView;
    private Bitmap bitmap;

    public HomeFragment() {
        // Required empty public constructor
    }

    private static String saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Chat/ProfileImages");

        if (!myDir.exists())
            myDir.mkdirs();

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image_" + n + ".jpg";
        File file = new File(myDir, fname);

        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root + "/Chat/ProfileImages/" + fname;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        pcchatapp = new Firebase("https://pcchatapp.firebaseio.com/");

        friendsID = new ArrayList();
        conversationID = new ArrayList();
        friendsData = new ArrayList<DataModelUser>();

        ME = DataModelMeSingleton.getInstance();

        tv = (TextView) view.findViewById(R.id.hometxtconvo);

        btn_profile = (Button) view.findViewById(R.id.homebtnprofile);
        btn_groups = (Button) view.findViewById(R.id.homebtngroups);
        btn_friends = (Button) view.findViewById(R.id.homebtnfriend);
        btn_requests = (Button) view.findViewById(R.id.homebtnrequest);
        btn_logout = (Button) view.findViewById(R.id.homebtnlogout);

        btn_groups.setOnClickListener(this);
        btn_friends.setOnClickListener(this);
        btn_requests.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_profile.setOnClickListener(this);
        btn_profile.setText(ME.getName());

        listView = (ListView) view.findViewById(R.id.home_lv_chats);
//
        pcchatapp.child("user_friend").child(ME.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "ON DATA CHANGE");
                Log.d(TAG, "MY ID:" + ME.getId());

                friendsData.clear();
                friendsID.clear();
                conversationID.clear();
                count = 0;
                tv.setText(count + " Conversations");
                listView.setAdapter(new CustomFriendsListAdapter(getActivity(), friendsID, friendsData));
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        HashMap<String, Object> hashMap = (HashMap<String, Object>) d.getValue();
                        if (!hashMap.get("ConversationID").toString().equals("null")) {
                            count = count + 1;
                            tv.setText(count + " Conversations");
                            Log.d(TAG, "FRIEND ID:" + d.getKey().toString());
                            Log.d(TAG, "CONVERSATION ID:" + hashMap.get("ConversationID").toString());
                            friendsID.add(d.getKey().toString());
                            conversationID.add(hashMap.get("ConversationID").toString());
                            pcchatapp.child("users").child(d.getKey().toString()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    DataModelUser dataModelUser = dataSnapshot.getValue(DataModelUser.class);
                                    friendsData.add(dataModelUser);
                                    listView.setAdapter(new CustomFriendsListAdapter(getActivity(), friendsID, friendsData));
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                    }
                } else {
                    listView.setAdapter(new CustomFriendsListAdapter(getActivity(), friendsID, friendsData));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setFriendSingleton(i);
                goToChatFragment();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homebtngroups:
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new GroupFragment())
                        .commit();
                break;
            case R.id.homebtnfriend:
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new FriendsFragment())
                        .commit();
                break;
            case R.id.homebtnrequest:
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new FriendsRequestFragment())
                        .commit();
                break;
            case R.id.homebtnprofile:
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view2 = inflater.inflate(R.layout.profiledialog, null);
                TextView name = (TextView) view2.findViewById(R.id.profiledialog_name);
                TextView email = (TextView) view2.findViewById(R.id.profiledialog_email);
                TextView phone = (TextView) view2.findViewById(R.id.profiledialog_phone);
                imageView = (ImageView) view2.findViewById(R.id.profiledialog_imageview);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        performSelect();
                    }
                });
                name.setText(ME.getName());
                email.setText(pcchatapp.getAuth().getProviderData().get("email").toString());
                phone.setText(ME.getPhone());
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setView(view2);
                alertDialog.show();
                break;
            case R.id.homebtnlogout:
                pcchatapp.unauth();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new LoginFragment())
                        .commit();
                break;
        }
    }

    private void setFriendSingleton(int i) {
        DataModelFriendSingleTon friend = DataModelFriendSingleTon.getInstance();
        friend.setUuidUserFriend(friendsID.get(i).toString());
        friend.setEmailUserFriend(friendsData.get(i).getEmail_id());
        friend.setImageUrlUserFriend(friendsData.get(i).getImage_url());
        friend.setNameUserFriend(friendsData.get(i).getName());
        friend.setPhoneUserFriend(friendsData.get(i).getPhone());
        friend.setConversationID(conversationID.get(i).toString());
    }

    private void goToChatFragment() {
        getFragmentManager().beginTransaction()
                .addToBackStack("")
                .replace(R.id.fragment, new ChatFragment())
                .commit();
    }

    private void performSelect() {
        Log.d("INVOKED", "Perform Select");

        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filePath = null;
        switch (requestCode) {

            case PICK_IMAGE:
                Log.d("INVOKED", "onActivityResult case PICK IMAGE");

                if (resultCode == Activity.RESULT_OK) {
                    Log.d("INVOKED", "onActivityResult == OK");

                    selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        Log.d("Image Selected", " URI" + selectedImageUri.toString());

                        try {
                            //selectedImagePath = getPath(selectedImageUri);
                            //   performCrop();
                            imageView.setImageURI(selectedImageUri);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Internal error", Toast.LENGTH_LONG).show();
                            Log.e(e.getClass().getName(), e.getMessage(), e);
                        }
                    }
                }
                break;

            case RESULT_CROP:
                Log.d("INVOKED", "onActivityResult case CROP");

                if (resultCode == Activity.RESULT_OK) {
                    Log.d("CASE CROP", " Result == OK");

                    Bundle extras = data.getExtras();
                    bitmap = extras.getParcelable("data");
                    String path = saveImage(bitmap);
                    Log.d("PATH AFTER CROPPING", path);
                    if (path != null) {
                        selectedImagePath = path;
                        decodeFile(path);
                    }
                }
                break;
        }
    }

    private void performCrop() {
        Log.d("INVOKED", "Performed Crop");
        try {
            if (selectedImageUri != null) {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(selectedImageUri, "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 280);
                cropIntent.putExtra("outputY", 280);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, RESULT_CROP);
            }
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "your device doesn't support the crop action!";
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void decodeFile(String filePath) {
        Log.d("INVOKED", "Decode File");

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        final int REQUIRED_SIZE = 1024;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);
        new AlertDialog.Builder(getActivity())
                .setTitle("Upload Picture")
                .setMessage("Are you sure you want to upload picture?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        imageView.setImageBitmap(bitmap);
                        Log.d("File PATH IS ", selectedImagePath + "");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return null;
        }
    }
}
