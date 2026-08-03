package com.ebbinghaus.vocab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;
    private final OperationLogInterceptor operationLogInterceptor;

    public WebMvcConfig(JwtInterceptor jwtInterceptor, AdminAuthInterceptor adminAuthInterceptor,
                        OperationLogInterceptor operationLogInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.adminAuthInterceptor = adminAuthInterceptor;
        this.operationLogInterceptor = operationLogInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/register", "/api/auth/login",
                        "/api/hello", "/api/health/**",
                        "/doc.html", "/swagger-ui.html", "/swagger-ui/**",
                        "/swagger-resources/**", "/v3/api-docs/**", "/webjars/**");

        registry.addInterceptor(adminAuthInterceptor).addPathPatterns("/api/admin/**");
        registry.addInterceptor(operationLogInterceptor).addPathPatterns("/api/admin/**");
    }
}
