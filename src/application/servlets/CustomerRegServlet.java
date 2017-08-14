package application.servlets;

import application.logic.*;
import application.utils.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
            sendAlert(response, "הינך רשום כבר במערכת", "location='index.html';");
        }
        else if(!customer.isValidUser()) {
            sendAlert(response, "נא למלא שדות חובה", "location='customerReg.html';");
        }
        else if(!customer.isValidPassword()) {
            sendAlert(response, "שגיאה באישור סיסמא", "location='customerReg.html';");
        }
        else if(appManager.isUserExists(em, customer.getUserName())) {
            sendAlert(response, "שם משתמש כבר קיים, נא לבחור שם אחר", "location='customerReg.html';");
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
       //     appManager.getGsonCustomerMap().put(customer.getUserName(),new GsonCustomer(customer));
            request.getSession(true).setAttribute(Constants.USERNAME, customer.getUserName()); //check
            sendAlert(response, "ההרשמה עברה בהצלחה", "location='index.html';");
        }
    }

    private void sendAlert(HttpServletResponse response, String message,String location) throws IOException{
        PrintWriter out = response.getWriter();
        out.println("<script type=\"text/javascript\">");
        out.println(String.format("alert('%s');", message));
        out.println(location);
        out.println("</script>");
    }
}
