#include <stddef.h>
#include <stdint.h>
#include <stdio.h>



#include "./include/mem.h"
#include "./include/mem_internals.h"


static struct block_header* get_header_from_contents(void* contents) {
    return (struct block_header*)(((uint8_t*)contents) - offsetof(struct block_header, contents));
}

static int test_allocation(void) {
    void* heap = heap_init(0);
    if (!heap) {
        printf("Test Allocation: FAIL (heap_init failed)\n");
        return 0;
    }
    void* ptr = _malloc(1024);
    if (!ptr) {
        printf("Test Allocation: FAIL (_malloc failed)\n");
        heap_term();
        return 0;
    }
    _free(ptr);
    heap_term();
    printf("Test Allocation: PASS\n");
    return 1;
}

static int test_single_free(void) {
    void* heap = heap_init(0);
    if (!heap) {
        printf("Test Single Free: FAIL (heap_init failed)\n");
        return 0;
    }
    void* block1 = _malloc(1024);
    void* block2 = _malloc(1024);
    if (!block1 || !block2) {
        printf("Test Single Free: FAIL (_malloc failed)\n");
        heap_term();
        return 0;
    }
    _free(block2);
    if (get_header_from_contents(block1)->is_free) {
        printf("Test Single Free: FAIL (block1 should not be free)\n");
        heap_term();
        return 0;
    }
    if (!get_header_from_contents(block2)->is_free) {
        printf("Test Single Free: FAIL (block2 should be free)\n");
        heap_term();
        return 0;
    }
    heap_term();
    printf("Test Single Free: PASS\n");
    return 1;
}

static int test_double_free(void) {
    void* heap = heap_init(0);
    if (!heap) {
        printf("Test Double Free: FAIL (heap_init failed)\n");
        return 0;
    }
    void* block1 = _malloc(1024);
    void* block2 = _malloc(1024);
    void* block3 = _malloc(1024);
    if (!block1 || !block2 || !block3) {
        printf("Test Double Free: FAIL (_malloc failed)\n");
        heap_term();
        return 0;
    }
    _free(block2);
    if (get_header_from_contents(block1)->is_free) {
        printf("Test Double Free: FAIL (block1 should not be free after freeing block2)\n");
        heap_term();
        return 0;
    }
    if (!get_header_from_contents(block2)->is_free) {
        printf("Test Double Free: FAIL (block2 should be free after freeing it)\n");
        heap_term();
        return 0;
    }
    if (get_header_from_contents(block3)->is_free) {
        printf("Test Double Free: FAIL (block3 should not be free before freeing it)\n");
        heap_term();
        return 0;
    }
    _free(block3);
    if (get_header_from_contents(block1)->is_free) {
        printf("Test Double Free: FAIL (block1 should not be free after freeing block3)\n");
        heap_term();
        return 0;
    }
    if (!get_header_from_contents(block2)->is_free) {
        printf("Test Double Free: FAIL (block2 should remain free after freeing block3)\n");
        heap_term();
        return 0;
    }
    if (!get_header_from_contents(block3)->is_free) {
        printf("Test Double Free: FAIL (block3 should be free after freeing it)\n");
        heap_term();
        return 0;
    }
    heap_term();
    printf("Test Double Free: PASS\n");
    return 1;
}

static int test_region_extend(void) {
    void* heap = heap_init(0);
    if (!heap) {
        printf("Test Region Extend: FAIL (heap_init failed)\n");
        return 0;
    }
    void* block1 = _malloc(1024);
    void* block2 = _malloc(1024);
    if (!block1 || !block2) {
        printf("Test Region Extend: FAIL (_malloc failed)\n");
        heap_term();
        return 0;
    }
    debug_heap(stdout, HEAP_START);
    void* block3 = _malloc(4096);
    if (!block3) {
        printf("Test Region Extend: FAIL (_malloc for block3 failed)\n");
        heap_term();
        return 0;
    }
    debug_heap(stdout, HEAP_START);
    heap_term();
    printf("Test Region Extend: PASS\n");
    return 1;
}

static int test_region_non_extend(void) {

    void* heap = heap_init(0);
    if (!heap) {
        printf("Test Region Non-Extend: FAIL (heap_init failed)\n");
        return 0;
    }
    void* block1 = _malloc(768);
    void* block2 = _malloc(1024);
    if (!block1 || !block2) {
        printf("Test Region Non-Extend: FAIL (_malloc failed)\n");
        heap_term();
        return 0;
    }
    _free(block1);
    void* block3 = _malloc(1024);
    if (!block3) {
        printf("Test Region Non-Extend: FAIL (_malloc for block3 failed)\n");
        heap_term();
        return 0;
    }
    heap_term();
    printf("Test Region Non-Extend: PASS\n");
    return 1;
}

void run_tests(void) {
    int failed = 0;
    if (!test_allocation()) failed++;
    if (!test_single_free()) failed++;
    if (!test_double_free()) failed++;
    if (!test_region_extend()) failed++;
    if (!test_region_non_extend()) failed++;

    if (failed == 0) {
        printf("All tests passed.\n");
    } else {
        printf("%d test(s) failed.\n", failed);
    }
}

