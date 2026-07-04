package de.robv.android.xposed.callbacks;

/** Заглушка только для компиляции, см. комментарий в IXposedHookLoadPackage. */
public class XC_LoadPackage {

    public static class LoadPackageParam {
        public String packageName;
        public ClassLoader classLoader;
        public String processName;
        public Object appInfo;
    }
}
