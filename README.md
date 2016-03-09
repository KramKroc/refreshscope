# refreshscope
Project that shows behaviour of @RefreshScope on a service with random port.

## Startup order

1. Discovery Service =>  ./gradlew discovery-service:bootRun
2. Config Service => ./gradlew config-server:bootRun
3. Sample-Service (Note: this service starts on random port so need to view output to see which port is used (or look at the eureka service listing) => ./gradlew sample-service:bootRun
4. API Gateway => ./gradlew apigateway:bootRun

## Endpoints to test

Hit sample-service endpoint directly (in this example the service started on port 53360)

```sh
$ curl http://localhost:53360/api/configured 
```

Via Zuul Proxy

```sh
$ curl http://localhost:8087/sample-service/api/configured
```

Both should return *Whats that Ted* as a response.

If you then trigger a refresh on the sample-service:

```sh
$ curl -X POST http://localhost:53360/refresh
```

Then repeat the calls above, the direct calls to the sample service still work, but eventually (after the apigateway syncs with eureka) the apigateway will fail:

```sh
$ curl http://localhost:8087/sample-service/api/configured
{"timestamp":1457564922732,"status":500,"error":"Internal Server Error","exception":"com.netflix.zuul.exception.ZuulException","message":"NUMBEROF_RETRIES_NEXTSERVER_EXCEEDED"}
```

This appears to be occuring because the refresh is causing the sample-service to re-register with the  Eureka Server, not with the original port, but with port 0. 

**Note**: I'm also surprised that the refresh triggers what appears to be an application restart (without shutting down jvm):

```sh
2016-03-09 22:55:42.301  INFO 50837 --- [o-auto-1-exec-5] o.s.boot.SpringApplication               : Starting application on Marks-MBP.home with PID 50837 (/Users/markcorkery/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot/1.3.2.RELEASE/62d0b690a08be593bb0288e0ec93f5bda52be6a/spring-boot-1.3.2.RELEASE.jar started by markcorkery in /Users/markcorkery/workspace/refreshscope/sample-service)
2016-03-09 22:55:42.301  INFO 50837 --- [o-auto-1-exec-5] o.s.boot.SpringApplication               : No active profile set, falling back to default profiles: default
2016-03-09 22:55:42.315  INFO 50837 --- [o-auto-1-exec-5] s.c.a.AnnotationConfigApplicationContext : Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@6cdfb862: startup date [Wed Mar 09 22:55:42 GMT 2016]; root of context hierarchy
2016-03-09 22:55:42.379  INFO 50837 --- [o-auto-1-exec-5] f.a.AutowiredAnnotationBeanPostProcessor : JSR-330 'javax.inject.Inject' annotation found and supported for autowiring
2016-03-09 22:55:42.386  INFO 50837 --- [o-auto-1-exec-5] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.retry.annotation.RetryConfiguration' of type [class org.springframework.retry.annotation.RetryConfiguration$$EnhancerBySpringCGLIB$$3dcfcc80] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2016-03-09 22:55:42.388  INFO 50837 --- [o-auto-1-exec-5] trationDelegate$BeanPostProcessorChecker : Bean 'configurationPropertiesRebinderAutoConfiguration' of type [class org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration$$EnhancerBySpringCGLIB$$3a91bc58] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2016-03-09 22:55:42.460  INFO 50837 --- [o-auto-1-exec-5] o.s.c.n.eureka.InstanceInfoFactory       : Setting initial instance status as: STARTING
2016-03-09 22:55:42.493  INFO 50837 --- [o-auto-1-exec-5] c.n.d.provider.DiscoveryJerseyProvider   : Using JSON encoding codec LegacyJacksonJson
2016-03-09 22:55:42.493  INFO 50837 --- [o-auto-1-exec-5] c.n.d.provider.DiscoveryJerseyProvider   : Using JSON decoding codec LegacyJacksonJson
2016-03-09 22:55:42.494  INFO 50837 --- [o-auto-1-exec-5] c.n.d.provider.DiscoveryJerseyProvider   : Using XML encoding codec XStreamXml
2016-03-09 22:55:42.494  INFO 50837 --- [o-auto-1-exec-5] c.n.d.provider.DiscoveryJerseyProvider   : Using XML decoding codec XStreamXml
2016-03-09 22:55:42.596  INFO 50837 --- [o-auto-1-exec-5] c.n.d.s.r.aws.ConfigClusterResolver      : Resolving eureka endpoints via configuration
2016-03-09 22:55:42.598  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Disable delta property : false
2016-03-09 22:55:42.598  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Single vip registry refresh property : null
2016-03-09 22:55:42.598  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Force full registry fetch : false
2016-03-09 22:55:42.598  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Application is null : false
2016-03-09 22:55:42.598  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Registered Applications size is zero : true
2016-03-09 22:55:42.598  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Application version is -1: true
2016-03-09 22:55:42.598  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Getting all instance registry info from the eureka server
2016-03-09 22:55:42.605  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : The response status is 200
2016-03-09 22:55:42.606  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Not registering with Eureka server per configuration
2016-03-09 22:55:42.718  INFO 50837 --- [o-auto-1-exec-5] o.s.c.support.DefaultLifecycleProcessor  : Starting beans in phase 0
2016-03-09 22:55:42.720  INFO 50837 --- [o-auto-1-exec-5] o.s.boot.SpringApplication               : Started application in 0.507 seconds (JVM running for 1491.327)
2016-03-09 22:55:42.759  INFO 50837 --- [o-auto-1-exec-5] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at: http://192.168.1.81:8888/
2016-03-09 22:55:42.885  INFO 50837 --- [o-auto-1-exec-5] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=sample-service, profiles=[default], label=null, version=null
2016-03-09 22:55:42.886  INFO 50837 --- [o-auto-1-exec-5] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource [name='configService', propertySources=[MapPropertySource [name='classpath:/config/sample-service.yml']]]
2016-03-09 22:55:42.891  INFO 50837 --- [o-auto-1-exec-5] o.s.boot.SpringApplication               : No active profile set, falling back to default profiles: default
2016-03-09 22:55:42.894  INFO 50837 --- [o-auto-1-exec-5] s.c.a.AnnotationConfigApplicationContext : Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@223d1487: startup date [Wed Mar 09 22:55:42 GMT 2016]; parent: org.springframework.context.annotation.AnnotationConfigApplicationContext@6cdfb862
2016-03-09 22:55:42.896  INFO 50837 --- [o-auto-1-exec-5] f.a.AutowiredAnnotationBeanPostProcessor : JSR-330 'javax.inject.Inject' annotation found and supported for autowiring
2016-03-09 22:55:42.912  INFO 50837 --- [o-auto-1-exec-5] o.s.boot.SpringApplication               : Started application in 0.725 seconds (JVM running for 1491.519)
2016-03-09 22:55:42.912  INFO 50837 --- [o-auto-1-exec-5] s.c.a.AnnotationConfigApplicationContext : Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@223d1487: startup date [Wed Mar 09 22:55:42 GMT 2016]; parent: org.springframework.context.annotation.AnnotationConfigApplicationContext@6cdfb862
2016-03-09 22:55:42.917  INFO 50837 --- [o-auto-1-exec-5] c.n.e.EurekaDiscoveryClientConfiguration : Unregistering application sample-service with eureka with status DOWN
2016-03-09 22:55:42.935  INFO 50837 --- [o-auto-1-exec-5] s.c.a.AnnotationConfigApplicationContext : Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@6cdfb862: startup date [Wed Mar 09 22:55:42 GMT 2016]; root of context hierarchy
2016-03-09 22:55:42.937  INFO 50837 --- [o-auto-1-exec-5] c.n.e.EurekaDiscoveryClientConfiguration : Unregistering application sample-service with eureka with status DOWN
2016-03-09 22:55:42.940  INFO 50837 --- [o-auto-1-exec-5] o.s.c.support.DefaultLifecycleProcessor  : Stopping beans in phase 0
2016-03-09 22:55:43.261  INFO 50837 --- [o-auto-1-exec-5] o.s.c.n.eureka.InstanceInfoFactory       : Setting initial instance status as: STARTING
2016-03-09 22:55:43.268  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_SAMPLE-SERVICE/marks-mbp.home:sample-service:0 - deregister  status: 200
2016-03-09 22:55:43.279  INFO 50837 --- [o-auto-1-exec-5] c.n.e.EurekaDiscoveryClientConfiguration : Unregistering application sample-service with eureka with status DOWN
2016-03-09 22:55:43.283  INFO 50837 --- [o-auto-1-exec-5] c.n.d.provider.DiscoveryJerseyProvider   : Using JSON encoding codec LegacyJacksonJson
2016-03-09 22:55:43.284  INFO 50837 --- [o-auto-1-exec-5] c.n.d.provider.DiscoveryJerseyProvider   : Using JSON decoding codec LegacyJacksonJson
2016-03-09 22:55:43.284  INFO 50837 --- [o-auto-1-exec-5] c.n.d.provider.DiscoveryJerseyProvider   : Using XML encoding codec XStreamXml
2016-03-09 22:55:43.284  INFO 50837 --- [o-auto-1-exec-5] c.n.d.provider.DiscoveryJerseyProvider   : Using XML decoding codec XStreamXml
2016-03-09 22:55:43.464  INFO 50837 --- [o-auto-1-exec-5] c.n.d.s.r.aws.ConfigClusterResolver      : Resolving eureka endpoints via configuration
2016-03-09 22:55:43.465  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Disable delta property : false
2016-03-09 22:55:43.465  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Single vip registry refresh property : null
2016-03-09 22:55:43.465  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Force full registry fetch : false
2016-03-09 22:55:43.465  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Application is null : false
2016-03-09 22:55:43.465  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Registered Applications size is zero : true
2016-03-09 22:55:43.465  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Application version is -1: true
2016-03-09 22:55:43.466  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Getting all instance registry info from the eureka server
2016-03-09 22:55:43.471  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : The response status is 200
2016-03-09 22:55:43.472  INFO 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Starting heartbeat executor: renew interval is: 30
2016-03-09 22:55:43.473  INFO 50837 --- [o-auto-1-exec-5] c.n.discovery.InstanceInfoReplicator     : InstanceInfoReplicator onDemand update allowed rate per min is 4
2016-03-09 22:55:43.474  INFO 50837 --- [o-auto-1-exec-5] c.n.e.EurekaDiscoveryClientConfiguration : Registering application sample-service with eureka with status UP
2016-03-09 22:55:43.475  WARN 50837 --- [o-auto-1-exec-5] com.netflix.discovery.DiscoveryClient    : Saw local status change event StatusChangeEvent [timestamp=1457564143475, current=UP, previous=DOWN]
2016-03-09 22:55:43.476  INFO 50837 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_SAMPLE-SERVICE/marks-mbp.home:sample-service:0: registering service...
2016-03-09 22:55:43.479  INFO 50837 --- [o-auto-1-exec-5] c.n.e.EurekaDiscoveryClientConfiguration : Unregistering application sample-service with eureka with status DOWN
2016-03-09 22:55:43.479  INFO 50837 --- [o-auto-1-exec-5] c.n.e.EurekaDiscoveryClientConfiguration : Registering application sample-service with eureka with status UP
2016-03-09 22:55:43.483  INFO 50837 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_SAMPLE-SERVICE/marks-mbp.home:sample-service:0 - registration status: 204
```
