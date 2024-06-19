#include <unistd.h>
#include <fcntl.h>
#include <assert.h>
#include <jni.h>
#include <stdio.h>

static int segment_fd = 0;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_hardware_SegmentClass_openSegment(JNIEnv *env, jobject thiz) {
    segment_fd = open("/dev/fpga_segment", O_WRONLY);
    assert(segment_fd != -1);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_hardware_SegmentClass_writeSegment(JNIEnv *env, jobject thiz, jint num) {
    char buf[7];
    sprintf(buf, "%06d", num);
    write(segment_fd, buf, 6);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_hardware_SegmentClass_closeSegment(JNIEnv *env, jobject thiz) {
    close(segment_fd);
}