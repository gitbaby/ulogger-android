package net.fabiszewski.ulogger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

class FilePreference extends Preference implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private String mText;

    public FilePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSummaryProvider(FilePreference.SimpleSummaryProvider.getInstance());
    }

    public FilePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSummaryProvider(FilePreference.SimpleSummaryProvider.getInstance());
    }

    public FilePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSummaryProvider(FilePreference.SimpleSummaryProvider.getInstance());
    }

    public FilePreference(Context context) {
        super(context);
        setSummaryProvider(FilePreference.SimpleSummaryProvider.getInstance());
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getKey())) {
            setText(sharedPreferences.getString(key, ""));
        }
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        setText(getPersistedString((String) defaultValue));
    }

    public void setText(String text) {
        mText = text;
        notifyChanged();
    }

    public String getText() {
        return mText;
    }

    public String getFileName() {
        String fileName = "";
        Uri uri;
        try {
            uri = Uri.parse(mText);
        } catch (NullPointerException e) {
            return fileName;
        }
        Activity activity = (Activity) getContext();
        Cursor cursor = activity.getContentResolver().query(uri, null, null,
                null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } finally {
            cursor.close();
        }
        return fileName;
    }

    public static final class SimpleSummaryProvider implements
            SummaryProvider<FilePreference> {
        private static FilePreference.SimpleSummaryProvider sSimpleSummaryProvider;

        private SimpleSummaryProvider() {
        }

        public static FilePreference.SimpleSummaryProvider getInstance() {
            if (sSimpleSummaryProvider == null) {
                sSimpleSummaryProvider = new FilePreference.SimpleSummaryProvider();
            }
            return sSimpleSummaryProvider;
        }

        @Override
        public CharSequence provideSummary(FilePreference preference) {
            final String fileName = preference.getFileName();
            if (TextUtils.isEmpty(fileName)) {
                return (preference.getContext().getString(R.string.not_set));
            }
            return fileName;
        }
    }

}
