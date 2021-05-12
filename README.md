Swan【天鹅】
================

#### 校验重复的框架.


# 模块
  * swan-annotation : 注解打标模块

  * swan-common : 系统公共模块 比如：系统配置、系统常量、枚举、异常、存储模块[目前存储默认为redis]、token校验接口模块、公共类
  
  * swan-core : 功能实现模块 比如：token验证实现、spi加载实现、定时清理布隆过滤器实现、aop切面拦截              

  * swan-spring-boot-starter : 支持spring boot starter模块
  
  * swan-spring-cloud-demo : spring cloud集成demo
  
  * swan-spring-spring-demo : spring mvc集成demo
 
#  特征
   
   *  支持spiring所有版本
   
   *  支持spring mvc
   
   *  支持spring boot、spring cloud
   
   *  存储支持reds 支持redes单点、cluster集群、哨兵集群
   
   *  丰富的扩展支持 支持存储替换成其他产品需要自己实现 

# 环境要求 

  * JDK版本jdk1.8 +
  
  * Spring 环境
  
# 简介 

  Swan是一个校验重复的框架。其工作原理是下发token到请求header里面，然后前端获取header里面的key对应的value。在方法保存的时候带key、value，框架在保存之间先保存这个key，然后在保存用户方法的内容。如果用户再次保存的时候会校验value是否存在，就达到校验重复的功能。
  目前框架存储使用的是redis 布隆过滤器，这个技术方案比较节省空间，性能O(1)。如果你的项目不用redis可以自定义sapi模块更换存储。目前key支持uuid及SnowflakeId。
  框架是基于注解拦截，不改变项目其他绿色轻量。
 
# QQ号
  QQ num：948351520
