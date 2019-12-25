package DependencyInjection;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * 依赖注入在用户首次向IoC容器索要bean时触发
 * @author Origi_hz2bfvo
 *
 */
public class AbstractBeanFactory {

	public Object getBean(String name){
		return doGetBean(name,null,null,false);
	}
	//实际获取bean，依赖注入发生的位置
	protected <T> T doGetBean(final String name,final Class<T> requiredType,final Object[] args,boolean typeCheckOnly){
		Object bean;
		//首先从缓存中取得Bean，处理已经被创建过的单例模式的Bean，对这种Bean的请求无需重复创建
		//Object sharedInstance = getSingleton(beanName);
		//完成FactoryBean的相关处理结果，取得FactoryBean的生产结果
		/*if(sharedInstance != null){
		     bean = getObjectForBeanInstance(sharedInstance,name,beanName,null);
		}else{
		    根据bean的名字取得BeanDefinition
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
		if(instanceWrapper != null){
			instanceWrapper = createBeanInstance(beanName,mbd,args);
			//处理特殊的合成BeanDefinition类
			//applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName);
			//对bean进行初始化，依赖注入，这里的exposedObject在初始化处理完成后作为依赖注入完成的bean返回
			//Object exposedObject = bean;
			populateBean(beanName,mbd,instanceWrapper);
			//exposedObject = initializeBean(beanNam,exposedObject,mbd);
		}
		return null;
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
		//return instantiateBean(beanName,mbd);
		return null;
	}
	//最常见的实例化过程
	protected BeanWrapper instantiateBean(String beanName,RootBeanDefinition mbd){
		//使用默认的实例化策略对bean进行实例化，默认的实例化策略是
		//使用CglibSubclassingInstantiationStrategy，使用CGLIB来对bean进行实例化
		return null;
	}
	
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
			//对属性进行注入
			applyPropertyValues(beanName,mbd,bw,pvs);
		}
	}
	
	protected void applyPropertyValues(String beanName,BeanDefinition mbd,BeanWrapper bw,PropertyValues pvs){
		if(pvs == null || pvs.isEmpty()){
			return;
		}
		MutablePropertyValues mpvs = null;
		List<PropertyValue> original = null;
		//对BeanDefinition进行解析
		//BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this,beanName,mbd,converter);
		//为解析值创建一个副本，副本数据会被注入到bean中
		List<PropertyValue> deepCopy = new ArrayList<PropertyValue>(original.size());
		//依赖注入
		bw.setPropertyValues(new MutablePropertyValues(deepCopy));
	}
}
