package com.bitsplease.fridgynote.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by Lago on 13/04/2018.
 */

public class NfcWrapper {
    private final String TAG = "FN-NfcWrapper";

    private Activity mContext;
    private NfcAdapter mNfcAdapter;

    public NfcWrapper(Activity context) throws NfcWrapperException {
        mContext = context;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            throw new NfcWrapperException("This device doesn't support NFC.");
        }

        if (!mNfcAdapter.isEnabled()) {
            throw new NfcWrapperException("NFC disabled.");
        }
    }

    public String handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (Mime.TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                return processTag(tag);
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        }
        /* **IN CASE WE SUPPORT NEW TAGS**

        else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }*/
        return null;
    }

    protected String processTag(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Unsupported Encoding", e);
                }
            }
        }

        return null;
    }

    public void setupForegroundDispatch() {
        final Intent intent = new Intent(mContext.getApplicationContext(), mContext.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(Mime.TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        mNfcAdapter.enableForegroundDispatch(mContext, pendingIntent, filters, techList);
    }

    public void stopForegroundDispatch() {
        mNfcAdapter.disableForegroundDispatch(mContext);
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }


    public static class NfcWrapperException extends Exception {

        public NfcWrapperException() {
            super();
        }

        public NfcWrapperException(String exc) {
            super(exc);
        }
    }
}
