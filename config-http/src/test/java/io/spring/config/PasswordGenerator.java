package io.spring.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

/**
 * BCrypt密码生成工具类
 * 用于生成BCrypt加密后的密码，可直接存入数据库
 */
public class PasswordGenerator {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("========================================");
        System.out.println("   BCrypt 密码生成工具");
        System.out.println("========================================");
        System.out.println();
        
        while (true) {
            try {
                System.out.print("请输入要加密的明文密码（输入 exit 退出）: ");
                String input = scanner.nextLine().trim();
                
                if ("exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) {
                    System.out.println("感谢使用，再见！");
                    break;
                }
                
                if (input.isEmpty()) {
                    System.out.println("⚠️  密码不能为空，请重新输入\n");
                    continue;
                }
                
                // 生成BCrypt加密密码
                String encodedPassword = passwordEncoder.encode(input);
                
                System.out.println();
                System.out.println("✅ 加密成功！");
                System.out.println("----------------------------------------");
                System.out.println("明文密码: " + input);
                System.out.println("加密密码: " + encodedPassword);
                System.out.println("----------------------------------------");
                System.out.println();
                System.out.println("💡 提示：");
                System.out.println("   1. 每次生成的加密密码都不同（包含随机盐值）");
                System.out.println("   2. 但都可以验证同一个明文密码");
                System.out.println("   3. 复制加密密码到数据库即可使用");
                System.out.println();
                
                // 验证密码
                System.out.print("是否验证此密码？(y/n): ");
                String verifyChoice = scanner.nextLine().trim();
                
                if ("y".equalsIgnoreCase(verifyChoice) || "yes".equalsIgnoreCase(verifyChoice)) {
                    System.out.print("请输入要验证的密码: ");
                    String verifyPassword = scanner.nextLine().trim();
                    
                    boolean matches = passwordEncoder.matches(verifyPassword, encodedPassword);
                    if (matches) {
                        System.out.println("✅ 密码验证成功！\n");
                    } else {
                        System.out.println("❌ 密码验证失败！\n");
                    }
                }
                
                System.out.println();
                
            } catch (Exception e) {
                System.out.println("❌ 发生错误: " + e.getMessage() + "\n");
            }
        }
        
        scanner.close();
    }
    
    /**
     * 快速生成单个密码（可通过命令行参数调用）
     * 使用方式: java PasswordGenerator <password>
     */
    public static void generateSingle(String password) {
        if (password == null || password.isEmpty()) {
            System.err.println("错误: 密码不能为空");
            System.exit(1);
        }
        
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("明文密码: " + password);
        System.out.println("加密密码: " + encodedPassword);
    }
}
