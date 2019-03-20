#include "ui.h"
#include "debug.h"
#include "drivers/system/system.h"
#include "drivers/flash/flash.h"
#include "app/qr_code/qrcode.h"
#include "drivers/display/display.h"

#include "lv_conf.h"
#include "lvgl/lvgl.h"

#define DOTSIZE 6
#define X_OFFSET 21
#define Y_OFFSET 101

QRCode _mew_qr;
uint8_t _mew_qr_buffer[qrcode_getBufferSize(4)];
uint8_t _mew_qr_data[64];

lv_color_t _mew_gd[MEW_DISPLAY_W * DOTSIZE];

extern const lv_img_t mew_logo;

void __mew_setpoint(uint8_t x, uint8_t y) {
	uint32_t i, xp, line;
	for (i=0; i<DOTSIZE; i++) {
		xp   = (x * DOTSIZE) + X_OFFSET;
		line = (i * MEW_DISPLAY_W);
		memset((void*)(_mew_gd + (line + xp)), 0, DOTSIZE * sizeof(lv_color_t));
	}
}

void mew_qrcode_set_data(uint8_t* data, uint32_t offset, uint32_t len) {
	memcpy((void*)(_mew_qr_data + offset), data, len);
}

void mew_display_qrcode(void) {
	uint8_t x, y;
	uint32_t ypos;
	int8_t retval = qrcode_initBytes(&_mew_qr, _mew_qr_buffer, 4, 0, _mew_qr_data, 64);

    lv_obj_t * img1 = lv_img_create(lv_scr_act(), NULL);
    lv_img_set_src(img1, &mew_logo);
    lv_obj_align(img1, NULL, LV_ALIGN_IN_TOP_LEFT, 6, 6);

    lv_style_t style_txt;
    lv_style_copy(&style_txt, &lv_style_plain);
    style_txt.text.font = &lv_font_dejavu_30;
    style_txt.text.letter_space = 2;
    style_txt.text.line_space = 1;
    style_txt.text.color = LV_COLOR_HEX(0xF01010);

    lv_obj_t * label1 =  lv_label_create(lv_scr_act(), NULL);
    lv_obj_set_style(label1, &style_txt);
    lv_label_set_text(label1, "MeW HPM");
    lv_obj_set_size(label1, LV_HOR_RES - 74, 24);
    lv_obj_align(label1, NULL, LV_ALIGN_IN_TOP_LEFT, 74 + 16 + 6, 16);

    lv_style_t style_txt2;
    lv_style_copy(&style_txt2, &lv_style_plain);
    style_txt2.text.font = &lv_font_dejavu_20;
    style_txt2.text.letter_space = 2;
    style_txt2.text.line_space = 1;
    style_txt2.text.color = LV_COLOR_HEX(0x107010);

    lv_obj_t * _mew_label_pincode = lv_label_create(lv_scr_act(), NULL);
    lv_obj_set_style(_mew_label_pincode, &style_txt2);
    lv_label_set_text(_mew_label_pincode, "Scan code...");
    lv_obj_set_size(_mew_label_pincode, LV_HOR_RES - 74, 24);
    lv_obj_align(_mew_label_pincode, NULL, LV_ALIGN_IN_TOP_LEFT, 74 + 20 + 6, 50);

	lv_task_handler();

	for (y=0; y<_mew_qr.size; y++) {
		memset((void*) _mew_gd, 0xFF, (MEW_DISPLAY_W * DOTSIZE) * sizeof(lv_color_t));
		for (x=0; x<_mew_qr.size; x++) {
			if (qrcode_getModule(&_mew_qr, x, y)) {
				__mew_setpoint(x, y);
			}
		}
		ypos = (y * DOTSIZE) + Y_OFFSET;
		mew_display_flush_sync(
				0,
				ypos,
				MEW_DISPLAY_W - 1,
				ypos + (DOTSIZE - 1),
				_mew_gd);
	}
}
