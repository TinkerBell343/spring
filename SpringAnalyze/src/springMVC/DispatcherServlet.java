package springMVC;

import org.springframework.beans.PropertyValues;

/**
 * 作为Servlet，DispatcherServlet的启动与Servlet的启动是相联系的。在Servlet的初始化过程中，
 * Servlet的init()方法会被调用，以进行初始化。
 * @author whisper
 *
 */
public class DispatcherServlet {


	public final void init() {
		try {
			PropertyValues pvs = new ServletConfigPropertyValues();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
