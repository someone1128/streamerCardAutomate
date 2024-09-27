package vip.xiaonuo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vip.xiaonuo.ai.ChatGptUtils;
import vip.xiaonuo.cosntants.ConfigConstants;
import vip.xiaonuo.cosntants.PromptConstants;
import vip.xiaonuo.entity.CardRequest;
import vip.xiaonuo.entity.SentenceCardRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class BatchCreateMain {

    public static void main(String[] args) {
        for (int i = 0; i < 70; i++) {
            try {
                // 生成选题，遍历选题
                String uuid = UUID.randomUUID() + UUID.randomUUID().toString() + UUID.randomUUID() + UUID.randomUUID();
                String text = ChatGptUtils.gptResp(StrUtil.format(PromptConstants.generateContent, uuid,uuid));
                System.out.println("text = " + text);
                JSONObject jsonObject = JSON.parseObject(text);
                List<SentenceCardRequest> sentenceList = jsonObject.getList("sentenceList", SentenceCardRequest.class);
                String folderPath = "G:\\Code\\JAVA\\streamerCardApiDemo\\streamerCardApiDemo\\img\\" + jsonObject.getString("topicName");

                // 随机金句颜色
                String color = "pure-color-"+ RandomUtil.randomInt(1,27);

                // 创建文件夹对象
                File folder = new File(folderPath);
                // 如果文件夹不存在则创建
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                // 创建 txt 文件
                String txtFileName = "topic_info.txt";
                File txtFile = new File(folder, txtFileName);

                // 写入 txt 文件内容
                String topicDescription = jsonObject.getString("topicDescription");
                List<String> topicTags = jsonObject.getJSONArray("topicTags").toJavaList(String.class);
                List<String> contentList = sentenceList.stream().map(SentenceCardRequest::getContent).collect(Collectors.toList());
                String txtContent = "Topic Description: " + topicDescription + "\n"
                        + "Topic Tags: " + String.join("\n", topicTags) + "\n"
                        + "Topic contentList: " + String.join("\n", contentList) + "\n";
                FileUtil.writeString(txtContent, txtFile, "UTF-8");

                // 根据选题遍历生成卡片创建文件夹，并创建一个 txt 作为
                for (SentenceCardRequest sentenceCardRequest : sentenceList) {
                    sentenceCardRequest.setColor(color);
                    try {
                        generateCardFromJson(sentenceCardRequest.buildCardRequest(), folderPath);
                    }catch (Exception e){
                        log.error("生成图片异常");
                        e.printStackTrace();
                    }
                }
            }catch (Exception ex){
                log.info("出现异常");
                ex.printStackTrace();
            }
        }
    }

    /**
     * 根据请求对象生成卡片保存路径
     * @param cardRequest
     */
    @SneakyThrows
    public static void generateCardFromJson(CardRequest cardRequest, String folderPath) {
        // 将请求对象转换为 JSON 字符串
        String requestBody = JSON.toJSONString(cardRequest);

        // 发起 POST 请求并获取响应
        HttpResponse response = HttpRequest.post(ConfigConstants.url)
                .body(requestBody)
                .setReadTimeout(999999)
                .setConnectionTimeout(999999)
                .header("Content-Type", "application/json")
                .execute();

        // 检查响应状态码
        if (response.getStatus() == 200) {
            // 获取响应的 byte 图片流
            byte[] imageBytes = response.bodyBytes();

            // 生成唯一的文件名
            String uniqueFileName = IdUtil.simpleUUID() + ".png";

            // 创建文件对象
            File imageFile = new File(folderPath, uniqueFileName);

            // 使用 BufferedImage 对图片进行裁剪
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            BufferedImage croppedImage = originalImage.getSubimage(1, 0, originalImage.getWidth() - 1, originalImage.getHeight());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(croppedImage, "png", bos);
            byte[] croppedImageBytes = bos.toByteArray();

            // 使用 Hutool 的 FileUtil 保存裁剪后的 byte 数据到文件
            FileUtil.writeBytes(croppedImageBytes, imageFile);

            System.out.println("裁剪后的图片已保存到: " + imageFile.getAbsolutePath());
        } else {
            System.err.println("请求失败，状态码: " + response.getStatus());
        }
    }


}