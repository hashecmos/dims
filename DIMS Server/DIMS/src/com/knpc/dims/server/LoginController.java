package com.knpc.dims.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginController extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4734599896162671722L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			login( req, resp );
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doGet( req, resp );

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	
	public void login(HttpServletRequest req, HttpServletResponse resp ) throws Exception {
		
		
		/*String userName = req.getParameter("uname");
		String password = req.getParameter("psw");
		
		ContentService cs = new ContentService();
		//cs.login(userName,password);
		HttpSession session = req.getSession(false);
		
		if (session == null )
		{
			resp.sendRedirect("Login.jsp");
		}else{
			String userName = req.getParameter("uname");
			String password = req.getParameter("psw");
			System.out.println("User Name is :: "+userName+" and password is :: "+password);
			
			// Set the constants
			// Use for wsi
			String uri = "http://ecmdemo1:9080/wsi/FNCEWS40MTOM/";
			// Get the connection
			Connection conn = Factory.Connection.getConnection(uri);
			// Get the user context
			UserContext uc = UserContext.get();
			// Build the subject using the FileNetP8 stanza
			// Use FileNetP8 for the EJB transport (also the default)
			uc.pushSubject(UserContext.createSubject(conn,userName,password,"FileNetP8WSI" ));
			try
			{
				// Get the default domain
				Domain domain = Factory.Domain.getInstance(conn, null);
				// Get an object store
				ObjectStore os = Factory.ObjectStore.fetchInstance(domain,"ECM", null);
			}catch(Exception e){
				session.setAttribute("loginError", e.getMessage());
				
				resp.sendRedirect("Login.jsp");
			}
			session.setAttribute("uname", userName);
			resp.sendRedirect("Main.jsp");
		}
		session.setAttribute("uname", userName);
		resp.sendRedirect("Main.jsp");
		*/
	}
}
