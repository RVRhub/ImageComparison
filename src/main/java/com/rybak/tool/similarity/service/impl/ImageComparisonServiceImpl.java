package com.rybak.tool.similarity.service.impl;

import com.rybak.tool.similarity.comparison.Comparison;
import com.rybak.tool.similarity.comparison.Rect;
import com.rybak.tool.similarity.exception.ImageComparisonException;
import com.rybak.tool.similarity.exception.StorageException;
import com.rybak.tool.similarity.exception.StorageFileNotFoundException;
import com.rybak.tool.similarity.service.ImageComparisonService;
import com.rybak.tool.similarity.storege.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class ImageComparisonServiceImpl implements ImageComparisonService {

    private final Logger log = LoggerFactory.getLogger(ImageComparisonServiceImpl.class);

    private static final String DOWNLOAD_2_IMAGES_EXCEPTION_MSG = "You need to download 2 images!";
    private static final String INCORRECTLY_LIST_OF_EXCEPTIONS_ERROR_MSG = "Incorrectly entered the list of exceptions";
    private static final String FOR_VALIDATION_REG_PATTER = "\\[+\\d{1,3}\\W*\\d{1,3}\\W*\\d{1,3}\\W*\\d{1,3}\\]+";
    private static final String GET_VALUE_REG_PATTERN = "\\d{1,3}\\W*\\d{1,3}\\W*\\d{1,3}\\W*\\d{1,3}";

    private final Path rootLocation;

    @Autowired
    public ImageComparisonServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file, String fileName) {
        try {
            if (file.isEmpty())
            {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            int index = file.getContentType().indexOf("/");
            String ext = file.getContentType().substring(index+1);

            Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName+"."+ext));
        } catch (IOException e) {
            log.error("Store Exception, fileName: " + fileName, e);
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }


    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            log.error("Load All Exception: ", e);
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            log.error("LoadAsResource Exception, fileName: " + filename, e);

            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public String validation(String excludeRects) {
        String errorMessage = null;
        if (excludeRects != null && excludeRects.length() > 0) {
            errorMessage = validateInputExcludeRects(excludeRects);
        }

        Stream<Path> files = loadAll();
        Path[] pathString = files.toArray(size -> new Path[size]);

        if (pathString.length != 2) {
            errorMessage = DOWNLOAD_2_IMAGES_EXCEPTION_MSG;
        }
        return errorMessage;
    }

    @Override
    public byte[] getComparisonImage(String excludeRects) {

        try {
            Stream<Path> files = loadAll();
            Path[] pathString = files.toArray(size -> new Path[size]);

            if(pathString.length != 2)
            {
                return getEmptyImage();
            }
            File file1 = loadAsResource(pathString[0].toString()).getFile();
            File file2 = loadAsResource(pathString[1].toString()).getFile();

            BufferedImage screenShotImg = ImageIO.read(file1);
            BufferedImage baseImg = ImageIO.read(file2);

            List<Rect> rectList = getListOfRects(excludeRects);
            Comparison comparison = new Comparison();
            BufferedImage result =  comparison.startComparison(baseImg, screenShotImg, rectList);

            return getBytesFromBufferedImg(result);

        } catch (IOException e) {
            log.error("ComparisonImage: " + excludeRects, e);
            throw new ImageComparisonException("Сan't perform the comparison of 2 images: ", e);
        }
    }

    private byte[] getBytesFromBufferedImg(BufferedImage result) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "png", baos);
        return baos.toByteArray();
    }

    @Override
    public byte[] getEmptyImage() {
        try {
            BufferedImage pixelImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            int[] pixel = new int[1];
            pixel[0] = 0xFFFFFFFF;
            pixelImage.setRGB(0, 0, 1, 1, pixel, 0, 1);
            return getBytesFromBufferedImg(pixelImage);
        } catch (IOException e) {
            log.error("Empty Image Exception: ", e);
            throw new ImageComparisonException("Cannot create an empty image: ", e);
        }
    }

    private String validateInputExcludeRects(String excludeRects) {
        Pattern pattern = Pattern.compile(FOR_VALIDATION_REG_PATTER);
        Matcher matcher = pattern.matcher(excludeRects);
        if (!matcher.find())
            return INCORRECTLY_LIST_OF_EXCEPTIONS_ERROR_MSG;
        return null;
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            log.error("Init Exception: ", e);
            throw new StorageException("Could not initialize storage", e);
        }
    }

    private List<Rect> getListOfRects(String excludeRects) {
        List<Rect> rectList = new ArrayList<>();
        Pattern pattern = Pattern.compile(GET_VALUE_REG_PATTERN);
        Matcher matcher = pattern.matcher(excludeRects);
        if (matcher.find()) {
            int[] value = getIntArray(matcher.group().split(","));

            rectList.add(new Rect(value[0], value[1], value[2], value[3]));
        }
        return rectList;
    }

    private int[] getIntArray(String[] strValue) {
        int[] value = new int[strValue.length];
        int i = 0;
        for (String str : strValue) {
            value[i] = Integer.parseInt(str.trim());
            i++;
        }

        return value;
    }
}
