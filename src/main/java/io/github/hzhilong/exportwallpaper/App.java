package io.github.hzhilong.exportwallpaper;

import io.github.hzhilong.baseapp.config.AppUIConfig;
import io.github.hzhilong.baseapp.state.data.BaseAppData;
import io.github.hzhilong.exportwallpaper.page.MainFrame;

import javax.swing.*;

/**
 * GUI程序
 *
 * @author hzhilong
 * @version 1.0
 */
public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppUIConfig.init();
            BaseAppData.initBaseData();
            new MainFrame().setVisible(true);
        });
    }
}
