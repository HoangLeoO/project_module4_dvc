package org.example.project_module4_dvc.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {

    // Khởi tạo Encoder với độ mạnh là 10 (Standard)
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(10);

    /**
     * Mã hóa mật khẩu từ dạng thô (raw) sang dạng hash (BCrypt)
     * @param rawPassword Mật khẩu người dùng nhập (vd: "123456")
     * @return Chuỗi mã hóa (vd: $2a$10$...)
     */
    public static String encrypt(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Kiểm tra mật khẩu có khớp không
     * @param rawPassword Mật khẩu người dùng nhập lúc đăng nhập
     * @param encodedPassword Mật khẩu đã mã hóa lưu trong Database
     * @return true nếu khớp, false nếu sai
     */
    public static boolean match(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    // Hàm main để bạn test nhanh tạo mật khẩu giả lập
    public static void main(String[] args) {
        String raw = "123456789";
        String hash = encrypt(raw);
        System.out.println("Password thô: " + raw);
        System.out.println("Password mã hóa: " + hash);

        // Test kiểm tra
        boolean check = match("123456789", hash);
        System.out.println("Kết quả check đúng: " + check);
    }
}