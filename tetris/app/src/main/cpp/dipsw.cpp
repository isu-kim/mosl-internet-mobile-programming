#include <unistd.h>
#include <fcntl.h>
#include <assert.h>
#include <jni.h>

static int dipsw_fd = -1;

extern "C"
JNIEXPORT void JNICALL Java_com_example_hardware_DipswClass_openDipsw
(JNIEnv * env, jobject obj){
    dipsw_fd = open("/dev/fpga_dipsw", O_RDONLY);
    assert(dipsw_fd != 0);
}

extern "C"
JNIEXPORT void JNICALL Java_com_example_hardware_DipswClass_closeDipsw
(JNIEnv * env, jobject obj){
    close(dipsw_fd);
}

extern "C"
JNIEXPORT jint JNICALL Java_com_example_hardware_DipswClass_getDipsw
(JNIEnv * env, jobject obj){
    short int re;
    read(dipsw_fd, &re, 2);

    return re;
}