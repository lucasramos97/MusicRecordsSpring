package br.com.musicrecords.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.net.HttpHeaders;
import io.swagger.models.auth.In;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
        .securityContexts(securityContext()).securitySchemes(apiKey()).select()
        .apis(RequestHandlerSelectors.basePackage("br.com.musicrecords.controller"))
        .paths(PathSelectors.any()).build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().title("Music Records API").description(
        "API documentation Music Records.\n\nLucas Ramos - lucasramosdev@gmail.com - <a href='https://github.com/lucasramos97' target='_blank'>GitHub</a> - <a href='https://www.linkedin.com/in/lucasramos97' target='_blank'>Linkedin</a>")
        .version("1.0.0").build();
  }

  private List<SecurityContext> securityContext() {
    return Arrays.asList(SecurityContext.builder().securityReferences(defaultAuth())
        .forPaths(PathSelectors.ant("/musics/**")).build());
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Arrays.asList(new SecurityReference("Token JWT", authorizationScopes));
  }

  private List<ApiKey> apiKey() {
    return Arrays.asList(new ApiKey("Token JWT", HttpHeaders.AUTHORIZATION, In.HEADER.name()));
  }

}
