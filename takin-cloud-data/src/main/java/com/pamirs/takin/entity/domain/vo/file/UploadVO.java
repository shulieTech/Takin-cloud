package com.pamirs.takin.entity.domain.vo.file;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.web.multipart.MultipartFile;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;

/**
 * TODO
 *
 * @author 张天赐
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UploadVO extends ContextExt {
    List<MultipartFile> fileList;
}
