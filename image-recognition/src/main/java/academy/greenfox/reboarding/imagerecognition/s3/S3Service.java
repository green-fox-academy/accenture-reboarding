package academy.greenfox.reboarding.imagerecognition.s3;

import java.io.File;
import java.util.UUID;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3Service {

  private String bucketName;

  private AmazonS3 s3client;

  public S3Service(
    @Value("${aws.bucket}") String bucketName, 
    @Value("${aws.access}") String accessKey,
    @Value("${aws.secret}") String secretKey) {
    
    this.bucketName = bucketName;

    AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    s3client = AmazonS3ClientBuilder
    .standard()
    .withCredentials(new AWSStaticCredentialsProvider(credentials))
    .withRegion(Regions.EU_CENTRAL_1)
    .build();
  }

  public String uploadFile(String path) {
    String objectName = UUID.randomUUID().toString() + ".jpg";
    s3client.putObject(
      new PutObjectRequest(bucketName, objectName, new File(path))
      .withCannedAcl(CannedAccessControlList.PublicRead));   
    return s3client.getUrl(bucketName, objectName).toExternalForm().toString(); 
  }
}
