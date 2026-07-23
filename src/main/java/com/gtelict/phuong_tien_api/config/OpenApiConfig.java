package com.gtelict.phuong_tien_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hệ thống Quản lý Tài sản & Phương tiện")
                        .description("API chia sẻ, khai thác dữ liệu thông tin cán bộ chiến sĩ, tài sản, trang thiết bị và danh mục qua LGSP.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("GTEL ICT")
                                .email("support@gtelict.com")))
                .tags(List.of(
                        new Tag().name("1. Cán bộ chiến sĩ")
                                .description("Dịch vụ khai thác dữ liệu thông tin cán bộ chiến sĩ qua LGSP"),
                        new Tag().name("2. Tài sản & Trang thiết bị")
                                .description("Dịch vụ quản lý, tra cứu tài sản & trang thiết bị"),
                        new Tag().name("3. Bàn giao tài sản")
                                .description("Dịch vụ lập, phê duyệt và tra cứu biên bản bàn giao tài sản"),
                        new Tag().name("4. Danh mục")
                                .description("Dịch vụ chia sẻ dữ liệu danh mục (Đơn vị, Địa giới, Dân tộc, Tôn giáo, Loại tài sản) qua LGSP")
                ));
    }

    @Bean
    public org.springdoc.core.customizers.OpenApiCustomizer globalHeaderOpenApiCustomizer() {
        return openApi -> {
            // Thêm Schema chung cho các lỗi (BadRequestResponse)
            if (openApi.getComponents() == null) {
                openApi.setComponents(new io.swagger.v3.oas.models.Components());
            }
            openApi.getComponents().addSchemas("BadRequestResponse", new io.swagger.v3.oas.models.media.Schema<>()
                    .type("object")
                    .addProperty("title", new io.swagger.v3.oas.models.media.StringSchema())
                    .addProperty("errorCode", new io.swagger.v3.oas.models.media.StringSchema())
                    .addProperty("message", new io.swagger.v3.oas.models.media.StringSchema())
                    .addProperty("uri", new io.swagger.v3.oas.models.media.StringSchema())
                    .addProperty("time", new io.swagger.v3.oas.models.media.StringSchema())
            );

            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                    io.swagger.v3.oas.models.responses.ApiResponses apiResponses = operation.getResponses();
                    
                    // Giữ lại 200 (nếu có auto-gen type), thêm các mã lỗi khác
                    apiResponses.addApiResponse("400", createErrorResponse("Lỗi dữ liệu đầu vào (VD: thiếu tham số hoặc sai định dạng)", "Lỗi tham số đầu vào", "400", "Dữ liệu không hợp lệ"));
                    
                    // Nếu URL path thật sự chứa {id}
                    if (path.contains("{id}")) {
                        apiResponses.addApiResponse("404", createErrorResponse("Không tìm thấy dữ liệu", "Không tìm thấy", "404", "Bản ghi không tồn tại hoặc đã bị xóa"));
                    }
                    
                    // Mã 409 (Conflict) dùng chính xác HttpMethod và chuỗi URL path
                    if (path.contains("/ban-giao") && (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.POST || httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.PUT)) {
                         apiResponses.addApiResponse("409", createErrorResponse("Xung đột dữ liệu (Trùng số biên bản hoặc Lock Timeout)", "Xung đột dữ liệu", "409", "Tài sản đang được giao dịch khác xử lý hoặc dữ liệu đã tồn tại"));
                    }

                    apiResponses.addApiResponse("500", createErrorResponse("Lỗi hệ thống máy chủ", "Lỗi hệ thống", "500", "Có lỗi xảy ra trong quá trình xử lý trên máy chủ"));
                });
            });
        };
    }

    private io.swagger.v3.oas.models.responses.ApiResponse createErrorResponse(String description, String title, String errorCode, String message) {
        String exampleJson = String.format("{\n" +
                "  \"title\": \"%s\",\n" +
                "  \"errorCode\": \"%s\",\n" +
                "  \"message\": \"%s\",\n" +
                "  \"uri\": \"/api/path\",\n" +
                "  \"time\": \"2024-05-15T10:00:00\"\n" +
                "}", title, errorCode, message);

        io.swagger.v3.oas.models.media.MediaType mediaType = new io.swagger.v3.oas.models.media.MediaType()
                .schema(new io.swagger.v3.oas.models.media.Schema<>().$ref("#/components/schemas/BadRequestResponse"))
                .addExamples("example", new io.swagger.v3.oas.models.examples.Example().value(exampleJson));

        return new io.swagger.v3.oas.models.responses.ApiResponse()
                .description(description)
                .content(new io.swagger.v3.oas.models.media.Content().addMediaType("application/json", mediaType));
    }
}
