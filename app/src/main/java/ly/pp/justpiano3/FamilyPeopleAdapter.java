package ly.pp.justpiano3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public final class FamilyPeopleAdapter extends BaseAdapter {
    private final List<HashMap<String, String>> list;
    private final JPApplication jpApplication;
    private final LayoutInflater li;
    private final OLFamily family;

    FamilyPeopleAdapter(List<HashMap<String, String>> list, JPApplication jpApplication, LayoutInflater layoutInflater, OLFamily olFamily) {
        this.jpApplication = jpApplication;
        this.list = list;
        li = layoutInflater;
        family = olFamily;
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
            view = li.inflate(R.layout.ol_c_family_view, null);
        }
        final String name = list.get(i).get("N");
        if (name == null) {
            return view;
        }
        String contribution = list.get(i).get("C");
        String lv = list.get(i).get("L");
        if (lv != null && !lv.isEmpty()) {
            lv = "Lv." + lv;
        }
        TextView positionText = view.findViewById(R.id.ol_family_position);
        TextView nameText = view.findViewById(R.id.ol_family_name);
        TextView contributionText = view.findViewById(R.id.ol_family_contribution);
        TextView lvText = view.findViewById(R.id.ol_family_count);
        ImageView sex = view.findViewById(R.id.ol_family_pic);
        if (list.get(i).get("S").equals("m")) {
            sex.setImageResource(R.drawable.m);
        } else if (list.get(i).get("S").equals("f")) {
            sex.setImageResource(R.drawable.f);
        } else {
            sex.setImageResource(R.drawable.null_pic);
        }
        lvText.setText(lv);
        nameText.setText(name);
        contributionText.setText(contribution);
        final String position = list.get(i).get("P");
        switch (position) {
            case "0":
                positionText.setText("族长");
                break;
            case "1":
                positionText.setText("副族长");
                break;
            case "2":
                positionText.setText("族员");
                break;
            default:
                positionText.setText("");
                break;
        }
        if (list.get(i).get("O").equals("0")) {
            nameText.setTextColor(jpApplication.getResources().getColor(R.color.white));
            positionText.setTextColor(jpApplication.getResources().getColor(R.color.white));
            contributionText.setTextColor(jpApplication.getResources().getColor(R.color.white));
            lvText.setTextColor(jpApplication.getResources().getColor(R.color.white));
        } else {
            nameText.setTextColor(jpApplication.getResources().getColor(R.color.white1));
            positionText.setTextColor(jpApplication.getResources().getColor(R.color.white1));
            contributionText.setTextColor(jpApplication.getResources().getColor(R.color.white1));
            lvText.setTextColor(jpApplication.getResources().getColor(R.color.white1));
        }
        final LinearLayout linearLayout = view.findViewById(R.id.ol_family_people);
        linearLayout.setOnClickListener(v -> {
            PopupWindow a = family.loadInfoPopupWindow(name, position);
            if (a != null) {
                int[] iArr = new int[2];
                linearLayout.getLocationOnScreen(iArr);
                a.showAtLocation(linearLayout, 51, iArr[0] + linearLayout.getWidth(), iArr[1]);
            }
        });
        return view;
    }
}
