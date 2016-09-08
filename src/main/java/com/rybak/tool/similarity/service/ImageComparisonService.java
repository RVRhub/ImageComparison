package com.rybak.tool.similarity.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface ImageComparisonService {

    void init();

    void store(MultipartFile file, String fileName);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    String validation(String excludeRects);

    byte[] getComparisonImage(String excludeRects);

    byte[] getEmptyImage();

}
