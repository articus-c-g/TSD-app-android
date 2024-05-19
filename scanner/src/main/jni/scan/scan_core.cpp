#include <scan.h>
#include <cutils/properties.h>
#include <pthread.h>
#include <mutex>

#ifdef LOG_TAG
#undef LOG_TAG
#endif
#define LOG_TAG "SCANLIB_CORE"

#define MAX_DATA_NUM     8192
#define DEAFAULT_CMD_LEN     16
#define DEAFAULT_TYPE_NUMBER   36

const char OPEN_CMD[] = {0xFF,0x54,0x0D};
const char CLOSE_CMD[] = {0xFF,0x55,0x0D};
const char QUERY_TIMEOUT_CMD[] = {0xFF,0x4D,0x0D,0x38,0x36,0x31,0x30,0x30,0x31,0x3F,0x2E};

const char type_check_command[][DEAFAULT_CMD_LEN] = {
    {0xFF,0x4D,0x0D,0x39,0x39,0x39,0x30,0x30,0x31,0x3F,0x2E}, //type 0 [all Code: 999001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x31,0x30,0x30,0x31,0x3F,0x2E}, //type 1 [Aztec Code: 931001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x36,0x30,0x30,0x31,0x3F,0x2E}, //type 2 [China Post: 936001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x30,0x30,0x30,0x33,0x3F,0x2E}, //type 3 [Codabar: 900003]
    {0xFF,0x4D,0x0D,0x39,0x32,0x32,0x30,0x30,0x31,0x3F,0x2E}, //type 4 [Codablock A: 922001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x33,0x30,0x30,0x31,0x3F,0x2E}, //type 5 [Codablock F: 923001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x38,0x30,0x30,0x32,0x3F,0x2E}, //type 6 [Code 11: 908002]
    {0xFF,0x4D,0x0D,0x39,0x30,0x31,0x30,0x30,0x31,0x3F,0x2E}, //type 7 [Code 39: 901001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x34,0x30,0x30,0x32,0x3F,0x2E}, //type 8 [Code 93: 904002]
    {0xFF,0x4D,0x0D,0x39,0x30,0x39,0x30,0x30,0x31,0x3F,0x2E}, //type 9 [Code 128: 909001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x39,0x30,0x30,0x31,0x3F,0x2E}, //type 10 [Data Matrix: 929001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x36,0x30,0x30,0x31,0x3F,0x2E}, //type 11 [EAN/JAN-8: 916001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x35,0x30,0x30,0x31,0x3F,0x2E}, //type 12 [EAN/JAN-13: 915001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x30,0x30,0x30,0x31,0x3F,0x2E}, //type 13 [GS1-128: 910001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x36,0x30,0x30,0x31,0x3F,0x2E}, //type 14 [GS1 Composite Codes: 926001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x30,0x30,0x30,0x31,0x3F,0x2E}, //type 15 [GS1 DataBar Expanded: 920001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x39,0x30,0x30,0x31,0x3F,0x2E}, //type 16 [GS1 DataBar Limited: 919001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x38,0x30,0x30,0x31,0x3F,0x2E}, //type 17 [GS1 DataBar Omnidiretional: 918001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x32,0x30,0x30,0x31,0x3F,0x2E}, //type 18 [Han Xin: 932001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x32,0x30,0x30,0x32,0x3F,0x2E}, //type 19 [Interleaved 2 of 5: 902002]
    {0xFF,0x4D,0x0D,0x39,0x33,0x37,0x30,0x30,0x31,0x3F,0x2E}, //type 20 [Korea Post: 937001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x37,0x30,0x30,0x31,0x3F,0x2E}, //type 21 [Matrix 2 of 5: 907001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x30,0x30,0x30,0x31,0x3F,0x2E}, //type 22 [MaxiCode: 930001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x37,0x30,0x30,0x31,0x3F,0x2E}, //type 23 [MSI-Plessey: 917001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x35,0x30,0x30,0x31,0x3F,0x2E}, //type 24 [Micro PDF417: 925001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x33,0x30,0x30,0x31,0x3F,0x2E}, //type 25 [NEC 2 of 5: 903001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x34,0x30,0x30,0x31,0x3F,0x2E}, //type 26 [PDF417: 924001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x38,0x30,0x30,0x31,0x3F,0x2E}, //type 27 [QR Code: 928001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x35,0x30,0x30,0x31,0x3F,0x2E}, //type 28 [Straight 2 of 5: 905001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x36,0x30,0x30,0x31,0x3F,0x2E}, //type 29 [Straight 2 of 5 IATA: 906001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x31,0x30,0x30,0x31,0x3F,0x2E}, //type 30 [Telepen: 911001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x37,0x30,0x30,0x31,0x3F,0x2E}, //type 31 [TCIF Linked Code 39: 927001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x31,0x30,0x30,0x31,0x3F,0x2E}, //type 32 [Trioptic Code: 921001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x32,0x30,0x30,0x33,0x3F,0x2E}, //type 33 [UPC-A: 912003]
    {0xFF,0x4D,0x0D,0x39,0x31,0x34,0x30,0x31,0x30,0x3F,0x2E}  //type 34 [UPC-E0: 914001]
};
const char type_open_command[][DEAFAULT_CMD_LEN] = {
    {0xFF,0x4D,0x0D,0x39,0x39,0x39,0x30,0x30,0x31,0x31,0x2E}, //type 0 [all Code: 999001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x31,0x30,0x30,0x31,0x31,0x2E}, //type 1 [Aztec Code: 931001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x36,0x30,0x30,0x31,0x31,0x2E}, //type 2 [China Post: 936001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x30,0x30,0x30,0x33,0x31,0x2E}, //type 3 [Codabar: 900003]
    {0xFF,0x4D,0x0D,0x39,0x32,0x32,0x30,0x30,0x31,0x31,0x2E}, //type 4 [Codablock A: 922001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x33,0x30,0x30,0x31,0x31,0x2E}, //type 5 [Codablock F: 923001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x38,0x30,0x30,0x32,0x31,0x2E}, //type 6 [Code 11: 908002]
    {0xFF,0x4D,0x0D,0x39,0x30,0x31,0x30,0x30,0x31,0x31,0x2E}, //type 7 [Code 39: 901001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x34,0x30,0x30,0x32,0x31,0x2E}, //type 8 [Code 93: 904002]
    {0xFF,0x4D,0x0D,0x39,0x30,0x39,0x30,0x30,0x31,0x31,0x2E}, //type 9 [Code 128: 909001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x39,0x30,0x30,0x31,0x31,0x2E}, //type 10 [Data Matrix: 929001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x36,0x30,0x30,0x31,0x31,0x2E}, //type 11 [EAN/JAN-8: 916001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x35,0x30,0x30,0x31,0x31,0x2E}, //type 12 [EAN/JAN-13: 915001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x30,0x30,0x30,0x31,0x31,0x2E}, //type 13 [GS1-128: 910001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x36,0x30,0x30,0x31,0x31,0x2E}, //type 14 [GS1 Composite Codes: 926001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x30,0x30,0x30,0x31,0x31,0x2E}, //type 15 [GS1 DataBar Expanded: 920001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x39,0x30,0x30,0x31,0x31,0x2E}, //type 16 [GS1 DataBar Limited: 919001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x38,0x30,0x30,0x31,0x31,0x2E}, //type 17 [GS1 DataBar Omnidiretional: 918001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x32,0x30,0x30,0x31,0x31,0x2E}, //type 18 [Han Xin: 932001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x32,0x30,0x30,0x32,0x31,0x2E}, //type 19 [Interleaved 2 of 5: 902002]
    {0xFF,0x4D,0x0D,0x39,0x33,0x37,0x30,0x30,0x31,0x31,0x2E}, //type 20 [Korea Post: 937001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x37,0x30,0x30,0x31,0x31,0x2E}, //type 21 [Matrix 2 of 5: 907001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x30,0x30,0x30,0x31,0x31,0x2E}, //type 22 [MaxiCode: 930001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x37,0x30,0x30,0x31,0x31,0x2E}, //type 23 [MSI-Plessey: 917001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x35,0x30,0x30,0x31,0x31,0x2E}, //type 24 [Micro PDF417: 925001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x33,0x30,0x30,0x31,0x31,0x2E}, //type 25 [NEC 2 of 5: 903001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x34,0x30,0x30,0x31,0x31,0x2E}, //type 26 [PDF417: 924001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x38,0x30,0x30,0x31,0x31,0x2E}, //type 27 [QR Code: 928001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x35,0x30,0x30,0x31,0x31,0x2E}, //type 28 [Straight 2 of 5: 905001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x36,0x30,0x30,0x31,0x31,0x2E}, //type 29 [Straight 2 of 5 IATA: 906001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x31,0x30,0x30,0x31,0x31,0x2E}, //type 30 [Telepen: 911001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x37,0x30,0x30,0x31,0x31,0x2E}, //type 31 [TCIF Linked Code 39: 927001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x31,0x30,0x30,0x31,0x31,0x2E}, //type 32 [Trioptic Code: 921001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x32,0x30,0x30,0x33,0x31,0x2E}, //type 33 [UPC-A: 912003]
    {0xFF,0x4D,0x0D,0x39,0x31,0x34,0x30,0x31,0x30,0x31,0x2E}  //type 34 [UPC-E0: 914001]
};
const char type_close_command[][DEAFAULT_CMD_LEN] = {
    {0xFF,0x4D,0x0D,0x39,0x39,0x39,0x30,0x30,0x31,0x30,0x2E}, //type 0 [all Code: 999001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x31,0x30,0x30,0x31,0x30,0x2E}, //type 1 [Aztec Code: 931001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x36,0x30,0x30,0x31,0x30,0x2E}, //type 2 [China Post: 936001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x30,0x30,0x30,0x33,0x30,0x2E}, //type 3 [Codabar: 900003]
    {0xFF,0x4D,0x0D,0x39,0x32,0x32,0x30,0x30,0x31,0x30,0x2E}, //type 4 [Codablock A: 922001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x33,0x30,0x30,0x31,0x30,0x2E}, //type 5 [Codablock F: 923001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x38,0x30,0x30,0x32,0x30,0x2E}, //type 6 [Code 11: 908002]
    {0xFF,0x4D,0x0D,0x39,0x30,0x31,0x30,0x30,0x31,0x30,0x2E}, //type 7 [Code 39: 901001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x34,0x30,0x30,0x32,0x30,0x2E}, //type 8 [Code 93: 904002]
    {0xFF,0x4D,0x0D,0x39,0x30,0x39,0x30,0x30,0x31,0x30,0x2E}, //type 9 [Code 128: 909001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x39,0x30,0x30,0x31,0x30,0x2E}, //type 10 [Data Matrix: 929001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x36,0x30,0x30,0x31,0x30,0x2E}, //type 11 [EAN/JAN-8: 916001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x35,0x30,0x30,0x31,0x30,0x2E}, //type 12 [EAN/JAN-13: 915001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x30,0x30,0x30,0x31,0x30,0x2E}, //type 13 [GS1-128: 910001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x36,0x30,0x30,0x31,0x30,0x2E}, //type 14 [GS1 Composite Codes: 926001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x30,0x30,0x30,0x31,0x30,0x2E}, //type 15 [GS1 DataBar Expanded: 920001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x39,0x30,0x30,0x31,0x30,0x2E}, //type 16 [GS1 DataBar Limited: 919001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x38,0x30,0x30,0x31,0x30,0x2E}, //type 17 [GS1 DataBar Omnidiretional: 918001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x32,0x30,0x30,0x31,0x30,0x2E}, //type 18 [Han Xin: 932001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x32,0x30,0x30,0x32,0x30,0x2E}, //type 19 [Interleaved 2 of 5: 902002]
    {0xFF,0x4D,0x0D,0x39,0x33,0x37,0x30,0x30,0x31,0x30,0x2E}, //type 20 [Korea Post: 937001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x37,0x30,0x30,0x31,0x30,0x2E}, //type 21 [Matrix 2 of 5: 907001]
    {0xFF,0x4D,0x0D,0x39,0x33,0x30,0x30,0x30,0x31,0x30,0x2E}, //type 22 [MaxiCode: 930001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x37,0x30,0x30,0x31,0x30,0x2E}, //type 23 [MSI-Plessey: 917001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x35,0x30,0x30,0x31,0x30,0x2E}, //type 24 [Micro PDF417: 925001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x33,0x30,0x30,0x31,0x30,0x2E}, //type 25 [NEC 2 of 5: 903001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x34,0x30,0x30,0x31,0x30,0x2E}, //type 26 [PDF417: 924001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x38,0x30,0x30,0x31,0x30,0x2E}, //type 27 [QR Code: 928001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x35,0x30,0x30,0x31,0x30,0x2E}, //type 28 [Straight 2 of 5: 905001]
    {0xFF,0x4D,0x0D,0x39,0x30,0x36,0x30,0x30,0x31,0x30,0x2E}, //type 29 [Straight 2 of 5 IATA: 906001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x31,0x30,0x30,0x31,0x30,0x2E}, //type 30 [Telepen: 911001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x37,0x30,0x30,0x31,0x30,0x2E}, //type 31 [TCIF Linked Code 39: 927001]
    {0xFF,0x4D,0x0D,0x39,0x32,0x31,0x30,0x30,0x31,0x30,0x2E}, //type 32 [Trioptic Code: 921001]
    {0xFF,0x4D,0x0D,0x39,0x31,0x32,0x30,0x30,0x33,0x30,0x2E}, //type 33 [UPC-A: 912003]
    {0xFF,0x4D,0x0D,0x39,0x31,0x34,0x30,0x31,0x30,0x30,0x2E}  //type 34 [UPC-E0: 914001]
};

