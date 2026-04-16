package io.spring.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired
    private ISysConfigService sysConfigService;
    
    /**
     * 发送配置变更通知邮件
     */
    public void sendConfigChangeNotification(String configKey, String oldValue, String newValue, 
                                            String operatorName, String operationType) {
        if (!sysConfigService.isEmailEnabled()) {
            return;
        }
        
        if (mailSender == null) {
            return;
        }
        
        try {
            String from = sysConfigService.getConfigValue("email.from");
            String to = sysConfigService.getConfigValue("email.to.admin");
            
            if (from == null || from.isEmpty() || to == null || to.isEmpty()) {
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("配置变更通知 - " + configKey);
            
            String content = buildEmailContent(configKey, oldValue, newValue, operatorName, operationType);
            message.setText(content);
            
            mailSender.send(message);
        } catch (Exception e) {
            // 邮件发送失败不影响主流程
            System.err.println("发送邮件失败: " + e.getMessage());
        }
    }
    
    private String buildEmailContent(String configKey, String oldValue, String newValue, 
                                    String operatorName, String operationType) {
        StringBuilder sb = new StringBuilder();
        sb.append("配置变更通知\n");
        sb.append("========================\n\n");
        sb.append("操作类型: ").append(operationType).append("\n");
        sb.append("配置键: ").append(configKey).append("\n");
        sb.append("操作人: ").append(operatorName).append("\n\n");
        
        if (oldValue != null) {
            sb.append("修改前的值:\n").append(oldValue).append("\n\n");
        }
        
        sb.append("修改后的值:\n").append(newValue).append("\n\n");
        sb.append("========================\n");
        sb.append("此邮件为系统自动发送，请勿回复。");
        
        return sb.toString();
    }
}
