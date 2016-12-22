package com.example.hyg.amap2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;

import java.util.List;

/**
 * Created by hyg on 2016/12/20.
 */

public class Location2Activity extends Activity {
    private EditText et_content;
    private Button bt_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        et_content = (EditText)findViewById(R.id.et_content);
        bt_search = (Button)findViewById(R.id.bt_search);
        control_init();


    }

    private void control_init() {

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("address", et_content.getText().toString());
                setResult(2,intent);
                finish();
            }
        });




        //在搜索过程中，对文字的适配等，和百度时一样
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
