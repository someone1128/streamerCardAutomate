package vip.xiaonuo.entity;

import lombok.Data;

@Data
public class SentenceCardRequest {
    private String temp = "tempJin";
    private String color = "pure-color-27";
    private String title;
    private String content;
    private String author;
    private String translate;
    private String padding;
    private String titleSize;
    private String contentSize = "30";
    private String authorSize = "20";
    private String translateSize = "15";
    private SwitchConfig switchConfig;
    // 这里的宽高是小红书 3:4 比例
    private Integer height = 586;
    private Integer width = 440;

    public CardRequest buildCardRequest() {
        CardRequest cardRequest = new CardRequest();
        cardRequest.setTemp("tempJin");
        cardRequest.setColor(getColor());
        cardRequest.setTitle(getTitle());
        cardRequest.setContent(getContent());
        cardRequest.setAuthor(getAuthor());
        cardRequest.setWidth(getWidth());
        cardRequest.setHeight(getHeight());
        cardRequest.setTranslate(getTranslate());
        cardRequest.setPadding(getPadding());
        cardRequest.setTitleSize(getTitleSize());
        cardRequest.setContentSize(getContentSize());
        cardRequest.setAuthorSize(getAuthorSize());
        cardRequest.setTranslateSize(getTranslateSize());
        if (getSwitchConfig()!=null) {
            cardRequest.setSwitchConfig(getSwitchConfig());
        }else{
            SwitchConfig config = new SwitchConfig();
            config.setShowTitle("false");
            cardRequest.setSwitchConfig(config);
        }
        return cardRequest;
    }
}

