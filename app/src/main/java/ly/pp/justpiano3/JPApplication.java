package ly.pp.justpiano3;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.SparseIntArray;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

public final class JPApplication extends Application {
    public static SoundPool mp;
    public static SoundPool sp;
    public static String kitiName = "";
    public long serverTimeInterval;
    static SharedPreferences sharedpreferences;
    private static SparseIntArray soundsIdMap = new SparseIntArray();
    private static Context context;
    public int mpIndex;
    boolean isMusicPause;
    String title = "";
    String f4072f = "";
    String f4073g = "";
    String f4074h = "";
    String f4075i;
    String f4076j;
    boolean changeNotesColor = true;
    private ConnectionService connectionService;
    private boolean isMusicPlay;
    private int whiteKeyHeight;
    private float f4034C;
    private float blackKeyWidth;
    private float chordVolume = 1f;
    private int notesDownSpeed = 6;
    private boolean isBindService;
    private int gameMode;
    private int badu;
    private boolean isOpenChord = true;
    private boolean showTouchNotesLevel = true;
    private boolean autoPlay = true;
    private boolean showLine = true;
    private boolean loadLongKeyboard;
    private boolean isShowDialog;
    private boolean compatibleMode = true;
    private boolean noteDismiss = true;
    private int playSongsMode;
    private float noteSize = 1;
    private Map<Byte, User> hashMap = new HashMap<>();
    private String accountName = "";
    private String password = "";
    private String nowSongsName = "";
    private String server = "111.67.204.158";
    private boolean keyboardPerfer;
    private ServiceConnection serviceconnection = new JPServiceConnection(this);
    private int widthPixels;
    private int heightPixels;
    private int animPosition;
    private int animFrame = 8;
    private float tempSpeed = 1;
    private float widthDiv8;
    private float halfHeightSub20;
    private float halfHeightSub10;
    private float whiteKeyHeightAdd90;
    private int roughLine;

