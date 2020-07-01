package ly.pp.justpiano3;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

final class ChangeSoundClick implements OnClickListener {
    private final SoundListAdapter soundListAdapter;
    private final String f5990b;
    private final int f5991c;

    ChangeSoundClick(SoundListAdapter c1324mv, String str, int i) {
        soundListAdapter = c1324mv;
        f5990b = str;
        f5991c = i;
    }

    @Override
    public final void onClick(View view) {
        switch (f5990b) {
            case "original":
                new SoundListPreferenceTask(soundListAdapter.soundListPreference).execute("original");
                break;
            case "more":
                Intent intent = new Intent();
                intent.setFlags(0);
                intent.setClass(soundListAdapter.context, SoundDownload.class);
                soundListAdapter.context.startActivity(intent);
                break;
            default:
                new SoundListPreferenceTask(soundListAdapter.soundListPreference).execute(f5990b, String.valueOf(soundListAdapter.f5984c[f5991c]));
                break;
        }
    }
}
