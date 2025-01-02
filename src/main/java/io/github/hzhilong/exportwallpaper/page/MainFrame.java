package io.github.hzhilong.exportwallpaper.page;

import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.component.BaseMainFrame;
import io.github.hzhilong.baseapp.component.LogArea;
import io.github.hzhilong.baseapp.menu.BaseAppearanceMenu;
import io.github.hzhilong.baseapp.menu.BaseHelpMenu;
import io.github.hzhilong.baseapp.state.setting.AppSettingItem;
import io.github.hzhilong.baseapp.state.setting.value.StringItemValue;
import io.github.hzhilong.exportwallpaper.bean.Resolution;
import io.github.hzhilong.exportwallpaper.log.PrintableAppender;
import io.github.hzhilong.exportwallpaper.state.MapIntItemValue;
import io.github.hzhilong.exportwallpaper.state.MapItemValue;
import io.github.hzhilong.exportwallpaper.worker.ExportWallpaperWorker;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 主窗口
 *
 * @author hzhilong
 * @version 1.0
 */
public class MainFrame extends BaseMainFrame {

    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

    private static final String APP_ICON = "/icon/icon.svg";
    private AppSettingItem<String> wallpaperDir;
    private AppSettingItem<String> exportDir;
    private AppSettingItem<Map<String, String>> exportedName;
    private AppSettingItem<Map<String, Integer>> exportedNameNum;

    private JFileChooser choExportDir;
    private JTextField txtExportDir;
    private JComboBox<Resolution> minResolution;

    public MainFrame() {
        super(APP_ICON, new Dimension(700, 550));
        init();
    }

    public void init() {
        wallpaperDir = new AppSettingItem<>("wallpaperDir", new StringItemValue(),
                "壁纸目录");
        exportDir = new AppSettingItem<>("exportDir", new StringItemValue(),
                "导出目录");
        exportedName = new AppSettingItem<>("exportedName", new MapItemValue(new HashMap<>()),
                "导出的文件名信息");
        exportedNameNum = new AppSettingItem<>("exportedNameNum", new MapIntItemValue(new HashMap<>()),
                "导出的文件名序号信息");

        setLayout(new MigLayout("nogrid, insets 10px, gapy 14, fill"));

        add(new JLabel("导出目录："));
        txtExportDir = new JTextField();
        txtExportDir.setEditable(false);
        txtExportDir.setText(exportDir.getValue());
        add(txtExportDir, "w 300px");
        JButton btnChooseDir = new JButton("选择");
        String btnGapAfter = "gapafter 10px";
        add(btnChooseDir, btnGapAfter);
        JButton btnOpenExportDir = new JButton("打开");
        add(btnOpenExportDir, "wrap");
        JButton btnOpenWallDir = new JButton("打开聚焦壁纸目录");
        add(btnOpenWallDir, btnGapAfter);
        JButton btnExport = new JButton("导出壁纸");
        add(btnExport, btnGapAfter);
        JButton btnClearLog = new JButton("清空日志");
        add(btnClearLog, btnGapAfter);
        add(new JLabel("最小分辨率："), "gapleft push");
        minResolution = new JComboBox<>();
        minResolution.addItem(new Resolution(600, 480));
        minResolution.addItem(new Resolution(1366, 768));
        minResolution.addItem(new Resolution(1920, 1080));
        add(minResolution, "wrap");
        LogArea logArea = new LogArea();
        add(logArea, "grow, push");
        PrintableAppender.setProcessingLogger(logArea);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new BaseAppearanceMenu(this, APP_ICON));
        menuBar.add(new BaseHelpMenu(this, APP_ICON));
        this.setJMenuBar(menuBar);

        choExportDir = new JFileChooser();
        choExportDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        btnOpenExportDir.addActionListener(e -> openExportDir());
        btnChooseDir.addActionListener(e -> chooseExportDir());
        btnOpenWallDir.addActionListener(e -> openWallDir());
        btnExport.addActionListener(e -> exportWallpaper());
        btnClearLog.addActionListener(e -> logArea.clear());
    }

    @Override
    protected void initApp() {
        log.info("查询锁屏壁纸目录…");
        String wallpaperDirSettingValue = wallpaperDir.getValue();
        if (StringUtils.notEmpty(wallpaperDirSettingValue)) {
            log.info("锁屏壁纸目录：{}", wallpaperDirSettingValue);
        } else {
            String userHome = System.getProperty("user.home");
            String packagePath = userHome + "\\AppData\\Local\\Packages";
            File packageDir = new File(packagePath);
            File[] packageFiles = packageDir.listFiles();
            String wappDirPath = null;
            if (packageFiles != null) {
                for (File file : packageFiles) {
                    if (file.isDirectory()) {
                        if (file.getName().contains("Microsoft.Windows.ContentDeliveryManager")) {
                            wappDirPath = packagePath + "\\" + file.getName() + "\\LocalState\\Assets";
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isEmpty(wappDirPath)) {
                log.info("未找到锁屏壁纸目录");
                log.info("可能未启用聚焦锁屏壁纸");
                for (Component component : getContentPane().getComponents()) {
                    if ((component instanceof JButton) || (component instanceof JTextField)) {
                        component.setEnabled(false);
                    }
                }
            } else {
                log.info("已找到锁屏壁纸目录：{}", wappDirPath);
                wallpaperDir.setValue(wappDirPath);
            }
        }
    }

    private void chooseExportDir() {
        // 读取保存的值
        String exportDirValue = exportDir.getValue();
        if (StringUtils.notEmpty(exportDirValue)) {
            File dir = new File(exportDirValue);
            if (dir.exists() && dir.isDirectory()) {
                choExportDir.setCurrentDirectory(dir);
            }
        }
        int flag = choExportDir.showDialog(MainFrame.this, "选择");
        if (flag == JFileChooser.APPROVE_OPTION) {
            String path = choExportDir.getSelectedFile().getPath();
            exportDir.setValue(path);
            txtExportDir.setText(path);
        }
    }

    private void openDir(String dir) {
        try {
            Desktop.getDesktop().open(new File(dir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openWallDir() {
        openDir(wallpaperDir.getValue());
    }

    private void openExportDir() {
        String exportDirValue = exportDir.getValue();
        if (StringUtils.isEmpty(exportDirValue)) {
            JOptionPane.showMessageDialog(MainFrame.this,
                    "请先选择导出目录", "提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            openDir(exportDir.getValue());
        }
    }

    private void exportWallpaper() {
        String exportDirValue = exportDir.getValue();
        if (StringUtils.isEmpty(exportDirValue)) {
            JOptionPane.showMessageDialog(MainFrame.this,
                    "请先选择导出目录", "提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            log.info("导出中...");
            new ExportWallpaperWorker(wallpaperDir.getValue(), exportDirValue,
                    exportedName, exportedNameNum, (Resolution) minResolution.getSelectedItem()).execute();
        }
    }
}
