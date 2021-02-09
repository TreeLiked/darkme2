package com.treeliked.darkme2.job;

import java.util.List;
import java.util.stream.Collectors;

import com.iutr.shared.model.Result;
import com.treeliked.darkme2.dao.IFileMapper;
import com.treeliked.darkme2.dataobject.IFilePO;
import com.treeliked.darkme2.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 定时删除无效的文件
 *
 * @author lss
 * @date 2021/2/8, 周一
 */
@Component
@Slf4j
public class TimedDeleteExpiredFileTask {

    @Autowired
    private IFileMapper fileMapper;

    @Autowired
    private FileService fileService;

    /**
     * 定时删除失效的文件，每半小时执行一次
     */
    @Scheduled(initialDelay = 60 * 1000, fixedRate = 1800 * 1000)
    public void deleteExpiredFile() {

        List<IFilePO> filePos = fileMapper.selectAll();
        if (CollectionUtils.isEmpty(filePos)) {
            log.info("定时任务：没有文件需要删除");
            return;
        }
        // 当前毫秒
        long currentMs = System.currentTimeMillis();
        // 收集过期的文件Id
        List<String> deleteFileIds = filePos.stream().filter(file -> {
            Integer saveDays = file.getSaveDays();
            if (saveDays == null || saveDays > 7) {
                // 大于7天的永久保留
                return false;
            }
            long last = saveDays * 3600 * 1000 * 24;
            return file.getGmtCreated().getTime() + last <= currentMs;
        }).map(IFilePO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deleteFileIds)) {
            log.info("定时任务：收集完成，没有文件过期");
            return;
        }
        log.info("定时任务：收集完成，以下文件需要清理");
        deleteFileIds.forEach(fileId -> {
            Result<String> res = fileService.deleteFile(null, fileId, true);
            if (res.isSuccess()) {
                log.info("文件ID: [{}] , 删除成功 ... ", fileId);
            } else {
                log.info("文件ID: [{}] , 删除失败 ... ", fileId);
            }
        });

    }
}
