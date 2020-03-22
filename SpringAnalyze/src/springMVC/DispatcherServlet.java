package springMVC;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.HttpServletBean;

/**
 * 作为Servlet，DispatcherServlet的启动与Servlet的启动是相联系的。在Servlet的初始化过程中，
 * Servlet的init()方法会被调用，以进行初始化。
 * @author whisper
 *
 */
public class DispatcherServlet {
	
	@Nullable
	private ConfigurableEnvironment environment;
	
	/** WebApplicationContext for this servlet. */
	@Nullable
	private WebApplicationContext webApplicationContext;
	
	private final Set<String> requiredProperties = new HashSet<>(4);
	
	private transient ServletConfig config;
	
	/** Flag used to detect whether onRefresh has already been called. */
	private volatile boolean refreshEventReceived = false;
	
	/** 同步刷新执行监视器 */
	private final Object onRefreshMonitor = new Object();
	
	/** Should we publish the context as a ServletContext attribute?. */
	private boolean publishContext = true;
	
	/**
	 * WebApplicationContext的ServletContext属性的前缀
	 * The completion is the servlet name.
	 */
	public static final String SERVLET_CONTEXT_PREFIX = FrameworkServlet.class.getName() + ".CONTEXT.";

	//HttServletBean
	public final void init() throws ServletException {
		
		PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
		if(!pvs.isEmpty()) {
			try {
				BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
				ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
				bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
				//默认为一个空实现，当有额外定制的属性编辑器时用于初始化设置这些编辑器
				//initBeanWrapper(bw);
				bw.setPropertyValues(pvs, true);
			} catch (BeansException ex) {
				// TODO: handle exception
				throw ex;
			}
		}
		//调用子类的initServletBean执行initWebApplicationContext()进行具体的初始化
		initServletBean();
	}
	
	private static class ServletConfigPropertyValues extends MutablePropertyValues {

		/**
		 * Create new ServletConfigPropertyValues.
		 * @param config the ServletConfig we'll use to take PropertyValues from
		 * @param requiredProperties set of property names we need, where
		 * we can't accept default values
		 * @throws ServletException if any required properties are missing
		 */
		public ServletConfigPropertyValues(ServletConfig config, Set<String> requiredProperties)
				throws ServletException {

			Set<String> missingProps = (!CollectionUtils.isEmpty(requiredProperties) ?
					new HashSet<>(requiredProperties) : null);

			Enumeration<String> paramNames = config.getInitParameterNames();
			while (paramNames.hasMoreElements()) {
				String property = paramNames.nextElement();
				Object value = config.getInitParameter(property);
				addPropertyValue(new PropertyValue(property, value));
				if (missingProps != null) {
					missingProps.remove(property);
				}
			}

			// Fail if we are still missing properties.
			if (!CollectionUtils.isEmpty(missingProps)) {
				throw new ServletException(
						"Initialization from ServletConfig for servlet '" + config.getServletName() +
						"' failed; the following required properties were missing: " +
						StringUtils.collectionToDelimitedString(missingProps, ", "));
			}
		}
	}
	
	/**
     * Returns this servlet's {@link ServletConfig} object.
     *
     * @return ServletConfig the <code>ServletConfig</code> object that
     *         initialized this servlet
     */
    //@Override
    public ServletConfig getServletConfig() {
        return config;
    }
    
    public ServletContext getServletContext() {
        return getServletConfig().getServletContext();
    }
    
    public ConfigurableEnvironment getEnvironment() {
		if (this.environment == null) {
			this.environment = createEnvironment();
		}
		return this.environment;
	}
    
    protected ConfigurableEnvironment createEnvironment() {
		return new StandardServletEnvironment();
	}
    //初始化过程在FrameWorkServlet中完成
    protected final void initServletBean() throws ServletException {
    	//初始化上下文
    	try {
    		this.webApplicationContext = initWebApplicationContext();
    		//默认为空实现，用于子类覆盖以执行任意的初始化操作
			//initFrameworkServlet();
		} catch (Exception e) {
			// TODO: handle exception
			//抛出异常
		}
    	//各种log判断
    }
    //与ContextLoaderListener初始化方法类似
    protected WebApplicationContext initWebApplicationContext() {
		WebApplicationContext rootContext =
				WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		WebApplicationContext wac = null;

		if (this.webApplicationContext != null) {
			// A context instance was injected at construction time -> use it
			wac = this.webApplicationContext;
			if (wac instanceof ConfigurableWebApplicationContext) {
				ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) wac;
				if (!cwac.isActive()) {
					// The context has not yet been refreshed -> provide services such as
					// setting the parent context, setting the application context id, etc
					if (cwac.getParent() == null) {
						// The context instance was injected without an explicit parent -> set
						// the root application context (if any; may be null) as the parent
						cwac.setParent(rootContext);
					}
					//设置属性，Refresh()启动容器初始化
					//configureAndRefreshWebApplicationContext(cwac);
				}
			}
		}
		if (wac == null) {
			// No context instance was injected at construction time -> see if one
			// has been registered in the servlet context. If one exists, it is assumed
			// that the parent context (if any) has already been set and that the
			// user has performed any initialization such as setting the context id
			//wac = findWebApplicationContext();
		}
		if (wac == null) {
			// No context instance is defined for this servlet -> create a local one
			//FrameworkServlet内的实现，创建后调用configureAndRefreshWebApplicationContext()
			//wac = createWebApplicationContext(rootContext);
		}

		if (!this.refreshEventReceived) {
			// Either the context is not a ConfigurableApplicationContext with refresh
			// support or the context injected at construction time had already been
			// refreshed -> trigger initial onRefresh manually here.
			synchronized (this.onRefreshMonitor) {
				//默认为空实现，可被重写用来执行任意特定的servlet相关刷新操作
				//onRefresh(wac);
			}
		}

		if (this.publishContext) {
			// Publish the context as a servlet context attribute.
			String attrName = getServletContextAttributeName();
			getServletContext().setAttribute(attrName, wac);
		}

		return wac;
	}
    
    public String getServletContextAttributeName() {
		return SERVLET_CONTEXT_PREFIX + getServletName();
	}
    
    /**
	 * Overridden method that simply returns {@code null} when no
	 * ServletConfig set yet.
	 * @see #getServletConfig()
	 */
	//@Override
	@Nullable
	public String getServletName() {
		return (getServletConfig() != null ? getServletConfig().getServletName() : null);
	}

}
