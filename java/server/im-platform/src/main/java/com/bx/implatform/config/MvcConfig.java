package com.bx.implatform.config ;

import com.bx.implatform.interceptor.AuthInterceptor ;
import com.bx.implatform.interceptor.XssInterceptor ;
import lombok.AllArgsConstructor ;
import org.springframework.context.annotation.Bean ;
import org.springframework.context.annotation.Configuration ;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder ;
import org.springframework.security.crypto.password.PasswordEncoder ;
import org.springframework.web.cors.CorsConfiguration ;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource ;
import org.springframework.web.filter.CorsFilter ;
import org.springframework.web.servlet.config.annotation.CorsRegistry ;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry ;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer ;

@Configuration
@AllArgsConstructor
public class MvcConfig implements WebMvcConfigurer{

	private final AuthInterceptor authInterceptor ;

	private final XssInterceptor xssInterceptor ;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(xssInterceptor).addPathPatterns("/**").excludePathPatterns("/error") ;
		registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/login", "/logout", "/register", "/refreshToken", "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**") ;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// 使用BCrypt加密密码
		return new BCryptPasswordEncoder() ;
	}

	/* @Bean
	public CorsFilter corsFilter() {
	    final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
	    final CorsConfiguration corsConfiguration = new CorsConfiguration();
	//	    是否允许请求带有验证信息
	    corsConfiguration.setAllowCredentials(true);
	//	    允许访问的客户端域名 http://douyin.shulijp.com
		List<String> allowedOrigins = new ArrayList<String>();
		allowedOrigins.add("https://weijc.cn");
		allowedOrigins.add("http://shulijp.com");
		allowedOrigins.add("http://localhost");
		corsConfiguration.setAllowedOrigins(allowedOrigins);
	//	    允许服务端访问的客户端请求头
	    corsConfiguration.addAllowedOrigin("*");
	    corsConfiguration.addAllowedHeader("*");
	//	    允许访问的方法名,GET POST等
	    corsConfiguration.addAllowedMethod("*");
	    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
	    return new CorsFilter(urlBasedCorsConfigurationSource);
	}*/

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 对所有路径应用跨域配置
				.allowedOriginPatterns("*") // 允许的域，例如：Angular应用的端口
				.allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的方法
				.allowedHeaders("*") // 允许的头
				.allowCredentials(true) ; // 是否允许凭据，如cookies
	}
}
