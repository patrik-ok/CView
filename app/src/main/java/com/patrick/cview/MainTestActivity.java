package com.patrick.cview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.patrick.cview.dragrecyclerview.AbstractDragRecycleViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainTestActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private List<String> d = Arrays.asList(
            "A", "B", "C", "D", "E", "F", "G"
            , "H", "I", "J", "K", "L", "M", "N"
            , "O", "P", "Q", "R", "S", "T"
            , "U", "V", "W", "X", "Y", "Z");
    private RecyclerView mRecyclerView;
    private List<String> datas;
    private EditText edAdd;
    private AbstractDragRecycleViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_recyclerview);
        initData();
        mRecyclerView = findViewById(R.id.rv);
        mAdapter = new AbstractDragRecycleViewAdapter<String, Vh>(this, mRecyclerView, datas) {

            @Override
            public Vh onCreateMyViewHolder(ViewGroup parent, int viewType) {
                return new Vh(LayoutInflater.from(MainTestActivity.this).inflate(R.layout.drag_recyclerview_item, null));
            }

            @Override
            public void onBindMyViewHolder(Vh holder, int position) {
                holder.tv.setText(datas.get(position).toString());
                holder.iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainTestActivity.this, "哈哈哈", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        edAdd = findViewById(R.id.et_add);
        findViewById(R.id.tv).setOnClickListener(this);
        findViewById(R.id.tv_add).setOnClickListener(this);
    }

    public static class Vh extends RecyclerView.ViewHolder {

        public Vh(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            iv = itemView.findViewById(R.id.iv_delete);
        }

        public TextView tv;
        public ImageView iv;
    }

    private void initData() {
        datas = new ArrayList<>();
//        直接用d操作集合会崩溃，Arrays.asList集合不可增删改；详细可以看我的博客
        for (int i = 0; i < d.size(); i++) {
            datas.add(d.get(i));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv:
                for (int i = 0; i < datas.size(); i++) {
                    Log.i(TAG, "onClick: ____" + datas.get(i));
                }
                break;
            case R.id.tv_add:
                mAdapter.add(edAdd.getText().toString().trim());
                edAdd.setText(null);
                break;
        }
    }


}
