#include <jni.h>

#include "piezo.cpp"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_tetris_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    // TODO: implement stringFromJNI()
    return env->NewStringUTF("[DEBUG] JNI Was Loaded Properly");
}