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
        .securityContexts(securityContext()).securitySchemes(Arrays.asList(apiKey())).select()
        .apis(RequestHandlerSelectors.basePackage("br.com.musicrecords.controller"))
        .paths(PathSelectors.any()).build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().title("Music Records API").description(
        "API documentation Music Records.\n\nLucas Ramos - lucasramosdev@gmail.com - <a href='https://github.com/lucasramos97' target='_blank'>GitHub</a> - <a href='https://www.linkedin.com/in/lucasramos97' target='_blank'>Linkedin</a>")
        .version("1.0.0").build();
  }

  private List<SecurityContext> securityContext() {
    SecurityContext authSecurityContext = SecurityContext.builder()
        .securityReferences(defaultAuth()).forPaths(PathSelectors.ant("/auth/test")).build();
    SecurityContext musicSecurityContext = SecurityContext.builder()
        .securityReferences(defaultAuth()).forPaths(PathSelectors.ant("/musics/**")).build();
    return Arrays.asList(authSecurityContext, musicSecurityContext);
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Arrays.asList(new SecurityReference("Token JWT", authorizationScopes));
  }

  private ApiKey apiKey() {
    return new ApiKey("Token JWT", HttpHeaders.AUTHORIZATION, In.HEADER.name());
  }

}
