package application.servlets;

import application.logic.AppManager;
import application.logic.Customer;
import application.logic.SignedUser;
import application.utils.Constants;
import application.utils.ServletUtils;
import application.utils.SessionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CustomerRegServlet", urlPatterns = {"/customerReg"})
public class CustomerRegServlet extends HttpServlet{

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        AppManager appManager = ServletUtils.getAppManager(getServletContext());

        Customer customer = new Customer();
        customer.setUserName(request.getParameter(Constants.USERNAME));
        customer.setEmail(request.getParameter(Constants.EMAIL));
        customer.setPhone(request.getParameter(Constants.PHONE));
        customer.setPassword(request.getParameter(Constants.PASSWORD));
        customer.setPassword2(request.getParameter(Constants.VERIFY_PASSWORD));
        customer.setType(SignedUser.Type.CUSTOMER);

        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        if(usernameFromSession != null) {
            ServletUtils.redirect(response, "הינך רשום כבר במערכת", "index.html");
        }
        else if(!customer.isValidUser()) {
            //ServletUtils.redirect(response, "נא למלא שדות חובה", "location='customerReg.html';");
            ServletUtils.redirect(response, "נא למלא שדות חובה", "");

        }
        else if(!customer.isValidPassword()) {
           // ServletUtils.redirect(response, "שגיאה באישור סיסמא", "location='customerReg.html';");
            ServletUtils.redirect(response, "שגיאה באישור סיסמא", "");
        }
        else if(appManager.isUserExists(em, customer.getUserName())) {
            //ServletUtils.redirect(response, "שם משתמש כבר קיים, נא לבחור שם אחר", "location='customerReg.html';");
            ServletUtils.redirect(response, "שם משתמש כבר קיים, נא לבחור שם אחר", "");

        }
        else {
            try{
                em.getTransaction().begin();
                em.persist(customer);
                em.getTransaction().commit();
            }
            finally {
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
                em.close();
            }
            request.getSession(true).setAttribute(Constants.USERNAME, customer.getUserName());
            ServletUtils.redirect(response, "", "index.html");
        }
    }
}
