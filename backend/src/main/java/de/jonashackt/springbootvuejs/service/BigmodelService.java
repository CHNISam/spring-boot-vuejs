package de.jonashackt.springbootvuejs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class BigmodelService {

    private final RestTemplate rest;
    private final String apiUrl;
    private final String apiKey;

    public BigmodelService(
        @Value("${bigmodel.api.url}") String apiUrl,
        @Value("${bigmodel.api.key}") String apiKey
    ) {
        this.rest = new RestTemplate();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    /**
     * 调用智谱 Chat API，生成摘要。
     * @param query  搜索关键词
     * @param briefs 每项包含 title/excerpt
     */
    public String summarize(String query, List<Map<String, String>> briefs) {
        // 1. 构造 prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("以下是搜索关键词：“")
              .append(query)
              .append("”的搜索结果，请基于这些结果生成一段简洁总结：\n");
        for (Map<String, String> b : briefs) {
            prompt.append("- 标题：").append(b.get("title"))
                  .append("，摘要：").append(b.get("excerpt")).append("\n");
        }

        // 2. 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 3. 构造消息体
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sys = new HashMap<>();
        sys.put("role", "system");
        sys.put("content", "你是一个聪明的摘要助手。");
        messages.add(sys);

        Map<String, String> usr = new HashMap<>();
        usr.put("role", "user");
        usr.put("content", prompt.toString());
        messages.add(usr);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "glm-4-flash");  // 请确保此模型在平台可用
        body.put("messages", messages);
        body.put("temperature", 0.7);
        body.put("max_tokens", 60);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        // 4. 调用远程 API
        ResponseEntity<Map> resp;
        try {
            resp = rest.exchange(apiUrl, HttpMethod.POST, req, Map.class);
        } catch (HttpClientErrorException.BadRequest ex) {
            String respBody = ex.getResponseBodyAsString();
            if (respBody != null && respBody.contains("\"code\":\"1211\"")) {
                return "AI 服务配置了不存在的模型，请检查配置";
            }
            throw ex;
        } catch (RestClientException ex) {
            throw new RuntimeException("调用 BigModel API 失败：网络或服务不可达", ex);
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RuntimeException("BigModel 返回非 2xx 或空 Body: " + resp.getStatusCode());
        }

        // 5. 解析响应
        Map<?, ?> data = resp.getBody();
        Object choicesObj = data.get("choices");
        if (!(choicesObj instanceof List)) {
            throw new RuntimeException("BigModel 返回结构不正确: " + data);
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) choicesObj;
        if (choices.isEmpty()) {
            return "未收到 AI 摘要";
        }

        Map<String, Object> first = choices.get(0);
        Object msgObj = first.get("message");
        if (!(msgObj instanceof Map)) {
            throw new RuntimeException("BigModel 返回结构不正确: " + first);
        }
        @SuppressWarnings("unchecked")
        Map<String, ?> message = (Map<String, ?>) msgObj;
        Object content = message.get("content");
        return content != null ? content.toString() : "未收到摘要内容";
    }
}
