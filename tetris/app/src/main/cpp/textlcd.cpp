#include <unistd.h>
#include <fcntl.h>
#include <assert.h>
#include <jni.h>
#include <cstring>
#include <sys/ioctl.h>

#define TEXTLCD_ON     1
#define TEXTLCD_OFF    2
#define TEXTLCD_INIT   3
#define TEXTLCD_CLEAR  4

#define TEXTLCD_LINE1  5
#define TEXTLCD_LINE2  6

static int fd = 0;

extern "C"
JNIEXPORT void JNICALL Java_com_example_hardware_TextLCDClass_on
        (JNIEnv *env, jobject obj) {
    if (fd == 0)
        fd = open("/dev/fpga_textlcd", O_WRONLY);
    assert(fd != -1);

    ioctl(fd, TEXTLCD_ON);
}

extern "C"
JNIEXPORT void JNICALL Java_com_example_hardware_TextLCDClass_off
        (JNIEnv *env, jobject obj) {
    if (fd != 0) {
        ioctl(fd, TEXTLCD_OFF);
        close(fd);
    }
    fd = 0;
}

extern "C"
JNIEXPORT void JNICALL Java_com_example_hardware_TextLCDClass_initialize
        (JNIEnv *env, jobject obj) {
    if (fd == 0)
        fd = open("/dev/fpga_textlcd", O_WRONLY);
    assert(fd != -1);

    ioctl(fd, TEXTLCD_INIT);
}

extern "C"
JNIEXPORT void JNICALL Java_com_example_hardware_TextLCDClass_clear
        (JNIEnv *env, jobject obj) {
    if (fd != 0) {
        ioctl(fd, TEXTLCD_CLEAR);
    }
}

extern "C"
JNIEXPORT void JNICALL Java_com_example_hardware_TextLCDClass_printLine1
        (JNIEnv *env, jobject obj, jstring msg) {
    const char *str;

    if (fd != 0) {
        str = env->GetStringUTFChars(msg, nullptr);
        ioctl(fd, TEXTLCD_LINE1);
        write(fd, str, strlen(str));
        env->ReleaseStringUTFChars(msg, str);
    }
}

extern "C"
JNIEXPORT void JNICALL Java_com_example_hardware_TextLCDClass_printLine2
        (JNIEnv *env, jobject obj, jstring msg) {
    const char *str;

    if (fd != 0) {
        str = env->GetStringUTFChars(msg, nullptr);
        ioctl(fd, TEXTLCD_LINE2);
        write(fd, str, strlen(str));
        env->ReleaseStringUTFChars(msg, str);
    }
}
