package ly.pp.justpiano3;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MelodySelectAdapter extends CursorAdapter {
    private final MelodySelect melodySelect;

    MelodySelectAdapter(Context context, Cursor cursor, MelodySelect melodySelect) {
        super(context, cursor, true);
        this.melodySelect = melodySelect;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.f_view, parent, false);
        inflate.setBackgroundResource(R.drawable.selector_ol_button);
        return inflate;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tx = view.findViewById(R.id.ol_s_p);
        String txstr = cursor.getString(cursor.getColumnIndex("name"));
        tx.setText(txstr);
        tx.setTextSize(13);
        tx.setBackgroundResource(R.drawable.selector_ol_button);
        view.setOnClickListener(v -> {
            melodySelect.mo2784a(melodySelect.search(tx.getText().toString()));
            melodySelect.autoctv.dismissDropDown();
            melodySelect.autoctv.clearFocus();
            try {
                ((InputMethodManager) melodySelect.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(melodySelect.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}