static BAUD_RATE_SETTING speeds_mapping[] = {
    {0      ,B0      },
    {50     ,B50     },
    {75     ,B75     },
    {110    ,B110    },
    {134    ,B134    },
    {150    ,B150    },
    {200    ,B200    },
    {300    ,B300    },
    {600    ,B600    },
    {1200   ,B1200   },
    {1800   ,B1800   },
    {2400   ,B2400   },
    {4800   ,B4800   },
    {9600   ,B9600   },
    {19200  ,B19200  },
    {38400  ,B38400  },
    {57600  ,B57600  },
    {115200 ,B115200 },
    {230400 ,B230400 },
    {460800 ,B460800 },
    {500000 ,B500000 },
    {576000 ,B576000 },
    {921600 ,B921600 },
    {1000000,B1000000},
    {1152000,B1152000},
    {1500000,B1500000},
    {2000000,B2000000},
    {2500000,B2500000},
    {3000000,B3000000},
    {3500000,B3500000},
    {4000000,B4000000},
};

static speed_t get_speed(unsigned int baudrate)
{
    unsigned int idx;
    for (idx = 0; idx < sizeof(speeds_mapping)/sizeof(speeds_mapping[0]); idx++){
        if (baudrate == (unsigned int)speeds_mapping[idx].ul_baud_rate){
            return speeds_mapping[idx].linux_baud_rate;
        }
    }
    return CBAUDEX;
}

