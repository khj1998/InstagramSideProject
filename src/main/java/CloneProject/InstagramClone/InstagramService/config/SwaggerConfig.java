package CloneProject.InstagramClone.InstagramService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("CloneProject.InstagramClone.InstagramService.controller"))
                .build()
                .useDefaultResponseMessages(false);
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Api documentation By KimHoJin")
                .description("Project API Documentation")
                .version("3.0")
                .build();
    }
}
