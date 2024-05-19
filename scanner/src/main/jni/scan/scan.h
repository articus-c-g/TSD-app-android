#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <android/log.h>
#include <fcntl.h>
#include <termios.h>
#include <wchar.h>


#ifndef ALOGD
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#endif
#ifndef ALOGI
#define  ALOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#endif
#ifndef ALOGW
#define  ALOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#endif
#ifndef ALOGE
#define  ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#endif


#define SCAN_DEVICE_NAME      "/dev/ttyMT1"

typedef struct __baudrate_mpping{
    unsigned int        ul_baud_rate;
    speed_t         linux_baud_rate;
}BAUD_RATE_SETTING;

//scan_core.cpp
speed_t get_speed(void);
int set_uart_parm(int fd, unsigned int new_baudrate, int length, char parity_c, int stopbits);
char* scan_read_data();
int scan_open_dev(bool open);
bool scan_type_check(int type);
int scan_enable_type(bool enable, int type);
char* scan_get_timeout();
int scan_set_timeout(int time);
int scan_set_command(const char* command);
char* scan_query_data(int command);
int profile_scan_type();