package com.gtelict.phuong_tien_api.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class SwaggerJsonController {
    @GetMapping(value = "/custom-swagger.json", produces = "application/json")
    public String getSwaggerJson() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/openapi.json");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}
