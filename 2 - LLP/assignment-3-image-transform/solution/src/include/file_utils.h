//
// Created by Gregory on 07.03.2025.
//

#ifndef IMAGE_TRANSFORM_FILE_UTILS_H
#define IMAGE_TRANSFORM_FILE_UTILS_H

#include <stdio.h>
#include <stdlib.h>

#include "../include/bmp.h"
#include "../include/padding.h"




FILE* open_file_read(const char* filename);
FILE* open_file_write(const char* filename);
void close_file(FILE* file, const char* filename);

enum read_status read_from_bmp_file(const char* filename, struct image* img);
enum write_status write_to_bmp_file(const char* filename, const struct image* img);

#endif //IMAGE_TRANSFORM_FILE_UTILS_H
