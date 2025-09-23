package org.kosa.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "file.upload")
@Configuration
@Data
@Component
public class FilesUploadConfig {
    @NotBlank
    private String dir;
    private String maxSize;
    private String allowedExtensions;

    public String[] getAllowedExtensionsArray() {
        return allowedExtensions.split(",");
    }
}
