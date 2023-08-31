package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Selection;
import android.text.Spannable;
import android.widget.Toast;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.thread.PlaySongs;
import ly.pp.justpiano3.utils.*;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.activity.OLMainMode;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.listener.DialogDismissClick;
import protobuf.dto.OnlineQuitRoomDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.util.Date;

public final class OLPlayRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLPlayRoomHandler(OLPlayRoom olPlayRoom) {
        weakReference = new WeakReference<>(olPlayRoom);
    }

    @Override
    public void handleMessage(Message message) {
        OLPlayRoom olPlayRoom = (OLPlayRoom) weakReference.get();
        try {
            switch (message.what) {
                case 1:
                    post(() -> {
                        olPlayRoom.mo2861a(olPlayRoom.playerGrid, message.getData());
                        String str1 = message.getData().getString("SI");
                        if (!str1.isEmpty()) {
                            int diao = message.getData().getInt("diao");
                            olPlayRoom.setdiao(diao);
                            str1 = "songs/" + str1 + ".pm";
                            PlaySongs.setSongPath(str1);
                            String[] a = olPlayRoom.querySongNameAndDiffByPath(str1);
                            String string = a[0];
                            String str2 = a[1];
                            if (string != null) {
                                olPlayRoom.songNameText.setText(string + "[难度:" + str2 + "]");
                                try {
                                    if (olPlayRoom.getMode() == RoomModeEnum.NORMAL.getCode()) {
                                        if (diao > 0) {
                                            olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().toString().charAt(0) + "+" + diao);
                                        } else if (diao < 0) {
                                            olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().toString().charAt(0) + "" + diao);
                                        } else {
                                            olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().toString().charAt(0) + "0" + diao);
                                        }
                                    }
                                    olPlayRoom.jpapplication.startPlaySongOnline(str1, olPlayRoom, olPlayRoom.getdiao());
                                } catch (Exception e) {
                                    return;
                                }
                            }
                        }
                        int i = message.getData().getBoolean("MSG_I") ? 1 : 0;
                        int i2 = message.getData().getInt("MSG_CT");
                        byte b = (byte) message.getData().getInt("MSG_CI");
                        String string = message.getData().getString("MSG_C");
                        if (!string.isEmpty()) {
                            olPlayRoom.mo2860a(i, string, i2, b);
                        }
                    });
                    return;
                case 2:
                case 4:
                    post(() -> {
                        if (olPlayRoom.msgList.size() > olPlayRoom.maxListValue) {
                            olPlayRoom.msgList.remove(0);
                        }
                        SharedPreferences ds = PreferenceManager.getDefaultSharedPreferences(olPlayRoom);
                        boolean showTime = ds.getBoolean("chats_time_show", false);
                        String time = "";
                        if (showTime) {
                            time = DateUtil.format(new Date(EncryptUtil.getServerTime()), "HH:mm");
                        }
                        message.getData().putString("TIME", time);
                        // 如果聊天人没在屏蔽名单中，则将聊天消息加入list进行渲染展示
                        if (!ChatBlackUserUtil.isUserInChatBlackList(olPlayRoom.jpapplication.getChatBlackList(), message.getData().getString("U"))) {
                            olPlayRoom.msgList.add(message.getData());
                        }

                        // 聊天音效播放
                        if (GlobalSetting.INSTANCE.getChatSound() && !message.getData().getString("U").equals(olPlayRoom.jpapplication.getKitiName())) {
                            SoundEngineUtil.playChatSound();
                        }

                        // 聊天记录存储
                        if (ds.getBoolean("save_chats", false)) {
                            try {
                                File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats");
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                String date = DateUtil.format(DateUtil.now(), "yyyy-MM-dd聊天记录");
                                file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats/" + date + ".txt");
                                if (!file.exists()) {
                                    file.createNewFile();
                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                                    fileOutputStream.write((date + ":\n").getBytes());
                                    fileOutputStream.close();
                                }
                                FileWriter writer = new FileWriter(file, true);
                                String str = message.getData().getString("M");
                                if (str.startsWith("//")) {
                                    writer.close();
                                    olPlayRoom.mo2862a(showTime);
                                    return;
                                } else if (message.getData().getInt("T") == 2) {
                                    writer.write((time + "[私]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + "\n"));
                                    writer.close();
                                } else if (message.getData().getInt("T") == 1) {
                                    writer.write((time + "[公]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + "\n"));
                                    writer.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        olPlayRoom.mo2862a(showTime);
                    });
                    return;
                case 3:
                    post(() -> {
                        String str1 = message.getData().getString("song_path");
                        int diao = message.getData().getInt("diao");
                        if (!str1.isEmpty()) {
                            str1 = "songs/" + str1 + ".pm";
                            PlaySongs.setSongPath(str1);
                            String[] a = olPlayRoom.querySongNameAndDiffByPath(str1);
                            String string = a[0];
                            String str2 = a[1];
                            if (string != null) {
                                olPlayRoom.setdiao(diao);
                                if (diao > 0) {
                                    olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().subSequence(0, 1) + "+" + diao);
                                } else if (diao < 0) {
                                    olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().subSequence(0, 1) + "" + diao);
                                } else {
                                    olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().subSequence(0, 1) + "0" + diao);
                                }
                                olPlayRoom.songNameText.setText(string + "[难度:" + str2 + "]");
                                try {
                                    olPlayRoom.jpapplication.startPlaySongOnline(str1, olPlayRoom, diao);
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    });
                    return;
                case 5:
                    post(() -> {
                        olPlayRoom.jpapplication.stopPlaySong();
                        String str1 = message.getData().getString("S");
                        if (!olPlayRoom.isOnStart) {
                            olPlayRoom.jpapplication.getConnectionService().writeData(OnlineProtocolType.QUIT_ROOM, OnlineQuitRoomDTO.getDefaultInstance());
                            Intent intent = new Intent(olPlayRoom, OLPlayHall.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("hallName", olPlayRoom.hallName);
                            bundle.putByte("hallID", olPlayRoom.hallID0);
                            intent.putExtras(bundle);
                            olPlayRoom.startActivity(intent);
                            olPlayRoom.finish();
                        } else if (!str1.isEmpty()) {
                            olPlayRoom.setdiao(message.getData().getInt("D"));
                            str1 = "songs/" + str1 + ".pm";
                            String str = olPlayRoom.querySongNameAndDiffByPath(str1)[0];
                            if (str != null) {
                                olPlayRoom.isOnStart = false;
                                Intent intent2 = new Intent(olPlayRoom, PianoPlay.class);
                                intent2.putExtra("head", 2);
                                intent2.putExtra("path", str1);
                                intent2.putExtra("name", str);
                                intent2.putExtra("diao", olPlayRoom.getdiao());
                                intent2.putExtra("roomMode", olPlayRoom.roomMode);
                                intent2.putExtra("hand", olPlayRoom.currentHand);
                                intent2.putExtra("bundle", olPlayRoom.bundle0);
                                intent2.putExtra("bundleHall", olPlayRoom.bundle2);
                                olPlayRoom.startActivity(intent2);
                                olPlayRoom.finish();
                            }
                        }
                    });
                    return;
                case 6:
                    post(() -> {
                        if (olPlayRoom.playButton != null) {
                            olPlayRoom.playButton.setText("取消准备");
                            olPlayRoom.playButton.setTextSize(14);
                        }
                    });
                    return;
                case 7:
                    post(() -> olPlayRoom.mo2861a(olPlayRoom.playerGrid, message.getData()));
                    return;
                case 8:
                    post(() -> {
                        olPlayRoom.jpapplication.stopPlaySong();
                        JPDialog jpdialog = new JPDialog(olPlayRoom);
                        jpdialog.setCancelableFalse();
                        jpdialog.setTitle("提示").setMessage("您已被房主移出房间!").setFirstButton("确定", (dialog, which) -> {
                            olPlayRoom.isOnStart = false;
                            Intent intent = new Intent(olPlayRoom, OLPlayHall.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("hallName", olPlayRoom.hallName);
                            bundle.putByte("hallID", olPlayRoom.hallID0);
                            intent.putExtras(bundle);
                            olPlayRoom.startActivity(intent);
                            olPlayRoom.finish();
                        }).showDialog();
                    });
                    return;
                case 9:
                    post(() -> {
                        String string = message.getData().getString("F");
                        switch (message.getData().getInt("T")) {
                            case 0:
                                if (!string.isEmpty()) {
                                    JPDialog jpdialog = new JPDialog(olPlayRoom);
                                    jpdialog.setTitle("好友请求");
                                    jpdialog.setMessage("[" + string + "]请求加您为好友,同意吗?");
                                    String finalString = string;
                                    jpdialog.setFirstButton("同意", (dialog, which) -> {
                                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                        builder.setType(1);
                                        builder.setReject(false);
                                        builder.setName(finalString);
                                        olPlayRoom.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                                        dialog.dismiss();
                                    });
                                    jpdialog.setSecondButton("拒绝", (dialog, which) -> {
                                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                        builder.setType(1);
                                        builder.setReject(true);
                                        builder.setName(finalString);
                                        olPlayRoom.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                                        dialog.dismiss();
                                    });
                                    jpdialog.showDialog();
                                }
                                return;
                            case 1:
                                DialogUtil.setShowDialog(false);
                                string = message.getData().getString("F");
                                int i = message.getData().getInt("I");
                                JPDialog jpdialog2 = new JPDialog(olPlayRoom);
                                jpdialog2.setTitle("请求结果");
                                if (i == 0) {
                                    jpdialog2.setMessage("[" + string + "]同意添加您为好友!");
                                } else if (i == 1) {
                                    jpdialog2.setMessage("对方拒绝了你的好友请求!");
                                } else if (i == 2) {
                                    jpdialog2.setMessage("对方已经是你的好友!");
                                } else if (i == 3) {
                                    jpdialog2.setTitle(message.getData().getString("title"));
                                    jpdialog2.setMessage(message.getData().getString("Message"));
                                }
                                jpdialog2.setFirstButton("确定", new DialogDismissClick());
                                try {
                                    jpdialog2.showDialog();
                                    return;
                                } catch (Exception e2) {
                                    return;
                                }
                            default:
                        }
                    });
                    return;
                case 10:
                    post(() -> {
                        String name = message.getData().getString("R");
                        olPlayRoom.getIntent().putExtra("R", name);
                        olPlayRoom.roomName = name;
                        olPlayRoom.roomNameView.setText("[" + olPlayRoom.roomID0 + "]" + olPlayRoom.roomName);
                    });
                    return;
                case 11:
                    post(() -> {
                        olPlayRoom.friendPlayerList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayRoom.friendPlayerList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayRoom.mo2863a(olPlayRoom.friendsListView, olPlayRoom.friendPlayerList, 1);
                        }
                        olPlayRoom.canNotNextPage = size < 20;
                    });
                    return;
                case 12:
                    post(() -> {
                        olPlayRoom.roomTabs.setCurrentTab(1);
                        String string = message.getData().getString("U");
                        if (string != null && !string.equals(JPApplication.kitiName)) {
                            olPlayRoom.userTo = "@" + string + ":";
                            olPlayRoom.sendText.setText(olPlayRoom.userTo);
                            CharSequence text = olPlayRoom.sendText.getText();
                            if (text instanceof Spannable) {
                                Selection.setSelection((Spannable) text, text.length());
                            }
                        }
                    });
                    return;
                case 13:
                    post(() -> {
                        olPlayRoom.friendPlayerList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayRoom.friendPlayerList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayRoom.mo2863a(olPlayRoom.friendsListView, olPlayRoom.friendPlayerList, 3);
                        }
                    });
                    return;
                case 14:
                    post(() -> {
                        Bundle data = message.getData();
                        String string = data.getString("Ti");
                        String string2 = data.getString("I");
                        JPDialog jpdialog = new JPDialog(olPlayRoom);
                        jpdialog.setTitle(string);
                        jpdialog.setMessage(string2);
                        jpdialog.setFirstButton("确定", new DialogDismissClick());
                        DialogUtil.handleGoldSend(olPlayRoom.jpapplication, jpdialog, data.getInt("T"), data.getString("N"), data.getString("F"));
                        jpdialog.showDialog();
                    });
                    return;
                case 15:
                    post(() -> {
                        olPlayRoom.invitePlayerList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayRoom.invitePlayerList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayRoom.mo2863a(olPlayRoom.playerListView, olPlayRoom.invitePlayerList, 3);
                        }
                    });
                    return;
                case 16:
                    post(() -> {
                        Bundle data = message.getData();
                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                        builder.setType(2);
                        builder.setName(data.getString("F"));
                        olPlayRoom.friendPlayerList.remove(message.arg1);
                        olPlayRoom.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                        olPlayRoom.mo2863a(olPlayRoom.friendsListView, olPlayRoom.friendPlayerList, 1);
                    });
                    return;
                case 21:
                    post(() -> {
                        Toast.makeText(olPlayRoom, "您已掉线,请检查您的网络再重新登录!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(olPlayRoom, OLMainMode.class);
                        olPlayRoom.startActivity(intent);
                        olPlayRoom.finish();
                    });
                    return;
                case 22:
                    post(() -> {
                        int i = message.getData().getInt("MSG_T");
                        int i2 = message.getData().getInt("MSG_CT");
                        byte b = (byte) message.getData().getInt("MSG_CI");
                        String string = message.getData().getString("MSG_C");
                        if (i != 0) {
                            olPlayRoom.mo2860a(i, string, i2, b);
                        }
                    });
                    return;
                case 23:
                    post(() -> olPlayRoom.showInfoDialog(message.getData()));
                    return;
                default:
            }
        } catch (Exception ignored) {
        }
    }
}
