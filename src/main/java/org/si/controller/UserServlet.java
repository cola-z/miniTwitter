package org.si.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.si.redistwitter.dao.MessageDao;
import org.si.redistwitter.dao.UserDao;
import org.si.redistwitter.object.User;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Servlet implementation class UserServlet
 */
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDao userDao = new UserDao();
	private MessageDao messageDao = new MessageDao();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		String logout = request.getParameter("logout");
		String otherUid = request.getParameter("uid");
		if(otherUid != null && !"".equals(otherUid)) {
			//System.out.println(otherUid);
			HttpSession s = request.getSession(false);
			User user = (User) s.getAttribute("user");
			if(user == null) return;
			ServletContext ctxt = request.getSession().getServletContext();
			JedisPool pool = (JedisPool) ctxt.getAttribute("pool");
			Jedis conn = pool.getResource(); 
			int ret = userDao.followUser(conn, user.getUid() + "", otherUid);
			if(ret > 0) {
				user.setFollowing(user.getFollowing() + 1);
				s.setAttribute("user", user);
			}
			
            List<Map<String, String>> messages = messageDao.getStatusMessage(conn, user.getUid() + "", 1, 3);
			List<Map<String, String>> otherMessages = messageDao.getLotMessages(conn, user.getUid() + "");
			request.setAttribute("otherMessages", otherMessages);
			request.setAttribute("messages", messages);
			
			request.getRequestDispatcher("/WEB-INF/jsp/user.jsp").forward(request, response);
			return;
		}
		//System.out.println(logout);
		if(login != null && !"".equals(login)) {
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			return;
		}
		if(logout != null && !"".equals(logout)) {
			HttpSession session = request.getSession();
			//session.removeAttribute("user");
			session.invalidate();
			System.out.println(request.getContextPath());
			response.sendRedirect(request.getContextPath() + "/main");
			return;
		}
		request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession(false);
		User u = (User) s.getAttribute("user");
		ServletContext ctxt = request.getSession().getServletContext();
		JedisPool pool = (JedisPool) ctxt.getAttribute("pool");
		
		System.out.println("**************************************");
		Jedis conn = pool.getResource(); //?
		System.out.println("====================================");
		
		try {
			if(u != null) {
				//List<Map<String, String>> messages = messageDao.getStatusMessage(conn, u.getUid() + "", "profile", 1, 3);
				List<Map<String, String>> messages = messageDao.getStatusMessage(conn, u.getUid() + "", 1, 3);
				
				List<Map<String, String>> otherMessages = messageDao.getLotMessages(conn, u.getUid() + "");
				request.setAttribute("otherMessages", otherMessages);
				
				request.setAttribute("messages", messages);
				request.getRequestDispatcher("/WEB-INF/jsp/user.jsp").forward(request, response);
				return;
			}
			
			String login = request.getParameter("login");
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			
			
			if(login != null && !"".equals(login)) {
				User user = userDao.login(conn, username, password);
				System.out.println(user);
				if(user != null) {
					HttpSession session = request.getSession(false);
					if(session == null) session = request.getSession();
					//List<Map<String, String>> messages = messageDao.getStatusMessage(conn, user.getUid() + "", "profile", 1, 3);
					List<Map<String, String>> messages = messageDao.getStatusMessage(conn, user.getUid() + "", 1, 3);
					
					List<Map<String, String>> otherMessages = messageDao.getLotMessages(conn, user.getUid() + "");
					if(conn != null) conn.close();
					request.setAttribute("otherMessages", otherMessages);

					
					request.setAttribute("messages", messages);
					session.setAttribute("user", user);
					request.getRequestDispatcher("/WEB-INF/jsp/user.jsp").forward(request, response);
				} else {
					request.setAttribute("info", "用户名不存在或密码出错");
					request.getRequestDispatcher("/WEB-INF/jsp/info.jsp").forward(request, response);
				}
				return;
			}  else {
				long uid = userDao.addUser(conn, username, password);
				//long l = userDao.createProfile(conn, username, password, uid+"");
				if(uid > 0) {
					request.setAttribute("info", "注册成功");
					request.getRequestDispatcher("/WEB-INF/jsp/info.jsp").forward(request, response);
				}else{
					request.setAttribute("info", "注册失败");
					request.getRequestDispatcher("/WEB-INF/jsp/info.jsp").forward(request, response);
				}
			}
		} finally {
			conn.close();
		}
		
	}

}
