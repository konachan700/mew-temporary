#ifndef __MEW_UI_LHANDLERS__
#define __MEW_UI_LHANDLERS__

#include "mew.h"

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define MEW_PINCODE_MAX_SIZE 8
#define MEW_PINCODE_MAGIC_ADD 0x43

#define MEW_WINDOW_ZERO 			0x00
#define MEW_WINDOW_INITIAL_QR 		0x01
#define MEW_WINDOW_PINPAD			0x02


unsigned int mew_lvgl_lhandler(void);
unsigned int mew_ui_lhandler(void);

void mew_ui_show_window(uint32_t win);




unsigned int mew_ui_show_pinpad(void);

void mew_qrcode_set_data(uint8_t* data, uint32_t offset, uint32_t len);
void mew_display_qrcode(void);

#endif
