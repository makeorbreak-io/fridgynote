package com.bitsplease.fridgynote.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.Note;
import com.bitsplease.fridgynote.controller.OwnedNoteTags;
import com.bitsplease.fridgynote.controller.TextNote;
import com.bitsplease.fridgynote.utils.BackEndCallback;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.DialogHelper;
import com.bitsplease.fridgynote.utils.ImageLoader;
import com.bitsplease.fridgynote.utils.PreferenceUtils;
import com.bitsplease.fridgynote.utils.SizeUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EditTextNoteActivity extends AppCompatActivity {
    private static final String TAG = "FN-EditTextNoteAct";
    public static final int GET_FROM_GALLERY = 5;
    public static final int REQUEST_IMAGE_CAPTURE = 102;

    private TextNote mNote;

    private EditText mTitleText;
    private EditText mBodyText;
    private LinearLayout mImagesLayout;
    private ImageView mAddImageButton;
    private FloatingActionButton mSubmitButton;
    private List<Bitmap> newCustomBitmaps;

    private boolean create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text_note);

        // TODO if noteId is empty this can be used to create note
        newCustomBitmaps = new ArrayList<>();
        Bundle b = getIntent().getExtras();
        final String noteId = b != null ? b.getString(Constants.EXTRA_NOTEID, "") : "";
        if(noteId.equals("")){
            create = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupUi();
                }
            });
        }
        BackendConnector.getNoteTags(this, new BackEndCallback() {
            @Override
            public void tagNotesCallback(List<Note> response) {
                create = false;
                for(Note n : response) {
                    if(n.getId().equals(noteId)) {
                        mNote = (TextNote) n;
                        break;
                    }
                }
                if(mNote != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setupUi();
                        }
                    });
                }
            }
        });
    }

    private void setupUi() {
        mTitleText = findViewById(R.id.note_title);
        mBodyText = findViewById(R.id.note_body);
        mImagesLayout = findViewById(R.id.note_images_layout);
        mAddImageButton = findViewById(R.id.add_image_view);
        mSubmitButton = findViewById(R.id.save_note);



        if(!create){

            mTitleText.setText(mNote.getTitle());
            mBodyText.setText(mNote.getBody());

            List<String> images = mNote.getImages();
            for (int i = 0; i < images.size(); ++i) {
                final String image = images.get(i);
                final LinearLayout l = new LinearLayout(this);
                ImageView v = new ImageView(this);
                l.addView(v);
                mImagesLayout.addView(l, i);
                int pix = SizeUtils.dpToPx(this, 100);

                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(pix, pix);
                p.setMargins(0, 0, 20, 0);
                l.setLayoutParams(p);
                new ImageLoader(v).execute(image);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(EditTextNoteActivity.this);
                        builder1.setMessage("Are you sure you want to delete this image?");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mImagesLayout.removeView(l);
                                        mNote.getImages().remove(image);
                                        dialog.cancel();
                                    }
                                });
                        builder1.setNegativeButton("No", null);

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });
            }
        }




        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
                //DialogHelper.showOkDialog(EditTextNoteActivity.this, "Done");
            }
        });

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });
    }

    private void submit() {
        if(mNote.getTagId() == null || mNote.getTagId().isEmpty()) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Choose tag for new note");

            OwnedNoteTags ownedTagsObj = OwnedNoteTags.getOwnedTags();
            HashMap<String, String> ownedTags = ownedTagsObj.getOwned();
            List<String> tagsInt = new ArrayList<>();
            List<String> tagNames = new ArrayList<>();

            Iterator it = ownedTags.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                tagsInt.add((String) pair.getKey());
                tagNames.add((String) pair.getValue());
            }
            final String[] tags = tagsInt.toArray(new String[0]);
            final String[] tagNamesArray = tagNames.toArray(new String[0]);
            b.setItems(tagNamesArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    actuallySubmitChanges(tags[which], tags[which]);
                    dialog.dismiss();
                }

            });

            b.show();
        } else {
            actuallySubmitChanges(mNote.getId(), mNote.getTagId());
        }
    }

    private void updateInternalNote() {
        String ownerId = mNote.getOwner().first;
        String ownerTagId = mNote.getOwner().second;
        Map<String, String> shared = mNote.getSharedUsers();
        List<String> labels = mNote.getLabels();

        mNote = new TextNote(mNote.getId(), mTitleText.getText().toString(), mBodyText.getText().toString(), mNote.getImages());
        mNote.setOwner(ownerId, ownerTagId);
        mNote.setSharedUsers(shared);
        mNote.setLabels(labels);
    }

    private void actuallySubmitChanges(String id, String tagId) {
        if(mNote == null){
            mNote = new TextNote(id, mTitleText.getText().toString(), mBodyText.getText().toString(), new ArrayList<String>());
            mNote.setOwner(PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, ""), tagId);
            mNote.setSharedUsers(new HashMap<String, String>());
            mNote.setLabels(new ArrayList<String>());
            BackendConnector.createTextNote(EditTextNoteActivity.this, mNote);
        }else{
            updateInternalNote();
            BackendConnector.uploadTextNote(EditTextNoteActivity.this, mNote);
        }
        finish();
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

    private boolean uploadBitmap(final Bitmap bitmap) {
        final LinearLayout l = new LinearLayout(this);
        final ImageView v = new ImageView(this);
        l.addView(v);
        mImagesLayout.addView(l, mImagesLayout.getChildCount() - 1);
        int pix = SizeUtils.dpToPx(this, 100);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(pix, pix);
        p.setMargins(0, 0, 20, 0);
        l.setLayoutParams(p);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newCustomBitmaps.add(bitmap);
                v.setImageBitmap(bitmap);
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditTextNoteActivity.this);
                builder1.setMessage("Are you sure you want to delete this image?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                newCustomBitmaps.remove(bitmap);
                                mImagesLayout.removeView(l);
                                dialog.cancel();
                            }
                        });
                builder1.setNegativeButton("No", null);

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        // TODO actually upload
        return false;
    }
}
