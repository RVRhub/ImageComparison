package com.rybak.tool.similarity.controller;

import com.rybak.tool.similarity.exception.StorageFileNotFoundException;
import com.rybak.tool.similarity.service.ImageComparisonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.stream.Collectors;

@Controller
public class ImageComparisonController {

    private static final String EXECUTE_YES = "yes";
    private static final String EXECUTE_NO = "no";
    private static final String ATTR_EXECUTE = "execute";
    private static final String ATTR_EXCLUDE_RECTS = "excludeRects";
    private static final String ATTR_FILES = "files";
    private static final String ATTR_MESSAGE = "message";
    private static final String YOU_SUCCESSFULLY_UPLOADED_MSG = "You successfully uploaded {0} file!";

    private final Logger log = LoggerFactory.getLogger(ImageComparisonController.class);

    private final ImageComparisonService imageComparisonService;

    @Autowired
    public ImageComparisonController(ImageComparisonService imageComparisonService) {
        this.imageComparisonService = imageComparisonService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) {

        log.debug("List Uploaded Files");

        model.addAttribute(ATTR_FILES, imageComparisonService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(ImageComparisonController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/comparison")
    public String startComparison(HttpSession session, @RequestParam("excludeRects") String excludeRects, RedirectAttributes redirectAttributes) throws IOException {

        log.debug("Start Comparison and Validation: {}", excludeRects);

        String errorMessage = imageComparisonService.validation(excludeRects);
        log.debug("Validation Message: {}" + errorMessage);

        if (errorMessage != null) {
            redirectAttributes.addFlashAttribute("error",
                    errorMessage);
            return "redirect:/";
        }

        session.setAttribute(ATTR_EXECUTE, EXECUTE_YES);
        session.setAttribute(ATTR_EXCLUDE_RECTS, excludeRects);
        return "uploadForm";
    }


    @GetMapping("/clean")
    public String cleanResult(HttpSession session) {
        log.debug("Clean Result.");

        session.setAttribute(ATTR_EXECUTE, EXECUTE_NO);
        return "uploadForm";
    }

    @RequestMapping(value = "/image/{start}")
    public Object getImage(HttpSession session) {

        String executeValue = (String) session.getAttribute(ATTR_EXECUTE);
        log.debug("Start Comparison, execute command: {}", executeValue);

        byte[] result;
        if (EXECUTE_YES.equals(executeValue)) {

            String excludeRects = (String) session.getAttribute(ATTR_EXCLUDE_RECTS);
            log.debug("Start Comparison, excludeRects: {}", excludeRects);
            result = imageComparisonService.getComparisonImage(excludeRects);

            cleanFolder();

        } else {
            result = imageComparisonService.getEmptyImage();
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<byte[]>(result, headers, HttpStatus.OK);
    }

    private void cleanFolder() {
        imageComparisonService.deleteAll();
        imageComparisonService.init();
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        log.debug("Serve File: {}", filename);

        Resource file = imageComparisonService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping("/first/")
    public String handleFileUploadFirst(@RequestParam("file") MultipartFile file,
                                        RedirectAttributes redirectAttributes) {
        log.debug("Handle FileUploadFirst {}", file.getOriginalFilename());

        imageComparisonService.store(file, "firstImage");
        redirectAttributes.addFlashAttribute(ATTR_MESSAGE,
                MessageFormat.format(YOU_SUCCESSFULLY_UPLOADED_MSG, "first"));

        return "redirect:/";
    }

    @PostMapping("/second/")
    public String handleFileUploadSecond(@RequestParam("file") MultipartFile file,
                                         RedirectAttributes redirectAttributes) {
        log.debug("Handle FileUploadSecond {}", file.getOriginalFilename());

        imageComparisonService.store(file, "secondFile");
        redirectAttributes.addFlashAttribute(ATTR_MESSAGE,
                MessageFormat.format(YOU_SUCCESSFULLY_UPLOADED_MSG, "second"));

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        log.error("StorageFileNotFound: ", exc);

        return ResponseEntity.notFound().build();
    }

}
