package de.robv.android.xposed;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Заглушка только для компиляции. На устройстве реальную реализацию
 * этого интерфейса из состава LSPosed/Xposed framework подставляет
 * сама система при загрузке модуля (наш jar подключён как compileOnly
 * и не попадает в APK).
 */
public interface IXposedHookLoadPackage {
    void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable;
}
