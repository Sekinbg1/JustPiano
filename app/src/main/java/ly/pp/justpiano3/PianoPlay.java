package ly.pp.justpiano3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public final class PianoPlay extends BaseActivity {
    private static final int f4579ab = 44100;
    private static int f4609ae = 0;
    public byte hallID;
    public TextView l_nandu;
    public TextView time_mid;
    HorizontalListView horizontalListView;
    TextView showHideGrade;
    Map userMap = null;
    boolean isRecord;
    PianoPlayHandler pianoPlayHandler = new PianoPlayHandler(this);
    View f4591J;
    int times;
    String songsName;
    TextView songName;
    TextView f4596O;
    TextView f4597P;
    ListView gradeListView;
    TextView finishSongName;
    List<Bundle> gradeList = new ArrayList<>();
    LayoutInflater layoutinflater;
    KeyBoardView keyboardview;
    boolean isShowingSongsInfo;
    Bundle roomBundle;
    boolean isPlayingStart;
    boolean f4619j;
    boolean f4620k;
    JPProgressBar jpprogressbar;
    ImageButton startPlayButton;
    PlayView playView;
    double nandu;
    int score;
    JPApplication jpapplication;
    View finishView;
    private View f4592K;
    private LayoutParams layoutparams2;
    private ConnectionService connectionService;
    private ProgressBar progressbar;
    private Bundle hallBundle;
    private AudioRecord audiorecord;
    private boolean f4611ag;
    private String recordRawPath;
    private String recordWavPath;
    private int roomMode = 0;
    private LayoutParams layoutparams;
    private double localRNandu;
    private double localLNandu;
    private int localSongsTime;
    private int playKind;

    static void m3786a(String str, String str2) {
        FileNotFoundException e2;
        FileInputStream fileInputStream;
        long j = f4579ab;
        long j2 = f4579ab * 4;
        byte[] bArr = new byte[f4609ae];
        FileInputStream fileInputStream2;
        FileOutputStream fileOutputStream;
        try {
            fileInputStream2 = new FileInputStream(str);
            try {
                fileOutputStream = new FileOutputStream(str2);
                try {
                    long size = 36 + fileInputStream2.getChannel().size();
                    fileOutputStream.write(new byte[]{(byte) 82, (byte) 73, (byte) 70, (byte) 70,
                            (byte) ((int) (255 & size)), (byte) ((int) ((size >> 8) & 255)),
                            (byte) ((int) ((size >> 16) & 255)), (byte) ((int) ((size >> 24) & 255)),
                            (byte) 87, (byte) 65, (byte) 86, (byte) 69, (byte) 102, (byte) 109, (byte) 116,
                            (byte) 32, (byte) 16, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 2,
                            (byte) 0, (byte) ((int) (255 & j)), (byte) ((int) ((j >> 8) & 255)),
                            (byte) ((int) ((j >> 16) & 255)), (byte) ((int) ((j >> 24) & 255)),
                            (byte) ((int) (255 & j2)), (byte) ((int) ((j2 >> 8) & 255)),
                            (byte) ((int) ((j2 >> 16) & 255)), (byte) ((int) ((j2 >> 24) & 255)),
                            (byte) 4, (byte) 0, (byte) 16, (byte) 0, (byte) 100, (byte) 97, (byte) 116,
                            (byte) 97, (byte) ((int) (255 & (fileInputStream2.getChannel().size() + 36))),
                            (byte) ((int) (((fileInputStream2.getChannel().size() + 36) >> 8) & 255)),
                            (byte) ((int) (((fileInputStream2.getChannel().size() + 36) >> 16) & 255)),
                            (byte) ((int) ((fileInputStream2.getChannel().size() >> 24) & 255))}, 0, 44);
                    while (fileInputStream2.read(bArr) != -1) {
                        fileOutputStream.write(bArr);
                    }
                    try {
                        fileInputStream2.close();
                        fileOutputStream.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                } catch (FileNotFoundException ignored) {
                }
            } catch (FileNotFoundException e6) {
                e2 = e6;
                fileInputStream = fileInputStream2;
                try {
                    e2.printStackTrace();
                    try {
                        fileInputStream.close();
                    } catch (IOException e32) {
                        e32.printStackTrace();
                    }
                } catch (Throwable th2) {
                    fileInputStream2 = fileInputStream;
                    try {
                        fileInputStream2.close();
                    } catch (IOException e7) {
                        e7.printStackTrace();
                    }
                }
            } catch (Throwable th4) {
                fileInputStream2.close();
            }
        } catch (IOException e9) {
            e9.printStackTrace();
        }
    }

    private List<Bundle> m3783a(List<Bundle> list, String str) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.valueOf((String) o2.get(str)).compareTo(Integer.valueOf((String) o1.get(str))));
        }
        return list;
    }

    private void m3785a(int i, boolean z) {
        if (z) {  //仅显示对话框就行了，其他不做不分析，用于按下后退键时
            if (i == 0) {
                f4596O.setVisibility(View.VISIBLE);
            } else {
                f4596O = findViewById(R.id.m_nandu);
                f4596O.setVisibility(View.GONE);
            }
            f4597P.setVisibility(View.VISIBLE);
            startPlayButton.setVisibility(View.VISIBLE);
            songName.setText(songsName);
            return;
        }
        isShowingSongsInfo = true;
        addContentView(f4591J, layoutparams2);
        songName = findViewById(R.id.m_name);
        progressbar = findViewById(R.id.m_progress);
        f4596O = findViewById(R.id.m_nandu);
        l_nandu = findViewById(R.id.l_nandu);
        time_mid = findViewById(R.id.time_mid);
        f4597P = findViewById(R.id.m_score);
        f4597P.setText("最高纪录:" + score);
        startPlayButton = findViewById(R.id.p_start);
        songName.setText(songsName);
        switch (i) {
            case 0:    //本地
                startPlayButton.setOnClickListener(v -> {
                    f4596O.setVisibility(View.GONE);
                    f4597P.setVisibility(View.GONE);
                    startPlayButton.setVisibility(View.GONE);
                    mo2906a(false);
                });
                f4596O.setText("右手难度:" + localRNandu);
                l_nandu.setText("左手难度:" + localLNandu);
                String str1 = localSongsTime / 60 >= 10 ? "" + localSongsTime / 60 : "0" + localSongsTime / 60;
                String str2 = localSongsTime % 60 >= 10 ? "" + localSongsTime % 60 : "0" + localSongsTime % 60;
                time_mid.setText("曲目时长:" + str1 + ":" + str2);
                return;
            case 1:    //在线曲库
                startPlayButton.setOnClickListener(v -> {
                    f4596O.setVisibility(View.GONE);
                    f4597P.setVisibility(View.GONE);
                    startPlayButton.setVisibility(View.GONE);
                    mo2906a(false);
                });
                time_mid.setText("难度:" + nandu);
                f4596O.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                return;
            case 2:    //联网对战
                songName.setText("请稍后...");
                progressbar.setVisibility(View.VISIBLE);
                f4596O.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                f4597P.setVisibility(View.GONE);
                startPlayButton.setVisibility(View.GONE);
                time_mid.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                addContentView(f4592K, layoutparams);
                f4592K.setVisibility(View.VISIBLE);
                addContentView(finishView, layoutparams2);
                ImageButton f4582A = finishView.findViewById(R.id.ol_ok);
                finishView.findViewById(R.id.share_score);
                f4582A.setOnClickListener(v -> {
                    Intent intent = new Intent(PianoPlay.this, OLPlayRoom.class);
                    intent.putExtras(roomBundle);
                    startActivity(intent);
                    finish();
                });
                finishView.setVisibility(View.GONE);
                sendMsg((byte) 23, (byte) 0, "", null);
                songName.setOnClickListener(v -> sendMsg((byte) 23, (byte) 0, "", null));
                return;
            case 3:    //考级
                songName.setText("请稍后...");
                progressbar.setVisibility(View.VISIBLE);
                f4596O.setVisibility(View.GONE);
                f4597P.setVisibility(View.GONE);
                startPlayButton.setVisibility(View.GONE);
                time_mid.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                addContentView(f4592K, layoutparams);
                f4592K.setVisibility(View.VISIBLE);
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("T", 2);
                    sendMsg((byte) 40, (byte) 0, jSONObject.toString(), null);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case 4:    //挑战
                songName.setText("请稍后...");
                progressbar.setVisibility(View.VISIBLE);
                f4596O.setVisibility(View.GONE);
                f4597P.setVisibility(View.GONE);
                startPlayButton.setVisibility(View.GONE);
                time_mid.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                addContentView(f4592K, layoutparams);
                f4592K.setVisibility(View.VISIBLE);
                sendMsg((byte) 16, (byte) 0, "3", null);
                return;
            default:
        }
    }

    private boolean recordReady(String str) {
        try {
            recordRawPath = Environment.getExternalStorageDirectory() + "/JustPiano/Record/buf.raw";
            recordWavPath = Environment.getExternalStorageDirectory() + "/JustPiano/Record/" + str + ".wav";
            f4609ae = AudioRecord.getMinBufferSize(f4579ab, 12, 2);
            int f4608aa = 1;
            audiorecord = new AudioRecord(f4608aa, f4579ab, 12, 2, f4609ae);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean m3792e() {
        FileOutputStream fileOutputStream;
        byte[] bArr = new byte[f4609ae];
        boolean z = true;
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Record");
            file.mkdirs();
            File file2 = new File(file, "buf.raw");
            if (file2.exists()) {
                file2.delete();
            }
            fileOutputStream = new FileOutputStream(file2);
        } catch (Exception e) {
            e.printStackTrace();
            f4611ag = false;
            Toast.makeText(this, "无效的储存路径,请检查SD卡是否插入!", Toast.LENGTH_SHORT).show();
            z = false;
            fileOutputStream = null;
        }
        while (f4611ag) {
            if (-3 != audiorecord.read(bArr, 0, f4609ae)) {
                try {
                    fileOutputStream.write(bArr);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        try {
            fileOutputStream.close();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return z;
    }

    public void m3794f() {
        f4591J = LayoutInflater.from(this).inflate(R.layout.pusedplay, null);
        layoutparams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutparams2.gravity = android.view.Gravity.CENTER;
        layoutparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutparams.topMargin = 0;
        layoutparams.leftMargin = 0;
        Bundle extras = getIntent().getExtras();
        playKind = extras.getInt("head");
        JPApplication jPApplication;
        String str;
        switch (playKind) {
            case 0:    //本地模式
                String songsPath = extras.getString("path");
                songsName = extras.getString("name");
                localRNandu = BigDecimal.valueOf(extras.getDouble("nandu")).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                localLNandu = BigDecimal.valueOf(extras.getDouble("leftnandu")).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                localSongsTime = extras.getInt("songstime");
                score = extras.getInt("score");
                isRecord = extras.getBoolean("isrecord");
                int hand = extras.getInt("hand");
                jPApplication = jpapplication;
                str = songsPath;
                playView = new PlayView(jPApplication, this, str, this, localRNandu, localLNandu, score, playKind, hand, 30, localSongsTime, 0);
                break;
            case 1:    //在线曲库
                songsName = extras.getString("songName");
                nandu = BigDecimal.valueOf(extras.getDouble("degree")).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                score = extras.getInt("topScore");
                String songId = extras.getString("songID");
                jPApplication = jpapplication;
                byte[] byteArray = extras.getByteArray("songBytes");
                playView = new PlayView(jPApplication, this, byteArray, this, nandu, score, 1, 0, songId);
                break;
            case 2:    //房间对战
                connectionService = jpapplication.getConnectionService();
                roomBundle = extras.getBundle("bundle");
                hallBundle = extras.getBundle("bundleHall");
                hallID = hallBundle.getByte("hallID");
                userMap = jpapplication.getHashmap();
                songsPath = extras.getString("path");
                songsName = extras.getString("name");
                int diao = extras.getInt("diao");
                hand = extras.getInt("hand");
                roomMode = extras.getInt("roomMode");
                f4592K = layoutinflater.inflate(R.layout.ol_score_view, null);
                horizontalListView = f4592K.findViewById(R.id.ol_score_list);
                showHideGrade = f4592K.findViewById(R.id.ol_score_button);
                int visibility = horizontalListView.getVisibility();
                if (visibility == View.VISIBLE) {
                    showHideGrade.setText("隐藏成绩");
                } else if (visibility == View.GONE) {
                    showHideGrade.setText("显示成绩");
                }
                showHideGrade.setOnClickListener(new ShowOrHideMiniGradeClick(this));
                gradeList.clear();
                finishView = layoutinflater.inflate(R.layout.ol_finish_view, null);
                finishSongName = finishView.findViewById(R.id.ol_song_name);
                gradeListView = finishView.findViewById(R.id.ol_finish_list);
                gradeListView.setCacheColorHint(0);
                jPApplication = jpapplication;
                str = songsPath;
                playView = new PlayView(jPApplication, this, str, this, nandu, nandu, score, playKind, hand, 30, 0, diao);
                break;
            case 3:    //大厅考级
            case 4:    //挑战
                connectionService = jpapplication.getConnectionService();
                roomBundle = extras.getBundle("bundle");
                hallBundle = extras.getBundle("bundleHall");
                hallID = hallBundle.getByte("hallID");
                userMap = jpapplication.getHashmap();
                String songBytes = extras.getString("songBytes");
                songsName = extras.getString("name");
                times = extras.getInt("times");
                hand = extras.getInt("hand");
                f4592K = layoutinflater.inflate(R.layout.ol_score_view, null);
                horizontalListView = f4592K.findViewById(R.id.ol_score_list);
                showHideGrade = f4592K.findViewById(R.id.ol_score_button);
                showHideGrade.setText("");
                showHideGrade.setOnClickListener(new ShowOrHideMiniGradeClick(this));
                gradeList.clear();
                jPApplication = jpapplication;
                playView = new PlayView(jPApplication, this, songBytes.getBytes(), this, nandu, score, playKind, hand, "");
                break;
        }
        if (isRecord) {
            isRecord = recordReady(songsName);
            if (!isRecord) {
                Toast.makeText(this, "初始化录音失败，无法录音!请检查内存卡剩余容量!", Toast.LENGTH_SHORT).show();
            }
        }
        setContentView(playView);
        keyboardview = new KeyBoardView(this, playView);
        addContentView(keyboardview, layoutparams2);
        m3785a(playKind, false);
        isPlayingStart = true;
    }

    void m3802m() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        jpapplication.setHeightPixels(displayMetrics.heightPixels);
        jpapplication.setWidthPixels(displayMetrics.widthPixels);
        jpapplication.loadSettings(1);
        JPApplication.teardownAudioStreamNative();
        JPApplication.unloadWavAssetsNative();
        for (int i = 108; i >= 24; i--) {
            JPApplication.preloadSounds(i);
        }
        JPApplication.confirmLoadSounds();
    }

    public final void sendMsg(byte b, byte b2, String str, byte[] bArr) {
        if (connectionService != null) {
            connectionService.writeData(b, (byte) 0, b2, str, bArr);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    public final void mo2905a(HorizontalListView listView, List<Bundle> list) {
        MiniScoreAdapter c1209io = (MiniScoreAdapter) listView.getAdapter();
        List<Bundle> a = m3783a(list, "M");
        if (c1209io == null) {
            ListAdapter c1209io2 = new MiniScoreAdapter(a, layoutinflater, roomMode);
            listView.setAdapter(c1209io2);
            return;
        }
        c1209io.mo3332a(a);
        c1209io.notifyDataSetChanged();
    }

    public final void mo2906a(boolean hasTimer) {
        progressbar.setVisibility(View.GONE);
        Message obtainMessage = pianoPlayHandler.obtainMessage();
        if (hasTimer) {  //联网模式发321倒计时器
            obtainMessage.what = 7;
            Timer timer = new Timer();
            timer.schedule(new StartPlayTimer(this, obtainMessage, timer), 0, 1000);
        } else {  //本地模式直接开始
            obtainMessage.what = 7;
            obtainMessage.arg1 = 0;
            pianoPlayHandler.handleMessage(obtainMessage);
        }
        if (isRecord && !f4611ag) {
            try {
                audiorecord.startRecording();
                f4611ag = true;
                new Thread(() -> {
                    if (m3792e()) {
                        PianoPlay.m3786a(recordRawPath, recordWavPath);
                    }
                }).start();
                Toast.makeText(this, "开始录音...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "录音出错...请在系统应用设置内将极品钢琴的录音权限打开!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                f4611ag = false;
            }
        }
    }

    public final void mo2907b(ListView listView, List<Bundle> list) {
        String str = "SC";
        if (roomMode > 0) {
            str = "E";
        }
        listView.setAdapter(new FinishScoreAdapter(m3783a(list, str), layoutinflater, roomMode));
    }

    final void mo2908c() {
        if (f4611ag) {
            isRecord = false;
            Toast.makeText(this, "录音完毕，录音文件储存为SD卡\\Justpiano\\Record\\" + songsName + ".wav", Toast.LENGTH_SHORT).show();
            if (audiorecord != null && f4611ag) {
                f4611ag = false;
                audiorecord.stop();
            }
        }
        if (audiorecord != null) {
            audiorecord.release();
            audiorecord = null;
        }
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            finish();
            super.onActivityResult(i, i2, intent);
        }
    }

    @Override
    public void onBackPressed() {
        JPDialog jpdialog = new JPDialog(this);
        switch (playKind) {
            case 0:
                m3785a(playKind, true);
                if (!isShowingSongsInfo) {
                    playView.startFirstNoteTouching = false;
                    isShowingSongsInfo = true;
                    f4591J.setVisibility(View.VISIBLE);
                    return;
                } else {
                    isShowingSongsInfo = false;
                    f4591J.setVisibility(View.GONE);
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    f4619j = true;
                    finish();
                }
                return;
            case 1:
                m3785a(playKind, true);
                if (!isShowingSongsInfo) {
                    playView.startFirstNoteTouching = false;
                    isShowingSongsInfo = true;
                    f4591J.setVisibility(View.VISIBLE);
                } else {
                    isShowingSongsInfo = false;
                    f4591J.setVisibility(View.GONE);
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    f4619j = true;
                    finish();
                }
                return;
            case 2:
                jpdialog.setTitle("提示");
                jpdialog.setMessage("退出弹奏并返回大厅?");
                jpdialog.setFirstButton("确定", (dialog, which) -> {
                    isShowingSongsInfo = false;
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    f4619j = true;
                    dialog.dismiss();
                    finish();
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick());
                jpdialog.setCancelableFalse();
                jpdialog.showDialog();
                return;
            case 3:
                jpdialog.setTitle("提示");
                jpdialog.setMessage("退出考级并返回大厅?");
                jpdialog.setFirstButton("确定", (dialog, which) -> {
                    isShowingSongsInfo = false;
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    f4619j = true;
                    dialog.dismiss();
                    finish();
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick());
                jpdialog.setCancelableFalse();
                jpdialog.showDialog();
                return;
            case 4:
                jpdialog.setTitle("提示");
                jpdialog.setMessage("您将损失一次挑战机会!退出挑战并返回大厅?");
                jpdialog.setFirstButton("确定", (dialog, which) -> {
                    isShowingSongsInfo = false;
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    f4619j = true;
                    dialog.dismiss();
                    finish();
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick());
                jpdialog.setCancelableFalse();
                jpdialog.showDialog();
                return;
            default:
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityNum = 5;
        JPStack.push(this);
        jpapplication = (JPApplication) getApplication();
        checkAnJian();
        layoutinflater = LayoutInflater.from(this);
        isShowingSongsInfo = false;
        f4619j = false;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (jpapplication.getHeightPixels() == 0) {
            jpprogressbar = new JPProgressBar(this);
            new PianoPlayTask(this).execute();
        } else {
            m3794f();
        }
    }

    private void checkAnJian() {
        final PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo app : apps) {
            String name = app.activityInfo.packageName;
            if (name.contains("nknpngmlmnmhmpmh") || name.contains("mobileanjian") || name.contains("Touchelper")) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        JPStack.create();
        JPStack.pop(this);
        if (isRecord) {
            mo2908c();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bundle bundle = new Bundle();
        Intent intent;
        switch (playKind) {
            case 0:
            case 1:
                isShowingSongsInfo = false;
                if (playView != null) {
                    playView.startFirstNoteTouching = false;
                }
                isPlayingStart = false;
                if (!f4620k) {
                    finish();
                    return;
                }
                return;
            case 2:
                if (!f4620k) {
                    isShowingSongsInfo = false;
                    if (playView != null) {
                        playView.startFirstNoteTouching = false;
                    }
                    isPlayingStart = false;
                    f4619j = true;
                    sendMsg((byte) 8, (byte) 0, "", null);
                    if (!f3995a) {
                        intent = new Intent(this, OLPlayHall.class);
                        bundle.putString("hallName", hallBundle.getString("hallName"));
                        bundle.putByte("hallID", hallBundle.getByte("hallID"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        return;
                    }
                    return;
                }
                return;
            case 3:
                isShowingSongsInfo = false;
                if (playView != null) {
                    playView.startFirstNoteTouching = false;
                }
                isPlayingStart = false;
                f4619j = true;
                if (!f3995a) {
                    intent = new Intent(this, OLPlayHall.class);
                    bundle.putString("hallName", hallBundle.getString("hallName"));
                    bundle.putByte("hallID", hallBundle.getByte("hallID"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return;
                }
                return;
            case 4:
                isShowingSongsInfo = false;
                if (playView != null) {
                    playView.startFirstNoteTouching = false;
                }
                isPlayingStart = false;
                f4619j = true;
                if (!f3995a) {
                    intent = new Intent(this, OLChallenge.class);
                    bundle.putString("hallName", hallBundle.getString("hallName"));
                    bundle.putByte("hallID", hallBundle.getByte("hallID"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return;
                }
                return;
            default:
        }
    }

    @Override
    protected void onResume() {
        if (f4619j) {
            finish();
            f4619j = false;
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (f4619j) {
            f4619j = false;
            isPlayingStart = false;
            f4620k = false;
            finish();
        }
    }
}
