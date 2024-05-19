#include <jni.h>
#include <scan.h>

#ifdef LOG_TAG
#undef LOG_TAG
#endif
#define LOG_TAG "SCANLIB_JNI"

jboolean openDev(JNIEnv *env, jobject thiz)
{
    int ret = scan_open_dev(true);
    ALOGD("%s, [ret=%d]\n", __func__, ret);
    return ret > 0 ? JNI_TRUE : JNI_FALSE;
}

jboolean closeDev(JNIEnv *env, jobject thiz)
{
    int ret = scan_open_dev(false);
    ALOGD("%s, [ret=%d]\n", __func__, ret);
    return ret > 0 ? JNI_TRUE : JNI_FALSE;
}

jstring readData(JNIEnv *env, jobject thiz)
{
    char *data = scan_read_data();
    jstring jstr = env->NewStringUTF(data);
    return jstr;
}

jboolean checkType(JNIEnv *env, jobject thiz, jint type)
{
    bool enable = scan_type_check(type);
    ALOGD("%s, [enable=%d]\n", __func__, enable);
    return enable;
}

jboolean enableType(JNIEnv *env, jobject thiz, jint type)
{
    int ret = scan_enable_type(true, type);
    ALOGD("%s, [ret=%d]\n", __func__, ret);
    return ret > 0 ? JNI_TRUE : JNI_FALSE;
}

jboolean disableType(JNIEnv *env, jobject thiz, jint type)
{
    int ret = scan_enable_type(false, type);
    ALOGD("%s, [ret=%d]\n", __func__, ret);
    return ret > 0 ? JNI_TRUE : JNI_FALSE;
}

jstring getTimeout(JNIEnv *env, jobject thiz)
{
    char *data = scan_get_timeout();
    ALOGD("%s, [data=%s]\n", __func__, data);
    jstring jstr = env->NewStringUTF(data);
    return jstr;
}

jboolean setTimeout(JNIEnv *env, jobject thiz, jint time)
{
    int ret = scan_set_timeout(time);
    ALOGD("%s, [ret=%d]\n", __func__, ret);
    return ret > 0 ? JNI_TRUE : JNI_FALSE;
}

jboolean sendCommand(JNIEnv *env, jobject thiz, jstring command)
{
    const char* str = env->GetStringUTFChars(command, NULL);
    ALOGD("%s, [str=%s]", __func__, str);
    if (str == NULL) {
        return false;
    }
    int ret = scan_set_command(str);
    ALOGD("%s, [ret=%d]\n", __func__, ret);
    env->ReleaseStringUTFChars(command, str);
    return ret > 0 ? JNI_TRUE : JNI_FALSE;
}

jstring queryData(JNIEnv *env, jobject thiz, jint command)
{
    char *data = scan_query_data(command);
    ALOGD("%s, [data=%s]\n", __func__, data);
    jstring jstr = env->NewStringUTF(data);
    return jstr;
}

jboolean profileScanType(JNIEnv *env, jobject thiz)
{
    int ret = profile_scan_type();
    ALOGD("%s, [ret=%d]\n", __func__, ret);
    return ret > 0 ? JNI_TRUE : JNI_FALSE;
}

static const char *classPathNameRx = "com/xcheng/scanner/ScanNative";

static JNINativeMethod methodsRx[] = {
    {"openDev", "()Z", (void*)openDev},
    {"closeDev", "()Z", (void*)closeDev},
    {"readData", "()Ljava/lang/String;", (void*)readData},
    {"checkType", "(I)Z", (void*)checkType},
    {"enableType", "(I)Z", (void*)enableType},
    {"disableType", "(I)Z", (void*)disableType},
    {"setTimeout", "(I)Z", (void*)setTimeout},
    {"getTimeout", "()Ljava/lang/String;", (void*)getTimeout},
    {"sendCommand", "(Ljava/lang/String;)Z", (void*)sendCommand},
    {"queryData", "(I)Ljava/lang/String;", (void*)queryData},
    {"profileScanType", "()Z", (void*)profileScanType},
};

/*
 * Register several native methods for one class.
 */
static jint registerNativeMethods(JNIEnv* env, const char* className,
    JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = env->FindClass(className);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
    if (clazz == NULL) {
        ALOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        ALOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    ALOGD("%s, success\n", __func__);
    return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 *
 * returns JNI_TRUE on success.
 */
static jint registerNatives(JNIEnv* env)
{
    jint ret = JNI_FALSE;

    if (registerNativeMethods(env, classPathNameRx,methodsRx,
        sizeof(methodsRx) / sizeof(methodsRx[0]))) {
        ret = JNI_TRUE;
    }

    ALOGD("%s, done\n", __func__);
    return ret;
}

// ----------------------------------------------------------------------------

/*
 * This is called by the VM when the shared library is first loaded.
 */

typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;

    ALOGI("JNI_OnLoad");

    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("ERROR: GetEnv failed");
        goto fail;
    }
    env = uenv.env;

    if (registerNatives(env) != JNI_TRUE) {
        ALOGE("ERROR: registerNatives failed");
        goto fail;
    }

    result = JNI_VERSION_1_4;

fail:
    return result;
}

