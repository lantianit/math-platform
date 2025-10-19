package com.zh.mathplatform.infrastructure.api.imagesearch.sub;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zh.mathplatform.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 获取图片列表
 */
@Slf4j
public class GetImageListApi {

    /**
     * 获取图片列表
     *
     * @param url
     * @return
     */
    public static List<ImageSearchResult> getImageList(String url) {
        try {
            log.info("开始获取图片列表，URL: {}", url);
            
            // 发起GET请求
            HttpResponse response = HttpUtil.createGet(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Referer", "https://graph.baidu.com/")
                    .execute();

            // 获取响应内容
            int statusCode = response.getStatus();
            String body = response.body();
            
            log.info("图片列表接口响应状态码: {}", statusCode);

            // 处理响应
            if (statusCode == 200) {
                // 解析 JSON 数据并处理
                List<ImageSearchResult> results = processResponse(body);
                log.info("成功获取 {} 张图片", results.size());
                return results;
            } else {
                log.error("接口调用失败，状态码: {}, 响应: {}", statusCode, body);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败，状态码: " + statusCode);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取图片列表失败，URL: {}, 错误信息: ", url, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取图片列表失败: " + e.getMessage());
        }
    }

    /**
     * 处理接口响应内容
     *
     * @param responseBody 接口返回的JSON字符串
     */
    private static List<ImageSearchResult> processResponse(String responseBody) {
        // 解析响应对象
        JSONObject jsonObject = new JSONObject(responseBody);
        if (!jsonObject.containsKey("data")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未获取到图片列表");
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (!data.containsKey("list")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未获取到图片列表");
        }
        JSONArray list = data.getJSONArray("list");
        return JSONUtil.toList(list, ImageSearchResult.class);
    }

    public static void main(String[] args) {
        String url = "https://graph.baidu.com/ajax/pcsimi?carousel=503&entrance=GENERAL&extUiData%5BisLogoShow%5D=1&inspire=general_pc&limit=30&next=2&render_type=card&session_id=16250747570487381669&sign=1265ce97cd54acd88139901733452612&tk=4caaa&tpl_from=pc";
        List<ImageSearchResult> imageList = getImageList(url);
        System.out.println("搜索成功" + imageList);
    }
}

