package com.dev5ops.healthtart.security;

import com.dev5ops.healthtart.user.service.CustomOAuth2UserService;
import com.dev5ops.healthtart.user.service.UserService;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private CustomOAuth2UserService customOAuth2UserService;
    private UserService userService;
    private Environment env;
    private JwtUtil jwtUtil;

    @Autowired
    public WebSecurity(BCryptPasswordEncoder bCryptPasswordEncoder, CustomOAuth2UserService customOAuth2UserService, UserService userService, Environment env, JwtUtil jwtUtil) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.customOAuth2UserService = customOAuth2UserService;
        this.userService = userService;
        this.env = env;
        this.jwtUtil = jwtUtil;
    }

    /* 설명. 인가(Authoriazation)용 메소드(인증 필터 추가) */
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());
        // CORS 설정 적용
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 로그인 시 추가할 authenticationManager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // HttpSecurity 설정
        http.authorizeHttpRequests((authz) ->
                        authz
                                .requestMatchers(new AntPathRequestMatcher("/api/oauth/kakao", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/index.html", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/verification-email/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/verify-code")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/send-sms")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/**", "OPTIONS")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/nickname/check", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/oauth2", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/**", "PATCH")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/mypage/edit/password", "PATCH")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/login/**", "OPTIONS")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/login/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/login/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/ocr/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/upload/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/exercise_equipment/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/exercise_equipment/by_body_part", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/exercise_equipment/equipment_list", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/exercise_equipment/**", "POST")).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/exercise_equipment/**/edit", "PATCH")).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/exercise_equipment/**/delete", "DELETE")).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/gym/gym_list", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/gym/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/gym/register", "POST")).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/gym/**/edit", "PATCH")).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/gym/**/delete", "DELETE")).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/equipment_per_gym/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/equipment_per_gym/**/body_part", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/equipment_per_gym/equipment_per_gym_list", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/equipment_per_gym/register", "POST")).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/equipment_per_gym/**/edit", "PATCH")).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/equipment_per_gym/**/delete", "DELETE")).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/oauth2/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/inbody/**", "GET")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/api/gpt/**", "GET")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/api/gpt/**", "PATCH")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/api/gpt/**", "POST")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/api/gpt/**", "OPTIONS")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/inbody/inbody_ranking", "GET")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/inbody/my-inbody/**", "GET")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/inbody/inbody_list", "GET")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/inbody/register", "POST")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/inbody/**/edit", "PATCH")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/inbody/**", "DELETE")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/history/ratings", "GET")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/history/**", "POST")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/workoutInfos/**", "GET")).hasRole("MEMBER")
                                .requestMatchers(new AntPathRequestMatcher("/routines/**", "DELETE")).hasRole("MEMBER")

//                                .requestMatchers(new AntPathRequestMatcher("/inbody/**", "OPTIONS")).permitAll() // OPTIONS 요청은 안해줘도 작동
                                .anyRequest().authenticated()
                )
                /* UserDetails를 상속받는 Service 계층 + BCrypt 암호화 */
                .authenticationManager(authenticationManager)

                /* OAuth2 로그인 설정 추가 */
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(new OAuth2AuthenticationSuccessHandler(jwtUtil))
//                        .failureHandler(new OAuth2AuthenticationFailureHandler())
                )

                .sessionManagement((session)
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWT 인증 필터 추가
        http.addFilter(getAuthenticationFilter(authenticationManager));
        http.addFilterBefore(new JwtFilter(userService, jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost")); // 프론트엔드 도메인 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // 허용할 HTTP 메서드 설정
        configuration.setAllowCredentials(true); // 인증 정보 허용 (쿠키 등)
        configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // 노출할 헤더 설정
        configuration.setMaxAge(3600L); // 1시간 동안 캐시

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Authentication 용 메소드(인증 필터 반환)
    private Filter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new AuthenticationFilter(authenticationManager, userService, env, jwtUtil);
    }
}