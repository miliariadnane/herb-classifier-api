package dev.nano.herbclassifier.controller;

import dev.nano.herbclassifier.service.ImageClassificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@Tag(
        name = "Herb Classifier API",
        description = "The Image upload API"
)
public class ImageUploadController {

    private final ImageClassificationService imageClassificationService;

    public ImageUploadController(ImageClassificationService imageClassificationService) {
        this.imageClassificationService = imageClassificationService;
    }

    @Operation(summary = "Classify an image", description = "Classify an image of a herb")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Returns the classification result", content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true))}),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true))}),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true))})
            }
    )
    @PostMapping(
            path = "/classify",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> classify(@RequestParam(value = "image") MultipartFile image) {
        String result = imageClassificationService.getHerbClassification(image);
        return ResponseEntity.ok(result);
    }
}
