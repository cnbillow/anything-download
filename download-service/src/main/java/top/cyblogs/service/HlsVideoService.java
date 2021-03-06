package top.cyblogs.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import top.cyblogs.data.SettingsData;
import top.cyblogs.ffmpeg.exec.DownloadM3U8;
import top.cyblogs.ffmpeg.listener.FFMpegListener;
import top.cyblogs.model.DownloadItem;
import top.cyblogs.model.enums.DownloadStatus;
import top.cyblogs.model.enums.DownloadType;
import top.cyblogs.utils.ServiceUtils;

import java.io.File;

/**
 * M3U8文件下载服务
 *
 * @author CY 测试通过
 */
@Slf4j
public class HlsVideoService {

    public static void download(String url, File targetFile, DownloadItem downloadStatus) {


        String name = targetFile.getName();
        int lastIndexOf = name.lastIndexOf(".");
        downloadStatus.setFileName(lastIndexOf == -1 ? name : name.substring(0, lastIndexOf));
        downloadStatus.setTargetPath(FileUtil.getCanonicalPath(targetFile));
        downloadStatus.setStatus(DownloadStatus.WAITING);
        downloadStatus.setStatusFormat("等待下载...");
        downloadStatus.setProgressFormat("0%");
        downloadStatus.setProgress(0D);
        downloadStatus.setCurrentSpeed(null);
        downloadStatus.setDownloadType(DownloadType.VIDEO);
        downloadStatus.setDownloadId(SecureUtil.md5(FileUtil.getCanonicalPath(targetFile)));
        ServiceUtils.addToList(downloadStatus);

        // 文件存在就跳过
        if (SettingsData.skipIfExists && targetFile.exists()) {
            downloadStatus.setStatusFormat("文件已存在!");
            downloadStatus.setStatus(DownloadStatus.FINISHED);
            downloadStatus.setTotalSize(FileUtil.readableFileSize(targetFile.length()));
            downloadStatus.setProgressFormat("100%");
            downloadStatus.setProgress(100D);
            return;
        }

        FileUtil.mkParentDirs(targetFile);

        DownloadM3U8.exec(url, targetFile, new FFMpegListener() {

            private long totalTime = 0;

            @Override
            public void start() {
                // 开始下载
                downloadStatus.setStatusFormat("正在下载...");
                downloadStatus.setStatus(DownloadStatus.DOWNLOADING);
                downloadStatus.setTotalSize("未知大小");
            }

            @Override
            public void progress(long current, long total) {
                this.totalTime = total;
                // 下载进度
                downloadStatus.setProgressFormat(ServiceUtils.ratioString(current, total, false));
                downloadStatus.setProgress((double) current / total * 100);
            }

            @Override
            public void over() {
                // 下载完成
                downloadStatus.setStatusFormat("下载完成!");
                downloadStatus.setStatus(DownloadStatus.FINISHED);
                downloadStatus.setProgressFormat(ServiceUtils.ratioString(totalTime, totalTime, false));
                downloadStatus.setProgress(100D);
            }
        });
    }
}
