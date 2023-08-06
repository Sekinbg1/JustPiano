package ly.pp.justpiano3;

import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.*;

import java.io.IOException;
import java.lang.ref.WeakReference;

public final class UsersInfoTask2 extends AsyncTask<String, Void, String> {
    private final WeakReference<UsersInfo> usersInfo;

    UsersInfoTask2(UsersInfo usersInfo) {
        this.usersInfo = new WeakReference<>(usersInfo);
    }

    @Override
    protected void onPostExecute(String str) {
        switch (str) {
            case "0":
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "资料修改成功!", Toast.LENGTH_LONG).show();
                break;
            case "4":
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "密码中含有无法识别的特殊符号!", Toast.LENGTH_SHORT).show();
                break;
            case "5":
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "原密码有错!请再试一遍", Toast.LENGTH_SHORT).show();
                break;
            case "6":
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "密码修改成功!", Toast.LENGTH_LONG).show();
                Editor edit = JPApplication.accountListSharedPreferences.edit();
                if (usersInfo.get().rememberNewPassword) {
                    edit.putString("name", usersInfo.get().jpapplication.getAccountName());
                    edit.putString("password", "");
                    edit.putString("current_password", null);
                    edit.putBoolean("chec_psw", usersInfo.get().rememberNewPassword);
                    edit.putBoolean("chec_autologin", usersInfo.get().autoLogin);
                } else {
                    edit.putString("password", "");
                    edit.putString("current_password", null);
                    edit.putBoolean("chec_psw", usersInfo.get().rememberNewPassword);
                    edit.putBoolean("chec_autologin", usersInfo.get().rememberNewPassword);
                }
                edit.apply();
                outLineAndDialog();
                break;
            default:
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected String doInBackground(String... strArr) {
        String str = "";
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(usersInfo.get().jpapplication.getServer())
                .port(8910)
                .addPathSegment("JustPianoServer")
                .addPathSegment("server")
                .addPathSegment("ChangeUserMsg")
                .build();
        // 定义处理逻辑
        FormBodyProcessor processor = (builder) -> {
            if (strArr[1] == null || strArr[2] == null) {
                builder.add("msg", strArr[1] == null ? usersInfo.get().pSign : "")
                        .add("age", strArr[1] == null ? String.valueOf(usersInfo.get().age) : "");
            } else {
                builder.add("oldPass", strArr[1])
                        .add("newPass", strArr[2]);
            }
            return builder.build();
        };
        RequestBody body = processor.process(new FormBody.Builder()
                .add("head", "0")
                .add("version", usersInfo.get().jpapplication.getVersion())
                .add("keywords", strArr[0])
                .add("userName", usersInfo.get().jpapplication.getAccountName()));
        // 请求
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = OkHttpUtil.client().newCall(request).execute();
            if (response.isSuccessful()) {
                str = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    protected void onPreExecute() {
        usersInfo.get().jpprogressBar.setCancelable(true);
        usersInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        usersInfo.get().jpprogressBar.show();
    }

    @FunctionalInterface
    public interface FormBodyProcessor {
        RequestBody process(FormBody.Builder builder);
    }

    private void outLineAndDialog() {
        BaseActivity.returnMainMode(usersInfo.get());
    }
}
