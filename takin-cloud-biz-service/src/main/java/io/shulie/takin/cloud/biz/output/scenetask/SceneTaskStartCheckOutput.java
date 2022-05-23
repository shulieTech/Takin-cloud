package io.shulie.takin.cloud.biz.output.scenetask;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author moriarty
 */
@Data
public class SceneTaskStartCheckOutput {
    private Boolean hasUnread;
    private List<FileReadInfo> fileReadInfos;

    @Data
    @Accessors(chain = true)
    public static class FileReadInfo {
        private String fileName;
        private String fileSize;
        private String readSize;
    }
}
