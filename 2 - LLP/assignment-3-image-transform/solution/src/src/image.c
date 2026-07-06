//
// Created by Gregory on 05.03.2025.
//

//image.c
#include <stdlib.h>

#include "../include/image.h"


void image_free(struct image *img) {

    free(img -> data);
    img->data = NULL;

}
