package presentation.web.controller;

import business.Login;
import business.exceptions.BackendException;
import business.exceptions.UserException;
import presentation.data.BrowseSelectData;
import presentation.data.LoginData;
import presentation.data.SessionCache;
import presentation.web.util.WebSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

/**
 * Servlet implementation class LogInController
 */

public class LogInController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private LoginData loginData = new LoginData();
	private static final Logger LOG = Logger.getLogger(LogInController.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogInController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(request.getServletPath());
		if(request.getServletPath().equals("/logout")){
			WebSession.INSTANCE.logout(request.getSession());
			response.sendRedirect(request.getContextPath() + "/login?msg=successfully logged out");
		} else {
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}


	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("it is post log in");
		int custid =  Integer.parseInt(request.getParameter("id"));
		String password =  request.getParameter("password");

		Login login = new Login(custid, password);
		LOG.config("Login found: " + login.getCustId());
		boolean loginSuccessful;
		try {
			int authorizationLevel = loginData.authenticate(login);
			loginData.loadCustomerForWeb(request.getSession(), login, authorizationLevel);
			request.getSession().setAttribute(SessionCache.SHOP_CART, BrowseSelectData.INSTANCE.obtainCurrentShoppingCartSubsystem());
			//CustomerSubsystem customerSubsystem = new CustomerSubsystemFacade();
			WebSession.INSTANCE.sync(request.getSession(), SessionCache.getInstance());
			loginSuccessful = true;
		} catch(UserException | BackendException e) {
			loginSuccessful = false;
			LOG.warning(e.getMessage());
			response.sendRedirect(request.getContextPath() + "/login?msg=" + URLEncoder.encode(e.getMessage()));
			return;
		}

		if(loginSuccessful){
			HttpSession session = request.getSession();
			String redirectTo = (String) session.getAttribute("login_redirect_to");
			if(redirectTo != null){
				session.removeAttribute("login_redirect_to");
				response.sendRedirect(redirectTo);
			} else {
				response.sendRedirect(request.getContextPath() + "/");
			}

		}
		else{
			response.sendRedirect(request.getContextPath() + "/login?msg=Wrong username or password");
		}
		
	}

}
