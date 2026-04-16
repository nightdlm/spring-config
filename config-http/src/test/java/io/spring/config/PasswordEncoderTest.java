package io.spring.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BCrypt密码加密测试类
 */
public class PasswordEncoderTest {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Test
    public void testPasswordEncoding() {
        String rawPassword = "admin123";
        
        // 生成加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        System.out.println("明文密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        
        // 验证密码
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        
        // 验证错误密码
        assertFalse(passwordEncoder.matches("wrong_password", encodedPassword));
    }
    
    @Test
    public void testMultipleEncoding() {
        String rawPassword = "test123";
        
        // 多次加密同一个密码，结果应该不同（因为盐值不同）
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);
        
        System.out.println("第一次加密: " + encoded1);
        System.out.println("第二次加密: " + encoded2);
        
        // 两次加密结果应该不同
        assertNotEquals(encoded1, encoded2);
        
        // 但都能验证通过
        assertTrue(passwordEncoder.matches(rawPassword, encoded1));
        assertTrue(passwordEncoder.matches(rawPassword, encoded2));
    }
    
    @Test
    public void testCommonPasswords() {
        // 测试常用密码
        String[] passwords = {"123456", "password", "admin", "admin123", "root"};
        
        for (String password : passwords) {
            String encoded = passwordEncoder.encode(password);
            assertTrue(passwordEncoder.matches(password, encoded), 
                "密码验证失败: " + password);
            System.out.println(password + " -> " + encoded);
        }
    }
}
