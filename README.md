Cat
================

#### High-Performance distributed transaction solution ( Notice ).


# Modules

  * Cat-admin: Transaction log management background
  
  * Cat-annotation : Framework common annotations
  
  * Cat-apache-dubbo : Support for the dubbo rpc framework 2.7.X

  * Cat-common :  Framework common class
  
  * Cat-core : Framework core package (annotation processing, log storage...)              
  
  * Cat-dashboard : Management background front-end
  
  * Cat-dubbo : Support for the dubbo framework Less than 2.7 version
  
  * Cat-springcloud : Support for the spring cloud rpc framework
  
  * Cat-spring-boot-starter : Support for the spring boot starter
  
  * Cat-demo : Examples using the Cat framework
 
#  Features
   
   *  All spring versions are supported and Seamless integration
   
   *  Provides support for the springcloud dubbo RPC framework
   
   *  Provides integration of the spring boot starter approach
   
   *  Support Nested transaction 
   
   *  Local transaction storage support :  oracle mysql 
   
   *  Transaction log serialization support : java hessian kryo protostuff
   
   *  Spi extension : Users can customize the storage of serialization and transaction logs

# Prerequisite 

  * You must use jdk1.8 +
  
  * You must be a user of the spring framework
  
  * You must use one of the dubbo, and springcloud RPC frameworks 
  
# About 

   Cat is a NOTICE solution for distributed transactions, Its rapid integration, zero penetration high performance has been run by a number of companies including my own company in the production environment.
  
   Its performance is nearly lossless compared to your RPC framework, its confrim cancel, and its log store is conducted asynchronously using a disruptor.
   
   
# Document
[Document Support](http://note.youdao.com/noteshare?id=0a11948424121449a5ec8a6c5e8507d4)

# QQ Group Support
  QQ group num：810268021

# QQ Me Support
  QQ num：948351520
  
 



