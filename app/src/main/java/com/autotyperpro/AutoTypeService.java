package com.autotyperpro;

import android.accessibilityservice.AccessibilityService;
import android.content.*;
import android.os.Bundle;
import android.view.accessibility.*;
import android.widget.Toast;
import java.util.List;

public class AutoTypeService extends AccessibilityService {
    private static final String ACTION_TYPE = "com.autotyperpro.ACTION_TYPE";

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(typeReceiver, new IntentFilter(ACTION_TYPE));
    }

    private final BroadcastReceiver typeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("text");
            if (text != null) {
                new Thread(() -> performTypeText(text)).start();
            }
        }
    };

    private void performTypeText(String text) {
        try { Thread.sleep(5000); } catch (InterruptedException e) {}

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByClassName("android.widget.EditText");

        boolean success = false;
        for (AccessibilityAccessorNodeInfo node : nodes) {
            if (node.isEditable()) {
                Bundle args = new Bundle();
                args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                success = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
                if (success) break;
            }
        }

        if (!success) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("AutoTyper", text);
            clipboard.setPrimaryClip(clip);
            for (AccessibilityNodeInfo node : nodes) {
                if (node.isEditable()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    break;
                }
            }
        }
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
    @Override public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(typeReceiver);
    }
}
