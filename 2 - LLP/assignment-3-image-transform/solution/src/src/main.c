#include <errno.h>

#include "../include/bmp.h"
#include "../include/file_utils.h"
#include "../include/rotation.h"


int main(int argc, char* argv[]) {
    if (argc != 4) {
        return ERROR_INVALID_ARGS;
    }

    enum transformation_type transformation = get_transformation_type(argv[3]);
    if (transformation == UNKNOWN ){
        return ERROR_UNKNOWN_CMD;
    }

    struct image original_img;
    enum read_status bmp_read_status = read_from_bmp_file(argv[1], &original_img);
    if (bmp_read_status == ERROR_NO_MEMORY) {
        return ERROR_NO_MEMORY;
    } else if (bmp_read_status != READ_OK) {
        return ERROR_FILE_OPEN;
    }

    struct image new_img = rotate(original_img, transformation);
    if (!new_img.data) {
        image_free(&original_img);
        return ENOMEM;
    }

    enum write_status bmp_write_status = write_to_bmp_file(argv[2], &new_img);

    if (bmp_write_status != WRITE_OK) {
        image_free(&original_img);
        image_free(&new_img);
        return 1;
    }

    image_free(&original_img);
    image_free(&new_img);


    return 0;
}