int set_uart_parm(int fd, unsigned int new_baudrate, int length, char parity_c, int stopbits)
{
    struct termios uart_cfg_opt;
    speed_t speed;
    char  using_custom_speed = 0;

    if(-1==fd)
        return -1;

    /* Get current uart configure option */
    if(-1 == tcgetattr(fd, &uart_cfg_opt))
        return -1;

    tcflush(fd, TCIOFLUSH);

    /* Baud rate setting section */
    speed = get_speed(new_baudrate);
    if (CBAUDEX != speed){
        /*set standard buadrate setting*/
        cfsetospeed(&uart_cfg_opt, speed);
        cfsetispeed(&uart_cfg_opt, speed);
        ALOGD("%s, Standard baud\r\n", __func__);
    } else {
        ALOGD("%s, Custom baud\r\n", __func__);
        using_custom_speed = 1;
    }
    /* Apply baudrate settings */
    if(-1==tcsetattr(fd, TCSANOW, &uart_cfg_opt))
        return -1;

    /* Set time out */
    uart_cfg_opt.c_cc[VTIME] = 1;
    uart_cfg_opt.c_cc[VMIN] = 0;

    /* Data length setting section */
    uart_cfg_opt.c_cflag &= ~CSIZE;
    switch(length)
    {
        default:
        case 8:
            uart_cfg_opt.c_cflag |= CS8;
            break;
        case 5:
            uart_cfg_opt.c_cflag |= CS5;
            break;
        case 6:
            uart_cfg_opt.c_cflag |= CS6;
            break;
        case 7:
            uart_cfg_opt.c_cflag |= CS7;
            break;
    }

    /* Parity setting section */
    uart_cfg_opt.c_cflag &= ~(PARENB|PARODD);
    switch(parity_c)
    {
        default:
        case 'N':
        case 'n':
            uart_cfg_opt.c_iflag &= ~INPCK;
            break;
        case 'O':
        case 'o':
            uart_cfg_opt.c_cflag |= (PARENB|PARODD);
            uart_cfg_opt.c_iflag |= INPCK;
            break;
        case 'E':
        case 'e':
            uart_cfg_opt.c_cflag |= PARENB;
            uart_cfg_opt.c_iflag |= INPCK;
            break;
    }

    /* Stop bits setting section */
    if(2==stopbits)
        uart_cfg_opt.c_cflag |= CSTOPB;
    else
        uart_cfg_opt.c_cflag &= ~CSTOPB;

    /* Using raw data mode */
    uart_cfg_opt.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);
    uart_cfg_opt.c_iflag &= ~(INLCR | IGNCR | ICRNL | IXON | IXOFF);
    uart_cfg_opt.c_oflag &=~(INLCR|IGNCR|ICRNL);
    uart_cfg_opt.c_oflag &=~(ONLCR|OCRNL);

    /* Apply new settings */
    if(-1==tcsetattr(fd, TCSANOW, &uart_cfg_opt))
        return -1;

    tcflush(fd,TCIOFLUSH);

    /* All setting applied successful */
    ALOGD("%s,nSpeed = %d,nBits = %d,nEvent = %c,nStop = %d\n",__func__,new_baudrate,length,parity_c,stopbits);
    return 0;
}

