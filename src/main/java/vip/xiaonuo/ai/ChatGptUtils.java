package vip.xiaonuo.ai;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.BaseMessage;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.completions.Completion;
import com.unfbx.chatgpt.entity.completions.CompletionResponse;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;
import vip.xiaonuo.ai.stream.ChatGptStreamRequest;
import vip.xiaonuo.ai.stream.OpenAiStreamHttpClient;
import vip.xiaonuo.cosntants.ConfigConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class ChatGptUtils {

    private static ChatGptStreamRequest chatGptStreamRequest = new ChatGptStreamRequest();

    /**
     * 测试对话
     */
    @Test
    public void test1(){
        gptResp("hello");
    }

    public static String gptResp(String prompt){
        //查询当前会话 3 条上下文聊天
        Message message = Message.builder().role(Message.Role.USER).content(prompt).build();
        ChatCompletion chatCompletion = ChatCompletion
                .builder()
                .messages(Collections.singletonList(message))
                .model(ConfigConstants.chatModel)
                .maxTokens(ConfigConstants.maxToken)
                .build();
        // 发送 gpt 判断
        InputStream is = chatGptStreamRequest.contextStreamChat(chatCompletion);
        StringBuilder answerContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        try {
            ChatGptHelper.gptReaderHandler(reader, answerContent);
        } catch (Exception e) {
            log.info("gpt读取异常");
            e.printStackTrace();
        }
        return answerContent.toString();
    }


}
