package vip.xiaonuo.entity;

import lombok.Data;

@Data
public class CardRequest {
    private String temp;
    private String color;
    private String icon;
    private String title;
    private String date;
    private String content;
    private String foreword;
    private String author;
    private String qrcodetitle;
    private String qrcodetext;
    private String qrcode;
    private String qrcodeImg;
    private String watermark;
    private String translate;
    private Integer width;
    private Integer height;
    private String padding;
    private String titleSize;
    private String contentSize;
    private String authorSize;
    private String fontScale;
    private String translateSize;
    private SwitchConfig switchConfig;
    // 加载字体
    private Boolean useLoadingFont = true;

}

