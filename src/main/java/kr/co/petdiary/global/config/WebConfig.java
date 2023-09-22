package kr.co.petdiary.global.config;

import kr.co.petdiary.global.auth.interceptor.OwnerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OwnerInterceptor())
                .addPathPatterns("/**");
    }
}