    public static void createSounds() {
        if (sp != null) {
            sp.release();
            sp = null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            SoundPool.Builder SoundPoolbuild = new SoundPool.Builder();
            SoundPoolbuild.setMaxStreams(6);
            SoundPoolbuild.setAudioAttributes(attrBuilder.build());
            sp = SoundPoolbuild.build();
        } else {
            sp = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public static void preloadSounds(int i) {
        try {
            soundsIdMap.put(i, sp.load(context.getFilesDir().getAbsolutePath() + "/Sounds/" + i + ".ogg", 1));
        } catch (Exception e1) {
            try {
                soundsIdMap.put(i, sp.load(context.getResources().getAssets().openFd("sound/" + i + ".ogg"), 1));
                copySound("sound/" + i + ".ogg", context.getFilesDir().getAbsolutePath() + "/Sounds/" + i + ".ogg");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void copySound(String assetName, String targetName) throws IOException {
        File targetFile;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            AssetManager assets = context.getAssets();
            targetFile = new File(targetName);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            inputStream = assets.open(assetName);
            outputStream = new FileOutputStream(targetFile, false);
            copy(inputStream, outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[2048];
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
        }
    }

    public static void reLoadOriginalSounds() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("sound_list", "original");
        edit.apply();
        for (int i = 108; i >= 24; i--) {
            try {
                soundsIdMap.put(i, sp.load(context.getResources().getAssets().openFd("sound/" + i + ".ogg"), 1));
                copySound("sound/" + i + ".ogg", context.getFilesDir().getAbsolutePath() + "/Sounds/" + i + ".ogg");
            } catch (IOException ignored) {
            }
        }
    }

    public static void confirmLoadSounds() {
        try {
            copySound("sound/110.ogg", context.getFilesDir().getAbsolutePath() + "/Sounds/110.ogg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (soundsIdMap.get(24) != 0) {
            return;
        }
        reLoadOriginalSounds();
    }

    public void m3520a(Canvas canvas, Rect rect, Rect rect2, PlayView playView, int i) {
        switch (i) {
            case 0:
            case 12:
            case 5:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyRightImage, null, rect2, null);
                return;
            case 1:
            case 10:
            case 8:
            case 6:
            case 3:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.blackKeyImage, null, rect2, null);
                return;
            case 2:
            case 9:
            case 7:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyMiddleImage, null, rect2, null);
                return;
            case 4:
            case 11:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyLeftImage, null, rect2, null);
                return;
            default:
        }
    }

    public final int getGameMode() {
        return gameMode;
    }

    public final void setGameMode(int i) {
        gameMode = i;  //0为正常游戏模式 1为自由模式 2为练习模式 3为欣赏模式
    }

    public final List<Rect> getKeyRectArray() {
        List<Rect> arrayList = new ArrayList<>();
        arrayList.add(new Rect(0, whiteKeyHeight, (int) widthDiv8, heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 + blackKeyWidth), (int) (whiteKeyHeight + f4034C + 5)));
        arrayList.add(new Rect((int) widthDiv8, whiteKeyHeight, (int) (widthDiv8 * 2), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 2 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 2 + blackKeyWidth), (int) (whiteKeyHeight + f4034C + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 2), whiteKeyHeight, (int) (widthDiv8 * 3), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 3), whiteKeyHeight, (int) (widthDiv8 * 4), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 4 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 4 + blackKeyWidth), (int) (whiteKeyHeight + f4034C + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 4), whiteKeyHeight, (int) (widthDiv8 * 5), heightPixels));
        arrayList.add(new Rect((int) ((widthDiv8 * 5) - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 5 + blackKeyWidth), (int) (whiteKeyHeight + f4034C + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 5), whiteKeyHeight, (int) (widthDiv8 * 6), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 6 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 6 + blackKeyWidth), (int) (whiteKeyHeight + f4034C + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 6), whiteKeyHeight, (int) (widthDiv8 * 7), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 7), whiteKeyHeight, (int) (widthDiv8 * 8), heightPixels));
        return arrayList;
    }

    public final int getHeightPixels() {
        return heightPixels;
    }

    public final void setHeightPixels(int i) {
        heightPixels = i;
    }

    public final int getWidthPixels() {
        return widthPixels;
    }

    public final void setWidthPixels(int i) {
        widthPixels = i;
    }

    public final boolean hasKeyboardPerfer() {
        return keyboardPerfer;
    }

    public final String getVersion() {
        String str = "4.3";
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return str;
        }
    }

    public final ServiceConnection mo2696L() {
        return serviceconnection;
    }

    public final ConnectionService getConnectionService() {
        return connectionService;
    }

    public final void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public final boolean getIsBindService() {
        return isBindService;
    }

    public final void setIsBindService(boolean z) {
        isBindService = z;
    }

    public final float getNoteSize() {
        return noteSize;
    }

    public final Map<Byte, User> getHashmap() {
        return hashMap;
    }

    public final String getAccountName() {
        if (accountName.isEmpty()) {
            accountName = sharedpreferences.getString("name", "");
        }
        return accountName;
    }

    public final void setAccountName(String str) {
        accountName = str;
    }

    public final String getPassword() {
        if (password.isEmpty()) {
            password = sharedpreferences.getString("password", "");
        }
        return password;
    }

    public final void setPassword(String str) {
        password = str;
    }

    public final String getNowSongsName() {
        return nowSongsName;
    }

    public final void setNowSongsName(String str) {
        nowSongsName = str;
    }

    public final String getKitiName() {
        if (kitiName.isEmpty()) {
            kitiName = sharedpreferences.getString("userKitiName", "");
        }
        return kitiName;
    }

    public final void setKitiName(String str) {
        kitiName = str;
    }

    public final List<Rect> getFireRectArray(PlayView playView) {
        List<Rect> arrayList = new ArrayList<>();
        arrayList.add(new Rect(0, (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) widthDiv8, (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) widthDiv8, (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 2), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 2 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 2 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 2), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 3), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 3), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 4), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 4 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 4 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 4), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 5), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 5 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 5 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 5), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 6), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 6 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 6 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 6), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 7), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 7), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 8), (int) halfHeightSub20));
        return arrayList;
    }

    public final void drawFire(PlayView playView, Canvas canvas, int i) {
        switch (i) {
            case 0:
                canvas.drawBitmap(playView.fireImage, null, new RectF(0, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8, halfHeightSub20), null);
                return;
            case 1:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 + blackKeyWidth, halfHeightSub20), null);
                return;
            case 2:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 2, halfHeightSub20), null);
                return;
            case 3:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 * 2 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), (widthDiv8 * 2 + blackKeyWidth), halfHeightSub20), null);
                return;
            case 4:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 2, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 3, halfHeightSub20), null);
                return;
            case 5:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 3, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 4, halfHeightSub20), null);
                return;
            case 6:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 * 4 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), (widthDiv8 * 4 + blackKeyWidth), halfHeightSub20), null);
                return;
            case 7:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 4, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 5, halfHeightSub20), null);
                return;
            case 8:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 * 5 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), (widthDiv8 * 5 + blackKeyWidth), halfHeightSub20), null);
                return;
            case 9:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 5, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 6, halfHeightSub20), null);
                return;
            case 10:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 * 6 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 6 + blackKeyWidth, halfHeightSub20), null);
                return;
            case 11:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 6, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 7, halfHeightSub20), null);
                return;
            case 12:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 7, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 8, halfHeightSub20), null);
                return;
            default:
        }
    }

    public final void setBackGround(Context context, String str, ViewGroup viewGroup) {
        System.gc();
        Bitmap bitmap = null;
        if (!PreferenceManager.getDefaultSharedPreferences(context).getString("skin_list", "original").equals("original")) {
            try {
                bitmap = BitmapFactory.decodeFile(context.getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".jpg");
            } catch (Exception ignored) {
            }
            if (bitmap == null) {
                try {
                    bitmap = BitmapFactory.decodeFile(context.getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".png");
                } catch (Exception ignored) {
                }
            }
            if (bitmap != null && viewGroup != null) {
                viewGroup.setBackground(new BitmapDrawable(getResources(), bitmap));
            }
        }
    }

    public final Bitmap loadImage(String str) {
        Bitmap bitmap = null;
        if (!PreferenceManager.getDefaultSharedPreferences(this).getString("skin_list", "original").equals("original")) {
            try {
                bitmap = BitmapFactory.decodeFile(getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".png");
            } catch (Exception e) {
                try {
                    return BitmapFactory.decodeStream(getResources().getAssets().open("drawable/" + str + ".png"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (bitmap != null) {
            return bitmap;
        }
        try {
            return BitmapFactory.decodeStream(getResources().getAssets().open("drawable/" + str + ".png"));
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public final int getAnimPosition() {
        return animPosition;
    }

    public final void setAnimPosition(int f) {
        animPosition = f;
    }

    public final void downNote() {
        animPosition += animFrame;
    }

    public final void mo2724e(float f) {
        f4034C = f;
    }

    public void setServerTimeInterval(long serverTime) {
        this.serverTimeInterval = serverTime - System.currentTimeMillis();
    }

    public long getServerTime() {
        return serverTimeInterval + System.currentTimeMillis();
    }

    public final void loadSettings(int i) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (i) {
            case 0:
                autoPlay = sharedPreferences.getBoolean("auto_play", true);
                tempSpeed = Float.parseFloat(sharedPreferences.getString("temp_speed", "1.0"));
                break;
            case 1:
                tempSpeed = 1;
                autoPlay = true;
                break;
        }
        isOpenChord = sharedPreferences.getBoolean("sound_check_box", true);
        chordVolume = Float.parseFloat(sharedPreferences.getString("b_s_vol", "1.0"));
        animFrame = Integer.parseInt(sharedPreferences.getString("anim_frame", "8"));
        keyboardPerfer = sharedPreferences.getBoolean("keyboard_perfer", true);
        showTouchNotesLevel = sharedPreferences.getBoolean("tishi_cj", true);
        showLine = sharedPreferences.getBoolean("show_line", true);
        compatibleMode = sharedPreferences.getBoolean("compatible_mode", true);
        loadLongKeyboard = sharedPreferences.getBoolean("open_long_key", false);
        roughLine = Integer.parseInt(sharedPreferences.getString("rough_line", "1"));
        badu = Integer.parseInt(sharedPreferences.getString("badu", "0"));
        notesDownSpeed = Integer.parseInt(sharedPreferences.getString("down_speed", "6"));
        noteSize = Float.parseFloat(sharedPreferences.getString("note_size", "1"));
        noteDismiss = sharedPreferences.getBoolean("note_dismiss", true);
        changeNotesColor = sharedPreferences.getBoolean("change_color", true);
    }

    public final void setTempSpeed() {
        tempSpeed = 1;
    }

    public final float getTempSpeed() {
        return tempSpeed;
    }

    public final boolean getNoteDismiss(){
        return noteDismiss;
    }

    public final int getDownSpeed() {
        return notesDownSpeed;
    }

    public final void setDownSpeed(int speed) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("down_speed", String.valueOf(speed)).apply();
        notesDownSpeed = speed;
    }

    public static void initSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("down_speed", "6");
        editor.putString("anim_frame", "8");
        editor.putString("note_size", "1");
        editor.putString("b_s_vol", "1.0");
        editor.putString("temp_speed", "1.0");
        editor.apply();
    }

    public final boolean getIfLoadlongKeyboard() {
        return loadLongKeyboard;
    }

    public final int getRoughLine() {
        return roughLine;
    }

    public final boolean getIsShowLine() {
        return showLine;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        context = getApplicationContext();
        soundsIdMap.put(0, 0);
        soundsIdMap.put(1, 0);
        soundsIdMap.put(2, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            SoundPool.Builder SoundPoolBuild = new SoundPool.Builder();
            SoundPoolBuild.setMaxStreams(3);
            SoundPoolBuild.setAudioAttributes(attrBuilder.build());
            mp = SoundPoolBuild.build();
        } else {
            mp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }
        sharedpreferences = getSharedPreferences(getSharedPreferences("account_list", 0).getString("accountList", "account"), 0);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        context = null;
    }

    public int getAnimFrame() {
        return animFrame;
    }

    public final boolean getIfShowNotesLevel() {
        return showTouchNotesLevel;
    }

    public final int getBadu() {
        return badu;
    }

    public final int getPlaySongsMode() {
        return playSongsMode;
    }

    public final void setPlaySongsMode(int n) {
        playSongsMode = n;
    }

    public final boolean getCompatibleMode() {
        return compatibleMode;
    }

    public final void setCompatibleMode(boolean mode) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean("compatible_mode", mode).apply();
        compatibleMode = mode;
    }

    public final boolean getAutoPlay() {
        return autoPlay;
    }

    public final float getChordVolume() {
        return chordVolume;
    }

    public final boolean getOpenChord() {
        return isOpenChord;
    }

    public final float getWidthDiv8() {
        return widthDiv8;
    }

    public final void setWidthDiv8(float f) {
        widthDiv8 = f;
    }

    public final float getHalfHeightSub20() {
        return halfHeightSub20;
    }

    public final void setHalfHeightSub20(float f) {
        halfHeightSub20 = f;
    }

    public final int getWhiteKeyHeight() {
        return whiteKeyHeight;
    }

    public final void setWhiteKeyHeight(int f) {
        whiteKeyHeight = f;
    }

    public final float mo2749x() {
        return f4034C;
    }

    public final float getBlackKeyWidth() {
        return blackKeyWidth;
    }

    public final void setBlackKeyWidth(float f) {
        blackKeyWidth = f;
    }

    public final float getHalfHeightSub10() {
        return halfHeightSub10;
    }

    public final void setHalfHeightSub10(float f) {
        halfHeightSub10 = f;
    }

    public final float getWhiteKeyHeightAdd90() {
        return whiteKeyHeightAdd90;
    }

    public final void setWhiteKeyHeightAdd90(float f) {
        whiteKeyHeightAdd90 = f;
    }

    public final String getServer() {
        return server;
    }

    public void setServer(String ser) {
        server = ser;
    }

    public final boolean getIsShowDialog() {
        return isShowDialog;
    }

    public final void setIsShowDialog(boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
    }

    public final int playSound(int note, float volume) {
        float f2 = volume > 1 ? (chordVolume * volume) / 100 : volume;
        int i2 = soundsIdMap.get(note);
        return i2 == 0 ? 0 : sp.play(i2, f2, f2, 1, 0, 1);
    }

    public final void stopSongs(int i) {
        sp.stop(i);
    }

    public void startMusic() {
        isMusicPause = false;
        isMusicPlay = false;
    }

    public void stopMusic() {
        mp.stop(soundsIdMap.get((mpIndex + 2) % 3));
        isMusicPause = false;
        isMusicPlay = false;
    }

    public boolean isPlayingMusic() {
        return isMusicPlay;
    }

    public void playMusic() {
        soundsIdMap.put(mpIndex, mp.play(soundsIdMap.get(mpIndex), 1, 1, 5, 0, 1));
        isMusicPause = false;
        isMusicPlay = true;
        mpIndex = (mpIndex + 1) % 3;
    }

    public void pauseMusic() {
        mp.pause(soundsIdMap.get((mpIndex + 2) % 3));
        isMusicPause = true;
        isMusicPlay = false;
    }

    public void resumeMusic() {
        mp.resume(soundsIdMap.get((mpIndex + 2) % 3));
        isMusicPause = false;
        isMusicPlay = true;
    }

    public String[] makeFFmpegCmd(int[] tickArray, byte[] trackArray, byte[] noteArray, byte[] volumeArray, int offset, int end, int mpIndex) {
        if (offset < end) {
            int length = end - offset;
            List<String> _commands = new ArrayList<>();
            for (int i = offset; i < end; i++) {
                _commands.add("-i");
                if ((trackArray != null && trackArray[i] == 85 && gameMode != 3) || noteArray[i] < 24 || noteArray[i] > 108) {
                    _commands.add(getFilesDir().getAbsolutePath() + "/Sounds/110.ogg");
                } else {
                    _commands.add(getFilesDir().getAbsolutePath() + "/Sounds/" + noteArray[i] + ".ogg");
                }
            }
            _commands.add("-filter_complex");
            StringBuilder delayCmd = new StringBuilder();
            if (trackArray == null) {
                for (int i = offset; i < end; i++) {
                    delayCmd.append('[').append(i - offset).append("]adelay=").append(tickArray[i]).append(",volume=")
                            .append(volumeArray[i] / 188f * length).append("[d").append(i).append("];");
                }
            } else {
                for (int i = offset; i < end; i++) {
                    delayCmd.append('[').append(i - offset).append("]adelay=").append(tickArray[i] * 1.025 / tempSpeed).append(",volume=")
                            .append(volumeArray[i] / 188f * length * chordVolume).append("[d").append(i).append("];");
                }
            }
            for (int i = offset; i < end; i++) {
                delayCmd.append("[d").append(i).append(']');
            }
            delayCmd.append("amix=inputs=").append(length).append(":duration=longest");
            _commands.add(delayCmd.toString());
            _commands.add("-ac");
            _commands.add("2");
            _commands.add("-ar");
            _commands.add("22050");
            _commands.add("-y");
            _commands.add(getFilesDir().getAbsolutePath() + "/temp" + mpIndex + ".wav");
            String[] commands = new String[_commands.size()];
            for (int i = 0; i < _commands.size(); i++) {
                commands[i] = _commands.get(i);
            }
            return commands;
        }
        return null;
    }

    public void ffmpegSongsTask(String[] command, final Runnable finishTask, int mpCount) {
        if (command != null) {
            FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    try {
                        mp.setOnLoadCompleteListener(null);
                        soundsIdMap.put(mpCount, mp.load(context.getFilesDir().getAbsolutePath() + "/temp" + mpCount + ".wav", 5));
                        if (finishTask != null) {
                            mp.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
                                soundsIdMap.put(mpCount, soundPool.play(sampleId, 1, 1, 5, 0, 1));
                                isMusicPlay = true;
                            });
                            mpIndex = (mpCount + 1) % 3;
                        }
                        ThreadPoolUtils.execute(finishTask);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void ffmpegPlayTask(String[] command, int mpCount) {
        if (command != null) {
            FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    try {
                        mp.setOnLoadCompleteListener(null);
                        soundsIdMap.put(mpCount, mp.load(context.getFilesDir().getAbsolutePath() + "/temp" + mpCount + ".wav", 5));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private static class CrashHandler implements Thread.UncaughtExceptionHandler {

        void init() {
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            final String errorLog = baos.toString();
            ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);  //瀹炰緥鍖栧壀鍒囨澘鏈嶅姟
            ClipData myClip = ClipData.newPlainText("errorLog", errorLog);
            myClipboard.setPrimaryClip(myClip);
            String str = errorLog.substring(0, 26);
            if (str.equals("java.lang.OutOfMemoryError")) {
                new Thread() {

                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(context, "内存溢出", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }.start();
            } else {
                new Thread() {

                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(context, "很抱歉，极品钢琴出现异常，错误信息已自动复制，可联系开发者粘贴错误信息!", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }.start();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(-1);
        }
    }
}
