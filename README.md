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
   
   *  支持spring mvc、spring boot、spring cloud
   
   *  存储支持reds 支持redes单点、cluster集群、哨兵集群

   *  丰富的扩展支持 支持存储替换成其他产品需要自己实现 

# 环境要求 

  * JDK版本jdk1.8 +
  
  * Spring 环境
  
# 简介 

  1、Swan是一个校验重复的框架
    工作流程：
      第一步：前端先重header中获取唯一key  
      第二步：如果保存成功key会记录到redis布隆过滤器里面，当再次重复保存会校验是否有以前保存过
      第三布：如果保存过程中有异常导致报错不成功，比如：逻辑异常、业务异常等前端需要重新获取key然后保存
    工作原理：
      第一步：框架会自动在header里面下发key，前端需要获取header里面的key，具体可以参考demo工程
      第二步：前端发起保存、修改会带上key到操作的目标方法中
      第三布：后端通过打标AOP切面会拦截保存方法，然后在布隆过滤器里面查询是否有操作过
      第四步：如果操作是重复的框架层面直接拒绝
      
  2、Swan使用技术
     SPI：解耦使用的存储技术、key生成方式模块化动态加载配置中的技术
     Redis：目前默认存储使用Redis布隆过滤器，原因性能O(1)使用内存低，比如：存储一亿个数据只需要12M的存储空间
     Spring：Swan是建立在Spring技术上的框架使用Spring AOP技术  
     
  3、对接简单
     Swan是绿色轻量级别产品。只需要引入jar+注释，详细见Swan-demo工程。如果你有兴趣可以下载源码，自定义符合你的应用场景。
     
  4、设计思想
     前端处理错误思想：由于异常分为各种自定义异常、及前端输出带有含义的业务异常。所以Swan没有选择帮助用户处理异常，因为Swan无法知道你的异常时什么类型的，如果你们项目比较同意都是抛出异常类可以自定义Swan注释支持，这样处理异常就不需要前端判断异常了。
     为什么只实现Redis存储：性能、资源占用。redis布隆过滤器算法比较优秀有支持分布式故选择Redis。 [`布隆过滤器学习`](https://blog.csdn.net/god8816/article/details/109774406)
     
     
# 联系方式
  QQ：948351520
  微信：ppgou88
  邮箱：948351520@qq.com
