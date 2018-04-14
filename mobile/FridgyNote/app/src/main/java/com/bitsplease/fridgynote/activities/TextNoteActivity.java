package com.bitsplease.fridgynote.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.TextNote;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.DialogHelper;
import com.bitsplease.fridgynote.utils.ImageLoader;
import com.bitsplease.fridgynote.utils.SizeUtils;

import java.io.InputStream;
import java.util.List;

public class TextNoteActivity extends AppCompatActivity {

    private TextView mTitleText;
    private TextView mBodyText;
    private LinearLayout mImagesLayout;
    private ImageView mAddImageButton;

    private TextNote mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_note);

        Bundle b = getIntent().getExtras();
        String noteId = b.getString(Constants.EXTRA_NOTEID, "");
        mNote = BackendConnector.getTextNote(noteId);

        setupUi();
    }

    private void setupUi() {
        mTitleText = findViewById(R.id.note_title_text);
        mBodyText = findViewById(R.id.note_body_text);
        mImagesLayout = findViewById(R.id.note_images_layout);
        mAddImageButton = findViewById(R.id.add_image_view);

        mTitleText.setText(mNote.getTitle());
        mBodyText.setText(mNote.getBody());

        List<String> images = mNote.getImages();
        for(int i = 0; i < images.size(); ++i) {
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
                // TODO codar isto
                DialogHelper.showOkDialog(TextNoteActivity.this, "Temos de implementar isto");
            }
        });
    }
}
