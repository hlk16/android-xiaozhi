package com.lhht.xiaozhi.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lhht.xiaozhi.R;

public class drawFragment extends Fragment {

    //这两个常量用于标识传递给Fragment的参数
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView mview;
    private Button tableRobot;
    private Button nbIot;
    private Button morePluge;



    private String mParam1;
    private String mParam2;
    //在创建Fragment实例时，系统会调用这个构造函数。
    // 注释表明这个构造函数是空的，因为Fragment的初始化工作通常在onCreate或onCreateView方法中进行。
    public drawFragment() {
        // Required empty public constructor
    }
    //这个方法用于创建Fragment实例，并传递参数给它。
    public static drawFragment newInstance(String param1, String param2) {
        drawFragment fragment = new drawFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    //先调用父类的onCreate方法，然后检查getArguments()是否为非空。
    // 如果不为空，就从Bundle中获取参数值，并赋给成员变量mParam1和mParam2。
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_draw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mview = view.findViewById(R.id.textView);
        tableRobot = view.findViewById(R.id.tableRobot);
        nbIot = view.findViewById(R.id.nbIot);
        morePluge = view.findViewById(R.id.morePluge);
        tableRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BluetoothActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}