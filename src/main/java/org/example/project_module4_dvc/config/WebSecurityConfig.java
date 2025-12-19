package org.example.project_module4_dvc.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // Tự sinh constructor để inject dependencies bên dưới
public class WebSecurityConfig {

    // Inject 2 biến này (đã được khai báo @Bean hoặc @Service ở file khác)
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // Inject thêm SuccessHandler nếu bạn có
    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // 1. Khởi tạo rỗng (BẮT BUỘC với thư viện bạn đang dùng)
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // 2. Set các giá trị dependency vào
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    // --- 1. ADMIN CHAIN (Ưu tiên cao nhất) ---
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**"); // Chỉ bắt các URL bắt đầu bằng /admin/

        http.csrf(AbstractHttpConfigurer::disable);

//        http.authorizeHttpRequests(auth -> auth
//                .anyRequest().hasRole("ADMIN") // Bắt buộc phải là ADMIN
//        );

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login/official",
                        "/error",          // 👈 BẮT BUỘC
                        "/css/**",
                        "/js/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );
        http .exceptionHandling(ex -> ex.accessDeniedPage("/error"));

        http.formLogin(form -> form
                .loginPage("/login/official")
                .loginProcessingUrl("/process-login-official")
                .successHandler(successHandler)
                .usernameParameter("username")
                .passwordParameter("password")
        );

        http.logout(form -> form
                .logoutUrl("/logout/official")
                .logoutSuccessUrl("/login/official?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        http.exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }

    // --- 2. LEADER CHAIN (Lãnh đạo UBND) ---
    @Bean
    @Order(2)
    public SecurityFilterChain leaderFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/leader/**"); // Chỉ bắt các URL bắt đầu bằng /leader/

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .anyRequest().hasAnyRole("CHU_TICH_UBND", "PHO_CHU_TICH_UBND")
        );

        http.formLogin(form -> form
                .loginPage("/login/official")
                .loginProcessingUrl("/process-login-official")
                .successHandler(successHandler)
                .usernameParameter("username")
                .passwordParameter("password")
        );

        http.logout(form -> form
                .logoutUrl("/logout/official")
                .logoutSuccessUrl("/login/official?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        http.exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }

    // --- 3. OFFICIAL & COMMON LOGIN CHAIN (Chuyên viên + Xử lý đăng nhập chung) ---
    @Bean
    @Order(3)
    public SecurityFilterChain officialCommonFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(
                "/officer/**",
                "/login/officer",
                "/process-login-official",
                "/logout/officer",
                "/css/**", "/js/**"
        );

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login/officer", "/process-login-official","/css/**", "/js/**").permitAll()
                .requestMatchers("/officer/**").hasAnyRole("CHUYEN_VIEN","CANBO_MOTCUA","CANBO_TU_PHAP","CANBO_DIA_CHINH","CANBO_KINH_TE")
                .anyRequest().authenticated()
        );
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        http.formLogin(form -> form
                .loginPage("/login/officer")
                .loginProcessingUrl("/process-login-official")
                .successHandler(successHandler)
                .failureUrl("/login/officer?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
        );

        http.logout(form -> form
                .logoutUrl("/logout/officer")
                .logoutSuccessUrl("/login/officer")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        http.exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }

    // --- 4. CITIZEN CHAIN (Người dân - Cuối cùng) ---
    @Bean
    @Order(4)
    public SecurityFilterChain citizenFilterChain(HttpSecurity http) throws Exception {
        // Chỉ bắt URL không bắt đầu bằng /admin, /leader, /officer để tránh trùng với các chain khác
        http.securityMatcher("/", "/login/**", "/register", "/assets/**");

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login/citizen", "/register", "/assets/**", "/403", "/404","citizen/**").permitAll()
                .anyRequest().hasRole("CONG_DAN") // Hoặc .authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login/citizen")
                .loginProcessingUrl("/process-login") // URL post form
                .successHandler(successHandler)
                .defaultSuccessUrl("/citizen/hoso", true)
                .usernameParameter("username")
                .passwordParameter("password")
        );

        http.logout(form -> form
                .logoutUrl("/logout/citizen")
                .logoutSuccessUrl("/login/citizen?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        http.exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }
}