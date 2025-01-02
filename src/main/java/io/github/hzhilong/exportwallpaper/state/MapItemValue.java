package io.github.hzhilong.exportwallpaper.state;

import io.github.hzhilong.baseapp.state.setting.value.ItemValue;

import java.util.Map;

/**
 * Map数据项
 *
 * @author hzhilong
 * @version 1.0
 */
public class MapItemValue extends ItemValue<Map<String, String>> {
    public MapItemValue() {
    }

    public MapItemValue(Map<String, String> value) {
        super(value);
    }
}
