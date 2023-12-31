package ly.pp.justpiano3.activity.online;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.protobuf.MessageLite;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.ChattingAdapter;
import ly.pp.justpiano3.adapter.ExpressAdapter;
import ly.pp.justpiano3.adapter.MainGameAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.enums.PlaySongsModeEnum;
import ly.pp.justpiano3.listener.AddFriendsClick;
import ly.pp.justpiano3.listener.ChangeRoomNameClick;
import ly.pp.justpiano3.listener.PlayerImageItemClick;
import ly.pp.justpiano3.listener.SendMailClick;
import ly.pp.justpiano3.listener.tab.PlayRoomTabChange;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.utils.ChatUtil;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.DialogUtil;
import ly.pp.justpiano3.utils.EncryptUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.SoundEffectPlayUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPPopupWindow;
import protobuf.dto.OnlineChangeRoomUserStatusDTO;
import protobuf.dto.OnlineCoupleDTO;
import protobuf.dto.OnlineLoadUserInfoDTO;
import protobuf.dto.OnlineQuitRoomDTO;
import protobuf.dto.OnlineRoomChatDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

/**
 * 房间
 */
public class OLRoomActivity extends OLBaseActivity implements Handler.Callback, View.OnClickListener, View.OnLongClickListener {
    // 防止横竖屏切换时前后台状态错误
    private boolean isChangeScreen;
    public int lv;
    public int cl;
    public Handler handler;
    public byte hallId;
    public String hallName;
    private final List<Bundle> friendPlayerList = new ArrayList<>();
    private boolean canNotNextPage;
    private EditText sendTextView;
    private List<Bundle> msgList = new ArrayList<>();
    public GridView playerGrid;
    private final List<Bundle> invitePlayerList = new ArrayList<>();
    public TabHost roomTabs;
    public boolean onStart = true;
    private String userTo = "";
    private ListView playerListView;
    private ListView friendsListView;
    public int page;
    public byte roomId;
    public int roomMode;
    public TextView roomNameView;
    public String roomName;
    public String playerKind = "";
    public Bundle roomInfoBundle;
    public Bundle hallInfoBundle;
    private boolean timeUpdateRunning;
    public ListView msgListView;
    private ImageView expressImageView;
    private LayoutInflater layoutInflater;
    public final List<Bundle> playerList = new ArrayList<>();
    private PopupWindow expressPopupWindow;
    private PopupWindow changeColorPopupWindow;
    protected TextView timeTextView;
    private int colorNum = 99;
    private ImageView changeColorButton;

