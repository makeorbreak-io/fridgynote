package com.bitsplease.fridgynote.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.LabelViewItem;
import com.bitsplease.fridgynote.controller.TextNote;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.ImageLoader;
import com.bitsplease.fridgynote.utils.SizeUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextNoteActivity extends AppCompatActivity {
    private static final String TAG = "FN-TextNoteAct";
    public static final int GET_FROM_GALLERY = 3;
    public static final int REQUEST_IMAGE_CAPTURE = 1000;

    private TextView mTitleText;
    private TextView mBodyText;
    private LinearLayout mImagesLayout;
    private ImageView mAddImageButton;
    private Spinner mLabelSpinner;

    private TextNote mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_note);

        Bundle b = getIntent().getExtras();
        String noteId = b != null ? b.getString(Constants.EXTRA_NOTEID, "") : "";
        mNote = BackendConnector.getTextNote(noteId);

        setupUi();
    }

    private void setupUi() {
        mTitleText = findViewById(R.id.note_title_text);
        mBodyText = findViewById(R.id.note_body_text);
        mImagesLayout = findViewById(R.id.note_images_layout);
        mAddImageButton = findViewById(R.id.add_image_view);
        //mLabelSpinner = findViewById(R.id.label_spinner);

        mTitleText.setText(mNote.getTitle());
        mBodyText.setText(mNote.getBody());

        List<String> images = mNote.getImages();
        for (int i = 0; i < images.size(); ++i) {
            LinearLayout l = new LinearLayout(this);
            ImageView v = new ImageView(this);
            l.addView(v);
            mImagesLayout.addView(l, i);
            int pix = SizeUtils.dpToPx(this, 100);
            l.setLayoutParams(new LinearLayout.LayoutParams(pix, pix));
            new ImageLoader(v).execute(images.get(i));
        }

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });

        //mLabelSpinner.setAdapter(new ArrayAdapter<LabelViewItem>());
    }

    private void addImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");

        builder.setMessage("Choose where to upload image from.");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                if (uploadBitmap(bitmap)) {
                    Toast.makeText(this, "Image added.", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Toast.makeText(this, "Unable to upload image.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (uploadBitmap(imageBitmap)) {
                Toast.makeText(this, "Image added.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to upload image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean uploadBitmap(Bitmap bitmap) {
        // TODO actually upload
        return false;
    }
}
