package in.co.rays.dto;


/**
 * role JavaDTO encapsulates role attributes
 * @author SunilOS
 *
 */
public class RoleDTO extends BaseDTO {
	
	
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * predefined role
	 */
	public static final int ADMIN = 1;
	public static final int STUDENT = 2;
	public static final int COLLEGE_SCHOOL = 3;
	public static final int KIOSK = 4;
	
	/**
	 * role name
	 */
	private String name;
	/**
	 * role description
	 */
	private String description;

	
	
	/*
	 * accessor
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKey() {

		return id + "";
	}

	public String getValue() {

		return name + "";
	}
}
