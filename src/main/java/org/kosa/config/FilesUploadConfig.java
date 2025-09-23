package org.kosa.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Data
@Component
public class FilesUploadConfig {
    private String dir;
    private String maxSize;
    private String allowedExtensions;

    public String[] getAllowedExtensionsArray() {
        return allowedExtensions.split(",");
    }
}
