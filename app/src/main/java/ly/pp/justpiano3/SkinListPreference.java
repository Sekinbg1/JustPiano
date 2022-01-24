package ly.pp.justpiano3;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import java.io.File;
import java.util.List;

public class SkinListPreference extends DialogPreference {
    File f5024d;
    String str1 = "";
    Context context;
    JPProgressBar jpProgressBar;
    private CharSequence[] f5021a;
    private CharSequence[] f5022b;
    private SkinListAdapter skinListAdapter;

    public SkinListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    private void m3906a() {
        String str = Environment.getExternalStorageDirectory() + "/JustPiano/Skins";
        List<File> localSkinList = SkinAndSoundFileHandle.getLocalSkinList(str);
        int size = localSkinList.size();
        f5021a = new CharSequence[(size + 2)];
        f5022b = new CharSequence[(size + 2)];
        for (int i = 0; i < size; i++) {
            str = localSkinList.get(i).getName();
            f5021a[i] = str.subSequence(0, str.lastIndexOf('.'));
            f5022b[i] = Environment.getExternalStorageDirectory() + "/JustPiano/Skins/" + localSkinList.get(i).getName();
        }
        f5021a[size] = "原生主题";
        f5022b[size] = "original";
        f5021a[size + 1] = "更多主题...";
        f5022b[size + 1] = "more";
    }

    final void deleteFiles(String str) {
        int i = 0;
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!str.equals("original") && sharedPreferences.getString("skin_list", "original").equals(str)) {
            file = context.getDir("Skin", Context.MODE_PRIVATE);
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    while (i < listFiles.length) {
                        listFiles[i].delete();
                        i++;
                    }
                }
            }
        }
        m3906a();
        skinListAdapter.mo3544a(f5021a, f5022b);
        skinListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        persistString(str1);
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        m3906a();
        jpProgressBar = new JPProgressBar(context);
        LinearLayout f5028h = new LinearLayout(context);
        f5028h.setLayoutParams(new LayoutParams(-1, -2));
        f5028h.setOrientation(LinearLayout.VERTICAL);
        f5028h.setMinimumWidth(400);
        f5028h.setPadding(20, 20, 20, 20);
        f5028h.setBackgroundColor(-1);
        ListView f5027g = new ListView(context);
        f5027g.setDivider(null);
        skinListAdapter = new SkinListAdapter(this, context, f5021a, f5022b);
        f5027g.setAdapter(skinListAdapter);
        f5028h.addView(f5027g);
        builder.setView(f5028h);
    }
}
