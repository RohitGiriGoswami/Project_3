package in.co.rays.ctl;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import in.co.rays.dto.BaseDTO;
import in.co.rays.dto.TimetableDTO;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.CourseModelInt;
import in.co.rays.model.ModelFactory;
import in.co.rays.model.SubjectModelInt;
import in.co.rays.model.TimeTableModelInt;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;


/**
 * TimeTable functionality controller.to perform add,delete and update operation.
 * @author SunilOS
 *
 */
@WebServlet(urlPatterns={"/ctl/TimeTableCtl"})
public class TimeTableCtl extends BaseCtl{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(TimeTableCtl.class);

	protected void preload(HttpServletRequest request) {
		CourseModelInt model = ModelFactory.getInstance().getCourseModel();
		SubjectModelInt  model1 = ModelFactory.getInstance().getSubjectModel();
		try {
			List l = model.list();
			List l1 = model1.list();
			request.setAttribute("courseList", l);
			request.setAttribute("subjectList", l1);
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected boolean validate(HttpServletRequest request) {
		log.debug("time table validate start");
		boolean pass = true;
		String examDate = request.getParameter("examDate");
		System.out.println("hmmmmmmmmmmm"+pass);
		if (DataValidator.isNull(request.getParameter("description"))) {
			request.setAttribute("description", PropertyReader.getValue("error.require", "Description"));
			pass = false;
		}
		System.out.println("kkkkkkk"+pass);
		if (DataValidator.isNull(request.getParameter("courseName"))) {
			request.setAttribute("courseName", PropertyReader.getValue("error.require", "course Name"));
			pass = false;
		}
		if (DataValidator.isNull(request.getParameter("subjectName"))) {
			request.setAttribute("subjectName", PropertyReader.getValue("error.require", "subject Name"));
			pass = false;
		}
		

		if (DataValidator.isNull(request.getParameter("semesterId"))) {
			request.setAttribute("semesterId", PropertyReader.getValue("error.require", "semester"));
			pass = false;
		}

		if (DataValidator.isNull(examDate)) {
			request.setAttribute("examDate", PropertyReader.getValue("error.require", "Exam Date"));
			pass = false;
		} else if (!DataValidator.isDate(examDate)) {
			request.setAttribute("examDate", PropertyReader.getValue("error.date", "Exam Date"));
			pass = false;
		}
		if (DataValidator.isNull(request.getParameter("examId"))) {
			request.setAttribute("examId", PropertyReader.getValue("error.require", "exam Time"));
			pass = false;
		}
		log.debug("time table validate end");
		System.out.println("kjkj>>>>" + pass);
		return pass;
	}

	protected BaseDTO populateDTO(HttpServletRequest request) {
		log.debug("time table populate start");
		TimetableDTO dto = new TimetableDTO();
		dto.setId(DataUtility.getLong(request.getParameter("id")));
		dto.setCourseId(DataUtility.getLong(request.getParameter("courseName")));
		dto.setDescription(DataUtility.getString(request.getParameter("description")));
		dto.setSemester(DataUtility.getString(request.getParameter("semesterId")));
		dto.setSubId(DataUtility.getLong(request.getParameter("subjectName")));
		dto.setExamDate(DataUtility.getDate(request.getParameter("examDate")));
		dto.setExamTime(DataUtility.getString(request.getParameter("examId")));
		populateBean(dto,request);
		log.debug("time table populate end");
		System.out.println("<<<>>>>>>++.." + dto);
		
		return dto;
	}

	/**
	 * Display Logics inside this method
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		log.debug("time table do get start");
		String op = DataUtility.getString(request.getParameter("operation"));
		long id = DataUtility.getLong(request.getParameter("id"));
		TimeTableModelInt model =ModelFactory.getInstance().getTimetableModel() ;
		if (id > 0 || op != null) {
			TimetableDTO dto;
			try {
				dto = model.findByPk(id);
				ServletUtility.setDto(dto, request);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e);
				ServletUtility.handleException(e, request, response);
				return;
			}

		}
		ServletUtility.forward(getView(), request, response);
		log.debug("time table doget end");
	}

	/**
	 * Submit logic inside it
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		System.out.println("method post..............");
		log.debug("time table dopost start");
		String op = DataUtility.getString(request.getParameter("operation"));
		long id = DataUtility.getLong(request.getParameter("id"));
		TimeTableModelInt model = ModelFactory.getInstance().getTimetableModel();
		if (OP_SAVE.equalsIgnoreCase(op) || OP_UPDATE.equalsIgnoreCase(op)) {
			TimetableDTO dto = (TimetableDTO) populateDTO(request);
			TimetableDTO dto1 = null;
			TimetableDTO dto2 = null;
			TimetableDTO dto3 = null;
			try {
				if (id > 0) {
					 model.update(dto);
					ServletUtility.setDto(dto, request);
					 ServletUtility.setSuccessMessage("Data is successfully Update", request);
				} else {
					try {
						dto1 = model.checkByCourseName(dto.getCourseId(), dto.getExamDate());
						dto2 = model.checkBySubjectName(dto.getCourseId(), dto.getSubId(), dto.getExamDate());
						dto3 = model.checkBySemester(dto.getCourseId(), dto.getSubId(), dto.getSemester(),
								dto.getExamDate());
						System.out.println("jjjjjjjjj"+dto1+"..."+dto2+"..."+dto3);
						if (dto1 == null && dto2 == null && dto3 == null) {
							System.out.println("hhhhhhhhhhh");
							model.add(dto);
							ServletUtility.setSuccessMessage("Data is successfully saved", request);
						} else {
//							ServletUtility.setDto(dto, request);
							ServletUtility.setErrorMessage("Exam already exist!", request);

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				//ServletUtility.setBean(bean, request);

			} catch (Exception e) {
				ServletUtility.setDto(dto, request);
				ServletUtility.setErrorMessage("Login id already exists", request);
			}
		} else if (OP_DELETE.equalsIgnoreCase(op)) {

			TimetableDTO dto = (TimetableDTO) populateDTO(request);
			try {
				model.delete(dto);
				ServletUtility.redirect(ORSView.TIMETABLE_LIST_CTL, request, response);
				return;
			} catch (ApplicationException e) {
				log.error(e);
				ServletUtility.handleException(e, request, response);
				return;
			}

		} else if (OP_CANCEL.equalsIgnoreCase(op)) {

			ServletUtility.redirect(ORSView.TIMETABLE_LIST_CTL, request, response);
			return;
		} else if (OP_RESET.equalsIgnoreCase(op)) {

			ServletUtility.redirect(ORSView.TIMETABLE_CTL, request, response);
			return;
		}
		ServletUtility.forward(getView(), request, response);
		log.debug("time table dopost end");
	}

	@Override
	protected String getView() {
		// TODO Auto-generated method stub
		return ORSView.TIMETABLE_VIEW;
	}

}
