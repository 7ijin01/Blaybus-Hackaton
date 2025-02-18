package com.blaybus.global.util;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Component
public class XuggleThumbnailGenerator {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public static BufferedImage generateThumbnail(String videoPath) throws IOException {
        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoPath)) {
            frameGrabber.start();

            Frame frame;
            Java2DFrameConverter converter = new Java2DFrameConverter();

            while ((frame = frameGrabber.grabImage()) != null) {
                BufferedImage bufferedImage = converter.convert(frame);
                if (bufferedImage != null) {
                    frameGrabber.stop();
                    return bufferedImage;
                }
            }
            frameGrabber.stop();
        } catch (Exception e) {
            throw new RuntimeException("썸네일 생성 실패", e);
        }
        return null; // 실패 시 null 반환
    }

    public ObjectId saveThumbnail(String videoObjectId) throws IOException {
        // 1. MongoDB에서 비디오 가져오기
        GridFSFile videoFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(videoObjectId))));

        if (videoFile == null) {
            throw new IOException("해당 비디오를 찾을 수 없습니다.");
        }

        GridFsResource videoResource = gridFsTemplate.getResource(videoFile);
        File tempVideoFile = File.createTempFile("video_", ".mp4");
        tempVideoFile.deleteOnExit(); // JVM 종료 시 자동 삭제

        // 2. 비디오 데이터를 임시 파일로 저장
        try (FileOutputStream fos = new FileOutputStream(tempVideoFile)) {
            fos.write(videoResource.getInputStream().readAllBytes());
        }

        // 3. 썸네일 생성
        BufferedImage thumbnail = generateThumbnail(tempVideoFile.getAbsolutePath());
        if (thumbnail == null) {
            throw new IOException("썸네일 생성에 실패하였습니다.");
        }

        // 4. 썸네일을 GridFS에 저장
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "jpg", baos);
        ByteArrayInputStream thumbnailInputStream = new ByteArrayInputStream(baos.toByteArray());

        ObjectId thumbnailId = gridFsTemplate.store(thumbnailInputStream, "thumbnail.jpg", "image/jpeg");

        // 5. 임시 파일 삭제
        tempVideoFile.delete();

        return thumbnailId;
    }
}