package presentation.web.filter;

import presentation.web.util.WebSession;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Mo nuaimat on 4/18/17.
 */
@WebFilter
public class AuthenticationFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(AuthenticationFilter.class.getName());


    @Override
    public void init(FilterConfig config)
            throws ServletException {

    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String requestPath = request.getRequestURI();
        boolean isLoggedIn = WebSession.INSTANCE.isLoggedIn(request.getSession());

        if (needsAuthentication(requestPath, request) && !isLoggedIn) {
        	LOG.info("Redirecting to " + request.getContextPath() + "/login");
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            chain.doFilter(req, res); // Logged-in user found, so just continue request.
        }
    }


    //basic validation of pages that do not require authentication
    private boolean needsAuthentication(String url, HttpServletRequest request) {

        if(url.endsWith(".css") || url.endsWith(".js") || 
        		url.equals(request.getContextPath() + "/login") || 
        		url.equals(request.getContextPath() + "/logout")|| 
                url.startsWith(request.getContextPath() + "/product") ||
                url.startsWith(request.getContextPath() + "/cart") ||
                url.equals("/") ||
                url.startsWith(request.getContextPath() + "/images/")
                ){
            return false;
        }
        return true; // for everything else
    }

    @Override
    public void destroy() {
        // If you have assigned any expensive resources as field of
        // this Filter class, then you could clean/close them here.
    }
}
