package in.co.rays.ctl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import in.co.rays.dto.BaseDTO;
import in.co.rays.dto.RoleDTO;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.model.ModelFactory;
import in.co.rays.model.RoleModelInt;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;


/**
 * role functionality controller.to perform add,delete ,update operation
 * @author SunilOS
 *
 */
@WebServlet(urlPatterns = { "/ctl/RoleCtl" })
public class RoleCtl extends BaseCtl {

	private static final long serialVersionUID = 1L;


	private static Logger log = Logger.getLogger(RoleCtl.class);

	protected boolean validate(HttpServletRequest request) {

		log.debug("RoleCtl Method validate Started");

		boolean pass = true;
         System.out.println(request.getParameter("name")+"......"+request.getParameter("description"));
		if (DataValidator.isNull(request.getParameter("name"))) {
			request.setAttribute("name", PropertyReader.getValue("error.require", "Name"));
			pass = false;
		}

		if (DataValidator.isNull(request.getParameter("description"))) {
			request.setAttribute("description", PropertyReader.getValue("error.require", "Description"));
			pass = false;
		}

		log.debug("RoleCtl Method validate Ended");

		return pass;
	}

	protected BaseDTO populateDTO(HttpServletRequest request) {
		RoleDTO dto = new RoleDTO();
		dto.setId(DataUtility.getLong(request.getParameter("id")));

		dto.setName(DataUtility.getString(request.getParameter("name")));
		dto.setDescription(DataUtility.getString(request.getParameter("description")));
		populateBean(dto, request);
		return dto;

	}
	
	/**
	 * Display Logic
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String op = request.getParameter("operation");
		long id = DataUtility.getLong(request.getParameter("id"));
		RoleModelInt model = ModelFactory.getInstance().getRoleModel();
		if (id > 0 || op != null) {
			RoleDTO dto;
			try {
				dto = model.findByPk(id);
				ServletUtility.setDto(dto, request);

			} catch (ApplicationException e) {
				log.error(e);
				ServletUtility.handleException(e, request, response);
				return;
			}

		}
		ServletUtility.forward(getView(), request, response);
	}

	
	/**
	 * Submit Logic
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String op = request.getParameter("operation");
		long id = DataUtility.getLong(request.getParameter("id"));
		RoleModelInt model = ModelFactory.getInstance().getRoleModel();
		System.out.println(" method do postkkkkkkkkk");
		if (OP_SAVE.equalsIgnoreCase(op) || OP_UPDATE.equalsIgnoreCase(op)) {

			RoleDTO dto = (RoleDTO) populateDTO(request);
			System.out.println("kkkkkkkkkkkk"+dto);
          //  System.out.println("kkkkk"+dto.getName()+"sdf"+dto.getDescription());
			try {
				if (id > 0) {
					model.update(dto);
					ServletUtility.setDto(dto, request);
					ServletUtility.setSuccessMessage("Data is successfully Update", request);
				} else {
					try {
						long pk = model.add(dto);
						ServletUtility.setDto(dto, request);
						ServletUtility.setSuccessMessage("Data is successfully saved", request);
					} catch (ApplicationException e) {
						log.error(e);
						ServletUtility.handleException(e, request, response);
						return;
					}
//					catch (DuplicateRecordException e) {
//						ServletUtility.setDto(dto, request);
//						ServletUtility.setErrorMessage("Role already exists", request);
//					}

				}

				

			} catch (ApplicationException e) {
				log.error(e);
				ServletUtility.handleException(e, request, response);
				return;
			}
//			catch (DuplicateRecordException e) {
//				ServletUtility.setDto(dto, request);
//				ServletUtility.setErrorMessage("Role already exists", request);
//			}

		} else if (OP_DELETE.equalsIgnoreCase(op)) {

			RoleDTO dto = (RoleDTO) populateDTO(request);
			try {
				model.delete(dto);
				ServletUtility.redirect(ORSView.ROLE_LIST_CTL, request, response);
				return;
			} catch (ApplicationException e) {
				log.error(e);
				ServletUtility.handleException(e, request, response);
				return;
			}

		} else if (OP_CANCEL.equalsIgnoreCase(op)) {

			ServletUtility.redirect(ORSView.ROLE_LIST_CTL, request, response);
			return;

		} else if (OP_RESET.equalsIgnoreCase(op)) {

			ServletUtility.redirect(ORSView.ROLE_CTL, request, response);
			return;

		}

		ServletUtility.forward(getView(), request, response);

		log.debug("RoleCtl Method doPOst Ended");
	}

	@Override
	protected String getView() {
		// TODO Auto-generated method stub
		return ORSView.ROLE_VIEW;
	}



}
