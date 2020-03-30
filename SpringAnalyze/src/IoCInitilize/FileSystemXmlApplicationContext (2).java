package IoCInitilize;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.BeanEntry;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.parsing.PropertyEntry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//import org.springframework.context.support.FileSystemXmlApplicationContext;
/**
 * 以继承了DefaultListableBeanFactory的此ApplicationContext为例
 * @author Origi_hz2bfvo
 *
 */
public class FileSystemXmlApplicationContext {
	//ThreadLocal是一个线程内部的存储类,可以在指定线程内部存储数据,只有指定线程能得到存储的数据
	//每个线程都持有一个ThreadLocalMap对象
	private final ThreadLocal<Set<EncodedResource>> resourcesCurrentlyBeingLoaded =
			new NamedThreadLocal<>("XML bean definition resources currently being loaded");
	
	private DocumentLoader documentLoader = new DefaultDocumentLoader();
	
	private final BeanDefinitionRegistry registry = null;
	
	private Class<? extends BeanDefinitionDocumentReader> documentReaderClass =
			DefaultBeanDefinitionDocumentReader.class;
	
	@Nullable
	private XmlReaderContext readerContext;
	
	private final ParseState parseState = new ParseState();
	
	/** Map of bean definition objects, keyed by bean name. */
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
	/** Names of beans that have already been created at least once. */
	private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));
	/** List of bean definition names, in registration order. */
	private volatile List<String> beanDefinitionNames = new ArrayList<>(256);
	/**按注册顺序列出手动注册的单例的名称 */
	private volatile Set<String> manualSingletonNames = new LinkedHashSet<>(16);
	
	String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";
 
	String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";
	
	String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";
	
	//在构造方法中调用了refresh()方法
	public FileSystemXmlApplicationContext(){
		refresh();
	}
	//IoC容器初始会由此进入
	public void refresh(){
		//prepareRefresh();
		//获取BeanFactory
		ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
		prepareBeanFactory(beanFactory);
		//这是一个模板方法,BeanFactory的准备工作完成后进行后置处理工作
		//此时所有的beanDefinition已经加载但还未实例化,例如在AbstractRefreshableWebApplicationContext
		//中会向beanFactory中添加ServerletContextAwareProcessor处理器
		//并注册web应用的scopes和环境有关的beans
		//postProcessBeanFactory();
		
		invokeBeanFactoryPostProcessors(beanFactory);
		//处理lazy-init属性
		finishBeanFactoryInitialization(beanFactory);
	}
	//容器启动的准备工作
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory){
		//为容器配置ClassLoader,存在直接设置,不存在则新建一个默认类加载器
		//beanFactory.setBeanClassLoader(getClassLoader());
		//设置EL表达式解析器Bean初始化完成后填充属性(applyPropertyValues())时会用到
		//beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
		//配置属性注册解析器PropertyEditor
		//beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this));
		//添加BeanPostProcessor,当bean被这个工厂创建的时候会用到PostProcessor
		//beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
		//设置忽略自动装配的接口,在ApplicationContextAwareProcessor注册后下面的接口已经注册完毕
		//beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
		//beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
		//beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
		//beanFactory.ignoreDependencyInterface(ApplicationaContextAware.class);
		//注册可以解析的依赖关系
		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
		beanFactory.registerResolvableDependency(ResourceLoader.class, this);
		//beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
		//beanFactory.registerResolvableDependency(ApplicationContext.class, this);
		//如果当前的beanFactory包含loadTimeWeaver(代码织入) bean,说明存在类加载期织入AspectJ,需要把
		//当前的beanFactory交给BeanPostProcessor的实现类LoadTimeWeaverAwareProcessor来处理,从而实现
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
		//创建XmlBeanDefinitionReader,并通过回调设置到beanFactory中去,这里创建使用的也是DefaultListableBeanFactory
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
		//为XmlBeanDefinitionReader配置ResourceLoader
//		beanDefinitionReader.setResourceLoader(this);
//		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
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
			//获取xml文件,通过doLoadBeanDefinition()来处理读入的数据
			//来实际完成对BeanDefinition的载入
			reader.loadBeanDefinitions(configResources);
		}
		//以String的形式获得配置文件的位置
		String[] configLocations = getConfigLocations();
		if(configLocations != null){
			reader.loadBeanDefinitions(configLocations);
		}
	}
	//XML形式的loadBeanDefinitions最终调用的方法
	public int loadBeanDefinitions(EncodedResource encodedResource) {
		Assert.notNull(encodedResource, "EncodedResource must not be null");
		//尝试获取当前线程持有的变量数据
		Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
		//如果没有持有任何数据则新建一个set并载入的resource
		if(currentResources == null) {
			currentResources = new HashSet<EncodedResource>(4);
			this.resourcesCurrentlyBeingLoaded.set(currentResources);
		}
		//检测encodeResource是否重复载入
		if(!currentResources.add(encodedResource)) {
			//抛出循环载入异常
		}
		//这里得到XML文件，并得到IO的InputSource准备进行读取
		try {
			InputStream inputStream = encodedResource.getResource().getInputStream();
			try {
				InputSource inputSource = new InputSource(inputStream);
				if(encodedResource.getEncoding() != null) {
					inputSource.setEncoding(encodedResource.getEncoding());
				}
				return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
			} 
			finally {
				inputStream.close();
			}
			
		}catch(Exception e) {
			throw new BeanDefinitionStoreException(
					"IOException parsing XML document from " + encodedResource.getResource(), e);
		}finally {
			currentResources.remove(encodedResource);
			if(currentResources.isEmpty()) {
				this.resourcesCurrentlyBeingLoaded.set(null);
			}
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
	//从具体的XML文件中实际载入BeanDefinition的方法
	protected int doLoadBeanDefinitions(InputSource inputSource,Resource resource){
		try {
//			int validationMode = getValidationModeForResource(resource);
			//载入xml文件的Document对象,这个解析过程是由DefaultDocumentLoader完成的
			Document doc = null;
//			Document doc = this.documentLoader.loadDocument(inputSource, getEntityResolver(), this.errorHandler, validationMode, isNamespaceAware());
			//在这里启动对BeanDefinition解析的详细过程
			return registerBeanDefinitions(doc,resource);
		}catch(Exception e) {
			//分类处理各种异常
			throw e;
		}
		
	}
	
	protected int registerBeanDefinitions(Document doc,Resource resource){
		//得到BeanDefinitionDocumentReader来对XML文件内定义的BeanDefinition进行解析
//		BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
		int countBefore = getRegistry().getBeanDefinitionCount();
		//调用doRegisterBeanDefinitions()，在这里按照Spring的Bean规则完成具体的解析过程,处理结果由BeanDefinitionHolder对象来持有
		//documentReader.registerBeanDefinitions(doc, createReaderContext(resource)); 
		return getRegistry().getBeanDefinitionCount() - countBefore;
	}
	
	protected BeanDefinitionDocumentReader createBeanDefinitionDocumentReader() {
		return BeanUtils.instantiateClass(this.documentReaderClass);
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
//		processBeanDefinition();
	}
	
	/*这样，得到了documentReader以后，开始处理BeanDefinition，具体的处理委托给BeanDefinitionParserDelegate来完成，ele对应在Spring BeanDefinition中定义的XML元素*/
	protected void processBeanDefinition(Element ele,BeanDefinitionParserDelegate delegate){
		//解析并返回BeanDefinitionHolder,这里会生成beanName,aliases[]
		//通过调用AbstractBeanDefinition beanDefinition = parserBeanDefinitionElement(Element ele,String beanName,BeanDefinition containingBean)
		//来引发对Bean元素的详细解析
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		if(bdHolder != null) {
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				//对Bean解析完成后开始进行BeanDefinition的注册
				//注册过程由synchronized修饰保证数据一致性,注册前先检查IoC容器中是否已经注册了同样的名字
				//如果有同样的名字且不允许覆盖,则抛出异常.注册操作本质上是将beanName作为key,beanDefinition作为value
				//放入beanDefinitionMap中去的过程.完成了BeanDefinition的注册也就完成了IoC容器的初始化过程.
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			} catch (Exception e) {
				// 打印异常
			}
			//BeanDefinition向IoC容器注册完以后发送消息
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
		
	}
	
	protected final XmlReaderContext getReaderContext() {
		Assert.state(this.readerContext != null, "No XmlReaderContext available");
		return this.readerContext;
	}
	
	public final BeanDefinitionRegistry getRegistry() {
		return this.registry;
	}
	
	
	//对Bean元素进行详细解析
	public AbstractBeanDefinition parserBeanDefinitionElement(Element ele,String beanName,BeanDefinition containingBean){
		this.parseState.push(new BeanEntry(beanName));
		
		String className= null;
		if(ele.hasAttribute("class")) {
			className = ele.getAttribute("class").trim();
		}
		String parent = null;
		if(ele.hasAttribute("parent")) {
			parent = ele.getAttribute("parent");
		}
		//创建BeanDefinition对象,准备信息载入
		AbstractBeanDefinition bd = createBeanDefinition(className,parent);
		//解析当前bean的元素属性并设置description的信息
//		parserBeanDefinitionAttributes(ele,containingBean,bd);
		bd.setDescription(DomUtils.getChildElementValueByTagName(ele, "description"));
		//解析bean元素的各种属性并设置在bd里面
		//.....
		//对指定的Bean元素的property子元素集合进行解析
		parsePropertyElements(ele,bd);
		return bd;
	}
	
	private AbstractBeanDefinition createBeanDefinition(String className, String parent) {
		// TODO Auto-generated method stub
		return null;
	}
	//
	public void parsePropertyElements(Element beanEle, BeanDefinition bd) {
		NodeList nl = beanEle.getChildNodes();
		//遍历所有Bean元素下定义的property元素
		for(int i=0;i<nl.getLength();i++) {
			Node node = nl.item(i);
			//判断是否是property元素后再开始解析
			if(node instanceof Element && DomUtils.nodeNameEquals(node, "property")) {
				parsePropertyElement((Element) node, bd);
			}
		}
	}
	
	public void parsePropertyElement(Element ele, BeanDefinition bd) {
		//取得property的名字
		String propertyName = ele.getAttribute("name");
		
		this.parseState.push(new PropertyEntry(propertyName));
		
		//判断同一个Bean中是否已经由同名的property存在，若有则不进行解析，直接返回，这样起作用的只是第一个
		if(bd.getPropertyValues().contains(propertyName)) {
			//处理异常
			return;
		}
		//解析property的值，封装到PropertyVlue中然后设置到BeanDefinitionHolder中
	}
	
	/*BeanDefinition的注册在DefaultListableBeanFactory中的实现*/
	public void registerBeanDefinition(String beanName,BeanDefinition beanDefinition) {
		Assert.hasText(beanName, "'beanName' must not be empty");
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");
		if(beanDefinition instanceof AbstractBeanDefinition) {
			//验证beanDefiniton
			((AbstractBeanDefinition) beanDefinition).validate();
		}
		//判断是否有已注册的同名beanDefinition
		BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
		if(existingDefinition != null) {
			//检验是否可以覆盖，否则抛出异常
			this.beanDefinitionMap.put(beanName, beanDefinition);
		}else {
			//判断当前是否已经开始执行bean创建
			if(hasBeanCreationStarted()) {
				//开始注册，通过同步锁确保数据的一致性
				synchronized (this.beanDefinitionMap) {
					//将beanDefinition放入beanDefinitionMap
					this.beanDefinitionMap.put(beanName, beanDefinition);
					List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
					updatedDefinitions.addAll(this.beanDefinitionNames);
					updatedDefinitions.add(beanName);
					//如果缓存中的手动注册单例bean列表中包含当前的备案则移除已经被注册的beanDefinition防止重复注册
					if (this.manualSingletonNames.contains(beanName)) {
						Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
						updatedSingletons.remove(beanName);
						this.manualSingletonNames = updatedSingletons;
					}
				}
			}else {
				//如果处于刚刚启动注册的阶段则
				this.beanDefinitionMap.put(beanName, beanDefinition);
				this.beanDefinitionNames.add(beanName);
				this.manualSingletonNames.remove(beanName);
			}
		}
		/*BeanDefinition的注册在IoC容器中建立了Bean Definition的数据映射*/
	}
	
	protected boolean hasBeanCreationStarted() {
		return !this.alreadyCreated.isEmpty();
	}
	
	protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {

	}
	//完成对lazy-init属性的处理
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		beanFactory.setTempClassLoader(null);
		beanFactory.freezeConfiguration();
		//此方法中触发getBean()方法
		beanFactory.preInstantiateSingletons();
	}
}
