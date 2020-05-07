package com.github.xjs.util.web;

//或者：
//
//（1）@WebFilter+ @Order + @Component order越大越靠后执行
//（2）@WebFilter + @Order + @ServletComponentScan
//@Configuration
//public class WebFilterConfig {
//
//    @Bean
//    public FilterRegistrationBean xssFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setDispatcherTypes(DispatcherType.REQUEST);
//        registration.setFilter(new XssFilter());
//        registration.addUrlPatterns("/*");
//        registration.setName("xssFilter");
//        registration.setOrder(Integer.MAX_VALUE);//越低越优先
//        return registration;
//    }
//
//    @Bean
//    public FilterRegistrationBean corsFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setDispatcherTypes(DispatcherType.REQUEST);
//        registration.setFilter(new CorsFilter());
//        registration.addUrlPatterns("/*");
//        registration.setName("corsFilter");
//        registration.setOrder(Integer.MAX_VALUE);//越低越优先
//        return registration;
//    }
//}
