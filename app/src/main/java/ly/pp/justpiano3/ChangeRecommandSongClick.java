package ly.pp.justpiano3;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.OLPlayRoom;

final class ChangeRecommandSongClick implements OnClickListener {
    private final ChattingAdapter chattingAdapter;
    private final String songPath;

    ChangeRecommandSongClick(ChattingAdapter chattingAdapter, String str) {
        this.chattingAdapter = chattingAdapter;
        songPath = str;
    }

    @Override
    public void onClick(View view) {
        Message obtainMessage = ((OLPlayRoom) chattingAdapter.activity).getHandler().obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("S", songPath);
        obtainMessage.setData(bundle);
        obtainMessage.what = 1;
        ((OLPlayRoom) chattingAdapter.activity).getHandler().sendMessage(obtainMessage);
    }
}
