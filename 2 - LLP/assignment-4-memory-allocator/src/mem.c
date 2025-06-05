#define _DEFAULT_SOURCE

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "./include/mem_internals.h"
#include "./include/mem.h"
#include "./include/util.h"

void debug_block(struct block_header* b, const char* fmt, ... );
void debug(const char* fmt, ... );

extern inline block_size size_from_capacity( block_capacity cap );
extern inline block_capacity capacity_from_size( block_size sz );

static bool            block_is_big_enough( size_t query, struct block_header* block ) { return block->capacity.bytes >= query; }
static size_t          pages_count   ( size_t mem )                      { return mem / getpagesize() + ((mem % getpagesize()) > 0); }
static size_t          round_pages   ( size_t mem )                      { return getpagesize() * pages_count( mem ) ; }

static void block_init( void* restrict addr, block_size block_sz, void* restrict next ) {
    *((struct block_header*)addr) = (struct block_header) {
            .next = next,
            .capacity = capacity_from_size(block_sz),
            .is_free = true
    };
}

static size_t region_actual_size( size_t query ) { return size_max( round_pages( query ), REGION_MIN_SIZE ); }

extern inline bool region_is_invalid( const struct region* r );



static void* map_pages(void const* addr, size_t length, int additional_flags) {
    return mmap( (void*) addr, length, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS | additional_flags , -1, 0 );
}

/*  аллоцировать регион памяти и инициализировать его блоком */
static size_t compute_required_bytes(size_t query){
    return region_actual_size(offsetof(struct block_header, contents) + query);}


static void* attempt_map(const void *addr, size_t length){
    void *mapped = map_pages(addr, length, MAP_FIXED_NOREPLACE);
    int8_t  zero_flag = 0;
    if (mapped == MAP_FAILED) {
        mapped = map_pages(addr, length, zero_flag);}
    return mapped;
}

static struct region finalize_region(void *mapped_addr, size_t length, const void *requested_addr){
    if (mapped_addr == MAP_FAILED) { return (struct region)REGION_INVALID; }
    struct region ac_reg = {};
    ac_reg.addr = mapped_addr;
    ac_reg.size = length;
    ac_reg.extends = (mapped_addr == requested_addr);
    block_init(ac_reg.addr, (block_size){ .bytes = length }, NULL);
    return ac_reg;
}

static struct region alloc_region(const void *addr, size_t query){
    size_t needed_bytes = compute_required_bytes(query);

    void *new_address = attempt_map(addr, needed_bytes);
    struct region alloc_reg = finalize_region(new_address, needed_bytes, addr);

    return alloc_reg;
}

static void* block_after( struct block_header const* block )         ;

void* heap_init( size_t initial ) {
    const struct region region = alloc_region( HEAP_START, initial );
    if ( region_is_invalid(&region) ) return NULL;

    return region.addr;
}

/*  освободить всю память, выделенную под кучу */


static int has_single_block(struct block_header *block) {
    return (!block->next && block  );
}

static void unmap_region(struct block_header *start, size_t size) {
    munmap((void*)start, size);
}

static void unmap_single_block(struct block_header *only_block) {
    size_t size = size_from_capacity(only_block->capacity).bytes;
    unmap_region(only_block, size);
}

//Проверка на разрыв между регионами
static int region_boundary_reached(struct block_header *block, struct block_header *block_next) {
    if (!block || !block_next) return 1;
    return (block_next != block_after(block));
}

void heap_term() {
    size_t accumulated_size = 0;

    struct block_header* block = (struct block_header*)HEAP_START;
    struct block_header* region_start = block;
    struct block_header* block_next = block;


    if (has_single_block(block)) {
        unmap_single_block(block);
        return;
    }

    while (block_next) {
        block = block_next;
        block_next = block->next;
        accumulated_size += size_from_capacity(block->capacity).bytes;

        if (region_boundary_reached(block, block_next)) {
            unmap_region(region_start, accumulated_size);
            region_start = block_next;
            accumulated_size = 0;
        }
    }
}


#define BLOCK_MIN_CAPACITY 32

/*  --- Разделение блоков (если найденный свободный блок слишком большой )--- */

static bool block_splittable( struct block_header* restrict block, size_t query) {
    return block-> is_free && query + offsetof( struct block_header, contents ) + BLOCK_MIN_CAPACITY <= block->capacity.bytes;
}

static block_capacity determine_capacity(size_t query) {
    block_capacity size;
    if (query > BLOCK_MIN_CAPACITY) {
        size.bytes = query;
    } else {
        size.bytes = BLOCK_MIN_CAPACITY;
    }
    return size;
}

