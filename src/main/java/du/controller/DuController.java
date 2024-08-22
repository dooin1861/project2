package du.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import du.service.BoardService;
import mybatis.SqlMapConfig;
import du.db.BoardDao;
import du.db.BoardDto;
import du.db.MemberDao;
import du.db.MemberDto;
import du.db.MemberMapper;

/**
 * Servlet implementation class DuController
 */
@WebServlet("/")
public class DuController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private SqlSessionFactory sqlSessionFactory;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DuController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String view = null;

		String uri = request.getRequestURI();
		String conPath = request.getContextPath();
		String com = uri.substring(conPath.length());

		if (com.equals("/")) { // 사용자가 루트 경로를 요청하면 main.jsp로 이동
			view = "main.jsp";
			
		

		} else if (com.equals("/list")) {
			String tmp = request.getParameter("page");
			int pageNo = (tmp != null && tmp.length() > 0) ? Integer.parseInt(tmp) : 1;

			request.setAttribute("msgList", new BoardService().getMsgList(pageNo));
			request.setAttribute("pgnList", new BoardService().getPagination(pageNo));
			System.out.println(new BoardService().getPagination(pageNo));
			view = "list.jsp";

		} else if (com.equals("/loginForm")) {
			view = "sign-in/loginForm.jsp";

		} else if (com.equals("/logout")) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate(); // 로그아웃 시 사용자 정보 삭제 = 세션 무효화
			}
			view = "redirect:" + request.getContextPath();

		} else if (com.equals("/login")) {
			request.setCharacterEncoding("utf-8");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			System.out.println(email + ", " + password);
			MemberDto memberDto = new MemberDto(0, "", email, password, "");
			MemberDao memberDao = new MemberDao();
			int login = memberDao.isMember(memberDto);
			if (login == 1) {
				System.out.println("로그인 성공");
				memberDto = memberDao.findMember(memberDto);
				System.out.println(memberDto);
				HttpSession session = request.getSession();
				session.setAttribute("customInfo", memberDto);
				session.setAttribute("userLoggedIn", true); // request가 아닌 session 상태여야 정보가 유지됨. 

			} else {
				System.out.println("로그인 실패");
			}
			view = "main.jsp";
			
		} else if (com.equals("/view")) {
			int num = Integer.parseInt(request.getParameter("num"));

			request.setAttribute("msg", new BoardService().getMsg(num));
			view = "view.jsp";

		} else if (com.equals("/write")) {
			String tmp = request.getParameter("num");
			int num = (tmp != null && tmp.length() > 0) ? Integer.parseInt(tmp) : 0;

			BoardDto dto = new BoardDto();
			String action = "insert";

			if (num > 0) {
				dto = new BoardService().getMsgForWrite(num);
				action = "update?num=" + num;
			}

			request.setAttribute("msg", dto);
			request.setAttribute("action", action);
			view = "write.jsp";

		} else if (com.equals("/insert")) {
			request.setCharacterEncoding("utf-8");
			String writer = request.getParameter("writer");
			String title = request.getParameter("title");
			String content = request.getParameter("content");

			try {
				new BoardService().writeMsg(writer, title, content);
				view = "redirect:list";

			} catch (Exception e) {
				request.setAttribute("errorMessage", e.getMessage());
				view = "errorBack.jsp";
			}

		} else if (com.equals("/update")) {
			request.setCharacterEncoding("utf-8");
			int num = Integer.parseInt(request.getParameter("num"));
			String writer = request.getParameter("writer");
			String title = request.getParameter("title");
			String content = request.getParameter("content");

			try {
				new BoardService().updateMsg(writer, title, content, num);
				view = "redirect:list";

			} catch (Exception e) {
				request.setAttribute("errorMessage", e.getMessage());
				view = "errorBack.jsp";
			}

		} else if (com.equals("/delete")) {
			int num = Integer.parseInt(request.getParameter("num"));

			new BoardService().deleteMsg(num);
			view = "redirect:list";
			
		} else if (com.equals("/registerForm")) {
			view = "register/register.jsp";

		} else if (com.equals("/register")) {
			request.setCharacterEncoding("UTF-8");
		    String email = request.getParameter("email");
		    String password = request.getParameter("password");
		    String name = request.getParameter("name");
		    
		    System.out.println(email);   // 작동 테스트
		    System.out.println(password);
		    System.out.println(name);
		    
		    MemberDto memberDto = new MemberDto(0, name, email, password, "");
		    new MemberDao().insertMember(memberDto);
		    
		    view = "main.jsp";
		}
		    
      
		
		// view에 담긴 문자열에 따라 포워딩 또는 리다이렉팅
		if (view.startsWith("redirect:")) {
			response.sendRedirect(view.substring(9));
		} else {
			request.getRequestDispatcher(view).forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
