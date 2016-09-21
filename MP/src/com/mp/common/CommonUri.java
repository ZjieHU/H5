package com.mp.common;

public class CommonUri {

	// 访问服务器基础链接
	public static final String BASE_URL = "http://mp.renmaidao.com.cn/";

	// 验证码
	public static final String VER_URL = BASE_URL + "VerCodeServlet";

	// 登录
	public static final String LOGIN_URL = BASE_URL + "LoginServlet";

	// 新增名片
	public static final String NEW_URL = BASE_URL + "ADDUpdateServlet";

	// 名片列表
	public static final String MP_URL = BASE_URL + "SELServlet";
	
	// 删除名片
	public static final String DEL_URL = BASE_URL + "DELServlet";
	
	// 查询名片
	public static final String MPURL_URL = BASE_URL + "MPURLServlet";
	
	// 头像上传
	public static final String PIC_URL = BASE_URL + "ResponseServlet";

}
