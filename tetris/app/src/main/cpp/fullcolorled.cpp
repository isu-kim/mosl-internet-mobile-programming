#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#define FULL_LED1	9
#define	FULL_LED2	8
#define FULL_LED3	7
#define FULL_LED4	6
#define ALL_LED		5

extern "C"
JNIEXPORT void JNICALL Java_com_example_hardware_FullColorLEDClass_Write
(JNIEnv* env, jobject thiz, jint led_num, jint val1, jint val2, jint val3)
{
    int fd,ret;
    char buf[3];

    fd = open("/dev/fpga_fullcolorled", O_WRONLY);
    if (fd < 0)
    {
        return;
    }
    ret = (int)led_num;
    switch(ret)

    {
        case FULL_LED1:
            ioctl(fd,FULL_LED1);
            break;
        case FULL_LED2:
            ioctl(fd,FULL_LED2);
            break;
        case FULL_LED3:
            ioctl(fd,FULL_LED3);
            break;
        case FULL_LED4:
            ioctl(fd,FULL_LED4);
            break;
        case ALL_LED:
            ioctl(fd,ALL_LED);
            break;
    }
    buf[0] = val1;
    buf[1] = val2;
    buf[2] = val3;

    write(fd,buf,3);

    close(fd);
}