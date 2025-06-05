//
// Created by Gregory on 05.03.2025.
//

#ifndef rotation_H
#define rotation_H

#include "image.h"

enum transformation_type {
    NONE,
    CW90,
    CCW90,
    FLIPH,
    FLIPV,
    UNKNOWN
};

struct image rotate(struct image source_img, enum transformation_type transformation);
struct image fliph(struct image  pic);
struct image flipv(struct image  pic);
struct image cw90(struct image  pic);
struct image ccw90(struct image  pic);
enum transformation_type get_transformation_type( const char *transformation);
struct image copy_image(struct image  pic);
#endif
