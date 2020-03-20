package springMVC;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Spring MVC启动类，也称为监听器。这个监听器是启动 IoC容器并把它载入到Web容器的主要功能模块，也是整个 Spring
 * Web应用加载IoC容器的第一个地方。 在ContextLoaderListener中实现的是ServletContextListener接口，
 * 这个接口的函数会结合Web容器的生命周期被调用。 当ServletContext发生变化时吗，监听器将做出预先指定的响应。
 * 
 * @author whisper
 *
 */
public class ContextLoaderListener {

	/**
	 * The root WebApplicationContext instance that this loader manages. In
	 * ContextLoader
	 */
	@Nullable
	private WebApplicationContext context;
	
	/**
	 * Config param for the root WebApplicationContext id,
	 * to be used as serialization id for the underlying BeanFactory: {@value}.
	 */
	public static final String CONTEXT_ID_PARAM = "contextId";
	
	/**
	 * Name of servlet context parameter (i.e., {@value}) that can specify the
	 * config location for the root context, falling back to the implementation's
	 * default otherwise.
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#DEFAULT_CONFIG_LOCATION
	 */
	public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
	/**
	 * Config param for the root WebApplicationContext implementation class to use: {@value}.
	 * @see #determineContextClass(ServletContext)
	 */
	public static final String CONTEXT_CLASS_PARAM = "contextClass";
	
	/**
	 * Name of the class path resource (relative to the ContextLoader class)
	 * that defines ContextLoader's default strategy names.
	 */
	private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";
	
	/**
	 * Map from (thread context) ClassLoader to corresponding 'current' WebApplicationContext.
	 * 从（线程上下文）类加载器映射到相应的当前 Web 应用程序上下文。
	 */
	private static final Map<ClassLoader, WebApplicationContext> currentContextPerThread = new ConcurrentHashMap<>(1);
	
	/**
	 * The 'current' WebApplicationContext, if the ContextLoader class is
	 * deployed in the web app ClassLoader itself.
	 */
	@Nullable
	private static volatile WebApplicationContext currentContext;
	
	private static final Properties defaultStrategies;
	
	static {
		// Load default strategy implementations from properties file.
		// This is currently strictly internal and not meant to be customized
		// by application developers.
		try {
			ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, ContextLoader.class);
			defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
		}
	}
	// 在服务器启动时，ServletContextListener的contextInitialized()被调用。 启动IoC容器的建立与初始化过程
	public void contextInitialized(ServletContextEvent event) {
		initWebApplicationContext(event.getServletContext());
	}

	// 开始对WebApplicationContext进行初始化
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		// 首先判断在ServletContext中是否已经有根上下文存在
		if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
			throw new IllegalStateException(
					"Cannot initialize context because there is already a root application context present - "
							+ "check whether you have multiple ContextLoader* definitions in your web.xml!");
		}
		// 输出日志
		servletContext.log("Initializing Spring root WebApplicationContext");
		
		try {
			// 将上下文存储在本地实例变量中,以确保当ServletContext关闭时context是可用的
			if (this.context == null) {
				//创建在ServletContext中存储的根上下文ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
				this.context = createWebApplicationContext(servletContext);
			}
			if (this.context instanceof ConfigurableWebApplicationContext) {
				ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) this.context;
				if (!cwac.isActive()) {
					//context实例并没有注入一个明确的双亲上下文
					if (cwac.getParent() == null) {
						//确保根上下文的双亲上下文，如果有
						ApplicationContext parent = loadParentContext(servletContext);
						cwac.setParent(parent);
					}
					//为上面创建好的context设置属性
					configureAndRefreshWebApplicationContext(cwac, servletContext);
				}
			}
			//将根上下文设置到ServletContext中去，以后的应用都是根据ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE属性值
			//取得根上下文的
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
			
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			if (ccl == ContextLoader.class.getClassLoader()) {
				currentContext = this.context;
			}
			else if (ccl != null) {
				currentContextPerThread.put(ccl, this.context);
			}
			return this.context;
		} catch (RuntimeException | Error ex) {
			//输出日志
			//logger.error("Context initialization failed", ex);
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
			throw ex;
		}

	}
	
	protected WebApplicationContext createWebApplicationContext(ServletContext sc) {
		//判断使用WebApplicationContext的哪个实现类，默认为XmlWebApplicationContext
		Class<?> contextClass = determineContextClass(sc);
		if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException("Custom context class [" + contextClass.getName() +
					"] is not of type [" + ConfigurableWebApplicationContext.class.getName() + "]");
		}
		//实例化并返回
		return (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);
	}
	
	protected Class<?> determineContextClass(ServletContext servletContext){
		//取得在ServletContext中对CONTEXT_CLASS_PARAM参数的配置
		String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
		if (contextClassName != null) {
			try {
				//尝试根据名称载入
				return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
			}
			catch (ClassNotFoundException ex) {
				throw new ApplicationContextException(
						"Failed to load custom context class [" + contextClassName + "]", ex);
			}
		}//如果没有额外配置
		else {
			//从配置文件获取默认的ContextClass
			contextClassName = defaultStrategies.getProperty(WebApplicationContext.class.getName());
			try {
				//尝试载入
				return ClassUtils.forName(contextClassName, ContextLoader.class.getClassLoader());
			}
			catch (ClassNotFoundException ex) {
				throw new ApplicationContextException(
						"Failed to load default context class [" + contextClassName + "]", ex);
			}
		}
	}
	
	@Nullable
	protected ApplicationContext loadParentContext(ServletContext servletContext) {
		return null;
	}
	//为上下文设置属性
	protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac, ServletContext sc) {
		if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
			//确保经过ServletContext->ConfigurableWebApplicationContext的转化后id与原始id保持一直
			String idParam = sc.getInitParameter(CONTEXT_ID_PARAM);
			if (idParam != null) {
				wac.setId(idParam);
			}else {
				// Generate default id...
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
						ObjectUtils.getDisplayString(sc.getContextPath()));
			}
		}
		//设置双亲上下文
		wac.setServletContext(sc);
		//设置ServletContext以及配置文件的位置参数
		String configLocationParam = sc.getInitParameter(CONFIG_LOCATION_PARAM);
		if (configLocationParam != null) {
			wac.setConfigLocation(configLocationParam);
		}
		// The wac environment's #initPropertySources will be called in any case when the context
		// is refreshed; do it eagerly here to ensure servlet property sources are in place for
		// use in any post-processing or initialization that occurs below prior to #refresh
		ConfigurableEnvironment env = wac.getEnvironment();
		if (env instanceof ConfigurableWebEnvironment) {
			((ConfigurableWebEnvironment) env).initPropertySources(sc, null);
		}
		//在配置位置以确定并且context refreshed之前，自定义有当前的ContextLoader创建的ConfigurableWebApplicationContext
		//默认的实现确保使用给定的web application context调用每一个由context init param指定的context initializer class
		customizeContext(sc, wac);
		//refresh()调用，启动容器初始化
		wac.refresh();
	}
	
	protected void customizeContext(ServletContext sc, ConfigurableWebApplicationContext wac) {
		//得到被指定ApplicationContextInitializer的实现类
		//List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> initializerClasses = determineContextInitializerClasses(sc);
		//将这些实现类实例化存入ContextLoader的属性contextInitializers中去
		//循环初始化contextInitializers中的对象
	}
}
