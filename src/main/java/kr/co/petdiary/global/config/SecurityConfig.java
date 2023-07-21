package kr.co.petdiary.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.petdiary.global.auth.jwt.filter.JwtAuthenticationFilter;
import kr.co.petdiary.global.auth.jwt.service.JwtService;
import kr.co.petdiary.global.auth.oauth2.handler.OAuth2LoginFailureHandler;
import kr.co.petdiary.global.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import kr.co.petdiary.global.auth.oauth2.service.CustomOAuth2UserService;
import kr.co.petdiary.global.auth.security.filter.CustomJsonLoginAuthenticationFilter;
import kr.co.petdiary.global.auth.security.handler.LoginFailureHandler;
import kr.co.petdiary.global.auth.security.handler.LoginSuccessHandler;
import kr.co.petdiary.global.auth.security.service.CustomLoginUserDetailsService;
import kr.co.petdiary.owner.repository.OwnerRepository;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리
 * JwtAuthenticationProcessingFilter는 AccessToken, RefreshToken 재발급
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomLoginUserDetailsService loginUserDetailsService;

    private final OwnerRepository ownerRepository;
    private final OwnerSearchRepository ownerSearchRepository;

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    private final ObjectMapper objectMapper;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //== Security 옵션 ==//
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //== URL별 권한 관리 설정 ==//
        http.authorizeHttpRequests(authorization -> authorization
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/v1/owners/sign-up").permitAll()
                .requestMatchers("/api/v1/owners/sign-in").permitAll()
                .anyRequest().authenticated()
        );

        //== 소셜 로그인 설정 ==//
        http.oauth2Login(oauth -> oauth
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler)
                .userInfoEndpoint(service -> service.userService(oAuth2UserService))
        );

        // LogoutFilter -> JwtAuthenticationFilter -> CustomJsonLoginAuthenticationFilter
        http
                .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(), CustomJsonLoginAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CustomJsonUsernamePasswordAuthenticationFilter Bean 등록
     */
    @Bean
    public CustomJsonLoginAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        final CustomJsonLoginAuthenticationFilter customJsonUsernamePasswordLoginFilter
                = new CustomJsonLoginAuthenticationFilter(objectMapper);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return customJsonUsernamePasswordLoginFilter;
    }

    /**
     * 사용자화한 ProviderManager Bean 등록
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(loginUserDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return LoginSuccessHandler.builder()
                .jwtService(jwtService)
                .ownerRepository(ownerRepository)
                .ownerSearchRepository(ownerSearchRepository)
                .build();
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return JwtAuthenticationFilter.builder()
                .jwtService(jwtService)
                .ownerSearchRepository(ownerSearchRepository)
                .build();
    }
}
