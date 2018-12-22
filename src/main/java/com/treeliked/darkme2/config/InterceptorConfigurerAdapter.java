package com.treeliked.darkme2.config;

import com.treeliked.darkme2.interceptor.ApiInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

/**
 * 注册自定义拦截器
 *
 * @author lqs2
 * @date 2018/12/10, Mon
 */
@Configuration
public class InterceptorConfigurerAdapter implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 对 file & memo api操作拦截进行session验证
        registry.addInterceptor(new ApiInterceptor()).addPathPatterns(new ArrayList<String>() {{
            add("/api/file/**");
            add("/api/memo/**");
        }});
    }
}
