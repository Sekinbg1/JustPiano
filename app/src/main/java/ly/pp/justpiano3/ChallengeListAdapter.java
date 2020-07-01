package ly.pp.justpiano3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public final class ChallengeListAdapter extends BaseAdapter {
    private List<HashMap> list;
    private LayoutInflater li;

    ChallengeListAdapter(List<HashMap> list, LayoutInflater layoutInflater) {
        this.list = list;
        li = layoutInflater;
    }

    @Override
    public final int getCount() {
        return list.size();
    }

    @Override
    public final Object getItem(int i) {
        return i;
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = li.inflate(R.layout.ol_c_challenge_view, null);
        }
        String name = (String) list.get(i).get("N");
        if (name == null) {
            return view;
        }
        String score = (String) list.get(i).get("S");
        String time = (String) list.get(i).get("T");
        String position = (String) list.get(i).get("P");
        TextView positionText = view.findViewById(R.id.ol_challenge_position);
        TextView nameText = view.findViewById(R.id.ol_challenge_user);
        TextView scoreText = view.findViewById(R.id.ol_challenge_score);
        TextView timeText = view.findViewById(R.id.ol_challenge_time);
        nameText.setText(name);
        scoreText.setText(score);
        timeText.setText(time);
        positionText.setText(position);
        switch (i) {
            case 0:
                nameText.setTextColor(0xFFFFD700);
                scoreText.setTextColor(0xFFFFD700);
                timeText.setTextColor(0xFFFFD700);
                positionText.setTextColor(0xFFFFD700);
                break;
            case 1:
                nameText.setTextColor(0xFFC0C0C0);
                scoreText.setTextColor(0xFFC0C0C0);
                timeText.setTextColor(0xFFC0C0C0);
                positionText.setTextColor(0xFFC0C0C0);
                break;
            case 2:
                nameText.setTextColor(0xFFD2B48C);
                scoreText.setTextColor(0xFFD2B48C);
                timeText.setTextColor(0xFFD2B48C);
                positionText.setTextColor(0xFFD2B48C);
                break;
            default:
                nameText.setTextColor(0xFFFFFFFF);
                scoreText.setTextColor(0xFFFFFFFF);
                timeText.setTextColor(0xFFFFFFFF);
                positionText.setTextColor(0xFFFFFFFF);
        }
        return view;
    }
}
