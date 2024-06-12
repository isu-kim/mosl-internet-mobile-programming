#include <unistd.h>
#include <fcntl.h>
#include <assert.h>
#include <jni.h>

static int piezo_fd = -1;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_hardware_PiezoClass_openPizeo(JNIEnv *env, jobject thiz) {
    piezo_fd = open("/dev/fpga_piezo", O_WRONLY);
    assert(piezo_fd != -1);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_hardware_PiezoClass_writePizeo(JNIEnv *env, jobject thiz, jchar data) {
    write(piezo_fd, &data, 1);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_hardware_PiezoClass_closePizeo(JNIEnv *env, jobject thiz) {
    if (piezo_fd != -1) {
        close(piezo_fd);
    } else {
        assert(piezo_fd != -1);
    }
}