package de.robv.android.xposed;

/** Заглушка только для компиляции, см. комментарий в IXposedHookLoadPackage. */
public class XposedHelpers {

    public static XC_MethodHook.Unhook findAndHookMethod(
            Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        return new XC_MethodHook.Unhook();
    }

    public static XC_MethodHook.Unhook findAndHookMethod(
            String className, ClassLoader classLoader, String methodName,
            Object... parameterTypesAndCallback) {
        return new XC_MethodHook.Unhook();
    }
}
