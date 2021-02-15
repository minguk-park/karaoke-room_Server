package com.example.kpuproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback sessionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        //Session.getCurrentSession().checkAndImplicitOpen();//바로 로그인을 기능
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(), "세션이 닫혔습니다. 다시 시도해 주세요: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("name", result.getNickname());
                    intent.putExtra("profile", result.getProfileImagePath());

                    if(result.getKakaoAccount().hasEmail() == OptionalBoolean.TRUE)
                        intent.putExtra("email", result.getKakaoAccount().getEmail());
                    else
                        intent.putExtra("email", "none");
                    if(result.getKakaoAccount().hasAgeRange() == OptionalBoolean.TRUE)
                        intent.putExtra("ageRange", result.getKakaoAccount().getAgeRange().getValue());
                    else
                        intent.putExtra("ageRange", "none");
                    if(result.getKakaoAccount().hasGender() == OptionalBoolean.TRUE)
                        intent.putExtra("gender", result.getKakaoAccount().getGender().getValue());
                    else
                        intent.putExtra("gender", "none");
                    if(result.getKakaoAccount().hasBirthday() == OptionalBoolean.TRUE)
                        intent.putExtra("birthday", result.getKakaoAccount().getBirthday());
                    else
                        intent.putExtra("birthday", "none");

                    new JsonLogin().execute(result.getNickname(),result.getKakaoAccount().getEmail());
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        public class JsonLogin extends AsyncTask<String,String,String> {
            @Override
            protected String doInBackground(String... strings) {
                String arr[] = new String[strings.length];
                for(int i =0;i<strings.length;i++){
                    arr[i]=strings[i];
                }
                JSONObject ob = new JSONObject();
                HttpURLConnection con = null;
                BufferedReader reader = null;
                try{
                    ob.accumulate("name",arr[0]);
                    ob.accumulate("email",arr[1]);
                    try{
                        URL url = new URL("http://192.168.25.37/kakao");
                        //URL url = new URL(urls[0]);
                        con=(HttpURLConnection)url.openConnection();
                        con.setRequestMethod("POST"); //post 방식
                        con.setRequestProperty("Cache-Control","no-cache"); //캐시 설정
                        con.setRequestProperty("Content-Type","application/json"); // json형태로 전송
                        con.setRequestProperty("Accept","text/html");//서버에 response 데이터를 html로 받음
                        con.setDoOutput(true);//OutStream으로 post데이터를 넘겨주겠다는 의미
                        con.setDoInput(true);//InputStream으로 서버의 응답을 받겠다는 의미
                        con.connect();

                        OutputStream outStream = con.getOutputStream();//스트림 생성
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                        writer.write(ob.toString());
                        writer.flush();
                        writer.close();

                        InputStream stream = con.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(stream));
                        StringBuffer buffer = new StringBuffer();

                        String line="";
                        while((line = reader.readLine())!=null){
                            buffer.append(line);
                        }
                        return buffer.toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
            //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            }
        }
    }
}