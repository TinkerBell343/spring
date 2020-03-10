
package DependencyInjection;

import java.lang.reflect.Constructor;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;

/**
 * 依赖注入在用户首次向IoC容器索要bean时触发
 * @author Origi_hz2bfvo
 *
 */
@SuppressWarnings({"unused","null"})
public class AbstractBeanFactory {
	
	Map<String, RootBeanDefinition> MergedBeanDefinition = new ConcurrentHashMap();
	//FactoryBean的缓存
	Map<String, Object> FactoryBeanObjectCache = new HashMap();
	
	/** Map between dependent bean names: bean name to Set of dependent bean names. 
	 * 被依赖的bean名称与依赖它的bean的名称集合的映射*/
	private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

	/** Map between depending bean names: bean name to Set of bean names for the bean's dependencies. 
	 * bean的名称与它依赖的bean的名称的集合的映射*/
	private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);
	
	/** Cache of singleton objects created by FactoryBeans: FactoryBean name to object. */
	private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);

	public Object getBean(String name){
		return doGetBean(name,null,null,false);
	}
	//实际获取bean，依赖注入发生的位置
	protected <T> T doGetBean(final String name,final Class<T> requiredType,final Object[] args,boolean typeCheckOnly){
		final String beanName = transformdBeanName(name);
		Object bean = null;
		//首先从缓存中取得Bean，处理已经被创建过的单例模式的Bean，对这种Bean的请求无需重复创建
		Object sharedInstance = getSingleton(beanName);
		//完成FactoryBean的相关处理结果，取得FactoryBean的生产结果
		if(sharedInstance != null){
		     bean = getObjectForBeanInstance(sharedInstance,name,beanName,null);
		}/*else{
		  检查当前IoC容器中能否存在此BeanDefinition，检查能否在当前的BeanFactory中取得需要的bean
		  如果取不到则顺着双亲BeanFactory链一直向上查找
		  BeanFactory parentBeanFactory = getParentBeanFactory();
		  if(parentBeanFactory != null && !containsBeanDefinition(beanName)){
		     String nameToLookUp = getParentFactory();
		     if(args != null){
		        return (T) parentBeanFactory.getBean(nameToLookUp,args);
		     }else{
		        return parentBeanFactory.getBean(nameToLookUp, requiredType);
		     }
		  }
		  //标记此bean为已创建（或将要创建）
		  markBeanAsCreated(beanName);
		           根据bean的名字取得BeanDefinition，由于beanDefinition存在继承关系，为了继承父类的属性，需要合并beanDefinition
		     final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
		     checkMergedBeanDefinition(mbd,beanName,args);
		           取得 当前bean的所有依赖bean，出发getBean的递归调用，直到取到一个没有任何依赖的bean为止
		     String[] dependsOn = mbd.getDependsOn();
		     if(dependsOn != null){
		           for(String dependsOnBean : dependsOn){
		               getBean(dependsOnBean);
		               registerDependentBean(dependsOnBean,beanName);
		           } 
		     }
		          如果当前bean没有依赖则根据配置(singleton或prototype)创建bean
		          调用createBean();
		}*/
		//对创建的bean进行检查，这时bean中已经包含了依赖关系的bean
		if(requiredType != null && bean != null && !requiredType.isAssignableFrom(bean.getClass())) {
			// 抛出异常
		}
		return (T) bean;
	}
	//将名字转换成beanName
	protected String transformdBeanName(String name) {
		//如果name以&开头直接返回
		//否则返回name中最后一个&以及其之后的部分
		return null;
	}
	
	/**
	 * Return the (raw) singleton object registered under the given name.
	 * <p>Checks already instantiated singletons and also allows for an early
	 * reference to a currently created singleton (resolving a circular reference).
	 * @param beanName the name of the bean to look for
	 * @param allowEarlyReference whether early references should be created or not
	 * @return the registered singleton object, or {@code null} if none found
	 */
	@Nullable
	protected Object getSingleton(String beanName) {
		//首先尝试从缓存获取bean如果获取不到再去单例的早期缓存中获取，依然获取不到则尝试
		//再尝试从单例工厂的缓存中获取此bean的ObjectFactory，若获取成功则调用getObject()
		//方法获取bean，并将生产结果放入单例的早期缓存中
		//getSingleton(beanName,true);
		return null;
	}
	
	public void registerDependentBean(String beanName, String dependentBeanName ) {
		//解析beanName的规范名称，在这里所有的别名最终都将被解析为一个统一的规范名称
		String canonicalName = canonicalName(beanName);
		
		synchronized (this.dependentBeanMap) {
			Set<String> dependentBeans =
					this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
			if (!dependentBeans.add(dependentBeanName)) {
				return;
			}
		}

		synchronized (this.dependenciesForBeanMap) {
			Set<String> dependenciesForBean =
					this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
			dependenciesForBean.add(canonicalName);
		}
	}
	
	public String canonicalName(String name) {
		return null;
	}
	
	protected Object createBean(final String beanName,final RootBeanDefinition mbd,final Object[] args){
		/*首先判断需要创建的bean是否可以被实例化，是否可以通过类加载器载入
		resolveBeanClass(mbd,beanName);
		*/
		//创建bean
		Object beanInstance = doCreateBean(beanName,mbd,args);
		return beanInstance;
	}
	
	protected Object doCreateBean(final String beanName,final RootBeanDefinition mbd,final Object[] args){
		//持有创建出的bean对象
		BeanWrapper instanceWrapper = null;
		//如果是单例则先把缓存中的同名bean清除
		if(mbd.isSingleton()){
			//instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
		}
		//创建bean
		if(instanceWrapper == null){
			instanceWrapper = createBeanInstance(beanName,mbd,args);
			
		}
		final Object bean = (instanceWrapper != null ? instanceWrapper.getWrappedInstance() : null);
		Class beanType = (instanceWrapper != null ? instanceWrapper.getWrappedClass() : null);
//		synchronized (mbd.postProcessingLock) {
//			if(!mbd.postProcessed) {
//				//将合并的 Bean 定义后处理器应用于指定的 Bean 定义，调用它们的方法?
//				applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName);
//				mbd.postProcessed = true;
//			}
//		}
		//对bean进行初始化，依赖注入，这里的exposedObject在初始化处理完成后作为依赖注入完成的bean返回
		Object exposedObject = bean;
		populateBean(beanName,mbd,instanceWrapper);
		//在完成对bean的生成和依赖注入后，，开始对bean进行初始化，这个初始化过程包含了对后置处理
		//器postProcessBeforeInitialization的回调
		exposedObject = initializeBean(beanName,exposedObject,mbd);
//		registerDisposableBeanIfNecessary(beanName,bean,mbd);
		return exposedObject;
	}
	
	protected BeanWrapper createBeanInstance(final String beanName,final RootBeanDefinition mbd,final Object[] args){
		//首先确认需要创建的bean是否可以实例化
		//Class beanClass = resolveBeanClass(mbd,beanName);
		//使用工厂方法实例化bean
		if(mbd.getFactoryMethodName() != null){
			//return instantiateUsingFactoryMethod(beanName,mbd,args);
		}
		/*Executalbe类型是Method、Constructor类型的父类，resolvedConstructorOrFactoryMethod在这里
		 * 用来缓存已经解析的构造函数或者工厂方法，一个类中有多个构造函数，判断使用哪个构造函数比较费时
		 * 使用缓存机制，如果已经解析过则无需再次解析
		 * if(mbd.resolvedConstructorOrFactoryMethod != null){
		 *   判断构造函数的参数是否解析完毕
			if(mbd.constructorArgumentsResolved){
				return autowireConstructor(beanName,mbd,null,args);
			}else{
				return instantiateBean(beanName,mbd);
			}
		}*/
		//判断使用哪个构造函数
		//Constructor[] ctors = determineConstructorsFromBeanPostProcessors(beanClass,beanName);
		//if(ctors != null || 是autowire || 构造函数参数不为空){
		//       使用选定的构造函数进行实例化
		//       return autowireConstructor(beanName,mbd,ctors,args);    
		//}      使用默认的构造函数进行实例化
		//return  instantiateBean(beanName,mbd);
		return null;
	}
	//最常见的实例化过程
	protected BeanWrapper instantiateBean(String beanName,RootBeanDefinition mbd){
		//使用默认的实例化策略对bean进行实例化，默认的实例化策略是
		//使用CglibSubclassingInstantiationStrategy，使用CGLIB来对bean进行实例化
		return null;
	}
	/**/
	protected void populateBean(String beanName,AbstractBeanDefinition mbd,BeanWrapper bw){
		//取得在BeanDefinition中设置的property值
		PropertyValues pvs = mbd.getPropertyValues();
		
		//开始依赖注入，先处理autowire的注入
		if (mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_NAME
				|| mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_TYPE) {
			MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
			//根据名字或者类型进行autowire
			if(mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_NAME){
				//autowireByName(beanName,mbd,bw,newPvs);
			}
			if(mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_TYPE){
				//autowireByType(beanName,mbd,bw,newPvs);
			}
			pvs = newPvs;
			//如果设置了dependency-check则进行依赖检查
//			checkDependencies(beanName, mbd, filteredPds, pvs);
			//对属性进行注入
			if(pvs != null) {
				
			}
			applyPropertyValues(beanName,mbd,bw,pvs);
		}
	}
	//按名称自动装配填入任何缺少的引用此工厂中其他bean的属性
	protected void autowireByName(
	        String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {

		//找到bean中不是简单属性的属性，会过忽略掉一些类型的属性，参看BeanUtils.isSimpleProperty
//	    String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
//	    for (String propertyName : propertyNames) {
//		    如果bean定义中包含了属性名，先实例化该属性名的bean
//	        if (containsBean(propertyName)) {
		        //使用bean的名字向IoC容器索取bean，并将得到的bean设置到当前bean中去
//	            Object bean = getBean(propertyName);
//	            pvs.add(propertyName, bean);
//	            registerDependentBean(propertyName, beanName);
//	        }
//	        else {
//	        	输出log
//	        }
//	    }
	}
	//按照类型自动装配
	protected void autowireByType(
	        String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {

//	    TypeConverter converter = getCustomTypeConverter();
//	    if (converter == null) {
//	        converter = bw;
//	    }
//
//	    Set<String> autowiredBeanNames = new LinkedHashSet<String>(4);
//	    String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
//	    for (String propertyName : propertyNames) {
//	        try {
//	            PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
//	            // Don't try autowiring by type for type Object: never makes sense,
//	            // even if it technically is a unsatisfied, non-simple property.
//	            if (!Object.class.equals(pd.getPropertyType())) {
//	                MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
//	                // Do not allow eager init for type matching in case of a prioritized post-processor.
//	                boolean eager = !PriorityOrdered.class.isAssignableFrom(bw.getWrappedClass());
//	                DependencyDescriptor desc = new AutowireByTypeDependencyDescriptor(methodParam, eager);
//	                Object autowiredArgument = resolveDependency(desc, beanName, autowiredBeanNames, converter);
//	                if (autowiredArgument != null) {
//	                    pvs.add(propertyName, autowiredArgument);
//	                }
//	                for (String autowiredBeanName : autowiredBeanNames) {
//	                    registerDependentBean(autowiredBeanName, beanName);
//	                }
//	                autowiredBeanNames.clear();
//	            }
//	        }
//	        catch (BeansException ex) {
//	        	
//	        }
//	    }
	}
	
	protected void applyPropertyValues(String beanName,BeanDefinition mbd,BeanWrapper bw,PropertyValues pvs){
		if(pvs == null || pvs.isEmpty()){
			return;
		}
		MutablePropertyValues mpvs = null;
		List<PropertyValue> original = null;
		//对BeanDefinition进行解析
//		BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this,beanName,mbd,converter);
		
		//为解析值创建一个副本，副本数据会被注入到bean中
		List<PropertyValue> deepCopy = new ArrayList<PropertyValue>(original.size());
		//依赖注入
		bw.setPropertyValues(new MutablePropertyValues(deepCopy));
	}
	
	protected Object initializeBean(String beanName, Object bean, RootBeanDefinition mbd) {
		//根据bean实现的接口分别设置对应的属性
		if(bean instanceof BeanNameAware) {
			((BeanNameAware) bean).setBeanName(beanName);
		}
		if(bean instanceof BeanClassLoaderAware) {
//			((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
		}
		if(bean instanceof BeanFactoryAware) {
//			((BeanFactoryAware) bean).setBeanFactory(this);
		}
		/*对后置处理器BeanPostProcessors的PostProcessBeforeInitialization的回调方法的调用 */
		Object wrappedBean = bean;
		if(mbd == null || !mbd.isSynthetic()) {
			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean,beanName);
		}
		/*调用bean的初始化方法，这个初始化方式是在BeanDefinition中通过定义init-method属性指定的
		 同时，如果bean实现了InitializingBean接口，那么这个bean的afterPropertiesSet实现也会被调用*/
		try {
			//通过调用invokeCustomInitMethod()->invoke()来激活init-method，invoke与直接调用的区别在于可以跨线程调用，在调用invoke的函数不在主线程里面的时候
//			invokeInitMethods(beanName,wrappedBean,mbd);
		}catch(Exception e) {
			//抛出异常
		}
		//对后置处理器BeanPostProcessors的PostProcessAfterInitialization的回调方法的调用
		if(mbd == null || !mbd.isSynthetic()) {
			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean,beanName);
		}
		return wrappedBean;
	}
	
	public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean,String beanName) {
		Object result = existingBean;
		//循环依次调用已经设置好的postProcessorsBeforeInitialization回调
		/*for(BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
			result = beanPostProcessor.postProcessBeforeInitialization(result, beanName);
		}*/
		return result;
	}
	
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean,String beanName) {
		Object result = existingBean;
		/*for(BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
		result = beanPostProcessor.postProcessAfterInitialization(result, beanName);
	    }*/
		return result;
	}
	
	protected Object getObjectForBeanInstance(Object beanInstance,String name,String beanName,RootBeanDefinition mbd) {
		//判断是否是对FactoryBean的调用
		//如果要获取的bean是FactoryBean的引用，并且缓存的对象不是FactoryBean类型。
		//意思就是要获取的bean实现了FactoryBean（属于工厂），但是获取的实例又不是FactoryBean类型的抛错。
		//bean是否是工厂是由它是否实现FactoryBean接口决定的
		if(BeanFactoryUtils.isFactoryDereference(name) && !(beanInstance instanceof FactoryBean)) {
			//throw Exception
		}
		//如果创建实例不是FactoryBean实例，或者是factoryBean的引用也就是普通的bean
		if(!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
			return beanInstance;
		}
		Object object = null;
		if(mbd == null) {
			//尝试从缓存获取已创建的FactoryBean单例
			object = getCachedObjectForFactoryBean(beanName);
		}
		if(object == null) {
			FactoryBean factory = (FactoryBean) beanInstance;
			//检测这个bean是否已经加载过
//			if(mbd == null && containsBeanDefinition(beanName)) {
			    //合并beanDefinition父类与子类
				mbd =  getMergedLocalBeanDefinition(beanName);
//			}
			boolean synthetic = (mbd != null && mbd.isSynthetic());
			//从FactoryBean中得到bean
			object = getObjectFromFactoryBean(factory,beanName,!synthetic);
		}
		return object;
	}

	protected Object getCachedObjectForFactoryBean(String beanName) {
		return this.FactoryBeanObjectCache.get(beanName);
	}
	
	protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) {
		
		//先根据beanName检查是否存在已经合并过的bean
		RootBeanDefinition mbd = this.MergedBeanDefinition.get(beanName);
		if(mbd != null) {
			return mbd;
		}
		return getMergedBeanDefinition(beanName,getBeanDefinition(beanName));
	}
	
	protected BeanDefinition getBeanDefinition(String beanName) {
		return null;
	}
	
	protected RootBeanDefinition getMergedBeanDefinition(String beanName,BeanDefinition bd) {
		return getMergedBeanDefinition(beanName,bd,null);
	}
	
	protected RootBeanDefinition getMergedBeanDefinition(String beanName,BeanDefinition bd,BeanDefinition containingBd) {
		
		//对bean进行父子类合并
		return null;
	}
	
	protected Object getObjectFromFactoryBean(FactoryBean factory, String beanName, boolean shouldPostProcess) {
		//判断是否是单例，如果是单例则要避免重复创建
		if(factory.isSingleton() && containsSingleton(beanName)) {
			//加锁
			synchronized (getSingletonMutex()) {
				//尝试从缓存获取已创建的FactoryBean
				Object object = this.FactoryBeanObjectCache.get(beanName);
				if(object == null) {
					//从factory中获取
					object = doGetObjectFromFactoryBean(factory,beanName,shouldPostProcess);
					//将新创建的对象放入缓存
					this.FactoryBeanObjectCache.put(beanName, (object != null ? object : null));
				}
				return (object != null ? object : null);
			}
		}else {
			return doGetObjectFromFactoryBean(factory,beanName,shouldPostProcess);
		}
	}
	
	protected boolean containsSingleton(String beanName) {
		return false;
	}
	
	protected Object getSingletonMutex() {
		return null;
	}
	
	private Object doGetObjectFromFactoryBean(final FactoryBean factory,final String beanName,final boolean shouldPostProcess) {
		
		AccessControlContext acc = AccessController.getContext();
		return AccessController.doPrivileged(new PrivilegedAction<Object>() {

			@Override
			public Object run() {
				// TODO Auto-generated method stub
				Object object = null;
				try {
					object = factory.getObject();
				}catch(Exception e) {
					e.printStackTrace();
				}
//				if(object ==null && isSingletonCurrentlyInCreation(beanName)) {
//					throw new BeanCreationException(beanName,"FactoryBean which is currently in creation returned null from getObject");
//				}
				if(object != null && shouldPostProcess) {
					//object = postProcessObjectFromFactoryBean(object,beanName);
				}
				return object;
			}
		}, acc);
	}
	
	//
}
