package com.zh.mathplatform.infrastructure.utils;

import java.awt.Color;

/**
 * 颜色相似度计算工具类
 * 使用欧几里得距离算法计算两个颜色之间的相似度
 * 
 * @author zhanghao
 */
public class ColorSimilarUtils {

    private ColorSimilarUtils() {
        // 工具类不需要实例化
    }

    /**
     * 计算两个颜色的相似度（基于RGB颜色对象）
     *
     * @param color1 第一个颜色
     * @param color2 第二个颜色
     * @return 相似度（0到1之间，1为完全相同）
     */
    public static double calculateSimilarity(Color color1, Color color2) {
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        // 计算欧氏距离
        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));

        // 计算相似度（距离越小，相似度越高）
        // 最大可能距离是 sqrt(3 * 255^2) ≈ 441.67
        return 1 - distance / Math.sqrt(3 * Math.pow(255, 2));
    }

    /**
     * 根据十六进制颜色代码计算相似度
     * 
     * 支持多种格式：
     * - 标准格式：#FF0000 或 #ff0000
     * - 0x格式：0xFF0000 或 0xff0000
     * - 短格式：0xF00 会被转为标准的 0xFF0000
     *
     * @param hexColor1 第一个颜色的十六进制代码（如 0xFF0000、#FF0000）
     * @param hexColor2 第二个颜色的十六进制代码（如 0xFE0101、#FE0101）
     * @return 相似度（0到1之间，1为完全相同）
     */
    public static double calculateSimilarity(String hexColor1, String hexColor2) {
        Color color1 = parseHexColor(hexColor1);
        Color color2 = parseHexColor(hexColor2);
        return calculateSimilarity(color1, color2);
    }

    /**
     * 解析十六进制颜色字符串为 Color 对象
     * 兼容多种格式（0xFF0000、#FF0000、0xF00等）
     * 
     * @param hexColor 十六进制颜色字符串
     * @return Color 对象
     */
    private static Color parseHexColor(String hexColor) {
        if (hexColor == null || hexColor.isEmpty()) {
            throw new IllegalArgumentException("颜色值不能为空");
        }
        
        // 去除前缀和空格
        String cleanHex = hexColor.trim();
        if (cleanHex.startsWith("#")) {
            cleanHex = "0x" + cleanHex.substring(1);
        }
        if (!cleanHex.startsWith("0x") && !cleanHex.startsWith("0X")) {
            cleanHex = "0x" + cleanHex;
        }
        
        try {
            // 解析为整数后转换为6位十六进制
            int colorValue = Integer.decode(cleanHex);
            return new Color(colorValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的颜色格式: " + hexColor, e);
        }
    }

    /**
     * 标准化十六进制颜色字符串
     * 统一转换为 0x 前缀的6位十六进制格式
     * 
     * @param hexColor 输入的颜色字符串
     * @return 标准化后的颜色字符串（如 0xFF0000）
     */
    public static String normalizeHexColor(String hexColor) {
        Color color = parseHexColor(hexColor);
        return String.format("0x%06X", color.getRGB() & 0xFFFFFF);
    }
}

