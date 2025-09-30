package com.digital.securityConfig;

import com.digital.exception.CustomForbiddenException;
import com.digital.exception.CustomUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class AppConfig {

    private final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    private final CustomUserDetailsService userDetailsService;
    private final AppFilter appFilter;

    public AppConfig(CustomUserDetailsService userDetailsService, AppFilter appFilter) {
        this.userDetailsService = userDetailsService;
        this.appFilter = appFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ===== CORS Filter =====
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200"); // Angular frontend
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Building Security Filter Chain...");

        http.csrf(AbstractHttpConfigurer::disable)
                .cors().and()
                .authorizeHttpRequests(auth -> auth

                        // auth/login
                        .requestMatchers("/api/auth/**").permitAll()

                        // user APIs
                        .requestMatchers("/api/user/otp", "/api/user/password").permitAll()
                        .requestMatchers("/api/user/create", "/api/user/status/*").hasRole("ADMIN")

                        // audit log
                        .requestMatchers("/api/auditLog").hasAnyRole("TEACHER", "STUDENT", "LIBRARIAN", "TRANSPORT")
                        .requestMatchers("/api/auditLog/*").hasRole("ADMIN")


                        // attendance rule apis
                        .requestMatchers( "/api/attendanceRules/**").hasRole("ADMIN")

                        // class session apis
                        .requestMatchers("/api/session").hasRole("TEACHER")
                        .requestMatchers("/api/session/get").hasAnyRole("TEACHER', 'STUDENT', 'ADMIN")

                         // attendance apis for STUDENT AND TEACHER
                        .requestMatchers("/api/attendance/join-session/*", "/api/attendance/leave-session/*")
                           .hasRole("STUDENT")

                        .requestMatchers("/api/attendance/auto-mark/*", "/api/attendance/check-in-list/*",
                                "/api/attendance/view/*", "/api/attendance/view-all/*", "/api/attendance/update/*")
                           .hasRole("TEACHER")

                        //report
                                .requestMatchers("/api/admin/exams/report-cards/generate")
                                .hasAnyRole("ADMIN", "TEACHER")
                                .requestMatchers("/reports/**").permitAll()   // static reports folder
                                .requestMatchers("/api/student/**").hasAnyRole("STUDENT", "PARENT")  // secure API
                                .requestMatchers("/api/teacher/**").hasAnyRole("ADMIN", "TEACHER")


                                // attendance apis for ADMIN
                        .requestMatchers("/api/attendance/admin/update/*", "/api/attendance/admin/view/*",
                                "/api/attendance/admin/view-all/*", "/api/attendance/admin/delete/*").hasRole("ADMIN")

                        .requestMatchers("/api/attendance/admin/pdf/*").hasAnyRole("ADMIN", "TEACHER")

                        // attendance rules
                        .requestMatchers("/api/attendanceRules/**").hasRole("ADMIN")


                        // subject apis
                        .requestMatchers("/api/admin/subject", "/api/admin/subject/*",
                         "/api/admin/subject/class/*", "/api/admin/subject/teacher/*",
                         "/api/admin/subject/update/*").hasRole("ADMIN")

                        // admin timetable apis
                        .requestMatchers("/api/admin/timetable/**").hasRole("ADMIN")

                        // teacher timetable and session apis
                        .requestMatchers("/teacher/timetable/*", "/teacher/sessions/*").hasRole("TEACHER")

                        // student timetable and session apis
                        .requestMatchers("/student/timetable/*", "/student/sessions/*").hasRole("STUDENT")

                        // class session apis
                        .requestMatchers("/api/session/joinLink").hasRole("TEACHER")
                        .requestMatchers("/api/session/get").hasAnyRole("TEACHER", "STUDENT", "ADMIN")

                        // Google Meet apis
                        .requestMatchers("/auth/google/**").permitAll()
                        .requestMatchers("/oauth2/callback/**").permitAll()

                        // student attendance
                        .requestMatchers("/api/attendance/join-session/*", "/api/attendance/leave-session/*").hasRole("STUDENT")

                        // teacher attendance
                        .requestMatchers("/api/attendance/auto-mark/*", "/api/attendance/check-in-list/*",
                                "/api/attendance/view/*", "/api/attendance/view-all/*", "/api/attendance/update/*")
                        .hasRole("TEACHER")

                        // admin attendance
                        .requestMatchers("/api/attendance/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/attendance/admin/pdf/*").hasAnyRole("ADMIN", "TEACHER")

                        // teacher APIs
                        .requestMatchers("/api/teacher/**").hasRole("ADMIN")

                        // class/section
                        .requestMatchers("/api/class/**", "/api/section/**").hasRole("ADMIN")

                        // students
                        .requestMatchers("/api/students/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/students/**").hasRole("STUDENT")

                        // ADMIN Academic Calendar Apis
                        .requestMatchers("/admin/calendar",
                                                  "/admin/calendar/*/holidays",
                                                  "/admin/calendar/*/events",
                                                  "/admin/calendar/view-all",
                                                  "/admin/calendar/view/*",
                                                  "/admin/calendar/*",
                                                  "/admin/calendar/holidays/*",
                                                  "/admin/calendar/holidays/remove/*",
                                                  "/admin/calendar/events/*",
                                                  "/admin/events/remove/*")
                        .hasRole("ADMIN")

                        // TEACHER view calendar events Api
                        .requestMatchers("/teacher/calendar").hasRole("TEACHER")

                        // STUDENT view calender events Api
                        .requestMatchers("/student/calendar").hasRole("STUDENT")

                        // any other API requires auth
                        .requestMatchers("/api/**").authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler())
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(appFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            throw new CustomUnauthorizedException("Unauthorized - Please log in");
        };
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            throw new CustomForbiddenException("Forbidden - You do not have access to this resource");
        };
    }
}
