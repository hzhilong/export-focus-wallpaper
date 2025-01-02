package io.github.hzhilong.exportwallpaper.log;

import io.github.hzhilong.baseapp.component.LogArea;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;

/**
 * 自定义日志输出
 *
 * @author hzhilong
 * @version 1.0
 */
public class PrintableAppender extends ConsoleAppender {

    private static LogArea processingLogger = null;

    @Override
    public void append(LoggingEvent event) {
        SwingUtilities.invokeLater(() -> {
            LogArea logger = getProcessingLogger();
            if (logger != null) {
                logger.append(event.getMessage() + "\n");
                logger.setCaretPosition(logger.getDocument().getLength());
            }
        });
        super.append(event);
    }

    public static synchronized LogArea getProcessingLogger() {
        return processingLogger;
    }

    public static synchronized void setProcessingLogger(LogArea logArea) {
        processingLogger = logArea;
    }
}
