package IoCInitilize;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.xml.sax.InputSource;

/**
 * 以继承了DefaultListableBeanFactory的此ApplicationContext为例
 * @author Origi_hz2bfvo
 *
 */
public class FileSystemXmlApplicationContext {
 
	//在构造方法中调用了refresh()方法
	public FileSystemXmlApplicationContext(){
		refresh();
	}
	//IoC容器初始会由此进入
	public void refresh(){
		ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
		
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
		//完成具体的解析过程
		//documentReader.registerBeanDefinitions(arg0, arg1);
		return 0;
	}
}
