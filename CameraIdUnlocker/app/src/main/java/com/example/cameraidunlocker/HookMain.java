package com.example.cameraidunlocker;

import android.hardware.camera2.CameraManager;
import android.util.Log;

import java.util.Arrays;
import java.util.LinkedHashSet;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Хук на CameraManager.getCameraIdList().
 *
 * Идея: getCameraIdList() намеренно скрывает physical camera id, входящие
 * в состав логической (мульти-)камеры. Но getCameraCharacteristics(id)
 * и openCamera(id) по факту работают, если id реально существует
 * (это подтверждено тестами через termux-camera-photo).
 *
 * Поэтому просто перебираем разумный диапазон числовых ID, пробуем
 * прочитать характеристики - если не упало с исключением, значит камера
 * реально есть, и добавляем её ID в список, который увидит приложение.
 */
public class HookMain implements IXposedHookLoadPackage {

    private static final String TAG = "CameraIdUnlocker";

    // Диапазон перебора с запасом. У большинства устройств скрытых камер
    // не больше 8-10, но 32 - безопасный запас по времени выполнения.
    private static final int MAX_PROBE_ID = 32;

    // Если нужно ограничить модуль конкретными приложениями (рекомендуется,
    // чтобы не трогать системные процессы) - перечислите пакеты здесь.
    // Оставьте пустым, чтобы хук работал во всех процессах, использующих
    // Camera2 API (включая LSPosed-скоуп, который вы задаёте в приложении).
    private static final String[] TARGET_PACKAGES = {
            // "net.sourceforge.opencamera",
            // "com.android.camera",
            // "com.oneplus.camera",
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        if (TARGET_PACKAGES.length > 0 && !contains(TARGET_PACKAGES, lpparam.packageName)) {
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                    CameraManager.class,
                    "getCameraIdList",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            try {
                                String[] original = (String[]) param.getResult();
                                if (original == null) original = new String[0];

                                CameraManager manager = (CameraManager) param.thisObject;
                                LinkedHashSet<String> ids = new LinkedHashSet<>(Arrays.asList(original));

                                for (int i = 0; i <= MAX_PROBE_ID; i++) {
                                    String id = String.valueOf(i);
                                    if (ids.contains(id)) continue;

                                    try {
                                        // Если камера реально существует, этот вызов
                                        // не выбросит исключение.
                                        manager.getCameraCharacteristics(id);
                                        ids.add(id);
                                        Log.i(TAG, "Добавлен скрытый камера id=" + id
                                                + " в пакете " + lpparam.packageName);
                                    } catch (Throwable ignored) {
                                        // такого ID не существует - пропускаем молча
                                    }
                                }

                                param.setResult(ids.toArray(new String[0]));
                            } catch (Throwable t) {
                                Log.e(TAG, "Ошибка при добавлении скрытых камер", t);
                            }
                        }
                    }
            );

            Log.i(TAG, "Хук установлен для пакета " + lpparam.packageName);

        } catch (Throwable t) {
            Log.e(TAG, "Не удалось установить хук в пакете " + lpparam.packageName, t);
        }
    }

    private static boolean contains(String[] arr, String value) {
        for (String s : arr) if (s.equals(value)) return true;
        return false;
    }
}