    protected void showCpDialog(String str, String str2) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_couple_dialog, findViewById(R.id.dialog));
        try {
            JSONObject jSONObject = new JSONObject(str2);
            JSONObject jSONObject2 = jSONObject.getJSONObject("P");
            User user = new User(jSONObject2.getString("N"), jSONObject2.getInt("D_H"),
                    jSONObject2.getInt("D_E"), jSONObject2.getInt("D_J"),
                    jSONObject2.getInt("D_T"), jSONObject2.getInt("D_S"),
                    jSONObject2.getString("S"), jSONObject2.getInt("L"), jSONObject2.getInt("C"));
            JSONObject jSONObject3 = jSONObject.getJSONObject("C");
            User user2 = new User(jSONObject3.getString("N"), jSONObject3.getInt("D_H"),
                    jSONObject3.getInt("D_E"), jSONObject3.getInt("D_J"),
                    jSONObject3.getInt("D_T"), jSONObject3.getInt("D_S"),
                    jSONObject3.getString("S"), jSONObject3.getInt("L"), jSONObject3.getInt("C"));
            JSONObject jSONObject4 = jSONObject.getJSONObject("I");
            TextView textView = inflate.findViewById(R.id.ol_player_level);
            TextView textView2 = inflate.findViewById(R.id.ol_player_class);
            TextView textView3 = inflate.findViewById(R.id.ol_player_clname);
            TextView textView4 = inflate.findViewById(R.id.ol_couple_name);
            TextView textView5 = inflate.findViewById(R.id.ol_couple_level);
            TextView textView6 = inflate.findViewById(R.id.ol_couple_class);
            TextView textView7 = inflate.findViewById(R.id.ol_couple_clname);
            ImageView imageView = inflate.findViewById(R.id.ol_player_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_player_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_player_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_player_hair);
            ImageView imageView4e = inflate.findViewById(R.id.ol_player_eye);
            ImageView imageView5 = inflate.findViewById(R.id.ol_player_shoes);
            ImageView imageView6 = inflate.findViewById(R.id.ol_couple_mod);
            ImageView imageView7 = inflate.findViewById(R.id.ol_couple_trousers);
            ImageView imageView8 = inflate.findViewById(R.id.ol_couple_jacket);
            ImageView imageView9 = inflate.findViewById(R.id.ol_couple_hair);
            ImageView imageView9e = inflate.findViewById(R.id.ol_couple_eye);
            ImageView imageView10 = inflate.findViewById(R.id.ol_couple_shoes);
            TextView textView8 = inflate.findViewById(R.id.couple_bless);
            ImageView imageView11 = inflate.findViewById(R.id.couple_type);
            ((TextView) inflate.findViewById(R.id.ol_player_name)).setText(user.getPlayerName());
            textView.setText("LV." + user.getLevel());
            textView2.setText("CL." + user.getCl());
            textView3.setText(Consts.nameCL[user.getCl()]);
            textView4.setText(user2.getPlayerName());
            textView5.setText("LV." + user2.getLevel());
            textView6.setText("CL." + user2.getCl());
            textView7.setText(Consts.nameCL[user2.getCl()]);
            textView8.setText(jSONObject4.getString("B"));
            imageView11.setImageResource(Consts.couples[jSONObject4.getInt("T")]);
            ImageLoadUtil.setUserDressImageBitmap(this, user, imageView, imageView2, imageView3, imageView4, imageView4e, imageView5);
            ImageLoadUtil.setUserDressImageBitmap(this, user2, imageView6, imageView7, imageView8, imageView9, imageView9e, imageView10);
            new JPDialogBuilder(this).setWidth(288).setTitle(str).loadInflate(inflate)
                    .setFirstButton("祝福:" + jSONObject4.getInt("P"), (dialog, which) -> {
                        try {
                            OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                            builder.setType(5);
                            builder.setRoomPosition(jSONObject4.getInt("I"));
                            sendMsg(OnlineProtocolType.COUPLE, builder.build());
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showInfoDialog(Bundle b) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_user_info_dialog, findViewById(R.id.dialog));
        try {
            User user = new User(b.getString("U"), b.getInt("DR_H"), b.getInt("DR_E"), b.getInt("DR_J"),
                    b.getInt("DR_T"), b.getInt("DR_S"), b.getString("S"), b.getInt("LV"), b.getInt("CL"));
            ImageView imageView = inflate.findViewById(R.id.ol_user_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_user_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_user_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_user_hair);
            ImageView imageView4e = inflate.findViewById(R.id.ol_user_eye);
            ImageView imageView5 = inflate.findViewById(R.id.ol_user_shoes);
            TextView textView = inflate.findViewById(R.id.user_info);
            TextView textView2 = inflate.findViewById(R.id.user_psign);
            ImageLoadUtil.setUserDressImageBitmap(this, user, imageView, imageView2, imageView3, imageView4, imageView4e, imageView5);
            int lv = b.getInt("LV");
            int targetExp = (int) ((0.5 * lv * lv * lv + 500 * lv) / 10) * 10;
            textView.setText("用户名称:" + b.getString("U")
                    + "\n用户等级:LV." + lv
                    + "\n经验进度:" + b.getInt("E") + "/" + targetExp
                    + "\n考级进度:CL." + b.getInt("CL")
                    + "\n所在家族:" + b.getString("F")
                    + "\n在线曲库冠军数:" + b.getInt("W")
                    + "\n在线曲库弹奏总分:" + b.getInt("SC"));
            textView2.setText("个性签名:\n" + (b.getString("P").isEmpty() ? "无" : b.getString("P")));
            new JPDialogBuilder(this).setWidth(324).setTitle("个人资料").loadInflate(inflate)
                    .setFirstButton("加为好友", new AddFriendsClick(this, user.getPlayerName()))
                    .setSecondButton("确定", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(int type, MessageLite msg) {
        if (OnlineUtil.getConnectionService() != null) {
            OnlineUtil.getConnectionService().writeData(type, msg);
        } else {
            Toast.makeText(this, "连接已断开，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    public void bindViewAdapter(ListView listView, List<Bundle> list, int i) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o2.getInt("O"), o1.getInt("O")));
        }
        listView.setAdapter(new MainGameAdapter(list, i));
    }

    public void sendMail(String str) {
        View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_1);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        TextView textView3 = inflate.findViewById(R.id.title_2);
        inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView2.setText("内容:");
        new JPDialogBuilder(this).setTitle("发送私信给:" + str).loadInflate(inflate)
                .setFirstButton("发送", new SendMailClick(this, textView, str))
                .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
    }

    public void setPrivateChatUserName(String str) {
        userTo = "@" + str + ":";
        if (!str.isEmpty() && !str.equals(OLBaseActivity.kitiName)) {
            sendTextView.setText(userTo);
        }
        Spannable text = sendTextView.getText();
        if (text != null) {
            Selection.setSelection(text, text.length());
        }
    }

    @Override
    public void onBackPressed() {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle("提示");
        jpDialogBuilder.setMessage("退出房间并返回大厅?");
        jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
            onStart = false;
            sendMsg(OnlineProtocolType.QUIT_ROOM, OnlineQuitRoomDTO.getDefaultInstance());
            SongPlay.INSTANCE.stopPlay();
            Intent intent = new Intent(this, OLPlayHall.class);
            Bundle bundle = new Bundle();
            bundle.putString("hallName", hallName);
            bundle.putByte("hallID", hallId);
            dialog.dismiss();
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });
        jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
        jpDialogBuilder.buildAndShowDialog();
    }

    protected void sendMessageClick(boolean isBroadcast) {
        OnlineRoomChatDTO.Builder builder = OnlineRoomChatDTO.newBuilder();
        builder.setIsBroadcast(isBroadcast);
        String str = String.valueOf(sendTextView.getText());
        if (!str.startsWith(userTo) || str.length() <= userTo.length()) {
            builder.setUserName("");
            builder.setMessage(str);
        } else {
            builder.setUserName(userTo);
            str = str.substring(userTo.length());
            builder.setMessage(str);
        }
        sendTextView.setText("");
        builder.setColor(colorNum);
        if (!str.isEmpty()) {
            sendMsg(OnlineProtocolType.ROOM_CHAT, builder.build());
        }
        userTo = "";
    }

    protected void changeChatColor(int lv, int colorNum, int color) {
        if (this.lv >= lv) {
            sendTextView.setTextColor(color);
            this.colorNum = colorNum;
        } else {
            Toast.makeText(this, "您的等级未达到" + lv + "级，不能使用该颜色!", Toast.LENGTH_SHORT).show();
        }
        if (changeColorPopupWindow != null) {
            changeColorPopupWindow.dismiss();
        }
    }

    protected void changeRoomTitleClick() {
        if (!playerKind.equals("G")) {
            View inflate = getLayoutInflater().inflate(R.layout.ol_room_title_change, findViewById(R.id.dialog));
            EditText text1 = inflate.findViewById(R.id.text_1);
            // 填充当前房间名称
            text1.setText(roomName);
            EditText text2 = inflate.findViewById(R.id.text_2);
            new JPDialogBuilder(this).setTitle("修改房名").loadInflate(inflate)
                    .setFirstButton("修改", new ChangeRoomNameClick(this, text1, text2))
                    .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        }
    }

    protected void nextFriendPageClick() {
        if (!canNotNextPage) {
            page += 20;
            if (page >= 0) {
                OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(page);
                sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
            }
        }
    }

    protected void onlineFriendListClick() {
        OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
        builder.setType(1);
        builder.setPage(-1);
        sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
    }

    protected void preFriendPageClick() {
        page -= 20;
        if (page < 0) {
            page = 0;
            return;
        }
        OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
        builder.setType(1);
        builder.setPage(page);
        sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
    }

    protected void changeColorClick() {
        if (changeColorPopupWindow != null) {
            int[] iArr = new int[2];
            changeColorButton.getLocationOnScreen(iArr);
            changeColorPopupWindow.showAtLocation(changeColorButton, Gravity.TOP | Gravity.START,
                    (int) (iArr[0] * 0.75f), (int) (iArr[1] * 0.75f));
        }
    }

    private void bindMsgListView() {
        int lastPosition = msgListView.getLastVisiblePosition();
        int count = msgListView.getCount();
        if (msgListView.getAdapter() != null) {
            ((ChattingAdapter) msgListView.getAdapter()).notifyDataSetChanged();
        } else {
            // 只new一次，msgList是引用，不要重新赋值
            msgListView.setAdapter(new ChattingAdapter(msgList, layoutInflater));
        }
        // 如果刷新的时候，位置不在最底部 或 看不到最底部的元素，则不弹回去
        if (lastPosition == count - 1) {
            // 这里计算offset很麻烦，就写了一个比较大的数
            msgListView.smoothScrollToPositionFromTop(lastPosition, -10000, 500);
        }
    }

    public void handleKicked() {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setCancelableFalse();
        jpDialogBuilder.setTitle("提示").setMessage("您已被房主移出房间!").setFirstButton("确定", (dialog, which) -> {
            onStart = false;
            Intent intent = new Intent(this, OLPlayHall.class);
            Bundle bundle = new Bundle();
            bundle.putString("hallName", hallName);
            bundle.putByte("hallID", hallId);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }).buildAndShowDialog();
    }

    public void handleChat(Message message) {
        // 消息处理（流消息，推荐消息等） 返回值表示是否拦截后续执行
        if (specialMessageHandle(message)) {
            return;
        }
        if (msgList.size() > Consts.MAX_CHAT_SAVE_COUNT) {
            msgList.remove(0);
        }
        String time = "";
        if (GlobalSetting.INSTANCE.getShowChatTime()) {
            time = DateUtil.format(new Date(EncryptUtil.getServerTime()), GlobalSetting.INSTANCE.getShowChatTimeModes());
        }
        message.getData().putString("TIME", time);
        // 如果聊天人没在屏蔽名单中，则将聊天消息加入list进行渲染展示
        if (!ChatUtil.isUserInChatBlackList(this, message.getData().getString("U"))) {
            msgList.add(message.getData());
        }
        // 聊天音效播放
        if (GlobalSetting.INSTANCE.getChatsSound() && !Objects.equals(message.getData().getString("U"), OLBaseActivity.getKitiName())) {
            SoundEffectPlayUtil.playSoundEffect(this, Uri.parse(GlobalSetting.INSTANCE.getChatsSoundFile()));
        }
        // 聊天记录存储
        ChatUtil.chatsSaveHandle(message, this, time);
        bindMsgListView();
    }

    /**
     * 特殊消息处理程序
     *
     * @param message 当前消息
     */
    private boolean specialMessageHandle(Message message) {
        Bundle data = message.getData();
        int type = data.getInt("T");
        switch (type) {
            case OnlineProtocolType.MsgType.SONG_RECOMMEND_MESSAGE -> {
                String item = data.getString("I");
                String songName = null;
                String songDifficulty = null;
                if (!item.isEmpty()) {
                    String path = "songs/" + item + ".pm";
                    List<Song> songByFilePath = JPApplication.getSongDatabase().songDao().getSongByFilePath(path);
                    for (Song song : songByFilePath) {
                        songName = song.getName();
                        songDifficulty = String.format(Locale.getDefault(), "%.1f", song.getRightHandDegree());
                    }
                }
                data.putString("M", data.getString("M") + songName + "[难度:" + songDifficulty + "]");
                message.setData(data);
            }
            case OnlineProtocolType.MsgType.STREAM_MESSAGE -> {
                String streamId = message.getData().getString(OnlineProtocolType.MsgType.StreamMsg.PARAM_ID);
                if (streamId != null && !streamId.isEmpty()) {
                    for (Bundle bundle : msgList) {
                        // 找到之前的流消息，将新的数据替换进去，随后就不用向下执行了
                        if (streamId.equals(bundle.getString(OnlineProtocolType.MsgType.StreamMsg.PARAM_ID))) {
                            bundle.putString("M", bundle.getString("M") + message.getData().getString("M"));
                            bundle.putBoolean(OnlineProtocolType.MsgType.StreamMsg.PARAM_STATUS,
                                    message.getData().getBoolean(OnlineProtocolType.MsgType.StreamMsg.PARAM_STATUS));
                            bindMsgListView();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void handleFriendRequest(Message message) {
        String string = message.getData().getString("F");
        switch (message.getData().getInt("T")) {
            case 0 -> {
                if (!string.isEmpty()) {
                    JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                    jpDialogBuilder.setTitle("好友请求");
                    jpDialogBuilder.setMessage("[" + string + "]请求加您为好友,同意吗?");
                    String finalString = string;
                    jpDialogBuilder.setFirstButton("同意", (dialog, which) -> {
                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                        builder.setType(1);
                        builder.setReject(false);
                        builder.setName(finalString);
                        sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                        dialog.dismiss();
                    });
                    jpDialogBuilder.setSecondButton("拒绝", (dialog, which) -> {
                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                        builder.setType(1);
                        builder.setReject(true);
                        builder.setName(finalString);
                        sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                        dialog.dismiss();
                    });
                    jpDialogBuilder.buildAndShowDialog();
                }
            }
            case 1 -> {
                DialogUtil.setShowDialog(false);
                string = message.getData().getString("F");
                int i = message.getData().getInt("I");
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                jpDialogBuilder.setTitle("请求结果");
                switch (i) {
                    case 0 -> jpDialogBuilder.setMessage("[" + string + "]同意添加您为好友!");
                    case 1 -> jpDialogBuilder.setMessage("对方拒绝了你的好友请求!");
                    case 2 -> jpDialogBuilder.setMessage("对方已经是你的好友!");
                    case 3 -> {
                        jpDialogBuilder.setTitle(message.getData().getString("title"));
                        jpDialogBuilder.setMessage(message.getData().getString("Message"));
                    }
                }
                jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                jpDialogBuilder.buildAndShowDialog();
            }
            default -> {
            }
        }
    }

    public void handleOffline() {
        Toast.makeText(this, "您已掉线，请检查您的网络再重新登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(this, OLMainMode.class);
        startActivity(intent);
        finish();
    }

    public void handleSetUserInfo(Message message) {
        Bundle data = message.getData();
        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
        builder.setType(2);
        builder.setName(data.getString("F"));
        friendPlayerList.remove(message.arg1);
        sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
        bindViewAdapter(friendsListView, friendPlayerList, 1);
    }

    public void handleInvitePlayerList(Message message) {
        doHandlePlayerList(message, invitePlayerList, playerListView);
    }

    public void handlePrivateChat(Message message) {
        roomTabs.setCurrentTab(1);
        String string = message.getData().getString("U");
        if (string != null && !string.equals(OLBaseActivity.kitiName)) {
            userTo = "@" + string + ":";
            sendTextView.setText(userTo);
            Spannable text = sendTextView.getText();
            if (text != null) {
                Selection.setSelection(text, text.length());
            }
        }
    }

    public void handleRefreshFriendList(Message message) {
        friendPlayerList.clear();
        Bundle data = message.getData();
        int size = data.size();
        if (size >= 0) {
            for (int i = 0; i < size; i++) {
                friendPlayerList.add(data.getBundle(String.valueOf(i)));
            }
            bindViewAdapter(friendsListView, friendPlayerList, 1);
        }
        canNotNextPage = size < 20;
    }

    public void handleRefreshFriendListWithoutPage(Message message) {
        doHandlePlayerList(message, friendPlayerList, friendsListView);
    }

    private void doHandlePlayerList(Message message, List<Bundle> friendPlayerList, ListView friendsListView) {
        friendPlayerList.clear();
        Bundle data = message.getData();
        int size = data.size();
        if (size >= 0) {
            for (int i = 0; i < size; i++) {
                friendPlayerList.add(data.getBundle(String.valueOf(i)));
            }
            bindViewAdapter(friendsListView, friendPlayerList, 3);
        }
    }

    public void handleDialog(Message message) {
        Bundle data = message.getData();
        String string = data.getString("Ti");
        String string2 = data.getString("I");
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle(string);
        jpDialogBuilder.setMessage(string2);
        jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
        DialogUtil.handleGoldSend(jpDialogBuilder, data.getInt("T"), data.getString("N"), data.getString("F"));
        jpDialogBuilder.buildAndShowDialog();
    }

    protected void initRoomActivity(Bundle savedInstanceState) {
        layoutInflater = LayoutInflater.from(this);
        getRoomPlayerMap().clear();
        roomNameView = findViewById(R.id.room_title);
        roomInfoBundle = getIntent().getExtras();
        hallInfoBundle = roomInfoBundle.getBundle("bundle");
        hallId = hallInfoBundle.getByte("hallID");
        hallName = hallInfoBundle.getString("hallName");
        roomId = roomInfoBundle.getByte("ID");
        roomName = roomInfoBundle.getString("R");
        playerKind = roomInfoBundle.getString("isHost");
        roomMode = roomInfoBundle.getInt("mode");
        roomNameView.setText("[" + roomId + "]" + roomName);
        roomNameView.setOnClickListener(this);
        playerGrid = findViewById(R.id.ol_player_grid);
        playerGrid.setCacheColorHint(Color.TRANSPARENT);
        playerGrid.setOnItemClickListener(new PlayerImageItemClick(this));
        playerList.clear();
        msgListView = findViewById(R.id.ol_msg_list);
        msgListView.setCacheColorHint(Color.TRANSPARENT);
        findViewById(R.id.ol_send_b).setOnClickListener(this);
        findViewById(R.id.ol_send_b).setOnLongClickListener(this);
        findViewById(R.id.pre_button).setOnClickListener(this);
        findViewById(R.id.next_button).setOnClickListener(this);
        findViewById(R.id.online_button).setOnClickListener(this);
        timeTextView = findViewById(R.id.time_text);
        friendsListView = findViewById(R.id.ol_friend_list);
        friendsListView.setCacheColorHint(Color.TRANSPARENT);
        sendTextView = findViewById(R.id.ol_send_text);
        expressImageView = findViewById(R.id.ol_express_b);
        changeColorButton = findViewById(R.id.ol_changecolor);
        playerListView = findViewById(R.id.ol_player_list);
        playerListView.setCacheColorHint(Color.TRANSPARENT);
        expressImageView.setOnClickListener(this);
        changeColorButton.setOnClickListener(this);
        handler = new Handler(this);
        PopupWindow expressPopupWindow = new JPPopupWindow(this);
        View expressView = LayoutInflater.from(this).inflate(R.layout.ol_express_list, null);
        expressPopupWindow.setContentView(expressView);
        ((GridView) expressView.findViewById(R.id.ol_express_grid)).setAdapter(
                new ExpressAdapter(this, Consts.expressions, expressPopupWindow, OnlineProtocolType.ROOM_CHAT));
        expressPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable._none, getTheme()));
        this.expressPopupWindow = expressPopupWindow;
        PopupWindow changeColorPopupWindow = new JPPopupWindow(this);
        View roomColorPickView = LayoutInflater.from(this).inflate(R.layout.ol_room_color_pick, null);
        changeColorPopupWindow.setContentView(roomColorPickView);
        changeColorPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable._none, getTheme()));
        roomColorPickView.findViewById(R.id.white).setOnClickListener(this);
        roomColorPickView.findViewById(R.id.yellow).setOnClickListener(this);
        roomColorPickView.findViewById(R.id.blue).setOnClickListener(this);
        roomColorPickView.findViewById(R.id.red).setOnClickListener(this);
        roomColorPickView.findViewById(R.id.orange).setOnClickListener(this);
        roomColorPickView.findViewById(R.id.purple).setOnClickListener(this);
        roomColorPickView.findViewById(R.id.pink).setOnClickListener(this);
        roomColorPickView.findViewById(R.id.gold).setOnClickListener(this);
        roomColorPickView.findViewById(R.id.green).setOnClickListener(this);
        roomColorPickView.findViewById(R.id.black).setOnClickListener(this);
        this.changeColorPopupWindow = changeColorPopupWindow;
        roomTabs = findViewById(R.id.tabhost);
        roomTabs.setup();
        TabHost.TabSpec newTabSpec = roomTabs.newTabSpec("tab1");
        newTabSpec.setContent(R.id.friend_tab);
        newTabSpec.setIndicator("好友");
        roomTabs.addTab(newTabSpec);
        newTabSpec = roomTabs.newTabSpec("tab2");
        newTabSpec.setContent(R.id.msg_tab);
        newTabSpec.setIndicator("聊天");
        roomTabs.addTab(newTabSpec);
        roomTabs.setOnTabChangedListener(new PlayRoomTabChange(this));
        timeUpdateRunning = true;
        ThreadPoolUtil.execute(() -> {
            do {
                try {
                    Message message = Message.obtain(handler);
                    message.what = 3;
                    handler.sendMessage(message);
                    Thread.sleep(60000);
                } catch (Exception e) {
                    timeUpdateRunning = false;
                }
            } while (timeUpdateRunning);
        });
        if (savedInstanceState != null) {
            msgList = savedInstanceState.getParcelableArrayList("msgList");
            bindMsgListView();
        } else {
            SongPlay.INSTANCE.setPlaySongsMode(PlaySongsModeEnum.ONCE);
        }
    }

    protected void setTabTitleViewLayout(int i) {
        TextView textView = roomTabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
        textView.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    }

    protected void changeScreenOrientation() {
        isChangeScreen = true;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.what == 3) {
            CharSequence format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(new Date());
            if (timeTextView != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                    timeTextView.setText(format + "  " + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%🔋");
                } else {
                    timeTextView.setText(format);
                    timeTextView.setTextSize(20);
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pre_button) {
            preFriendPageClick();
        } else if (id == R.id.online_button) {
            onlineFriendListClick();
        } else if (id == R.id.next_button) {
            nextFriendPageClick();
        } else if (id == R.id.ol_send_b) {
            sendMessageClick(false);
        } else if (id == R.id.ol_express_b) {
            expressPopupWindow.showAtLocation(expressImageView, Gravity.CENTER, 0, 0);
        } else if (id == R.id.ol_changecolor) {
            changeColorClick();
        } else if (id == R.id.white) {
            changeChatColor(0, 48, 0xffffffff);
        } else if (id == R.id.yellow) {
            changeChatColor(10, 1, 0xFFFFFACD);
        } else if (id == R.id.blue) {
            changeChatColor(14, 2, 0xFF00FFFF);
        } else if (id == R.id.red) {
            changeChatColor(18, 3, 0xFFFF6666);
        } else if (id == R.id.orange) {
            changeChatColor(22, 4, 0xFFFFA500);
        } else if (id == R.id.purple) {
            changeChatColor(25, 5, 0xFFBA55D3);
        } else if (id == R.id.pink) {
            changeChatColor(30, 6, 0xFFFA60EA);
        } else if (id == R.id.gold) {
            changeChatColor(35, 7, 0xFFFFD700);
        } else if (id == R.id.green) {
            changeChatColor(40, 8, 0xFFB7FF72);
        } else if (id == R.id.black) {
            changeChatColor(50, 9, 0xFF000000);
        } else if (id == R.id.room_title) {
            changeRoomTitleClick();
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("msgList", new ArrayList<>(msgList));
    }

    @Override
    protected void onDestroy() {
        timeUpdateRunning = false;
        msgList.clear();
        playerList.clear();
        invitePlayerList.clear();
        friendPlayerList.clear();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!onStart) {
            onStart = true;
            sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, OnlineChangeRoomUserStatusDTO.newBuilder().setStatus("N").build());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!onStart) {
            onStart = true;
            sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, OnlineChangeRoomUserStatusDTO.newBuilder().setStatus("N").build());
            roomTabs.setCurrentTab(1);
            if (msgListView != null && msgListView.getAdapter() != null) {
                msgListView.setSelection(msgListView.getAdapter().getCount() - 1);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (onStart && !isChangeScreen) {
            onStart = false;
            sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, OnlineChangeRoomUserStatusDTO.newBuilder().setStatus("B").build());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int vid = v.getId();
        if (vid == R.id.ol_send_b) {
            String text = sendTextView.getText().toString();
            if (!text.isEmpty()) {
                new JPDialogBuilder(this)
                        .setTitle("全服消息")
                        .setMessage("您是否需要发送全服广播？\n(如无\"全服广播\"商品，将为您自动扣费5音符购买)\n发送内容：\"" + sendTextView.getText() + '\"')
                        .setFirstButton("确定", (dialog, i) -> {
                            sendMessageClick(true);
                            dialog.dismiss();
                        })
                        .setSecondButton("取消", (dialog, which) -> dialog.dismiss())
                        .buildAndShowDialog();
            }
            return true;
        }
        return false;
    }
}
