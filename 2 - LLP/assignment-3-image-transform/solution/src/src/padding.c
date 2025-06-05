//
// Created by Gregory on 07.03.2025.
//

#include "../include/padding.h"



size_t get_padding(size_t width) {
    size_t bytes_per_pixel = sizeof(struct pixel);
    return (4 - (width * bytes_per_pixel) % 4) % 4;
}
