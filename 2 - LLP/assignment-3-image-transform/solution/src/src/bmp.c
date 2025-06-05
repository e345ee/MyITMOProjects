//
// Created by Gregory on 05.03.2025.
//

// bmp.c
#include <stdio.h>
#include <stdlib.h>

#include "../include/bmp.h"
#include "../include/padding.h"

#define BMP_KEY_NUMBER    0x4D42
#define BMP_FILE_HEADER_SIZE 54
#define BMP_DIB_HEADER_SIZE 40
#define BMP_BITS_PER_PIXEL  24
#define BMP_NO_COMPRESSION  0


static enum read_status read_bmp_header(FILE* in, struct bmp_header* header) {

    fread(header, sizeof(*header), 1, in);
    return READ_OK;
}


static enum read_status read_bmp_pixels(FILE* in, struct image* img) {
    size_t padding = get_padding(img->width);

    for (size_t i = 0; i < img->height; ++i) {
        size_t row = img->height - 1 - i;
        if (fread(&img->data[row * (size_t)img->width],
                  sizeof(struct pixel),
                  (size_t)img->width,
                  in) != (size_t)img->width)
        {
            return READ_INVALID_BITS;
        }
        if (fseek(in, (long)padding, SEEK_CUR) != 0) {
            return READ_INVALID_BITS;
        }
    }

    return READ_OK;
}

enum read_status from_bmp(FILE* in, struct image* img) {

    struct bmp_header header;
    enum read_status header_status = read_bmp_header(in, &header);
    if (header_status != READ_OK) {
        return READ_INVALID_BITS;
    }

    img->width  = header.biWidth;
    img->height = header.biHeight;

    img->data = malloc((size_t)img->height * img->width * sizeof(struct pixel));
    if (!img->data) {
        return ERROR_NO_MEMORY;
    }

    enum read_status pixel_status = read_bmp_pixels(in, img);
    if (pixel_status != READ_OK) {
        free(img->data);
        img->data = NULL;
        return pixel_status;
    }

    return READ_OK;
}


static enum write_status write_bmp_pixels(FILE* out, const struct image* img) {
    size_t padding = get_padding(img->width);

    for (size_t i = 0; i < img->height; ++i) {
        size_t row = img->height - 1 - i;

        if (fwrite(&img->data[row * img->width],
                   sizeof(struct pixel),
                   img->width,
                   out) != img->width)
        {
            return WRITE_ERROR;
        }

        for (size_t p = 0; p < padding; ++p) {
            if (fputc('\0', out) == EOF) {
                return WRITE_ERROR;
            }
        }
    }
    return WRITE_OK;
}

enum write_status to_bmp(FILE* out, const struct image* img) {
    if (!out || !img) {
        return WRITE_ERROR;
    }

    size_t pixel_data_size = img->height * (img->width * sizeof(struct pixel) + get_padding(img->width));

    struct bmp_header header = {
            .bfType = BMP_KEY_NUMBER,
            .bfileSize = BMP_FILE_HEADER_SIZE + pixel_data_size,
            .bfReserved = 0,
            .bOffBits   = BMP_FILE_HEADER_SIZE,
            .biSize     = BMP_DIB_HEADER_SIZE,
            .biWidth    = img->width,
            .biHeight   = img->height,
            .biPlanes   = 1,
            .biBitCount = BMP_BITS_PER_PIXEL,
            .biCompression = BMP_NO_COMPRESSION,
            .biSizeImage   = pixel_data_size,
            .biXPelsPerMeter = 0,
            .biYPelsPerMeter = 0,
            .biClrUsed      = 0,
            .biClrImportant = 0
    };

    if (fwrite(&header, sizeof(struct bmp_header), 1, out) != 1) {
        return WRITE_ERROR;
    }

    return write_bmp_pixels(out, img);
}

