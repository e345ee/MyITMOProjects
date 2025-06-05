//
// Created by Gregory on 05.03.2025.
//

#ifndef BMP_H
#define BMP_H

#include <stdio.h>

#include "image.h"


#pragma pack(push, 1)
struct bmp_header {
    uint16_t bfType;
    uint32_t bfileSize;
    uint32_t bfReserved;
    uint32_t bOffBits;
    uint32_t biSize;
    int32_t biWidth;
    int32_t biHeight;
    uint16_t biPlanes;
    uint16_t biBitCount;
    uint32_t biCompression;
    uint32_t biSizeImage;
    int32_t biXPelsPerMeter;
    int32_t biYPelsPerMeter;
    uint32_t biClrUsed;
    uint32_t biClrImportant;
};
#pragma pack(pop)

enum read_status {
    READ_OK = 0,
    READ_ERROR = 1,
    READ_INVALID_BITS = 3,
    ERROR_NO_MEMORY =  12,
    ERROR_INVALID_ARGS = 56,
    ERROR_FILE_OPEN = 2,
    ERROR_UNKNOWN_CMD = 55,

};
enum write_status {
    WRITE_OK = 0,
    WRITE_ERROR,
    ERROR_CLOSE
};


enum read_status from_bmp(FILE *in, struct image *img);

enum write_status to_bmp(FILE *out, const struct image *img);


#endif
