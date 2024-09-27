package vip.xiaonuo.ai;

import cn.hutool.core.util.StrUtil;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import vip.xiaonuo.utils.FastjsonUtils;
import vip.xiaonuo.utils.ListUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class ChatGptHelper {

    /**
     * gpt 读取处理
     *
     * @param reader
     * @param answerContent
     * @throws IOException
     */
    public static StringBuilder gptReaderHandler(BufferedReader reader, StringBuilder answerContent) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            //首先对行数据进行处理
            if (StrUtil.isBlank(line)) {
                continue;
            }
            line = line.replace("data:", "");
            if (StrUtil.contains(line, "[DONE]")) {
                continue;
            }
            String oneWord = catchTextGpt(line);
            if (oneWord == null) {
                continue;
            }
            answerContent.append(oneWord);
        }
        return answerContent;
    }

    /**
     * 处理文字打印
     */
    public static String catchTextGpt(String str) {
        try {
            List<ChatChoice> choices = FastjsonUtils.readValue(str, ChatCompletionResponse.class).getChoices();
            if (ListUtils.isBlank(choices)) {
                return "";
            }
            ChatChoice chatChoice = choices.get(0);
            Message message = chatChoice.getDelta();
            return message == null ? "" : message.getContent();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}
