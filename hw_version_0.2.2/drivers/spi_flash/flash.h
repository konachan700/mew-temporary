#ifndef _MEW_FLASH_DRIVER_
#define _MEW_FLASH_DRIVER_

#include "mew.h"
#include "../hw_crypt/crypto.h"

#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/spi.h>

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define MEW_FLASH_SPI                           SPI1

#define MEW_FLASH_SPI_GPIO_PINS			GPIO5 | GPIO6 | GPIO7
#define MEW_FLASH_SPI_GPIO_PORT			GPIOA
#define MEW_FLASH_SPI_GPIO_AF_NUMBER		GPIO_AF5

#define MEW_FLASH_GPIO_PORT_WP                  GPIOB
#define MEW_FLASH_GPIO_PIN_WP                   GPIO8

#define MEW_FLASH_GPIO_PORT_HOLD                GPIOC
#define MEW_FLASH_GPIO_PIN_HOLD                 GPIO0

#define MEW_FLASH_GPIO_PORT_CS                  GPIOC
#define MEW_FLASH_GPIO_PIN_CS                   GPIO1

#define MEW_FLASH_SECTOR_SIZE                   2048

#define MEW_PR_NAME_MAX_SIZE                    128
#define MEW_PR_LOGIN_MAX_SIZE                   128
#define MEW_PR_URL_MAX_SIZE                     256

// как сделать индекс:
// читаем первые 64к от сектора в ССМ, вторые 64к выбрасываем, ибо мало оперативки для стирания страницы. 
// Там лежат связи. То есть всего может быть 16384 паролей, чего вполне достаточно.

typedef struct {
    uint16_t parent;
    uint16_t child;
} mew_records_relation;

typedef struct {
    unsigned char sha256[MEW_HASH_SIZE];
    unsigned int state;
    unsigned int password_id;
    unsigned int folder_id;
    unsigned int sorting_id;
    unsigned char display_name[MEW_PR_NAME_MAX_SIZE];
    unsigned char login[MEW_PR_LOGIN_MAX_SIZE];
    unsigned char url[MEW_PR_LOGIN_MAX_SIZE];
    uint64_t password_pointer;
    
} mew_password_record;
//#if sizeof(mew_password_record) >= MEW_FLASH_SECTOR_SIZE
//#error "mew_password_record too large"
//#endif

unsigned int mew_spi_flash_init(void);
void mew_spi_flash_xfer(uint8_t* buffer_read, uint16_t count_read, uint8_t* buffer_write, uint16_t count_write);

void mew_flash_get_id(uint8_t* buf);

#endif