package pana.com.cloudnarysample;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static int RESULT_LOAD_IMAGE = 1;
    private Button uploadButton, capture;
    private NetworkImageView imageView;
    private Cloudinary cloudinary;
    private String url;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        uploadButton = (Button) view.findViewById(R.id.uploadImage);
        imageView = (NetworkImageView) view.findViewById(R.id.imageView);
        capture = (Button) view.findViewById(R.id.captureButton);
        captureImage();
        uploadImage();
        cloudinary();
        return view;
    }

    private void cloudinary() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "moosabaloch",
                "api_key", "976698733356172",
                "api_secret", "2mpFCUAhsCzasvMQWbcUPG0waJM"));
    }


    private void captureImage() {
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "" + url, Toast.LENGTH_SHORT).show();
                imageView.setImageUrl(url, imageLoader);
               /* Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);*/

            }
        });
    }


    private void uploadImage() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Upload upload = new Upload();
            upload.execute(picturePath);
            Toast.makeText(getActivity(), "File Path=> " + picturePath, Toast.LENGTH_SHORT).show();

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }

    class Upload extends AsyncTask<String, Void, HashMap<String, Object>> {
        @Override
        protected void onPostExecute(HashMap<String, Object> s) {
            super.onPostExecute(s);
            HashMap<String, Object> jsonObject = s;
            try {
                url = (String) jsonObject.get("url");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected HashMap<String, Object> doInBackground(String... params) {
            File file = new File(params[0]);
            HashMap<String, Object> uploadResult = null;
            try {
                uploadResult = (HashMap<String, Object>) cloudinary.uploader().upload(file, ObjectUtils.emptyMap());

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Result is ", "" + uploadResult.toString());

            return uploadResult;
        }
    }
}
