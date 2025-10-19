package com.zh.mathplatform.infrastructure.api.imagesearch.sub;

import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取图片列表页面地址
 */
@Slf4j
public class GetImageFirstUrlApi {

    /**
     * 获取图片列表页面地址
     *
     * @param url
     * @return
     */
    public static String getImageFirstUrl(String url) {
        try {
            log.info("开始获取图片列表页面地址，URL: {}", url);
            
            // 使用 Jsoup 获取 HTML 内容
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .referrer("https://graph.baidu.com/")
                    .timeout(10000)
                    .get();

            // 获取所有 <script> 标签
            Elements scriptElements = document.getElementsByTag("script");
            log.info("找到 {} 个 script 标签", scriptElements.size());

            // 遍历找到包含 `firstUrl` 的脚本内容
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                if (scriptContent.contains("\"firstUrl\"")) {
                    log.info("找到包含 firstUrl 的脚本");
                    // 正则表达式提取 firstUrl 的值
                    Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*\"(.*?)\"");
                    Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        String firstUrl = matcher.group(1);
                        // 处理转义字符
                        firstUrl = firstUrl.replace("\\/", "/");
                        log.info("成功提取 firstUrl: {}", firstUrl);
                        return firstUrl;
                    }
                }
            }

            log.error("未找到包含 firstUrl 的脚本");
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未找到 firstUrl");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取图片列表页面地址失败，URL: {}, 错误信息: ", url, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取图片列表页面地址失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 请求目标 URL
        String url = "https://graph.baidu.com/s?card_key=&entrance=GENERAL&extUiData[isLogoShow]=1&f=all&isLogoShow=1&session_id=16250747570487381669&sign=1265ce97cd54acd88139901733452612&tpl_from=pc";
        String imageFirstUrl = getImageFirstUrl(url);
        System.out.println("搜索成功，结果 URL：" + imageFirstUrl);
    }
}

