#include "ui.h"
#include "drivers/system/system.h"
#include "drivers/flash/flash.h"
#include "debug.h"

#include "lv_conf.h"
#include "lvgl/lvgl.h"

static volatile uint32_t _mew_current_window = MEW_WINDOW_ZERO;
static volatile uint32_t _mew_lvgl_lhandler_time = 0;

void mew_ui_show_window(uint32_t win) {
	_mew_current_window = win;
}

unsigned int mew_ui_lhandler(void) {
	switch (_mew_current_window) {
	case MEW_WINDOW_INITIAL_QR:
		mew_display_qrcode();
		break;
	case MEW_WINDOW_PINPAD:

		break;
	}

	_mew_current_window = MEW_WINDOW_ZERO;
	return 0;
}

unsigned int mew_lvgl_lhandler(void) {
    if (_mew_lvgl_lhandler_time < mew_get_millis()) {
        lv_task_handler();
        _mew_lvgl_lhandler_time = mew_get_millis() + 50;
    }
    return 0;
}
