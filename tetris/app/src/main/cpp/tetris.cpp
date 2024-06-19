#include <jni.h>

#include "piezo.cpp"
#include "segment.cpp"
#include "dipsw.cpp"
#include "textlcd.cpp"
#include "fullcolorled.cpp"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_tetris_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    // TODO: implement stringFromJNI()
    return env->NewStringUTF("[DEBUG] JNI Was Loaded Properly");
}