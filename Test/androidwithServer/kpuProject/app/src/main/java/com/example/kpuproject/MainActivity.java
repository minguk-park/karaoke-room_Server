package com.example.kpuproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kakao.network.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

    public class MainActivity extends AppCompatActivity {
    String strNickname,strProfile,strEmail,strAgeRange,strGender,strBirthday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvNickname = findViewById(R.id.tvNickname);
        ImageView ivProfile = findViewById(R.id.ivProfile); // ImageView 선언.
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnSignout = findViewById(R.id.btnSignout);

        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvAgeRange = findViewById(R.id.tvAgeRange);
        TextView tvGender = findViewById(R.id.tvGender);
        TextView tvBirthday = findViewById(R.id.tvBirthday);

        Intent intent = getIntent();
        strNickname = intent.getStringExtra("name");
        strProfile = intent.getStringExtra("profile");
        strEmail = intent.getStringExtra("email");
        strAgeRange = intent.getStringExtra("ageRange");
        strGender = intent.getStringExtra("gender");
        strBirthday = intent.getStringExtra("birthday");

        tvNickname.setText(strNickname);
        Glide.with(this).load(strProfile).into(ivProfile);

        tvNickname.setText(strNickname);
        tvEmail.setText(strEmail);
        tvAgeRange.setText(strAgeRange);
        tvGender.setText(strGender);
        tvBirthday.setText(strBirthday);

        btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() { //로그아웃 함수 호출
                    @Override
                    public void onCompleteLogout() { // 로그아웃이 성공적으로 됬을 때 실행
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });
        btnSignout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("탈퇴하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        int result = errorResult.getErrorCode();

                                        if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                            Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "회원탈퇴에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) { //세션이 닫혔을 때 동작
                                        Toast.makeText(getApplicationContext(), "로그인 세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onNotSignedUp() { //가입되지 않는 회원이 탈퇴를 요구할 때 동작
                                        Toast.makeText(getApplicationContext(), "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onSuccess(Long result) {
                                        Toast.makeText(getApplicationContext(), "회원탈퇴에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        new expireInfo().execute(strEmail);
                                        startActivity(intent);

                                        finish();
                                    }
                                });

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }
        public class expireInfo extends AsyncTask<String,String,String> {
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
                    ob.accumulate("email",arr[0]);
                    try{
                        URL url = new URL("http://192.168.25.37/expire");
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
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        }
}
