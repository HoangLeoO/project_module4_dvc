//package org.example.project_module4_dvc.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor // Tự sinh constructor để inject dependencies bên dưới
//public class WebSecurityConfig {
//
//    // Inject 2 biến này (đã được khai báo @Bean hoặc @Service ở file khác)
//    private final UserDetailsService userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//
//    // Inject thêm SuccessHandler nếu bạn có
//    private final CustomAuthenticationSuccessHandler successHandler;
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        // 1. Khởi tạo rỗng (BẮT BUỘC với thư viện bạn đang dùng)
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
//        // 2. Set các giá trị dependency vào
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder);
//
//        return authProvider;
//    }
//
//    // --- 1. ADMIN CHAIN (Ưu tiên cao nhất) ---
//    @Bean
//    @Order(1)
//    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
//        http.securityMatcher("/admin/**"); // Chỉ bắt các URL bắt đầu bằng /admin/
//
//        http.csrf(AbstractHttpConfigurer::disable);
//
////        http.authorizeHttpRequests(auth -> auth
////                .anyRequest().hasRole("ADMIN") // Bắt buộc phải là ADMIN
////        );
//
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers("/login/official", "/css/**", "/js/**").permitAll()
//                // Admin và Lãnh đạo cũng có thể vào xem trang của chuyên viên nếu cần
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
//        );
//
//        http.formLogin(form -> form
//                .loginPage("/login/official")
//                .loginProcessingUrl("/process-login-official")
//                .successHandler(successHandler)
//                .usernameParameter("username")
//                .passwordParameter("password")
//        );
//
//        http.logout(form -> form
//                .logoutUrl("/logout/official")
//                .logoutSuccessUrl("/login/official?logout")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//        );
//
//        return http.build();
//    }
//
//    // --- 2. LEADER CHAIN (Lãnh đạo UBND) ---
//    @Bean
//    @Order(2)
//    public SecurityFilterChain leaderFilterChain(HttpSecurity http) throws Exception {
//        http.securityMatcher("/leader/**"); // Chỉ bắt các URL bắt đầu bằng /leader/
//
//        http.csrf(AbstractHttpConfigurer::disable);
//
//        http.authorizeHttpRequests(auth -> auth
//                .anyRequest().hasAnyRole("CHU_TICH_UBND", "PHO_CHU_TICH_UBND")
//        );
//
//        http.formLogin(form -> form
//                .loginPage("/login/official")
//                .loginProcessingUrl("/process-login-official")
//                .successHandler(successHandler)
//                .usernameParameter("username")
//                .passwordParameter("password")
//        );
//
//        http.logout(form -> form
//                .logoutUrl("/logout/official")
//                .logoutSuccessUrl("/login/official?logout")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//        );
//        return http.build();
//    }
//
//    // --- 3. OFFICIAL & COMMON LOGIN CHAIN (Chuyên viên + Xử lý đăng nhập chung) ---
//    @Bean
//    @Order(3)
//    public SecurityFilterChain officialCommonFilterChain(HttpSecurity http) throws Exception {
//        // Chain này quản lý thư mục /official VÀ các URL đăng nhập/xử lý chung cho cán bộ
//        http.securityMatcher(
//                "/official/**",
//                "/login/official",
//                "/process-login-official",
//                "/logout/official",
//                "/css/**", "/js/**" // Tài nguyên tĩnh cho trang admin
//        );
//
//        http.csrf(AbstractHttpConfigurer::disable);
//
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers("/login/official", "/css/**", "/js/**").permitAll()
//                // Admin và Lãnh đạo cũng có thể vào xem trang của chuyên viên nếu cần
//                .requestMatchers("/official/**").hasAnyRole("CHUYEN_VIEN","CHU_TICH_UBND", "PHO_CHU_TICH_UBND")
//                .anyRequest().authenticated()
//        );
//
//        http.formLogin(form -> form
//                .loginPage("/login/official")
//                .loginProcessingUrl("/process-login-official")
//                .successHandler(successHandler)
//                .usernameParameter("username")
//                .passwordParameter("password")
//        );
//
//        http.logout(form -> form
//                .logoutUrl("/logout/official")
//                .logoutSuccessUrl("/login/official?logout")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//        );
//
//        return http.build();
//    }
//
//    // --- 4. CITIZEN CHAIN (Người dân - Cuối cùng) ---
//    @Bean
//    @Order(4)
//    public SecurityFilterChain citizenFilterChain(HttpSecurity http) throws Exception {
//        // Không dùng securityMatcher cụ thể -> Mặc định bắt tất cả những gì còn lại (/**)
//
//        http.csrf(AbstractHttpConfigurer::disable);
//
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers("/", "/login/citizen", "/register", "/assets/**").permitAll()
//                .anyRequest().hasRole("CONG_DAN") // Hoặc .authenticated()
//        );
//
//        http.formLogin(form -> form
//                .loginPage("/login/citizen")
//                .loginProcessingUrl("/process-login") // URL post form
//                .successHandler(successHandler)
//                .usernameParameter("username")
//                .passwordParameter("password")
//        );
//
//        http.logout(form -> form
//                .logoutUrl("/logout/citizen")
//                .logoutSuccessUrl("/login/citizen?logout")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//        );
//
//        return http.build();
//    }
//}