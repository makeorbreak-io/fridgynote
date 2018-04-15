package com.bitsplease.fridgynote.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.LabelViewItem;
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.Note;
import com.bitsplease.fridgynote.controller.TextNote;
import com.bitsplease.fridgynote.utils.BackEndCallback;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.DialogHelper;
import com.bitsplease.fridgynote.utils.ImageLoader;
import com.bitsplease.fridgynote.utils.ImageUploadCallback;
import com.bitsplease.fridgynote.utils.PreferenceUtils;
import com.bitsplease.fridgynote.utils.SizeUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TextNoteActivity extends AppCompatActivity implements ImageUploadCallback, BackEndCallback {
    private static final String TAG = "FN-TextNoteAct";
    public static final int GET_FROM_GALLERY = 3;
    public static final int REQUEST_IMAGE_CAPTURE = 1000;

    private TextView mTitleText;
    private TextView mBodyText;
    private LinearLayout mImagesLayout;
    private ImageView mAddImageButton;
    private Spinner mLabelSpinner;
    private Button mShareButton;

    private TextNote mNote;
    private String mNoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_note);

        Bundle b = getIntent().getExtras();
        mNoteId = b != null ? b.getString(Constants.EXTRA_NOTEID, "") : "";
        BackendConnector.getNoteTags(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_sync:
                BackendConnector.getNoteTags(this, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupUi() {
        mTitleText = findViewById(R.id.note_title_text);
        mBodyText = findViewById(R.id.note_body_text);
        mImagesLayout = findViewById(R.id.note_images_layout);
        mAddImageButton = findViewById(R.id.add_image_view);
        mShareButton = findViewById(R.id.share_button);
        //mLabelSpinner = findViewById(R.id.label_spinner);

        mTitleText.setText(mNote.getTitle());
        mBodyText.setText(mNote.getBody());

        while (mImagesLayout.getChildCount() > 1) {
            mImagesLayout.removeViewAt(0);
        }

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

        Map<String, String> users = mNote.getAllUsersExcept(PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, ""));
        final List<String> keys = new ArrayList<>(users.keySet());
        for (int i = 0; i < 5; ++i) {
            findViewById(getViewParent(getUserSharedId(i))).setVisibility(View.GONE);
        }
        for (int i = 0; i < keys.size(); ++i) {
            TextView v = findViewById(getUserSharedId(i));
            View parent = findViewById(getViewParent(getUserSharedId(i)));
            parent.setVisibility(View.VISIBLE);
            final int index = i;
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogHelper.showOkDialog(TextNoteActivity.this, keys.get(index));
                }
            });
            v.setText("" + keys.get(i).toUpperCase().charAt(0));
        }

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TextNoteActivity.this);
                builder.setTitle("Share note with user");

                final EditText input = new EditText(TextNoteActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("User name");
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNote.getSharedUsers().put(String.valueOf(input.getText()), "");
                        BackendConnector.uploadTextNote(TextNoteActivity.this, mNote);
                        setupUi();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        findViewById(R.id.text_note_container).setOnTouchListener(new View.OnTouchListener() {
            private float prevY = -1;
            private long sentTimestamp = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (prevY == -1) {
                            prevY = event.getY();
                        } else {
                            if (prevY > event.getY()) {
                                long currentMilis = System.currentTimeMillis();
                                if(sentTimestamp == -1 || currentMilis - sentTimestamp >= TimeUnit.SECONDS.toMillis(5)) {
                                    Log.d("FN-test", "SENDING");
                                    BackendConnector.sendTextNoteToWebApp(TextNoteActivity.this, mNote);
                                    sentTimestamp = currentMilis;
                                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.text_note_container), "Note sent to web app.", Snackbar.LENGTH_LONG);
                                    mySnackbar.show();
                                }
                            }
                            prevY = event.getY();
                        }
                        return true;
                }
                return false;
            }
        });

        //mLabelSpinner.setAdapter(new ArrayAdapter<LabelViewItem>());
    }

    private int getUserSharedId(int index) {
        switch (index) {
            case 0:
                return R.id.share_user_1;
            case 1:
                return R.id.share_user_2;
            case 2:
                return R.id.share_user_3;
            case 3:
                return R.id.share_user_4;
            case 4:
                return R.id.share_user_5;
        }
        return 0;
    }

    private int getViewParent(int viewId) {
        switch (viewId) {
            case R.id.share_user_1:
                return R.id.share_user_1_parent;
            case R.id.share_user_2:
                return R.id.share_user_2_parent;
            case R.id.share_user_3:
                return R.id.share_user_3_parent;
            case R.id.share_user_4:
                return R.id.share_user_4_parent;
            case R.id.share_user_5:
                return R.id.share_user_5_parent;
        }
        return 0;
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
        LinearLayout l = new LinearLayout(this);
        ImageView v = new ImageView(this);
        l.addView(v);
        mImagesLayout.addView(l, mImagesLayout.getChildCount() - 1);
        int pix = SizeUtils.dpToPx(this, 100);
        l.setLayoutParams(new LinearLayout.LayoutParams(pix, pix));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO delete
            }
        });


        BackendConnector.uploadBitmap(this, bitmap, this);
        //BackendConnector.uploadTextNote(this, mNote);
        // TODO actually upload
        // multipart form data
        // post /notes/text
        return false;
    }

    @Override
    public void newImageId(String id) {
        Log.d(TAG, "Received image id " + id);
        mNote.getImages().add(id);
        BackendConnector.uploadTextNote(this, mNote);
    }

    @Override
    public void tagNotesCallback(List<Note> response) {
        for (Note n : response) {
            if (!(n instanceof TextNote)) {
                continue;
            }
            TextNote note = (TextNote) n;
            mNote = note;
            if (note.getId().equals(mNoteId)) {
                break;
            }
        }
        setupUi();
    }
}
