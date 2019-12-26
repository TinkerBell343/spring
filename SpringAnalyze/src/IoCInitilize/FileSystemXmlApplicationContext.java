package IoCInitilize;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * 以继承了DefaultListableBeanFactory的此ApplicationContext为例
 * @author Origi_hz2bfvo
 *
 */
public class FileSystemXmlApplicationContext {
	
	String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";
 
	String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";
	
	String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";
	
	//在构造方法中调用了refresh()方法
	public FileSystemXmlApplicationContext(){
		refresh();
	}
	//IoC容器初始会由此进入
	public void refresh(){
		//获取BeanFactory
		ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
		prepareBeanFactory(beanFactory);
	}
	//容器启动的准备工作
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory){
		//为容器配置ClassLoader，存在直接设置，不存在则新建一个默认类加载器
		//beanFactory.setBeanClassLoader(getClassLoader());
		//设置EL表达式解析器Bean初始化完成后填充属性(applyPropertyValues())时会用到
		//beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
		//配置属性注册解析器PropertyEditor
		//beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this));
		//添加BeanPostProcessor，当bean被这个工厂创建的时候会用到PostProcessor
		//beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
		//设置忽略自动装配的接口，在ApplicationContextAwareProcessor注册后下面的接口已经注册完毕
		//beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
		//beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
		//beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
		//beanFactory.ignoreDependencyInterface(ApplicationaContextAware.class);
		//注册可以解析的依赖关系
		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
		beanFactory.registerResolvableDependency(ResourceLoader.class, this);
		//beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
		//beanFactory.registerResolvableDependency(ApplicationContext.class, this);
		//如果当前的beanFactory包含loadTimeWeaver(代码织入) bean，说明存在类加载期织入AspectJ，需要把
		//当前的beanFactory交给BeanPostProcessor的实现类LoadTimeWeaverAwareProcessor来处理，从而实现
		//类加载器织入AspectJ的目的
		if(beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)){
			//beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			//beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}
		//注册系统配置组件bean
		if(!beanFactory.containsBean(SYSTEM_PROPERTIES_BEAN_NAME)){
			beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, System.getProperties());
		}
		//注册系统环境组件bean
		if(!beanFactory.containsBean(SYSTEM_ENVIRONMENT_BEAN_NAME)){
			beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, System.getenv());
		}
	}
	//若已建立BeanFactory则销毁并关闭此BeanFactory
	private void refreshBeanFactory(){
		if(hasBeanFactory()){
			destroyBeans();
			closeBeanFactory();
		}
		DefaultListableBeanFactory beanFactory = createBeanFactory();
		//启动对BeanDefinition的载入
		loadBeanDefinitions(beanFactory);
	}
	
	private boolean hasBeanFactory(){
		//若已建立BeanFactory则返回true
		return true;
	}
	
	private void destroyBeans(){
		//销毁Beans
	}
	
	private void closeBeanFactory(){
		//关闭工厂
	}
	
	public DefaultListableBeanFactory createBeanFactory(){
		return new DefaultListableBeanFactory(getInternalParentBeanFactory());
	}
	
	private BeanFactory getInternalParentBeanFactory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory){
		//创建XmlBeanDefinitionReader,并通过回调设置到beanFactory中去，这里创建使用的也是DefaultListableBeanFactory
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
		//initBeanDefinitionReader(beanDefinitionReader);
		loadBeanDefinitions(beanDefinitionReader);
	}
	public int loadBeanDefinitions(String location,Set<Resource> actualResources) throws BeanDefinitionStoreException{
		//获取ResourceLoader
		ResourceLoader resourceLoader = getResourceLoader();
		try {
			//调用DefaultResourceLoader的getResource()方法完成具体的Resource定位
			Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader){
		//以Resource的方式获取配置文件的资源位置
		Resource[] configResources = getConfigResources();
		if(configResources != null){
			//这里使用reader.loadBeanDefinitions()方法来载入数据
			//此方法循环调用loadBeanDefinition(Resource res) 方法
			//获取xml文件，通过doLoadBeanDefinition()来处理读入的数据
			//来实际完成对BeanDefinition的载入
			reader.loadBeanDefinitions(configResources);
		}
		//以String的形式获得配置文件的位置
		String[] configLocations = getConfigLocations();
		if(configLocations != null){
			reader.loadBeanDefinitions(configLocations);
		}
	}
	
	private ResourceLoader getResourceLoader(){
		//这里使用的DefaultResourceLoader
		return null;
	}
	
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory(){
		refreshBeanFactory();
		//返回刷新后的BeanFactory
		return null;
	}
	
	protected Resource[] getConfigResources(){
		return null;
	}
	
	protected String[] getConfigLocations(){
		return null;
	}
	//实际载入BeanDefinition的方法
	protected int doLoadBeanDefinitions(InputSource inputSource,Resource resource){
		//载入xml文件的Document对象，这个解析过程是由DefaultDocumentLoader完成的
		//在这里启动对BeanDefinition解析的详细过程
		return registerBeanDefinitions();
	}
	
	protected int registerBeanDefinitions(){
		//得到BeanDefinitionDocumentReader来对XML文件内定义的BeanDefinition进行解析
		BeanDefinitionDocumentReader documentReader = null;
		//完成具体的解析过程，处理结果由BeanDefinitionHolder对象来持有
		//documentReader.registerBeanDefinitions(arg0, arg1); 调用doRegisterBeanDefinitions()
		return 0;
	}
	
	protected void doRegisterBeanDefinitions(Element root){
		parserBeanDefinitions();
	}
	//创建代理解析BeanDefinition
	protected void parserBeanDefinitions(){
		parserDefaultElement();
	}
	//这里按标签类型(bean,alias,import,beans....)分别调用相应的处理方法
	private void parserDefaultElement(){
		//processBeanDefinition();
	}
	
	protected void processBeanDefinition(Element ele,BeanDefinitionParserDelegate delegate){
		//解析并返回BeanDefinitionHolder,这里会生成beanName,aliases[]
		//通过调用AbstractBeanDefinition beanDefinition = parserBeanDefinitionElement(Element ele,String beanName,BeanDefinition containingBean)
		//来引发对Bean元素的详细解析
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		//对Bean解析完成后开始进行BeanDefinition的注册
		//注册过程synchronized修饰保证数据一致性，注册前先检查IoC容器中是否已经注册了同样的名字
		//如果有同样的名字且不允许覆盖，则抛出异常。注册操作本质上是将beanName作为key，beanDefinition作为value
		//放入beanDefinitionMap中去的过程。完成了BeanDefinition的注册也就完成了IoC容器的初始化过程。
		BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, null);
	}
	
	public AbstractBeanDefinition parserBeanDefinitionElement(Element ele,String beanName,BeanDefinition containingBean){
		//创建BeanDefinition对象，准备信息载入
		//AbstractBeanDefinition bd = createBeanDefinition(className,parent);
		//解析bean的元素属性
		//parserBeanDefinitionAttributes(ele,beanName,bd);
		//解析bean元素的各种属性
		return null;
	}
}
