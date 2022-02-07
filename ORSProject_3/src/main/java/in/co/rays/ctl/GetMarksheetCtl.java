package in.co.rays.ctl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import in.co.rays.dto.BaseDTO;
import in.co.rays.dto.MarksheetDTO;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.MarksheetModelInt;
import in.co.rays.model.ModelFactory;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;


/**
 * get marksheet functionality ctl.to perform  get marksheet opeation
 * @author SunilOS
 *
 */

@WebServlet(name = "GetMarksheetCtl", urlPatterns = { "/ctl/GetMarksheetCtl" })
public class GetMarksheetCtl extends BaseCtl {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(GetMarksheetCtl.class);

	protected boolean validate(HttpServletRequest request) {
		log.debug("get marksheet validate start");
		boolean pass = true;
		if (DataValidator.isNull(request.getParameter("rollNo"))) {
			request.setAttribute("rollNo", PropertyReader.getValue("error.require", "Roll No"));
			pass = false;
		}
		System.out.println("<>>>>" + pass + "<><>>" + request.getParameter("rollNo"));
		log.debug("get marksheet validate start");
		return pass;
	}

	protected BaseDTO populateDTO(HttpServletRequest request) {
		log.debug("get Marksheet populate bean start");
		MarksheetDTO dto = new MarksheetDTO();

		dto.setId(DataUtility.getLong(request.getParameter("id")));

		dto.setRollNo(DataUtility.getString(request.getParameter("rollNo")));
		dto.setName(DataUtility.getString(request.getParameter("name")));

		dto.setPhysics(DataUtility.getInt(request.getParameter("physics")));

		dto.setChemistry(DataUtility.getInt(request.getParameter("chemistry")));

		dto.setMaths(DataUtility.getInt(request.getParameter("maths")));

		
		return dto;
	}
	 /**
     * Concept of Display method
     *
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		log.debug("marksheet ctl do get start");
		ServletUtility.forward(getView(), request, response);
		log.debug("marksheet ctl do get end");
	}
	 /**
     * Concept of submit method
     *
     */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		log.debug("marksheet ctl do post start");
		System.out.println("get marksheet do post <><>>");
		String op = DataUtility.getString(request.getParameter("operation"));
		long id = DataUtility.getLong(request.getParameter("id"));
		MarksheetModelInt model = ModelFactory.getInstance().getMarksheetModel();
		MarksheetDTO dto = (MarksheetDTO) populateDTO(request);
		if (OP_GO.equalsIgnoreCase(op)) {
			try {
				dto = model.findByRollNo(dto.getRollNo());
				if (dto != null) {
					ServletUtility.setDto(dto, request);
				} else {
					ServletUtility.setErrorMessage("Roll No does not exist", request);
				}
			} catch (ApplicationException e) {
				e.printStackTrace();
				log.equals(e);
				ServletUtility.handleException(e, request, response);
				return;
			}
		}
		ServletUtility.forward(getView(), request, response);
		log.debug("marksheet ctl do post end");
	}

	@Override
	protected String getView() {
		// TODO Auto-generated method stub
		return ORSView.GET_MARKSHEET_VIEW;
	}

}
