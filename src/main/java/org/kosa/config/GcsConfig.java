package org.kosa.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Google Cloud Storage 클라이언트를 빈으로 등록합니다.
 * GOOGLE_APPLICATION_CREDENTIALS 환경 변수에 서비스 계정 키가 지정되어 있어야 합니다.
 */
@Configuration
public class GcsConfig {

    @Bean
    public Storage storage() throws Exception {
        String cred = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
        Path p = Paths.get(cred);

        Path keyPath = p;
        if (Files.isDirectory(p)) {
            try (Stream<Path> s = Files.list(p)) {
                keyPath = s.filter(f -> f.toString().endsWith(".json"))
                        .findFirst()
                        .orElseThrow(() -> new FileNotFoundException("No .json key in " + p));
            }
        }
        try (InputStream in = Files.newInputStream(keyPath)) {
            return StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(ServiceAccountCredentials.fromStream(in))
                    .build()
                    .getService();
        }
    }

}
