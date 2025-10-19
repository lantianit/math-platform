package com.zh.mathplatform.infrastructure.api.imagesearch.sub;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图的页面地址
 */
@Slf4j
public class GetImagePageUrlApi {

    /**
     * 获取图片页面地址
     *
     * @param imageUrl
     * @return
     */
    public static String getImagePageUrl(String imageUrl) {
        // 1. 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;

        try {
            // 2. 发送 POST 请求到百度接口
            HttpResponse response = HttpRequest.post(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Referer", "https://graph.baidu.com/")
                    .form(formData)
                    .timeout(10000)
                    .execute();
            
            // 记录响应状态和内容用于调试
            int statusCode = response.getStatus();
            String responseBody = response.body();
            log.info("百度接口响应状态码: {}", statusCode);
            log.info("百度接口响应内容: {}", responseBody);
            
            // 判断响应状态
            if (HttpStatus.HTTP_OK != statusCode) {
                log.error("接口调用失败，状态码: {}, 响应: {}", statusCode, responseBody);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败，状态码: " + statusCode);
            }
            
            // 解析响应
            Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);

            // 3. 处理响应结果
            if (result == null) {
                log.error("响应解析失败，返回 null");
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "响应解析失败");
            }
            
            Object statusObj = result.get("status");
            log.info("接口返回 status: {}", statusObj);
            
            if (!Integer.valueOf(0).equals(statusObj)) {
                log.error("接口返回错误状态: {}, 完整响应: {}", statusObj, result);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口返回错误状态: " + statusObj);
            }
            
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (data == null) {
                log.error("响应中没有 data 字段");
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "响应中没有 data 字段");
            }
            
            String rawUrl = (String) data.get("url");
            if (rawUrl == null || rawUrl.isEmpty()) {
                log.error("响应中没有 url 字段");
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效结果");
            }
            
            // 对 URL 进行解码
            String searchResultUrl = URLDecoder.decode(rawUrl, StandardCharsets.UTF_8);
            log.info("获取到搜索结果 URL: {}", searchResultUrl);
            return searchResultUrl;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("搜索失败，图片URL: {}, 错误信息: ", imageUrl, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        String result = getImagePageUrl(imageUrl);
        System.out.println("搜索成功，结果 URL：" + result);
    }
}

