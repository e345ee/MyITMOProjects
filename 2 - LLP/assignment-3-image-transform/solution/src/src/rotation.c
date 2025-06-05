//
// Created by Gregory on 05.03.2025.
//

//rotation.c

#include <stdlib.h>
#include <string.h>

#include "../include/rotation.h"



enum transformation_type get_transformation_type(const char *transformation) {
    if (strcmp(transformation, "cw90") == 0) return CW90;
    if (strcmp(transformation, "ccw90") == 0) return CCW90;
    if (strcmp(transformation, "fliph") == 0) return FLIPH;
    if (strcmp(transformation, "flipv") == 0) return FLIPV;
    if (strcmp(transformation, "none") == 0) return NONE;
    return UNKNOWN;
}

struct pixel*  creat_image(struct image const pic){
    struct pixel* data = malloc(pic.width * pic.height * sizeof(struct pixel));
    if (!data) {
        return NULL;
    }
    return data;
}

struct image init_img(struct image const pic){
    struct image new_image = {0};
    new_image.width = pic.width;
    new_image.height = pic.height;
    new_image.data = creat_image(new_image);
    if (new_image.data == NULL){
        struct image zero_image = {0};
        return zero_image;}
    return new_image;
}

struct  image rev_init_img(struct image const pic){
    struct image new_image = {0};
    new_image.height = pic.width;
    new_image.width = pic.height;
    new_image.data = creat_image(new_image);
    return new_image;
}

struct image rotate(struct image source_img, enum transformation_type transformation) {
    switch (transformation) {
        case CW90:
            return cw90(source_img);
        case CCW90:
            return ccw90(source_img);
        case FLIPH:
            return fliph(source_img);
        case FLIPV:
            return flipv(source_img);
        case NONE:
        default:
            return copy_image(source_img);
    }
}

struct image copy_image(struct image const pic) {
    struct image new_image = init_img(pic);
    if (new_image.data == NULL){
        return new_image;
    }
    memcpy(new_image.data, pic.data, new_image.width * new_image.height * sizeof(struct pixel));
    return new_image;
}



struct image cw90(struct image const pic) {
    struct image new_image = rev_init_img(pic);
    if (new_image.data == NULL){
        return new_image;
    }

    for (uint32_t y1 = 0; y1 < pic.height; ++y1) {
        for (uint32_t x1 = 0; x1 < pic.width; ++x1) {
            struct pixel current_pixel = pic.data[y1 * pic.width + x1];
            uint32_t x2 = pic.height - y1 - 1;
            uint32_t y2 = x1;
            new_image.data[y2 * new_image.width + x2] = current_pixel;
        }
    }
    return new_image;
}

struct image ccw90(struct image const pic) {
    struct image new_image = rev_init_img(pic);
    if (new_image.data == NULL){
        return new_image;
    }

    for (uint32_t y1 = 0; y1 < pic.height; ++y1) {
        for (uint32_t x1 = 0; x1 < pic.width; ++x1) {
            struct pixel cur_pixel = pic.data[y1 * pic.width + x1];
            uint32_t x2 = y1;
            uint32_t y2 = pic.width - x1 - 1;
            new_image.data[y2 * new_image.width + x2] = cur_pixel;
        }
    }
    return new_image;
}

struct image fliph(struct image const pic) {
    struct image new_image = init_img(pic);
    if (new_image.data == NULL){
        return new_image;
    }

    for (uint32_t y1 = 0; y1 < pic.height; ++y1) {
        for (uint32_t x1 = 0; x1 < pic.width; ++x1) {
            uint32_t x2 = pic.width - 1 - x1;
            new_image.data[y1 * new_image.width + x2] = pic.data[y1 * pic.width + x1];
        }
    }
    return new_image;
}

struct image flipv(struct image const pic) {
    struct image new_image = init_img(pic);
    if (new_image.data == NULL){
        return new_image;
    }

    for (uint32_t y1 = 0; y1 < pic.height; ++y1) {
        for (uint32_t x1 = 0; x1 < pic.width; ++x1) {
            uint32_t y2 = pic.height - 1 - y1;
            new_image.data[y2 * new_image.width + x1] = pic.data[y1 * pic.width + x1];
        }
    }
    return new_image;
}