static void perform_split(struct block_header* block, block_capacity size) {
    block_size size1 = { size_from_capacity(size).bytes };
    block_size size2 = { block->capacity.bytes - size.bytes };

    void *addr1 = (void*)block;
    void *addr2 = block->contents + size.bytes;
    void *last_block = block->next;

    block_init(addr1, size1, addr2);
    block_init(addr2, size2, last_block);
}

static bool split_if_too_big(struct block_header* block, size_t query) {
    if (!block_splittable(block, query)) {
        return false;
    }
    block_capacity chosen_capacity = determine_capacity(query);
    perform_split(block, chosen_capacity);
    return true;
}



/*  --- Слияние соседних свободных блоков --- */

static void* block_after( struct block_header const* block )              {
    return  (void*) (block->contents + block->capacity.bytes);
}
static bool blocks_continuous (
        struct block_header const* fst,
        struct block_header const* snd ) {
    return (void*)snd == block_after(fst);
}

static bool mergeable(struct block_header const* restrict fst, struct block_header const* restrict snd) {
    return fst->is_free && snd->is_free && blocks_continuous( fst, snd ) ;
}



static bool try_merge_with_next( struct block_header* block ) {

    if(block->next && mergeable(block, block->next)){


        size_t next_capacity_bytes = size_from_capacity(block->next->capacity).bytes;
        size_t old_capacity = block->capacity.bytes;
        size_t new_capacity = old_capacity + next_capacity_bytes;

        block->capacity.bytes = new_capacity;

        struct block_header* next_of_next = block->next->next;
        block->next = next_of_next;

        return true;
    }
    else{
        return false;
    }
}


/*  --- ... ecли размера кучи хватает --- */

struct block_search_result {
    enum {BSR_FOUND_GOOD_BLOCK, BSR_REACHED_END_NOT_FOUND, BSR_CORRUPTED} type;
    struct block_header* block;
};


static struct block_search_result find_good_or_last  ( struct block_header* restrict block, size_t sz )    {


    if(!block) return (struct block_search_result) {BSR_CORRUPTED, block};


    struct block_header *last = block;
    while (block){

        while(try_merge_with_next(block)) {     }

        if(block->is_free && block_is_big_enough(sz, block)){

            return (struct block_search_result) { BSR_FOUND_GOOD_BLOCK, block };
        }
        last = block;
        block = block->next;
    }
    return (struct block_search_result) {
            .type = BSR_REACHED_END_NOT_FOUND,
            .block = last};
}

/*  Попробовать выделить память в куче начиная с блока `block` не пытаясь расширить кучу
 Можно переиспользовать как только кучу расширили. */
static struct block_search_result try_memalloc_existing(size_t query, struct block_header* block) {

    if (block == NULL) {
        return (struct block_search_result){ .type = BSR_CORRUPTED, .block = NULL };
    }

    struct block_search_result result = find_good_or_last(block, query);
    if (result.type != BSR_FOUND_GOOD_BLOCK) {
        return result;
    }

    split_if_too_big(result.block, query);
    result.block->is_free = false;

    return result;
}




static struct block_header* grow_heap( struct block_header* restrict last, size_t query ) {
    if(!last) return NULL;
    struct region new_reg = alloc_region(block_after(last), query);

    if(region_is_invalid(&new_reg)) return NULL;

    last->next = new_reg.addr;

    if(try_merge_with_next(last)) return last;
    return new_reg.addr;
}

/*  Реализует основную логику malloc и возвращает заголовок выделенного блока */
static struct block_header* memalloc(size_t query, struct block_header* heap_start) {
    struct block_search_result result = try_memalloc_existing(query, heap_start);
    if (result.type == BSR_CORRUPTED) {
        return NULL;
    }
    while (result.type == BSR_REACHED_END_NOT_FOUND) {
        struct block_header* new_block = grow_heap(result.block, query);
        if (new_block == NULL) {
            return NULL;
        }
        result = try_memalloc_existing(query, new_block);
    }
    return result.block;
}

void* _malloc( size_t query ) {
    struct block_header* const addr = memalloc( query, (struct block_header*) HEAP_START );
    if (addr) return addr->contents;
    else return NULL;
}

static struct block_header* block_get_header(void* contents) {
    return (struct block_header*) (((uint8_t*)contents)-offsetof(struct block_header, contents));
}

void _free( void* mem ) {
    if (!mem) return ;
    struct block_header* header = block_get_header( mem );
    header->is_free = true;
    /*  ??? */
    while(try_merge_with_next(header));
}
