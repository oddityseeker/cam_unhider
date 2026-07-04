package com.example.cameraidunlocker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Camera ID Unlocker\n\n" +
                "Это модуль LSPosed. Настраивается только через LSPosed Manager:\n" +
                "1. Включите модуль\n" +
                "2. Задайте область действия (Scope) - приложения камеры\n" +
                "3. Перезапустите выбранные приложения");
        tv.setPadding(40, 100, 40, 40);
        tv.setTextSize(16);
        setContentView(tv);
    }
}
