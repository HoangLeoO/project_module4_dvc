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

    @Bean
    @Order(1)
    public SecurityFilterChain officialFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**", "/leader/**", "/official/**", "/login/official", "/process-login-official", "/logout/official", "/css/**", "/js/**", "/images/**", "/assets/**", "/fonts/**");

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login/official", "/css/**", "/js/**", "/images/**", "/assets/**", "/fonts/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/leader/**").hasAnyRole("CHU_TICH_UBND", "PHO_CHU_TICH_UBND")
                .anyRequest().authenticated()
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

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain citizenFilterChain(HttpSecurity http) throws Exception {
        // Chain mặc định cho công dân ("/**")
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login/citizen", "/register", "/css/**", "/js/**", "/images/**", "/assets/**", "/fonts/**").permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login/citizen")
                .loginProcessingUrl("/process-login") // URL post form
                .successHandler(successHandler)
                .usernameParameter("username")
                .passwordParameter("password")
        );

        http.logout(form -> form
                .logoutUrl("/logout/citizen")
                .logoutSuccessUrl("/login/citizen?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        return http.build();
    }
}