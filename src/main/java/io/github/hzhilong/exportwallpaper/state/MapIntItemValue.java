package io.github.hzhilong.exportwallpaper.state;

import io.github.hzhilong.baseapp.state.setting.value.ItemValue;

import java.util.Map;

/**
 * Map数据项
 *
 * @author hzhilong
 * @version 1.0
 */
public class MapIntItemValue extends ItemValue<Map<String, Integer>> {
    public MapIntItemValue() {
    }

    public MapIntItemValue(Map<String, Integer> value) {
        super(value);
    }
}
