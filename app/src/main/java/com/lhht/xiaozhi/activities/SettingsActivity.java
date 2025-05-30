package com.lhht.xiaozhi.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.lhht.xiaozhi.R;
import com.lhht.xiaozhi.settings.SettingsManager;
import com.lhht.xiaozhi.adapters.WsUrlAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    private SettingsManager settingsManager;
    private TextInputEditText tokenInput;
    private SwitchMaterial enableTokenSwitch;
    private RecyclerView wsUrlList;
    private MaterialButton addWsUrlButton;
    private WsUrlAdapter wsUrlAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        settingsManager = new SettingsManager(this);
        
        tokenInput = findViewById(R.id.tokenInput);
        enableTokenSwitch = findViewById(R.id.enableTokenSwitch);
        ExtendedFloatingActionButton saveButton = findViewById(R.id.saveButton);
        wsUrlList = findViewById(R.id.wsUrlList);
        addWsUrlButton = findViewById(R.id.addWsUrlButton);

        // 加载当前设置
        tokenInput.setText(settingsManager.getToken());
        enableTokenSwitch.setChecked(settingsManager.isTokenEnabled());

        // 加载WebSocket地址列表
        Set<String> wsUrls = settingsManager.getWsUrls();
        if (wsUrls == null) {
            wsUrls = new HashSet<>();
            wsUrls.add(settingsManager.getWsUrl()); // 添加当前的URL
        }
        setupWsUrlList(new ArrayList<>(wsUrls), settingsManager.getWsUrl());

        // 根据Token开关状态更新Token输入框状态
        updateTokenInputState();
        enableTokenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateTokenInputState());

        // 添加新的WebSocket地址
        addWsUrlButton.setOnClickListener(v -> wsUrlAdapter.addUrl(""));

        // 保存设置
        saveButton.setOnClickListener(v -> {
            String token = tokenInput.getText().toString();
            boolean enableToken = enableTokenSwitch.isChecked();

            // 获取当前所有WebSocket地址
            ArrayList<String> currentUrls = wsUrlAdapter.getUrls();
            String selectedWsUrl = wsUrlAdapter.getSelectedUrl();

            // 保存设置
            if (!selectedWsUrl.isEmpty()) {
                settingsManager.saveSettings(selectedWsUrl, token, enableToken);
            }
            settingsManager.saveWsUrls(new HashSet<>(currentUrls));
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupWsUrlList(ArrayList<String> urls, String currentUrl) {
        wsUrlList.setLayoutManager(new LinearLayoutManager(this));
        wsUrlAdapter = new WsUrlAdapter(urls, currentUrl, url -> wsUrlAdapter.removeUrl(url));
        wsUrlList.setAdapter(wsUrlAdapter);
    }

    private void updateTokenInputState() {
        tokenInput.setEnabled(enableTokenSwitch.isChecked());
    }
} 