char* scan_read_data()
{
    int Fd = 0,i = 0,length = 0,ret = -1;
    char recv_data[MAX_DATA_NUM] = {'\0'};

    Fd = open(SCAN_DEVICE_NAME, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
    if (Fd == -1) {
        ALOGE("%s, open device fail! Fd = %d", __func__, Fd);
        return NULL;
    }

    ret = set_uart_parm(Fd, 115200, 8, 'N', 1);
    if (ret == -1) {
        ALOGD("%s, set uart parm fail! ret = %d", __func__, ret);
        return NULL;
    }

    for(i = 0;i < 100;i++)
    {
        usleep(10000);
        ret = read(Fd, &recv_data[0], MAX_DATA_NUM);
        length = strlen(recv_data);
        if (ret < 0) {
            ALOGE("%s, read data error! ret = %d", __func__, ret);
        } else {
            ALOGD("%s, length = %d ", __func__, length);
            for (i=0; i<length; i++){
                ALOGD("%s, %.2X ", __func__, recv_data[i]);
            }
//            if (0x0D == static_cast<int>(recv_data[length - 2])
//                && 0x0A == static_cast<int>(recv_data[length - 1])) {
//                ALOGD("%s, read data success", __func__);
//                break;
//            }
            break;
        }
    }
    close(Fd);
    return &recv_data[0];
}

int scan_set_timeout(int time)
{
    int Fd = 0,i = 0,length = 0,ret = -1;
    char set_timeout[] = {0xFF,0x4D,0x0D,0x38,0x36,0x31,0x30,0x30,0x31,0x33,0x30,0x30,0x30,0x2E};
    char *timeArray = (char*)malloc(sizeof(char)*5);

    memset(timeArray, 0, sizeof(char)*5);
    sprintf(timeArray, "%d", time);
    ALOGD("%s, time = %d, timeArray = %s\n", __func__, time, timeArray);

    for (i=0; i<4; i++){
        ALOGD("%s, %.2X ", __func__, timeArray[i]);
        set_timeout[9 + i] = timeArray[i] - '0' + 0x30;
    }
    for (i=0; i<14; i++){
        ALOGD("%s, %.2X ", __func__, set_timeout[i]);
    }
    Fd = open(SCAN_DEVICE_NAME, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
    if (Fd == -1) {
        ALOGE("%s, open device fail! Fd = %d", __func__, Fd);
        return NULL;
    }

    ret = set_uart_parm(Fd, 115200, 8, 'N', 1);
    if (ret == -1) {
        ALOGD("%s, set uart parm fail! ret = %d", __func__, ret);
        return NULL;
    }

    ret = write(Fd, set_timeout, 14);
    if (ret < 0) {
        ALOGE("%s, write timeout fail! ret = %d", __func__, ret);
    } else {
        ALOGD("%s, write timeout successful! ret = %d", __func__, ret);
    }
    usleep(100000);
    close(Fd);
    return ret;
}

char* scan_get_timeout()
{
    int Fd = 0,i=0,length = 0,ret = -1;
    char recv_data[16] = {'\0'};
    char *timeout = (char*)malloc(sizeof(char)*5);

    Fd = open(SCAN_DEVICE_NAME, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
    if (Fd == -1) {
        ALOGE("%s, open device fail! Fd = %d", __func__, Fd);
        return NULL;
    }

    ret = set_uart_parm(Fd, 115200, 8, 'N', 1);
    if (ret == -1) {
        ALOGE("%s, set uart parm fail! ret = %d", __func__, ret);
        return NULL;
    }

    length = strlen(QUERY_TIMEOUT_CMD);
    ret = write(Fd, QUERY_TIMEOUT_CMD, length);
    if (ret < 0) {
        ALOGE("%s, write fail! ret = %d", __func__, ret);
    } else {
        ALOGD("%s, write successful! ret = %d", __func__, ret);
    }

    memset(timeout, 0, sizeof(char)*5);

    for(i = 0;i < 10;i++)
    {
        usleep(10000);
        ret = read(Fd, &recv_data[0], 16);
        length = strlen(recv_data);
        if (ret < 0) {
            ALOGE("%s, check type error! ret = %d", __func__, ret);
        } else {
            for (i=0; i<length; i++){
                ALOGD("%s, %.2X ", __func__, recv_data[i]);
            }
            //ACK
            if (6 == static_cast<int>(recv_data[length - 2])) {
                //ASCII--1
                for (i=0; i<4; i++){
                    timeout[i] = recv_data[length - 6 + i];
                }
                ALOGD("%s, %s", __func__, timeout);
                break;
            }
        }
    }
    close(Fd);
    return timeout;
}

bool scan_type_check(int type)
{
    int Fd = 0,i = 0,length = 0,ret = -1;
    char recv_data[DEAFAULT_CMD_LEN] = {'\0'};
    bool enable = false;

    ALOGD("%s, type = %d", __func__, type);

    Fd = open(SCAN_DEVICE_NAME, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
    if (Fd == -1) {
        ALOGE("%s, open device fail! Fd = %d", __func__, Fd);
        return enable;
    }

    ret = set_uart_parm(Fd, 115200, 8, 'N', 1);
    if (ret == -1) {
        ALOGE("%s, set uart parm fail! ret = %d", __func__, ret);
        return enable;
    }

    length = strlen(type_check_command[type]);
    ret = write(Fd, type_check_command[type], length);
    if (ret < 0) {
        ALOGE("%s, write check fail! ret = %d", __func__, ret);
    } else {
        ALOGD("%s, write check successful! ret = %d", __func__, ret);
    }

    for(i = 0;i < 10;i++)
    {
        usleep(10000);
        ret = read(Fd, &recv_data[0], DEAFAULT_CMD_LEN);
        length = strlen(recv_data);
        if (ret < 0) {
            ALOGE("%s, check type error! ret = %d", __func__, ret);
        } else {
            for (i=0; i<length; i++){
                ALOGD("%s, %.2X ", __func__, recv_data[i]);
            }
            //ACK
            if (6 == static_cast<int>(recv_data[length - 2])) {
                //ASCII--1
                enable = 49 == static_cast<int>(recv_data[length - 3]);
                ALOGD("%s, [enable=%d]\n", __func__, enable);
                break;
            }
        }
    }
    close(Fd);
    return enable;
}

int scan_open_dev(bool opendev)
{
    int Fd = 0,length = 0,ret = -1;

    Fd = open(SCAN_DEVICE_NAME, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
    if (Fd == -1) {
        ALOGE("%s, open device fail! Fd = %d", __func__, Fd);
        return Fd;
    }

    ret = set_uart_parm(Fd, 115200, 8, 'N', 1);
    if (ret == -1) {
        ALOGD("%s, set uart parm fail! ret = %d", __func__, ret);
        return ret;
    }

    if (opendev) {
        length = strlen(OPEN_CMD);
        ret = write(Fd, OPEN_CMD, length);
        if (ret < 0) {
            ALOGD("%s, open fail! ret = %d", __func__, ret);
        } else {
            ALOGD("%s, open successful! ret = %d", __func__, ret);
        }
    } else {
        length = strlen(CLOSE_CMD);
        ret = write(Fd, CLOSE_CMD, length);
        if (ret < 0) {
            ALOGD("%s, close fail! ret = %d", __func__, ret);
        } else {
            ALOGD("%s, close successful! ret = %d", __func__, ret);
        }
    }

    close(Fd);
    return ret;
}

int scan_enable_type(bool enable, int type)
{
    int Fd = 0,length = 0,ret = -1;

    Fd = open(SCAN_DEVICE_NAME, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
    if (Fd == -1) {
        ALOGE("%s, open device fail! Fd = %d", __func__, Fd);
        return -1;
    }

    ret = set_uart_parm(Fd, 115200, 8, 'N', 1);
    if (ret == -1) {
        ALOGE("%s, set uart parm fail! ret = %d",  __func__, ret);
        return -1;
    }

    if (enable) {
        length = strlen(type_open_command[type]);
        ret = write(Fd, type_open_command[type], length);
        if (ret < 0) {
            ALOGE("%s, write enable fail! ret = %d", __func__, ret);
        } else {
            ALOGD("%s, write enable successful! ret = %d", __func__, ret);
        }
    } else {
        length = strlen(type_close_command[type]);
        ret = write(Fd, type_close_command[type], length);
        if (ret < 0) {
            ALOGE("%s, write disable fail! ret = %d", __func__, ret);
        } else {
            ALOGD("%s, write disable successful! ret = %d", __func__, ret);
        }
    }
    close(Fd);
    return ret;
}

int scan_set_command(const char* command)
{
    int Fd = 0,i = 0,length = 0,ret = -1;
    char *write_data = (char*)malloc(sizeof(char)*16);
    char prefix[] = {0xFF,0x4D,0x0D,0x2E};

    length = strlen(command);
    ALOGD("%s, command = %s, length = %d\n", __func__, command, length);
    memset(write_data, 0, sizeof(char)*16);
    for (i=0; i<3; i++){
        write_data[i] = prefix[i];
    }
    for (i = 0; i<length; i++) {
        write_data[i+3] = command[i] - '0' + 0x30;
    }
    write_data[length+3] = prefix[3];
    length = strlen(write_data);
    ALOGD("%s, length = %d", __func__, length);
    for (i=0; i<length; i++){
        ALOGD("%s, %.2X ", __func__, write_data[i]);
    }

    Fd = open(SCAN_DEVICE_NAME, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
    if (Fd == -1) {
        ALOGE("%s, open device fail! Fd = %d", __func__, Fd);
        return NULL;
    }

    ret = set_uart_parm(Fd, 115200, 8, 'N', 1);
    if (ret == -1) {
        ALOGD("%s, set uart parm fail! ret = %d", __func__, ret);
        return NULL;
    }

    ret = write(Fd, write_data, length);
    if (ret < 0) {
        ALOGE("%s, write command fail! ret = %d", __func__, ret);
    } else {
        ALOGD("%s, write command successful! ret = %d", __func__, ret);
    }
    usleep(100000);
    close(Fd);
    return ret;
}

char* scan_query_data(int command)
{
    int Fd = 0,i = 0,ret = -1;
    int length = 0,commandlength = 0,writelength = 0,readlength = 0;
    char recv_data[DEAFAULT_CMD_LEN] = {'\0'};
    bool enable = false;
    char *write_data = (char*)malloc(sizeof(char)*DEAFAULT_CMD_LEN);
    char *commandArray = (char*)malloc(sizeof(char)*DEAFAULT_CMD_LEN);
    char *data = (char*)malloc(sizeof(char)*DEAFAULT_CMD_LEN);
    char prefix[] = {0xFF,0x4D,0x0D,0x3F,0x2E};

    memset(commandArray, 0, sizeof(char)*DEAFAULT_CMD_LEN);
    sprintf(commandArray, "%d", command);
    commandlength = strlen(commandArray);
    ALOGD("%s, commandlength = %d, commandArray = %s\n", __func__, commandlength, commandArray);

    memset(write_data, 0, sizeof(char)*DEAFAULT_CMD_LEN);
    for (i=0; i<3; i++){
        write_data[i] = prefix[i];
    }
    for (i = 0; i<commandlength; i++) {
        write_data[i+3] = commandArray[i] - '0' + 0x30;
    }
    write_data[commandlength+3] = prefix[3];
    write_data[commandlength+4] = prefix[4];
    writelength = strlen(write_data);
    ALOGD("%s, writelength = %d", __func__, writelength);
    for (i=0; i<writelength; i++){
        ALOGD("%s, %.2X ", __func__, write_data[i]);
    }

    Fd = open(SCAN_DEVICE_NAME, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
    if (Fd == -1) {
        ALOGE("%s, open device fail! Fd = %d", __func__, Fd);
        return NULL;
    }

    ret = set_uart_parm(Fd, 115200, 8, 'N', 1);
    if (ret == -1) {
        ALOGE("%s, set uart parm fail! ret = %d", __func__, ret);
        return NULL;
    }

    ret = write(Fd, write_data, writelength);
    if (ret < 0) {
        ALOGE("%s, write query fail! ret = %d", __func__, ret);
    } else {
        ALOGD("%s, write query successful! ret = %d", __func__, ret);
    }

    memset(data, '\0', sizeof(char)*DEAFAULT_CMD_LEN);

    for(i = 0;i < 10;i++)
    {
        usleep(10000);
        ret = read(Fd, &recv_data[0], DEAFAULT_CMD_LEN);
        length = strlen(recv_data);
        if (ret < 0) {
            ALOGE("%s, check type error! ret = %d", __func__, ret);
        } else {
            for (i=0; i<length; i++){
                ALOGD("%s, %.2X ", __func__, recv_data[i]);
            }

            //ACK
            if (6 == static_cast<int>(recv_data[length - 2])) {
                //ASCII--1
                readlength = length - commandlength - 2;
                for (i=0; i<readlength; i++){
                    data[i] = recv_data[commandlength + i];
                }
                ALOGD("%s, %s", __func__, data);
                break;
            }
        }
    }
    close(Fd);
    return data;
}

int profile_scan_type()
{
    int Fd = 0,i = 0,length = 0,ret = -1;
    //889003,002995C80.
    char profile_cmd[] = {0xFF,0x4D,0x0D,0x38,0x38,0x39,0x30,0x30,0x33,0x2C,0x30,0x30,0x32,0x39,0x39,0x35,0x43,0x38,0x30,0x2E};

    Fd = open(SCAN_DEVICE_NAME, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
    if (Fd == -1) {
        ALOGE("%s, open device fail! Fd = %d", __func__, Fd);
        return Fd;
    }

    ret = set_uart_parm(Fd, 115200, 8, 'N', 1);
    if (ret == -1) {
        ALOGD("%s, set uart parm fail! ret = %d", __func__, ret);
        return ret;
    }

    ret = write(Fd, profile_cmd, 20);
    if (ret < 0) {
        ALOGD("%s, profile_cmd fail! ret = %d", __func__, ret);
    } else {
        ALOGD("%s, profile_cmd successful! ret = %d", __func__, ret);
    }
    usleep(100000);
    close(Fd);
    return ret;
}