package com.autotyperpro;

import android.content.*;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.input_text);
        startBtn = findViewById(R.id.start_btn);

        startBtn.setOnClickListener(v -> startTyping());
    }

    private void startTyping() {
        String text = inputText.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "请输入要输入的内容！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isAccessibilityServiceEnabled()) {
            showAccessibilityDialog();
            return;
        }

        new AlertDialog.Builder(this)
            .setTitle("准备输入")
            .setMessage("请在 5 秒内点击目标输入框！\n点击确定后开始倒计时...")
            .setPositiveButton("确定", (d, w) -> {
                Intent intent = new Intent("com.autotyperpro.ACTION_TYPE");
                intent.putExtra("text", text);
                sendBroadcast(intent);
                Toast.makeText(this, "5秒后自动填入...", Toast.LENGTH_LONG).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private boolean isAccessibilityServiceEnabled() {
        String service = getPackageName() + "/" + AutoTypeService.class.getCanonicalName();
        try {
            int accessibilityEnabled = Settings.Secure.getInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            if (accessibilityEnabled == 1) {
                String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (settingValue != null) {
                    return settingValue.toLowerCase().contains(service.toLowerCase());
                }
 ciclo           }
        } catch (Exception e) {}
        return false;
    }

    private void showAccessibilityDialog() {
        new AlertDialog.Builder(this)
            .setTitle("需要无障碍权限")
            .setMessage("请开启「AutoTyperPro 服务」以允许自动输入文字")
            .setPositiveButton("去开启", (d, w) -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)))
            .setNegativeButton("取消", null)
            .show();
    }
}
