package petdori.apiserver.global.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${s3.bucket.cloudfront_domain}")
    private String cloudFrontDomain;

    @Value("${s3.bucket.raw_dog_image_path}")
    private String dogImageUploadDirName;

    public String uploadDogImage(MultipartFile dogImage) {
        String fileName = dogImage.getOriginalFilename();
        String filePath = dogImageUploadDirName + fileName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(dogImage.getSize());

        try {
            amazonS3Client.putObject(bucket, filePath, dogImage.getInputStream(), metadata);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return cloudFrontDomain + dogImageUploadDirName + fileName;
    }
}
