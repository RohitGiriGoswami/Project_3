package in.co.rays.ctl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import in.co.rays.dto.BaseDTO;
import in.co.rays.dto.RoleDTO;
import in.co.rays.dto.UserDTO;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.ModelFactory;
import in.co.rays.model.RoleModelInt;
import in.co.rays.model.UserModelInt;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;


/**
 * login functionality controller. perform login operation
 * @author SunilOS
 *
 */

@WebServlet(urlPatterns = { "/LoginCtl" })
public class LoginCtl extends BaseCtl {

	private static final long serialVersionUID = 1L;

	public static final String OP_REGISTER = "Register";
	public static final String OP_SIGN_IN = "SignIn";
	public static final String OP_SIGN_UP = "SignUp";
	public static final String OP_LOG_OUT = "logout";
	private static Logger log = Logger.getLogger(LoginCtl.class);

	protected boolean validate(HttpServletRequest request) {
		boolean pass = true;
		String op = request.getParameter("operation");
		if (OP_SIGN_UP.equals(op) || OP_LOG_OUT.equals(op)) {
			return pass;
		}
		System.out.println(request.getParameter("login") + ".........." + request.getParameter("password"));

		if (DataValidator.isNull(request.getParameter("login"))) {
			request.setAttribute("login", PropertyReader.getValue("error.require", "Login Id"));
			pass = false;
		} else if (!DataValidator.isEmail(request.getParameter("login"))) {
			request.setAttribute("login", PropertyReader.getValue("error.email", "Login "));
			pass = false;
		}
		if (DataValidator.isNull(request.getParameter("password"))) {
			request.setAttribute("password", PropertyReader.getValue("error.require", "password"));
			pass = false;
		}
		System.out.println(pass+"/////");
		return pass;

	}

	protected BaseDTO populateDTO(HttpServletRequest request) {
		UserDTO dto = new UserDTO();
		System.out.println(request.getParameter("login"));
		dto.setId(DataUtility.getLong(request.getParameter("id")));
		dto.setLogin(DataUtility.getString(request.getParameter("login")));
		dto.setPassword(DataUtility.getString(request.getParameter("password")));
		return dto;

	}
	
	/**
	 * Display Login form
	 *
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		System.out.println(request.getParameter("login"));
		String op = request.getParameter("operation");
		UserModelInt model = ModelFactory.getInstance().getUserModel();
		RoleModelInt model1 = ModelFactory.getInstance().getRoleModel();
		HttpSession session = request.getSession(true);
		long id = DataUtility.getLong(request.getParameter("id"));
		if (OP_LOG_OUT.equals(op)) {
			session = request.getSession();
			System.out.println("log out button");
			session.invalidate();
			ServletUtility.setSuccessMessage("LogOut Successfully", request);
			ServletUtility.forward(ORSView.LOGIN_VIEW, request, response);
			return;
		}
		if (id > 0) {
			UserDTO dto;
			try {
				dto = model.findByPk(id);
				ServletUtility.setDto(dto, request);
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ServletUtility.handleException(e, request, response);
				return;
			}

		}
		ServletUtility.forward(getView(), request, response);

	}

	
	/**
	 * Submit Login form
	 *
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String op = request.getParameter("operation");
		System.out.println(";;;"+op);
		HttpSession session = request.getSession(true);
		UserModelInt userModel = ModelFactory.getInstance().getUserModel();
		RoleModelInt model1 = ModelFactory.getInstance().getRoleModel();
		long id = DataUtility.getLong(request.getParameter("id"));
		if (OP_SIGN_IN.equalsIgnoreCase(op)) {
			UserDTO dto = (UserDTO) populateDTO(request);
			try {
				System.out.println(dto.getLogin() + "///////" + dto.getPassword()+"kkkkk"+userModel);
				dto = userModel.authenticate(dto.getLogin(), dto.getPassword());
				System.out.println(dto + "absaddsdfds");
				if (dto != null) {
					session.setAttribute("user", dto);
					long roleId = dto.getRoleId();
					RoleDTO rdto = model1.findByPk(roleId);
					
					
					if(rdto == null) {
						System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
					}
					
					if (rdto != null) {
						session.setAttribute("role", rdto.getName());
					}
					String uri = (String) request.getParameter("uri");
					System.out.println("><>>>>" + uri);
					if (uri == null || "null".equalsIgnoreCase(uri)) {
						ServletUtility.redirect(ORSView.WELCOME_CTL, request, response);
						return;
					} else {
						System.out.println();
						if (rdto.getId() == 1) {
							ServletUtility.redirect(uri, request, response);
						} else {
							ServletUtility.redirect(ORSView.WELCOME_CTL, request, response);
						}

						return;
					}

				} else {
					dto = (UserDTO) populateDTO(request);
					ServletUtility.setDto(dto, request);
					ServletUtility.setErrorMessage("Invalid LoginId And Password", request);
				}

			} catch (ApplicationException e) {
				log.error(e);
				ServletUtility.handleException(e, request, response);
				return;
			}

		} else if (OP_SIGN_UP.equalsIgnoreCase(op)) {

			ServletUtility.redirect(ORSView.USER_REGISTRATION_CTL, request, response);
			return;

		}

		ServletUtility.forward(getView(), request, response);

	}

	@Override
	protected String getView() {
		// TODO Auto-generated method stub
		return ORSView.LOGIN_VIEW;
	}

}
