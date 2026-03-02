package za.co.userdashboard.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){

        return  new OpenAPI()
                .info(new Info()
                        .title("Users API")
                        .version("1.0")
                        .description("REST API for Managing Users"))
                .components(new Components()
                        .addSchemas("ErrorResponse",new ObjectSchema()
                                .addProperty("status",new IntegerSchema().example(400))
                                .addProperty("message", new StringSchema().example("Resource not found"))));


    }


}

