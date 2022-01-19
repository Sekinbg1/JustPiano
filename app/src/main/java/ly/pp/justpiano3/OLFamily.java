package ly.pp.justpiano3;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OLFamily extends BaseActivity implements OnClickListener {
    public JPApplication jpapplication;
    public ConnectionService cs;
    public JPProgressBar jpprogressBar;
    public int position;  //0为族长 1为副族长 2为族员，3为非本家族的人
    public TextView declaration;
    public TextView info;
    FamilyHandler familyHandler;
    String familyID;
    String peopleNow;  //目前选择人的名字
    List<HashMap> familyList;
    int familyPageNum;
    String myFamilyPosition;
    String myFamilyContribution;
    String myFamilyCount;
    String myFamilyName;
    int listPosition;
    byte[] myFamilyPicArray;
    List<HashMap> peopleList = new ArrayList<>();
    ListView peopleListView;
    PopupWindow infoWindow;
    private LayoutInflater layoutinflater;
    private Button manageFamily;
    private Button inOut;

    void positionHandle() {
        switch (position) {
            case 0:
                manageFamily.setEnabled(true);
                inOut.setText("解散家族");
                break;
            case 1:
            case 2:
                inOut.setText("退出家族");
                break;
            case 3:
                inOut.setText("申请加入");
                break;
        }
    }

    private void inOutFamily() {
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle("提示");
        switch (position) {
            case 0:
                jpdialog.setMessage("确定要解散你的家族吗?");
                break;
            case 1:
            case 2:
                jpdialog.setMessage("确定要退出家族吗?");
                break;
            case 3:
                jpdialog.setMessage("申请加入家族需要族长或副族长的批准!");
                break;
        }
        jpdialog.setFirstButton("确定", new InOutFamilyClick(this)).setSecondButton("取消", new DialogDismissClick());
        try {
            jpdialog.showDialog();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    void loadManageFamilyPopupWindow(Bundle b) {
        PopupWindow popupWindow = new PopupWindow(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_managefamily_view, null);
        Button button = inflate.findViewById(R.id.ol_family_levelup);
        Button button2 = inflate.findViewById(R.id.ol_family_changedecl);
        Button button3 = inflate.findViewById(R.id.ol_family_changepic);
        Button button4 = inflate.findViewById(R.id.ol_family_changetest);
        if (b.getInt("R", 0) == 1) {
            button.setEnabled(true);
        }
        TextView info = inflate.findViewById(R.id.ol_family_levelup_info);
        info.setText(b.getString("I", "不断提升您的等级与考级，即可将您的家族升级为人数更多、规模更大的家族!"));
        popupWindow.setContentView(inflate);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        button2.setOnClickListener(this);
        button.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        popupWindow.showAtLocation(manageFamily, Gravity.CENTER, 0, 0);
    }

    //显示个人资料
    void showInfoDialog(Bundle b) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_info_dialog, findViewById(R.id.dialog));
        try {
            User User = new User(b.getString("U"), new JSONObject(b.getString("DR")), b.getString("S"), b.getInt("LV"), b.getInt("CL"));
            ImageView imageView = inflate.findViewById(R.id.ol_user_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_user_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_user_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_user_hair);
            ImageView imageView5 = inflate.findViewById(R.id.ol_user_shoes);
            TextView textView = inflate.findViewById(R.id.user_info);
            TextView textView2 = inflate.findViewById(R.id.user_psign);
            imageView.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_m0.png")));
            if (User.getTrousers() <= 0) {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_t" + (User.getTrousers() - 1) + ".png")));
            }
            if (User.getJacket() <= 0) {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_j" + (User.getJacket() - 1) + ".png")));
            }
            if (User.getHair() <= 0) {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_h" + (User.getHair() - 1) + ".png")));
            }
            if (User.getShoes() <= 0) {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_s" + (User.getShoes() - 1) + ".png")));
            }
            int lv = b.getInt("LV");
            int targetExp = (int) ((0.5 * lv * lv * lv + 500 * lv) / 10) * 10;
            textView.setText("用户名称:" + b.getString("U")
                    + "\n用户等级:Lv." + lv
                    + "\n经验进度:" + b.getInt("E") + "/" + targetExp
                    + "\n考级进度:Cl." + b.getInt("CL")
                    + "\n所在家族:" + b.getString("F")
                    + "\n在线曲库冠军数:" + b.getInt("W")
                    + "\n在线曲库弹奏总分:" + b.getInt("SC"));
            textView2.setText("个性签名:\n" + (b.getString("P").isEmpty() ? "无" : b.getString("P")));
            new JPDialog(this).setTitle("个人资料").loadInflate(inflate).setFirstButton("加为好友", new AddFriendsMailClick(this, User.getPlayerName())).setSecondButton("确定", new DialogDismissClick()).showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (jpprogressBar != null && jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("K", 0);
            sendMsg((byte) 18, (byte) 0, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, OLPlayHallRoom.class);
        intent.putExtra("HEAD", 16);
        intent.putExtra("pageNum", familyPageNum);
        intent.putExtra("position", listPosition);
        intent.putExtra("myFamilyPosition", myFamilyPosition);
        intent.putExtra("myFamilyContribution", myFamilyContribution);
        intent.putExtra("myFamilyCount", myFamilyCount);
        intent.putExtra("myFamilyName", myFamilyName);
        intent.putExtra("myFamilyPicArray", myFamilyPicArray);
        intent.putExtra("familyList", (Serializable) familyList);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.manage_family:
                jpprogressBar.show();
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("K", 8);
                    jSONObject.put("I", familyID);
                    sendMsg((byte) 18, (byte) 0, jSONObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.in_out:
                if (infoWindow != null && infoWindow.isShowing()) {
                    infoWindow.dismiss();
                }
                inOutFamily();
                break;
            case R.id.ol_chat_b:
                if (infoWindow != null && infoWindow.isShowing()) {
                    infoWindow.dismiss();
                }
                showSendDialog(peopleNow, 0);
                break;
            case R.id.ol_kickout_b:
                if (infoWindow != null && infoWindow.isShowing()) {
                    infoWindow.dismiss();
                }
                JPDialog jpDialog = new JPDialog(this);
                jpDialog.setTitle("提示");
                jpDialog.setMessage("确定要将Ta移出家族吗?");
                jpDialog.setFirstButton("确定", new KickFamilyClick(this)).setSecondButton("取消", new DialogDismissClick());
                try {
                    jpDialog.showDialog();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                break;
            case R.id.ol_showinfo_b:
                if (infoWindow != null && infoWindow.isShowing()) {
                    infoWindow.dismiss();
                }
                if (cs != null) {
                    JSONObject jSONObject = new JSONObject();
                    try {
                        jSONObject.put("C", 0);
                        jSONObject.put("F", peopleNow);
                        sendMsg((byte) 2, (byte) 0, jSONObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.ol_couple_b:  //提升/撤职副族长
                if (infoWindow != null && infoWindow.isShowing()) {
                    infoWindow.dismiss();
                }
                jpprogressBar.show();
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("K", 7);
                    jSONObject.put("F", peopleNow);
                    sendMsg((byte) 18, (byte) 0, jSONObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ol_family_changedecl:
                showSendDialog(" ", 1);
                break;
            case R.id.ol_family_changepic:
                /*jpDialog = new JPDialog(this);
                jpDialog.setTitle("抱歉");
                jpDialog.setMessage("当前客户端不支持家族族徽的上传，可联系开发者上传或等待日后的版本更新!");
                jpDialog.setFirstButton("确定", new DialogDismissClick());
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("image/*");
                startActivityForResult(intent, 2);*/
                break;
            case R.id.ol_family_changetest:
                break;
            case R.id.ol_family_levelup:
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("K", 10);
                    sendMsg((byte) 18, (byte) 0, jSONObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityNum = 7;
        familyHandler = new FamilyHandler(this);
        JPStack.create();
        JPStack.push(this);
        Bundle b = getIntent().getExtras();
        familyID = b.getString("familyID");
        familyPageNum = b.getInt("pageNum");
        listPosition = b.getInt("position");
        myFamilyContribution = b.getString("myFamilyContribution");
        myFamilyCount = b.getString("myFamilyCount");
        myFamilyName = b.getString("myFamilyName");
        myFamilyPosition = b.getString("myFamilyPosition");
        myFamilyPicArray = b.getByteArray("myFamilyPicArray");
        familyList = (List<HashMap>) getIntent().getSerializableExtra("familyList");
        jpprogressBar = new JPProgressBar(this);
        jpprogressBar.show();
        layoutinflater = LayoutInflater.from(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        jpapplication = (JPApplication) getApplication();
        setContentView(R.layout.family);
        cs = jpapplication.getConnectionService();
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("K", 1);
            jSONObject.put("I", familyID);
            sendMsg((byte) 18, (byte) 0, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        inOut = findViewById(R.id.in_out);
        inOut.setOnClickListener(this);
        manageFamily = findViewById(R.id.manage_family);
        manageFamily.setOnClickListener(this);
        peopleListView = findViewById(R.id.family_people_list);
        declaration = findViewById(R.id.declaration);
        info = findViewById(R.id.info_text);
    }

    PopupWindow loadInfoPopupWindow(String name, String positionStr) {
        PopupWindow popupWindow = new PopupWindow(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_buttonlist_view, null);
        Button button = inflate.findViewById(R.id.ol_showinfo_b);  //个人资料
        Button button2 = inflate.findViewById(R.id.ol_chat_b);  //私信
        Button button3 = inflate.findViewById(R.id.ol_kickout_b);  //移出家族
        Button button4 = inflate.findViewById(R.id.ol_couple_b);  //提升/撤职副族长
        button2.setText("发送私信");
        button3.setText("移出家族");
        inflate.findViewById(R.id.ol_closepos_b).setVisibility(View.GONE);
        popupWindow.setContentView(inflate);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        if (position > 1 || (position == 1 && !positionStr.equals("2"))) {
            button3.setVisibility(View.GONE);
        }
        if (name.equals(jpapplication.getKitiName())) {
            button3.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
        }
        if (position > 0) {
            button4.setVisibility(View.GONE);
        } else {
            switch (positionStr) {
                case "1":
                    button4.setText("撤职副族长");
                    break;
                case "2":
                    button4.setText("晋升副族长");
                    break;
                default:
                    button4.setVisibility(View.GONE);
                    break;
            }
            button4.setOnClickListener(this);
        }
        if (position > 2) {
            button.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
        }
        button2.setOnClickListener(this);
        button.setOnClickListener(this);
        button3.setOnClickListener(this);
        peopleNow = name;
        infoWindow = popupWindow;
        return popupWindow;
    }

    //发送私信和祝语,i = 0私信，i = 1改变宣言
    private void showSendDialog(String str, int i) {
        String str2;
        String str3;
        View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_1);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        inflate.findViewById(R.id.title_2).setVisibility(View.GONE);
        inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
        textView2.setText("内容:");
        if (i == 0) {
            str2 = "发送";
            str3 = "发送私信给:" + str;
        } else if (i == 1) {
            str3 = "设置家族宣言";
            str2 = "修改";
            textView.setText(declaration.getText().toString().substring(6));
        } else {
            return;
        }
        new JPDialog(this).setTitle(str3).loadInflate(inflate).setFirstButton(str2, new ChangeDeclarationClick(this, textView, i, str)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    final void mo2907b(ListView listView, List<HashMap> list) {
        listView.setAdapter(new FamilyPeopleAdapter(list, jpapplication, layoutinflater, this));
    }

    public final void sendMsg(byte b, byte b2, String str) {
        if (cs != null) {
            cs.writeData(b, b2, (byte) 0, str, null);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (i != null && requestCode == 2 && resultCode == Activity.RESULT_OK) {
            try {
                String filePath = getFileFromUri(i.getData(), this);
                if (filePath != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    byte[] pic = compressScale(bitmap);
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("K", 11);
                    jSONObject.put("J", GZIP.arrayToZIP(pic));
                    sendMsg((byte) 18, (byte) 0, jSONObject.toString());
                } else {
                    Toast.makeText(this, "上传出现错误!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileFromUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        switch (uri.getScheme()) {
            case "content":
                int sdkVersion = Build.VERSION.SDK_INT;
                if (sdkVersion < 11) return getRealPathFromUri_BelowApi11(context, uri);
                if (sdkVersion < 19) return getRealPathFromUri_Api11To18(context, uri);
                else return getRealPathFromUri_AboveApi19(context, uri);
            case "file":
                return uri.getPath();
            default:
                return null;
        }
    }

    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    } else {
                        contentUri = MediaStore.Files.getContentUri("external");
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        return null;
    }

    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        //这个有两个包不知道是哪个。。。。不过这个复杂版一般用不到
        CursorLoader loader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.MediaColumns.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private byte[] compressScale(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 32f;
        int be = 1;// be=1表示不缩放
        if (w > h && w > hh) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / hh);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    private byte[] compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        return baos.toByteArray();
    }*/

    @Override
    protected void onDestroy() {
        JPStack.create();
        JPStack.pop(this);
        super.onDestroy();
    }
}
