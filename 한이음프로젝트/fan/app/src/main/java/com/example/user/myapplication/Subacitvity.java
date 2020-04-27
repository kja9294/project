package com.example.user.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.TextView;

public class Subacitvity extends AppCompatActivity{
    String sub=" ";
    @Override
    protected  void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_sub);
        String value= getPreferences();
        final TextView model=(TextView)findViewById(R.id.model);
        model.setText(value);
        Intent intent = getIntent();
        Button submit=(Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText test=(EditText) findViewById(R.id.test);
                sub=test.getText().toString();
                savePreferences(sub);
                model.setText(sub);
                Intent data=new Intent();
                data.putExtra("주제",sub);
                setResult(0,data);
                finish();
            }
        });

        Intent data=new Intent();
        data.putExtra("주제",value);
        setResult(0,data);
    }
    private String getPreferences(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        return pref.getString("hi", " ");

    }



    // 값 저장하기

    private void savePreferences(String sub){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putString("hi", sub);

        editor.commit();

    }



    // 값(Key Data) 삭제하기

    private void removePreferences(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.remove("hi");

        editor.commit();

    }



    // 값(ALL Data) 삭제하기

    private void removeAllPreferences(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.clear();

        editor.commit();

    }
}
