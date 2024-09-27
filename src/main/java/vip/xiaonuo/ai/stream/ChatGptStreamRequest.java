package vip.xiaonuo.ai.stream;

import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import vip.xiaonuo.cosntants.ConfigConstants;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄志源大魔王
 * @date 2023/5/8 14:14
 * @project snowy-master
 * @company 智影科技
 * @description
 */
@Slf4j
@Data
public class ChatGptStreamRequest {

	public static OpenAiStreamHttpClient client;

	@Getter
	private OpenAiStreamHttpClient.Builder builder;

	public static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());

	public static OkHttpClient okHttpClient = new OkHttpClient
			.Builder()
			.addInterceptor(httpLoggingInterceptor)
			.connectTimeout(30, TimeUnit.SECONDS)
			.writeTimeout(60, TimeUnit.SECONDS)
			.readTimeout(60, TimeUnit.SECONDS)
			.build();

	public ChatGptStreamRequest() {
		//！！！！千万别再生产或者测试环境打开BODY级别日志！！！！
		//！！！生产或者测试环境建议设置为这三种级别：NONE,BASIC,HEADERS,！！！
		httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

		builder = OpenAiStreamHttpClient.builder()
				// 自定义key的获取策略：默认KeyRandomStrategy
				.keyStrategy(new KeyRandomStrategy())
				.okHttpClient(okHttpClient)
				.apiHost("https://api.deepseek.com/");
	}

	/**
	 * 上下文聊天
	 */
	public InputStream contextStreamChat(ChatCompletion chatCompletion) {
		builder.apiHost(ConfigConstants.url);
		builder.apiKey(Arrays.asList(ConfigConstants.apiKey));
		client = builder.build();
		InputStream inputStream = client.streamChatCompletion(chatCompletion);
		return inputStream;
	}


}
