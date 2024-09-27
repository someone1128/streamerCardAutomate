import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.core.io.FileUtil;
import vip.xiaonuo.entity.SwitchConfig;
import vip.xiaonuo.entity.CardRequest;

import java.io.File;

public class SaveImageRequest {

    public static void main(String[] args) {
        // 构建请求对象
        CardRequest cardRequest = new CardRequest();
        cardRequest.setTemp("tempA");
        cardRequest.setColor("#FF5733");
        cardRequest.setIcon("http://example.com/icon.png");
        cardRequest.setTitle("Sample Title");
        cardRequest.setDate("2024-08-06");
        cardRequest.setContent("This is the content of the card.");
        cardRequest.setForeword("This is the foreword.");
        cardRequest.setAuthor("Author Name");
        cardRequest.setQrcodetitle("QR Code Title");
        cardRequest.setQrcodetext("QR Code Text");
        cardRequest.setQrcode("http://example.com/qrcode");
        cardRequest.setQrcodeImg("http://example.com/qrcodeImg.png");
        cardRequest.setWatermark("Sample Watermark");
        cardRequest.setPadding("10px");
        cardRequest.setFontScale("1.2");

        SwitchConfig switchConfig = new SwitchConfig();
        switchConfig.setShowIcon("true");
        switchConfig.setShowDate("true");
        switchConfig.setShowTitle("true");
        switchConfig.setShowContent("true");
        switchConfig.setShowAuthor("true");
        switchConfig.setShowTextCount("false");
        switchConfig.setShowQRCode("true");
        switchConfig.setShowForeword("true");

        cardRequest.setSwitchConfig(switchConfig);

        // 将请求对象转换为 JSON 字符串
        String requestBody = JSON.toJSONString(cardRequest);

        // 发起 POST 请求并获取响应
        HttpResponse response = HttpRequest.post("http://localhost:3003/saveImg")
                .body(requestBody)
                .setReadTimeout(999999)
                .setConnectionTimeout(999999)
                .header("Content-Type", "application/json")
                .execute();

        // 检查响应状态码
        // 检查响应状态码
        if (response.getStatus() == 200) {
            // 获取响应的 byte 图片流
            byte[] imageBytes = response.bodyBytes();

            // 生成唯一的文件名
            String uniqueFileName = IdUtil.simpleUUID() + ".png";

            // 指定文件夹路径
            String folderPath = "G:\\Code\\JAVA\\streamerCardApiDemo\\streamerCardApiDemo\\img";

            // 创建文件夹对象
            File folder = new File(folderPath);
            // 如果文件夹不存在则创建
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 创建文件对象
            File imageFile = new File(folder, uniqueFileName);

            // 使用 Hutool 的 FileUtil 保存 byte 数据到文件
            FileUtil.writeBytes(imageBytes, imageFile);

            System.out.println("图片已保存到: " + imageFile.getAbsolutePath());
        } else {
            System.err.println("请求失败，状态码: " + response.getStatus());
        }

    }
}


