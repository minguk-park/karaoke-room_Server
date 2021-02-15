package com.example.loginserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String strNickname,strProfile;
    private TextView tvData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvData = (TextView)findViewById(R.id.textView);
        Button btn = (Button)findViewById(R.id.testBtn);
        Button login = (Button)findViewById(R.id.logBtn);
        Button log = (Button)findViewById(R.id.log);

        Intent intent = getIntent();
        strNickname = intent.getStringExtra("name");
        strProfile = intent.getStringExtra("profile");

        //버튼이 눌리면 아래 리스너가 수행된다.
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아래 링크를 파라미터를 넘겨준다는 의미.
                new JSONTask().execute("http://192.168.25.37/users");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsonSend().execute("http://192.168.25.37/register");
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsonLogin().execute("http://192.168.25.37/login");
            }
        });
    }
    public class JsonLogin extends AsyncTask<String,String,String> {
        EditText editId = (EditText)findViewById(R.id.logId);
        EditText editPwd= (EditText)findViewById(R.id.logpwd);
        @Override
        protected String doInBackground(String... strings) {
            JSONObject ob = new JSONObject();
            String id = editId.getText().toString();
            String pwd = editPwd.getText().toString();
            HttpURLConnection con = null;
            BufferedReader reader = null;
            try{
                ob.accumulate("id",id);
                ob.accumulate("pwd",pwd);
                try{
                    URL url = new URL("http://192.168.25.37/login");
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
            if(result == "login success"){

            }
            tvData.setText(result);
        }
    }

    public class JsonSend extends AsyncTask<String,String,String>{
        EditText editId = (EditText)findViewById(R.id.Id);
        EditText editPwd= (EditText)findViewById(R.id.pwd);
        @Override
        protected String doInBackground(String... strings) {
            JSONObject ob = new JSONObject();
            String id = editId.getText().toString();
            String pwd = editPwd.getText().toString();
            HttpURLConnection con = null;
            BufferedReader reader = null;
            try{
                ob.accumulate("id",id);
                ob.accumulate("pwd",pwd);
                try{
                    URL url = new URL("http://192.168.25.37/register");
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
            tvData.setText(result);
        }
    }

    public class JSONTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("user_id", "androidTest");
                jsonObject.accumulate("name", "yun");

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL("http://192.168.25.37/users");
                    //URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();//연결 수행

                    //입력 스트림 생성
                    InputStream stream = con.getInputStream();

                    //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuffer buffer = new StringBuffer();

                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";

                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
                    return buffer.toString();

                    //아래는 예외처리 부분이다.
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
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
                }//finally 부분
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tvData.setText(result);
        }
    }
}