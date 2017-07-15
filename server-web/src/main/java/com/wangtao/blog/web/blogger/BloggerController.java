package com.wangtao.blog.web.blogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wangtao.blog.common.constant.interfaces.ISystemBaseConstant;
import com.wangtao.blog.common.exception.blogger.BloggerException;
import com.wangtao.blog.common.response.ResponseParameterEntity;
import com.wangtao.blog.common.util.StringUtils;
import com.wangtao.blog.core.blogger.IBloggerService;
import com.wangtao.blog.core.region.IBaseRegionService;
import com.wangtao.blog.model.entity.blogger.BloggerEntity;
import com.wangtao.blog.web.base.AbstractBaseController;

/**
 * @ClassName:BloggerController
 * @author: KarlWang
 * @Description: TODO(博主控制层)
 * @date:2017年4月10日 下午4:26:31
 * @see com.wangtao.blog.web.blogger.BloggerController
 */
@Controller
@RequestMapping("/blogger")
public class BloggerController extends AbstractBaseController{

	private final static Logger logger = Logger.getLogger(BloggerController.class);
	
	@Autowired
	IBloggerService bloggerService;
	
	@Autowired
	IBaseRegionService baseRegionService;
	
/*	@RequestMapping(value = "/")
	public String index() {
		return "admin/login";
	}*/
	
	@RequestMapping("/login")
	public String login(HttpServletRequest request, 
			HttpServletResponse response, @ModelAttribute("resp") ResponseParameterEntity resp) {
		HttpSession session = request.getSession();
		Object userObj = session.getAttribute(ISystemBaseConstant.BLOGGER_LOGIN_SESSION_KEY);
		if(null != userObj) {
			return "blogger/home";
			// return "forward:blogger/home.jsp";
		}
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String verifyCode =  request.getParameter("verifyCode");
		if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(verifyCode)) {
			if(request.getSession().getAttribute("verifyCode").equals(verifyCode.toLowerCase())) {
				try{
					BloggerEntity loginBlogger = bloggerService.validate(userName,password,verifyCode);
					session.setAttribute(ISystemBaseConstant.BLOGGER_LOGIN_SESSION_KEY, loginBlogger);
					resp.setResultFlag(true);
				}catch(BloggerException e) {
					logger.error(e.getMessage(), e);
					resp.setResultFlag(false);
					resp.setMessage(e.getMessage());
				}
			} else{
				resp.setResultFlag(false);
				resp.setMessage("验证码错误！");
			}
			success(response, resp);
			return null;
		}
		return "blogger/login";
	}
	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("resp") ResponseParameterEntity resp) {
		HttpSession session = request.getSession();
		session.removeAttribute(ISystemBaseConstant.BLOGGER_LOGIN_SESSION_KEY);
		session.invalidate();
		resp.setResultFlag(true);
		success(response, resp);
		return "blogger/login";
	}
}