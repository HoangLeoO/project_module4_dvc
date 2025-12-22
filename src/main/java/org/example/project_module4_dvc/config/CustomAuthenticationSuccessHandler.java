package org.example.project_module4_dvc.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = "/";

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if ("ROLE_ADMIN".equals(role)) {
                redirectUrl = "/admin/";
                break;
            }

            // Redirect cho Lãnh đạo
            if ("ROLE_CHU_TICH_UBND".equals(role) || "ROLE_PHO_CHU_TICH_UBND".equals(role)) {
                redirectUrl = "/leader/dashboard";
                break;
            }
            if (role.equals("ROLE_CANBO_MOTCUA")) {

                // Chuyển hướng đến officer dashboard
                response.sendRedirect("/officer/dashboard");
                return; // Kết thúc
            }
            if (role.equals("ROLE_CANBO_TU_PHAP") || role.equals("ROLE_CANBO_DIA_CHINH")
                    || role.equals("ROLE_CANBO_KINH_TE")) {

                // Chuyển hướng đến officer dashboard
                response.sendRedirect("/specialist/dashboard");
                return; // Kết thúc
            }



            // Redirect cho Công dân
            if ("ROLE_CONG_DAN".equals(role) || "ROLE_USER".equals(role)) {
                redirectUrl = "/";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
