package org.si.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.si.redistwitter.dao.MessageDao;
import org.si.redistwitter.object.User;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Servlet implementation class MessageServlet
 */
public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private MessageDao messageDao = new MessageDao();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("发推");
		String message = request.getParameter("message");
		System.out.println(message);
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("user");
		ServletContext ctxt = request.getSession().getServletContext();
		JedisPool pool = (JedisPool) ctxt.getAttribute("pool");
		Jedis conn = pool.getResource();
		if(user != null) {
			long mid = messageDao.addMessage(conn, user.getUid() + "", message);
			if(mid > 0) {
				HttpSession s = request.getSession(false);
				User u = (User) s.getAttribute("user");
				int newPosts = u.getPosts() + 1;
				u.setPosts(newPosts);
				s.setAttribute("user", u);
			}
			//request.getRequestDispatcher("/WEB-INF/jsp/user.jsp").forward(request, response);
			request.getRequestDispatcher("user").forward(request, response);
			//response.sendRedirect(request.getContextPath() + "/user");
		}
		
	}

}
