package ly.pp.justpiano3;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

final class ChangeRecommandSongClick implements OnClickListener {
    private final ChattingAdapter chattingAdapter;
    private final String f5360b;

    ChangeRecommandSongClick(ChattingAdapter chattingAdapter, String str) {
        this.chattingAdapter = chattingAdapter;
        f5360b = str;
    }

    @Override
    public final void onClick(View view) {
        Message obtainMessage = ((OLPlayRoom) chattingAdapter.activity).getHandler().obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("S", f5360b);
        obtainMessage.setData(bundle);
        obtainMessage.what = 1;
        ((OLPlayRoom) chattingAdapter.activity).getHandler().sendMessage(obtainMessage);
    }
}
