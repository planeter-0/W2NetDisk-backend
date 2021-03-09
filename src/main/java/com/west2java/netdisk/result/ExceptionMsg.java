
package com.west2java.netdisk.result;
//实现响应的枚举类
public enum ExceptionMsg {
	SUCCESS("200", "操作成功"),
	FAILED("999999","操作失败"),
    UserNameUsed("000100","该用户名已被使用"),
    AUTHENTICATION_FAILED("000400","认证失败，请重定向至登陆页面"),
    LimitSize("000310","超出网盘容量限制"),
    UNPASSED("000300","未审核或审核未通过"),
    ParamError("000001", "参数错误！"),

    FileEmpty("000400","上传文件为空"),
    LimitPictureSize("000401","图片大小必须小于100M"),
    LimitPictureType("000402","图片格式必须为'jpg'、'png'、'jpge'、'gif'、'bmp'")
    ;
   private ExceptionMsg(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    private String code;
    private String msg;
    
	public String getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}

    
}

