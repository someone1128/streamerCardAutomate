package vip.xiaonuo.ai.stream;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.OpenAiApi;
import com.unfbx.chatgpt.constant.OpenAIConst;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.completions.Completion;
import com.unfbx.chatgpt.entity.embeddings.Embedding;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import com.unfbx.chatgpt.exception.BaseException;
import com.unfbx.chatgpt.exception.CommonError;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.function.KeyStrategyFunction;
import com.unfbx.chatgpt.interceptor.DefaultOpenAiAuthInterceptor;
import com.unfbx.chatgpt.interceptor.DynamicKeyOpenAiAuthInterceptor;
import com.unfbx.chatgpt.interceptor.OpenAiAuthInterceptor;
import com.unfbx.chatgpt.sse.ConsoleEventSourceListener;
import io.reactivex.Single;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 描述： open ai 客户端
 *
 * @author https:www.unfbx.com
 * 		2023-02-28
 */

@Slf4j
@Data
public class OpenAiStreamHttpClient {
	@Getter
	@NotNull
	private List<String> apiKey;
	/**
	 * 自定义api host使用builder的方式构造client
	 */
	@Getter
	private String apiHost;
	/**
	 * 自定义的okHttpClient
	 * 如果不自定义 ，就是用sdk默认的OkHttpClient实例
	 */
	@Getter
	private OkHttpClient okHttpClient;

	/**
	 * api key的获取策略
	 */
	@Getter
	private KeyStrategyFunction<List<String>, String> keyStrategy;

	@Getter
	private OpenAiApi openAiApi;

	/**
	 * 自定义鉴权处理拦截器<br/>
	 * 可以不设置，默认实现：DefaultOpenAiAuthInterceptor <br/>
	 * 如需自定义实现参考：DealKeyWithOpenAiAuthInterceptor
	 *
	 * @see DynamicKeyOpenAiAuthInterceptor
	 * @see DefaultOpenAiAuthInterceptor
	 */
	@Getter
	private OpenAiAuthInterceptor authInterceptor;

	/**
	 * 构造实例对象
	 *
	 * @param builder
	 */
	private OpenAiStreamHttpClient(Builder builder) {
		if (CollectionUtil.isEmpty(builder.apiKey)) {
			throw new BaseException(CommonError.API_KEYS_NOT_NUL);
		}
		apiKey = builder.apiKey;

		if (StrUtil.isBlank(builder.apiHost)) {
			builder.apiHost = OpenAIConst.OPENAI_HOST;
		}
		apiHost = builder.apiHost;

		if (Objects.isNull(builder.keyStrategy)) {
			builder.keyStrategy = new KeyRandomStrategy();
		}
		keyStrategy = builder.keyStrategy;

		if (Objects.isNull(builder.authInterceptor)) {
			builder.authInterceptor = new DefaultOpenAiAuthInterceptor();
		}
		authInterceptor = builder.authInterceptor;
		//设置apiKeys和key的获取策略
		authInterceptor.setApiKey(this.apiKey);
		authInterceptor.setKeyStrategy(this.keyStrategy);

		if (Objects.isNull(builder.okHttpClient)) {
			builder.okHttpClient = this.okHttpClient();
		} else {
			//自定义的okhttpClient  需要增加api keys
			builder.okHttpClient = builder.okHttpClient
					.newBuilder()
					.addInterceptor(authInterceptor)
					.build();
		}
		okHttpClient = builder.okHttpClient;

		this.openAiApi = new Retrofit.Builder()
				.baseUrl(apiHost)
				.client(okHttpClient)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(JacksonConverterFactory.create())
				.build().create(OpenAiApi.class);
	}

	/**
	 * 文本转换向量
	 * @param embedding
	 * @return
	 */
	public EmbeddingResponse embeddings(Embedding embedding) {
		Single<EmbeddingResponse> embeddings = this.openAiApi.embeddings(embedding);
		return embeddings.blockingGet();
	}

	public EmbeddingResponse embeddings(List<String> input) {
		Embedding embedding = Embedding.builder().input(input).build();
		return this.embeddings(embedding);
	}

	/**
	 * 构造
	 *
	 * @return
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * 创建默认的OkHttpClient
	 */
	private OkHttpClient okHttpClient() {
		if (Objects.isNull(this.authInterceptor)) {
			this.authInterceptor = new DefaultOpenAiAuthInterceptor();
		}
		this.authInterceptor.setApiKey(this.apiKey);
		this.authInterceptor.setKeyStrategy(this.keyStrategy);
		OkHttpClient okHttpClient = new OkHttpClient
				.Builder()
				.addInterceptor(this.authInterceptor)
				.connectTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(50, TimeUnit.SECONDS)
				.readTimeout(50, TimeUnit.SECONDS)
				.build();
		return okHttpClient;
	}

	public InputStream streamChatCompletion(ChatCompletion chatCompletion)  {
		if (!chatCompletion.isStream()) {
			chatCompletion.setStream(true);
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			String requestBody = mapper.writeValueAsString(chatCompletion);
			String url;
			if (this.apiHost.contains("https://api.deepseek.com/")) {
				url = this.apiHost + "chat/completions";
			}else{
				url = this.apiHost + "v1/chat/completions";
			}
			Request request = new Request.Builder()
					.url(url)
					.post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
					.build();
			System.out.println("url = " + url);
			System.out.println("requestBody = " + requestBody);
			Response execute = okHttpClient.newCall(request).execute();
			ResponseBody body = execute.body();
			return body.byteStream();
		} catch (JsonProcessingException e) {
			String msg = "openAI 请求参数解析错误：";
			log.error(msg, e);
		} catch (IOException e) {
			String msg = "openAI 请求连接超时：";
			log.error(msg, e);
		}
		return null;
	}

	public static final class Builder {
		private @NotNull
		List<String> apiKey;
		/**
		 * api请求地址，结尾处有斜杠
		 *
		 * @see OpenAIConst
		 */
		private String apiHost;

		/**
		 * 自定义OkhttpClient
		 */
		private OkHttpClient okHttpClient;


		/**
		 * api key的获取策略
		 */
		private KeyStrategyFunction keyStrategy;

		/**
		 * 自定义鉴权拦截器
		 */
		private OpenAiAuthInterceptor authInterceptor;

		public Builder() {
		}

		public Builder apiKey(@NotNull List<String> val) {
			apiKey = val;
			return this;
		}

		/**
		 * @param val api请求地址，结尾处有斜杠
		 * @return
		 * @see OpenAIConst
		 */
		public Builder apiHost(String val) {
			apiHost = val;
			return this;
		}

		public Builder keyStrategy(KeyStrategyFunction val) {
			keyStrategy = val;
			return this;
		}

		public Builder okHttpClient(OkHttpClient val) {
			okHttpClient = val;
			return this;
		}

		public Builder authInterceptor(OpenAiAuthInterceptor val) {
			authInterceptor = val;
			return this;
		}

		public OpenAiStreamHttpClient build() {
			return new OpenAiStreamHttpClient(this);
		}
	}
}
