package io.spring.config.utils;

import java.util.ArrayList;
import java.util.List;

public class DiffUtils {
    
    /**
     * 简单的文本差异对比
     */
    public static List<String> diff(String oldText, String newText) {
        List<String> diffResult = new ArrayList<>();
        
        if (oldText == null && newText == null) {
            return diffResult;
        }
        
        if (oldText == null) {
            diffResult.add("+ " + newText);
            return diffResult;
        }
        
        if (newText == null) {
            diffResult.add("- " + oldText);
            return diffResult;
        }
        
        String[] oldLines = oldText.split("\n");
        String[] newLines = newText.split("\n");
        
        int maxLen = Math.max(oldLines.length, newLines.length);
        
        for (int i = 0; i < maxLen; i++) {
            String oldLine = i < oldLines.length ? oldLines[i] : null;
            String newLine = i < newLines.length ? newLines[i] : null;
            
            if (oldLine == null) {
                diffResult.add("+ " + newLine);
            } else if (newLine == null) {
                diffResult.add("- " + oldLine);
            } else if (!oldLine.equals(newLine)) {
                diffResult.add("- " + oldLine);
                diffResult.add("+ " + newLine);
            }
        }
        
        return diffResult;
    }
    
    /**
     * 生成HTML格式的diff展示
     */
    public static String generateHtmlDiff(String oldText, String newText) {
        List<String> diff = diff(oldText, newText);
        StringBuilder html = new StringBuilder();
        
        html.append("<div class='diff-container'>");
        
        for (String line : diff) {
            if (line.startsWith("+ ")) {
                html.append("<div class='diff-line added'>").append(escapeHtml(line)).append("</div>");
            } else if (line.startsWith("- ")) {
                html.append("<div class='diff-line removed'>").append(escapeHtml(line)).append("</div>");
            } else {
                html.append("<div class='diff-line unchanged'>").append(escapeHtml(line)).append("</div>");
            }
        }
        
        html.append("</div>");
        return html.toString();
    }
    
    private static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}
