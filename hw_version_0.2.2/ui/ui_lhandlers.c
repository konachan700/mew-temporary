#include "ui.h"
#include "drivers/system/system.h"
#include "debug.h"

#include "lv_conf.h"
#include "lvgl/lvgl.h"

static volatile uint32_t _mew_lvgl_lhandler_time = 0;

unsigned int mew_lvgl_lhandler(void) {
    if (_mew_lvgl_lhandler_time < mew_get_millis()) {
        lv_task_handler();
        _mew_lvgl_lhandler_time = mew_get_millis() + 50;
    }
    return 0;
}
