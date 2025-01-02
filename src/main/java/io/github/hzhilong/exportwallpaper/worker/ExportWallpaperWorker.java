package io.github.hzhilong.exportwallpaper.worker;

import io.github.hzhilong.base.bean.BuResult;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.baseapp.state.setting.AppSettingItem;
import io.github.hzhilong.exportwallpaper.bean.ExportedImg;
import io.github.hzhilong.exportwallpaper.bean.Resolution;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 导出壁纸的线程
 *
 * @author hzhilong
 * @version 1.0
 */
public class ExportWallpaperWorker extends SwingWorker<List<BuResult<ExportedImg>>, BuResult<ExportedImg>> {

    private static final Logger log = LoggerFactory.getLogger(ExportWallpaperWorker.class);

    // 文件最小限制
    private static final int FILE_SIZE_MIN = 20 * 1024;

    private final String wallpaperDir;
    private final String exportedDir;
    private final AppSettingItem<Map<String, String>> exportedNameSettingItem;
    private final AppSettingItem<Map<String, Integer>> exportedNameNumSettingItem;
    private final Map<String, String> exportedName;
    private final Map<String, Integer> exportedNameNum;
    private final Resolution minResolution;

    public ExportWallpaperWorker(String wallpaperDir, String exportedDir, AppSettingItem<Map<String, String>> exportedName, AppSettingItem<Map<String, Integer>> exportedNameNum, Resolution minResolution) {
        this.wallpaperDir = wallpaperDir;
        this.exportedDir = exportedDir;
        this.exportedNameSettingItem = exportedName;
        this.exportedName = exportedName.getValue();
        this.exportedNameNumSettingItem = exportedNameNum;
        this.exportedNameNum = exportedNameNum.getValue();
        this.minResolution = minResolution;
    }

    @Override
    protected List<BuResult<ExportedImg>> doInBackground() throws Exception {
        List<BuResult<ExportedImg>> results = new ArrayList<>();
        File[] wallpaperFiles = new File(wallpaperDir).listFiles();
        if (wallpaperFiles != null) {
            for (File srcFile : wallpaperFiles) {
                BuResult<ExportedImg> result = null;
                String srcFileName = srcFile.getName();
                try {
                    if (exportedName.containsKey(srcFileName)) {
                        String savedFilePath = exportedName.get(srcFileName);
                        File savedFile = new File(savedFilePath);
                        if (savedFilePath.startsWith(exportedDir + File.separator) && savedFile.exists()) {
                            result = buildFailResult("已导出过该壁纸", srcFile);
                            continue;
                        }
                    }
                    if (srcFile.isDirectory()) {
                        result = buildFailResult("非文件", srcFile);
                    } else if (srcFile.length() < FILE_SIZE_MIN) {
                        result = buildFailResult("文件过小，< 20kb", srcFile);
                    } else {
                        int width;
                        int height;
                        try {
                            BufferedImage bufferedImage = ImageIO.read(srcFile);
                            width = bufferedImage.getWidth();
                            height = bufferedImage.getHeight();
                        } catch (Exception e) {
                            result = buildFailResult("非图片文件", srcFile);
                            continue;
                        }
                        if (width < minResolution.getWidth() || height < minResolution.getHeight()) {
                            result = buildFailResult("分辨率较小", srcFile);
                            continue;
                        }
                        try {
                            BasicFileAttributes attributes = Files.readAttributes(srcFile.toPath(), BasicFileAttributes.class);
                            long creationTime = attributes.creationTime().toMillis();
                            String formatedCreationTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(creationTime));
                            Integer num = exportedNameNum.getOrDefault(formatedCreationTime, 1);
                            String fileName = formatedCreationTime + "_" + num + ".jpeg";
                            File desFile = new File(exportedDir + File.separator + fileName);
                            ExportedImg img = new ExportedImg();
                            img.setSrc(srcFile);
                            img.setDes(desFile);
                            img.setW(width);
                            img.setH(height);
                            FileUtils.copyFile(srcFile, desFile);
                            exportedName.put(srcFileName, desFile.getPath());
                            exportedNameNum.put(formatedCreationTime, num + 1);
                            result = BuResult.newSuccess(img);
                        } catch (Exception e) {
                            result = buildFailResult("复制图片文件出错，" + e.getMessage(), srcFile);
                        }
                    }
                } finally {
                    if (result != null) {
                        results.add(result);
                        publish(result);
                    }
                }
            }
        }
        return results;
    }

    private BuResult<ExportedImg> buildFailResult(String msg, File src) {
        BuResult<ExportedImg> result = new BuResult<>();
        result.setMsg(msg);
        ExportedImg img = new ExportedImg();
        img.setSrc(src);
        result.setData(img);
        return result;
    }

    @Override
    protected void process(List<BuResult<ExportedImg>> results) {
        if (ListUtil.notEmpty(results)) {
            for (BuResult<ExportedImg> result : results) {
                ExportedImg img = result.getData();
                if (result.isFail()) {
                    log.info("导出壁纸失败 {} ： {}", img.getSrc().getName(), result.getMsg());
                } else {
                    log.info("导出壁纸成功 {} =>> {}  {}x{}", img.getSrc().getName(), img.getDes().getName(), img.getW(), img.getH());
                }
            }
        }
    }

    @Override
    protected void done() {
        super.done();
        exportedNameSettingItem.setValue(exportedName);
        exportedNameNumSettingItem.setValue(exportedNameNum);
        try {
            log.info("已导出{}张壁纸", get().stream().filter(BuResult::isSuccess).count());
        } catch (InterruptedException | ExecutionException e) {
            log.info(e.getMessage());
        }
    }
}
