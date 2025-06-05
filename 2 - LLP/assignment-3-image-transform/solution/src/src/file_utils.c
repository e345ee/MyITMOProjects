//
// Created by Gregory on 07.03.2025.
//


#include <stdio.h>

#include "../include/bmp.h"
#include "../include/file_utils.h"

FILE* open_file_read(const char* filename) {
    FILE* f = fopen(filename, "rb");
    return f;
}

FILE* open_file_write(const char* filename) {
    FILE* f = fopen(filename, "wb");
    return f;
}

void close_file(FILE* file, const char* filename) {
    if (fclose(file) != 0) {
        fprintf(stderr, "Warning: couldn't properly close file \"%s\".\n", filename);
    }
}

enum read_status read_from_bmp_file(const char* filename, struct image* img) {
    FILE* file = open_file_read(filename);
    if (!file) {
        return ERROR_FILE_OPEN;
    }

    enum read_status status = from_bmp(file, img);
    close_file(file, filename);
    return status;
}

enum write_status write_to_bmp_file(const char* filename, const struct image* img) {
    FILE* file = open_file_write(filename);
    if (!file) {
        return WRITE_ERROR;
    }

    enum write_status status = to_bmp(file, img);
    close_file(file, filename);
    return status;
}
