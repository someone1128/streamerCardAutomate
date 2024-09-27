package vip.xiaonuo.cosntants;

/**
 * 配置
 */
public class ConfigConstants {

    public static final String url = "https://api.fantasyfinal.cn/";
    //public static final String url = "https://api.deepseek.com/";

    public static final String apiKey = "sk-9Zg5LKE9yMK3tvWRE594DfAf484d4bE6Be41B481A4C956A9";

    /**
     * 使用模型，默认 claude-3.5
     */
    public static final String chatModel = "claude-3-5-sonnet-20240620";

    /**
     * 这个不知道啥子意思就别动就可以了，一般不用改
     */
    public static final Integer maxToken = 2048;

    /**
     * 流光卡片 api 地址，记得需要先运行或者部署流光卡片 API
     */
    public static final String createImgUrl = "http://localhost:3003/saveImg";

}
