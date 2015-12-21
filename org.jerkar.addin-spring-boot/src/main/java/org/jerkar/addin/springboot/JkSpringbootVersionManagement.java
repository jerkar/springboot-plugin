package org.jerkar.addin.springboot;

import org.jerkar.api.depmanagement.JkDependencyExclusions;
import org.jerkar.api.depmanagement.JkVersionProvider;

import static org.jerkar.addin.springboot.JkSpringModules.*;

/**
 * Versions suggested by Spring boot. The defaults are the ones suggested by
 * springboot version <code>1.2.7.RELEASE</code>
 * 
 * @author djeang
 * @formatter:off
 */
public class JkSpringbootVersionManagement {

    public String comAtomikosVersion = "3.9.3";

    public String comFasterxmlJacksonCoreVersion = "2.4.6";

    public String comFasterxmlJacksonDataformatVersion = "2.4.6";

    public String comFasterxmlJacksonDatatypeVersion = "2.4.6";

    public String comZaxxerVersion = "2.2.5";

    public String ioDropwizardMetricsVersion = "3.1.2";

    public String ioUndertowVersion = "1.1.8.Final";

    public String orgApacheActivemqVersion = "5.10.2";

    public String orgApacheLoggingLog4jVersion = "2.1";

    public String orgApacheTomcatEmbedVersion = "8.0.28";

    public String orgApacheTomcatVersion = "8.0.28";

    public String orgAspectjVersion = "1.8.7";

    public String orgCodehausGroovyVersion = "2.4.4";

    public String orgCrashubVersion = "1.3.2";

    public String orgEclipseJettyVersion = "9.2.13.v20150730";

    public String orgEclipseJettyWebsocketVersion = "9.2.13.v20150730";
    
    public String orgEclipseJettyJspVersion = "2.2.0.v201112011158"; 

    public String orgGlassfishJerseyContainersVersion = "2.14";

    public String orgGlassfishJerseyExtVersion = "2.14";

    public String orgHamcrestVersion = "1.3";

    public String orgHornetqVersion = "2.4.7.Final";

    public String orgProjectreactorSpringVersion = "1.1.3.RELEASE";

    public String orgProjectreactorVersion = "1.1.6.RELEASE";

    public String orgSlf4jVersion = "1.7.12";

    public String orgSpockframeworkVersion = "0.7-groovy-2.0";

    public String orgSpringframeworkAmqpVersion = "1.4.6.RELEASE";

    public String orgSpringframeworkBatchVersion = "3.0.5.RELEASE";

    public String orgSpringframeworkBootVersion = "1.2.7.RELEASE";

    public String orgSpringframeworkCloudVersion = "1.1.1.RELEASE";

    public String orgSpringframeworkIntegrationVersion = "4.1.6.RELEASE";

    public String orgSpringframeworkSecurityVersion = "3.2.8.RELEASE";

    public String orgSpringframeworkVersion = "4.1.8.RELEASE";

    public String orgSpringframeworkWsVersion = "2.2.2.RELEASE";

    public String orgThymeleafVersion = "2.1.4.RELEASE";

    private JkSpringbootVersionManagement() {

    }

    public static JkSpringbootVersionManagement v1_2_7() {
        return new JkSpringbootVersionManagement();
    }
    
    public static JkSpringbootVersionManagement v1_3_1() {
        JkSpringbootVersionManagement result = new JkSpringbootVersionManagement();
        result.orgSpringframeworkBootVersion = "1.3.1.RELEASSE";
        result.comZaxxerVersion = "2.4.3";
        result.orgApacheActivemqVersion = "5.12.1";
        result.orgApacheLoggingLog4jVersion = "2.4.1";
        result.orgApacheTomcatEmbedVersion = "8.0.30";
        result.orgEclipseJettyVersion = "9.2.14.v20151106";
        result.orgEclipseJettyWebsocketVersion = result.orgEclipseJettyVersion;
        result.orgGlassfishJerseyContainersVersion = "2.22.1";
        result.orgGlassfishJerseyExtVersion = result.orgGlassfishJerseyContainersVersion;
        result.orgSpockframeworkVersion = "1.0-groovy-2.4";
        result.orgSpringframeworkAmqpVersion = "1.5.2.RELEASE";
        result.orgSpringframeworkBatchVersion = "3.0.6.RELEASE";
        result.orgSpringframeworkCloudVersion = "1.2.1.RELEASE";
       
        return result;
    }

    public JkVersionProvider versionProvider() {
        return JkVersionProvider.of()
                .and("antlr:antlr", "2.7.7")
                .and("ch.qos.logback:logback-classic", "1.1.3")
                .and("com.atomikos:transactions-jdbc", comAtomikosVersion)
                .and("com.atomikos:transactions-jms", comAtomikosVersion)
                .and("com.atomikos:transactions-jta", comAtomikosVersion)
                .and("com.fasterxml.jackson.core:jackson-annotations", comFasterxmlJacksonCoreVersion)
                .and("com.fasterxml.jackson.core:jackson-core", comFasterxmlJacksonCoreVersion)
                .and("com.fasterxml.jackson.core:jackson-databind", comFasterxmlJacksonCoreVersion)
                .and("com.fasterxml.jackson.dataformat:jackson-dataformat-xml", comFasterxmlJacksonDataformatVersion)
                .and("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", comFasterxmlJacksonDataformatVersion)
                .and("com.fasterxml.jackson.datatype:jackson-datatype-jdk8", comFasterxmlJacksonDatatypeVersion)
                .and("com.fasterxml.jackson.datatype:jackson-datatype-joda", comFasterxmlJacksonDatatypeVersion)
                .and("com.fasterxml.jackson.datatype:jackson-datatype-jsr310", comFasterxmlJacksonDatatypeVersion)
                .and("com.gemstone.gemfire:gemfire", "7.0.2")
                .and("com.github.mxab.thymeleaf.extras:thymeleaf-extras-data-attribute", "1.3")
                .and("com.google.code.gson:gson", "2.3.1")
                .and("com.googlecode.json-simple:json-simple", "1.1.1")
                .and("com.h2database:h2", "1.4.190")
                .and("com.jayway.jsonpath:json-path", "0.9.1")
                .and("com.samskivert:jmustache", "1.10")
                .and("com.sun.mail:javax.mail", "1.5.4")
                .and("com.zaxxer:HikariCP", comZaxxerVersion)
                .and("com.zaxxer:HikariCP-java6", comZaxxerVersion)
                .and("commons-beanutils:commons-beanutils", "1.9.2")
                .and("commons-collections:commons-collections", "3.2.1")
                .and("commons-dbcp:commons-dbcp", "1.4")
                .and("commons-digester:commons-digester", "2.1")
                .and("commons-pool:commons-pool", "1.6")
                .and("io.dropwizard.metrics:metrics-core", ioDropwizardMetricsVersion)
                .and("io.dropwizard.metrics:metrics-ganglia", ioDropwizardMetricsVersion)
                .and("io.dropwizard.metrics:metrics-graphite", ioDropwizardMetricsVersion)
                .and("io.dropwizard.metrics:metrics-servlets", ioDropwizardMetricsVersion)
                .and("io.undertow:undertow-core", ioUndertowVersion)
                .and("io.undertow:undertow-servlet", ioUndertowVersion)
                .and("io.undertow:undertow-websockets-jsr", ioUndertowVersion)
                .and("javax.cache:cache-api", "1.0.0")
                .and("javax.jms:jms-api", "1.1-rev-1")
                .and("javax.mail:javax.mail-api", "1.5.4")
                .and("javax.servlet:javax.servlet-api", "3.1.0")
                .and("javax.servlet:jstl", "1.2")
                .and("javax.transaction:javax.transaction-api", "1.2")
                .and("jaxen:jaxen", "1.1.6")
                .and("joda-time:joda-time", "2.5")
                .and("junit:junit", "4.12")
                .and("log4j:log4j", "1.2.17")
                .and("mysql:mysql-connector-java", "5.1.36")
                .and("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect", "1.2.9")
                .and("org.apache.activemq:activemq-broker", orgApacheActivemqVersion)
                .and("org.apache.activemq:activemq-client", orgApacheActivemqVersion)
                .and("org.apache.activemq:activemq-jms-pool", orgApacheActivemqVersion)
                .and("org.apache.activemq:activemq-pool", orgApacheActivemqVersion)
                .and("org.apache.commons:commons-dbcp2", "2.0.1")
                .and("org.apache.commons:commons-pool2", "2.2")
                .and("org.apache.derby:derby", "10.10.2.0")
                .and("org.apache.httpcomponents:httpasyncclient", "4.0.2")
                .and("org.apache.httpcomponents:httpclient", "4.3.6")
                .and("org.apache.httpcomponents:httpmime", "4.3.6")
                .and("org.apache.logging.log4j:log4j-api", orgApacheLoggingLog4jVersion)
                .and("org.apache.logging.log4j:log4j-core", orgApacheLoggingLog4jVersion)
                .and("org.apache.logging.log4j:log4j-slf4j-impl", orgApacheLoggingLog4jVersion)
                .and("org.apache.solr:solr-solrj", "4.7.2")
                .and("org.apache.tomcat:tomcat-jdbc", orgApacheTomcatVersion)
                .and("org.apache.tomcat:tomcat-jsp-api", orgApacheTomcatVersion)
                .and("org.apache.tomcat.embed:tomcat-embed-core", orgApacheTomcatEmbedVersion)
                .and("org.apache.tomcat.embed:tomcat-embed-el", orgApacheTomcatEmbedVersion)
                .and("org.apache.tomcat.embed:tomcat-embed-jasper", orgApacheTomcatEmbedVersion)
                .and("org.apache.tomcat.embed:tomcat-embed-logging-juli", orgApacheTomcatEmbedVersion)
                .and("org.apache.tomcat.embed:tomcat-embed-websocket", orgApacheTomcatEmbedVersion)
                .and("org.apache.velocity:velocity", "1.7")
                .and("org.apache.velocity:velocity-tools", "2.0")
                .and("org.aspectj:aspectjrt", orgAspectjVersion)
                .and("org.aspectj:aspectjtools", orgAspectjVersion)
                .and("org.aspectj:aspectjweaver", orgAspectjVersion)
                .and("org.codehaus.btm:btm", "2.1.4")
                .and("org.codehaus.groovy:groovy", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-all", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-ant", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-bsf", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-console", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-docgenerator", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-groovydoc", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-groovysh", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-jmx", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-json", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-jsr223", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-nio", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-servlet", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-sql", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-swing", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-templates", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-test", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-testng", orgCodehausGroovyVersion)
                .and("org.codehaus.groovy:groovy-xml", orgCodehausGroovyVersion)
                .and("org.codehaus.janino:janino", "2.6.1")
                .and("org.crashub:crash.cli", orgCrashubVersion)
                .and("org.crashub:crash.connectors.ssh", orgCrashubVersion)
                .and("org.crashub:crash.connectors.telnet", orgCrashubVersion)
                .and("org.crashub:crash.embed.spring", orgCrashubVersion)
                .and("org.crashub:crash.plugins.cron", orgCrashubVersion)
                .and("org.crashub:crash.plugins.mail", orgCrashubVersion)
                .and("org.crashub:crash.shell", orgCrashubVersion)
                .and("org.eclipse.jetty:jetty-annotations", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-continuation", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-deploy", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-http", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-io", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-jmx", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-jsp", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-plus", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-security", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-server", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-servlet", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-servlets", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-util", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-webapp", orgEclipseJettyVersion)
                .and("org.eclipse.jetty:jetty-xml", orgEclipseJettyVersion)
                .and("org.eclipse.jetty.orbit:javax.servlet.jsp", orgEclipseJettyJspVersion)
                .and("org.eclipse.jetty.websocket:javax-websocket-server-impl", orgEclipseJettyWebsocketVersion)
                .and("org.eclipse.jetty.websocket:websocket-server", orgEclipseJettyWebsocketVersion)
                .and("org.flywaydb:flyway-core", "3.1")
                .and("org.freemarker:freemarker", "2.3.23")
                .and("org.glassfish:javax.el", "3.0.0")
                .and("org.glassfish.jersey.containers:jersey-container-servlet", orgGlassfishJerseyContainersVersion)
                .and("org.glassfish.jersey.containers:jersey-container-servlet-core",
                        orgGlassfishJerseyContainersVersion)
                .and("org.glassfish.jersey.core:jersey-server", "2.14")
                .and("org.glassfish.jersey.ext:jersey-bean-validation", orgGlassfishJerseyExtVersion)
                .and("org.glassfish.jersey.ext:jersey-spring3", orgGlassfishJerseyExtVersion)
                .and("org.glassfish.jersey.media:jersey-media-json-jackson", "2.14")
                .and("org.hamcrest:hamcrest-core", orgHamcrestVersion)
                .and("org.hamcrest:hamcrest-library", orgHamcrestVersion)
                .and("org.hibernate:hibernate-ehcache", "4.3.11.Final")
                .and("org.hibernate:hibernate-entitymanager", "4.3.11.Final")
                .and("org.hibernate:hibernate-envers", "4.3.11.Final")
                .and("org.hibernate:hibernate-jpamodelgen", "4.3.11.Final")
                .and("org.hibernate:hibernate-validator", "5.1.3.Final")
                .and("org.hornetq:hornetq-jms-client", orgHornetqVersion)
                .and("org.hornetq:hornetq-jms-server", orgHornetqVersion)
                .and("org.hsqldb:hsqldb", "2.3.3")
                .and("org.javassist:javassist", "3.18.1-GA")
                .and("org.jdom:jdom2", "2.0.6")
                .and("org.jolokia:jolokia-core", "1.2.3")
                .and("org.liquibase:liquibase-core", "3.3.5")
                .and("org.mockito:mockito-core", "1.10.19")
                .and("org.mongodb:mongo-java-driver", "2.12.5")
                .and("org.projectreactor:reactor-core", orgProjectreactorVersion)
                .and("org.projectreactor:reactor-groovy", orgProjectreactorVersion)
                .and("org.projectreactor:reactor-groovy-extensions", orgProjectreactorVersion)
                .and("org.projectreactor:reactor-logback", orgProjectreactorVersion)
                .and("org.projectreactor:reactor-net", orgProjectreactorVersion)
                .and("org.projectreactor.spring:reactor-spring-context", orgProjectreactorSpringVersion)
                .and("org.projectreactor.spring:reactor-spring-core", orgProjectreactorSpringVersion)
                .and("org.projectreactor.spring:reactor-spring-messaging", orgProjectreactorSpringVersion)
                .and("org.projectreactor.spring:reactor-spring-webmvc", orgProjectreactorSpringVersion)
                .and("org.slf4j:jcl-over-slf4j", orgSlf4jVersion)
                .and("org.slf4j:jul-to-slf4j", orgSlf4jVersion)
                .and("org.slf4j:log4j-over-slf4j", orgSlf4jVersion)
                .and("org.slf4j:slf4j-api", orgSlf4jVersion)
                .and("org.slf4j:slf4j-jdk14", orgSlf4jVersion)
                .and("org.slf4j:slf4j-log4j12", orgSlf4jVersion)
                .and("org.spockframework:spock-core", orgSpockframeworkVersion)
                .and("org.spockframework:spock-spring", orgSpockframeworkVersion)
                .and(Fwk.AOP, orgSpringframeworkVersion)
                .and(Fwk.ASPECTS, orgSpringframeworkVersion)
                .and(Fwk.BEANS, orgSpringframeworkVersion)
                .and(Fwk.CONTEXT, orgSpringframeworkVersion)
                .and(Fwk.CONTEXT_SUPPORT, orgSpringframeworkVersion)
                .and(Fwk.CORE, orgSpringframeworkVersion)
                .and(Fwk.EXPRESSION, orgSpringframeworkVersion)
                .and(Fwk.INSTRUMENT, orgSpringframeworkVersion)
                .and(Fwk.INSTRUMENT_TOMCAT, orgSpringframeworkVersion)
                .and(Fwk.JDBC, orgSpringframeworkVersion)
                .and(Fwk.JMS, orgSpringframeworkVersion)
                .and(Fwk.MESSAGING, orgSpringframeworkVersion)
                .and(Fwk.ORM, orgSpringframeworkVersion)
                .and(Fwk.OXM, orgSpringframeworkVersion)
                .and(Fwk.TEST, orgSpringframeworkVersion)
                .and(Fwk.TX, orgSpringframeworkVersion)
                .and(Fwk.WEB, orgSpringframeworkVersion)
                .and(Fwk.WEBMVC, orgSpringframeworkVersion)
                .and(Fwk.WEBMVC_PORTLET, orgSpringframeworkVersion)
                .and(Fwk.WEBSOCKET, orgSpringframeworkVersion)
                .and(Fwk.GROUP, "springloaded", "1.2.4.RELEASE")
                .and(Amqp.RABBIT, orgSpringframeworkAmqpVersion)
                .and(Batch.CORE, orgSpringframeworkBatchVersion)
                .and(Batch.INFRASTRUCTURE, orgSpringframeworkBatchVersion)
                .and(Batch.INTEGRATION, orgSpringframeworkBatchVersion)
                .and(Batch.TEST, orgSpringframeworkBatchVersion)
                .and(Boot.ACTUATOR, orgSpringframeworkBootVersion)
                .and(Boot.AUTOCONFIGURE, orgSpringframeworkBootVersion)
                .and(Boot.CONFIGURATION_PROCESSOR, orgSpringframeworkBootVersion)
                .and(Boot.DEPENDENCY_TOOLS, orgSpringframeworkBootVersion)
                .and(Boot.LOADER, orgSpringframeworkBootVersion)
                .and(Boot.LOADER_TOOLS, orgSpringframeworkBootVersion)
                .and(Boot.STARTER, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_ACTUATOR, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_AMQP, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_AOP, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_BATCH, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_CLOUD_CONNECTORS, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_DATA_ELASTICSEARCH, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_DATA_GEMFIRE, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_DATA_JPA, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_DATA_MONGODB, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_DATA_REST, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_DATA_SOLR, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_FREEMARKER, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_GROOVY_TEMPLATES, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_HATEOAS, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_HORNETQ, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_INTEGRATION, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_JDBC, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_JERSEY, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_JETTY, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_JTA_ATOMIKOS, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_JTA_BITRONIX, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_LOG4J, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_LOG4J2, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_LOGGING, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_MAIL, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_MOBILE, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_MUSTACHE, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_PARENT, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_REDIS, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_REMOTE_SHELL, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_SECURITY, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_SOCIAL_FACEBOOK, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_SOCIAL_LINKEDIN, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_SOCIAL_TWITTER, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_TEST, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_THYMELEAF, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_TOMCAT, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_UNDERTOW, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_VELOCITY, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_WEB, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_WEBSOCKET, orgSpringframeworkBootVersion)
                .and(Boot.STARTER_WS, orgSpringframeworkBootVersion)
                .and(Cloud.CLOUDFOUNDRY_CONNECTOR, orgSpringframeworkCloudVersion)
                .and(Cloud.CORE, orgSpringframeworkCloudVersion)
                .and(Cloud.HEROKU_CONNECTOR, orgSpringframeworkCloudVersion)
                .and(Cloud.LOCALCONFIG_CONNECTOR, orgSpringframeworkCloudVersion)
                .and(Cloud.SPRING_SERVICE_CONNECTOR, orgSpringframeworkCloudVersion)
                .and(Data.CQL, "1.1.4.RELEASE")
                .and(Data.CASSANDRA, "1.1.4.RELEASE")
                .and(Data.COMMONS, "1.9.4.RELEASE")
                .and(Data.COUCHBASE, "1.2.4.RELEASE")
                .and(Data.ELASTICSEARCH, "1.1.4.RELEASE")
                .and(Data.GEMFIRE, "1.5.4.RELEASE")
                .and(Data.JPA, "1.7.4.RELEASE")
                .and(Data.MONGODB, "1.6.4.RELEASE")
                .and(Data.MONGODB_CROSS_STORE, "1.6.4.RELEASE")
                .and(Data.MONGODB_LOG4J, "1.6.4.RELEASE")
                .and(Data.NEO4J, "3.2.4.RELEASE")
                .and(Data.REDIS, "1.4.4.RELEASE")
                .and(Data.REST_CORE, "2.2.4.RELEASE")
                .and(Data.REST_WEBMVC, "2.2.4.RELEASE")
                .and(Data.SOLR, "1.3.4.RELEASE")
                .and(Hateoas.HATEOAS, "0.16.0.RELEASE")
                .and(Integration.AMQP, orgSpringframeworkIntegrationVersion)
                .and(Integration.CORE, orgSpringframeworkIntegrationVersion)
                .and(Integration.EVENT, orgSpringframeworkIntegrationVersion)
                .and(Integration.FEED, orgSpringframeworkIntegrationVersion)
                .and(Integration.FILE, orgSpringframeworkIntegrationVersion)
                .and(Integration.FTP, orgSpringframeworkIntegrationVersion)
                .and(Integration.GEMFIRE, orgSpringframeworkIntegrationVersion)
                .and(Integration.GROOVY, orgSpringframeworkIntegrationVersion)
                .and(Integration.HTTP, orgSpringframeworkIntegrationVersion)
                .and(Integration.IP, orgSpringframeworkIntegrationVersion)
                .and(Integration.JDBC, orgSpringframeworkIntegrationVersion)
                .and(Integration.JMS, orgSpringframeworkIntegrationVersion)
                .and(Integration.JMX, orgSpringframeworkIntegrationVersion)
                .and(Integration.JPA, orgSpringframeworkIntegrationVersion)
                .and(Integration.MAIL, orgSpringframeworkIntegrationVersion)
                .and(Integration.MONGODB, orgSpringframeworkIntegrationVersion)
                .and(Integration.MQTT, orgSpringframeworkIntegrationVersion)
                .and(Integration.REDIS, orgSpringframeworkIntegrationVersion)
                .and(Integration.RMI, orgSpringframeworkIntegrationVersion)
                .and(Integration.SCRIPTING, orgSpringframeworkIntegrationVersion)
                .and(Integration.SECURITY, orgSpringframeworkIntegrationVersion)
                .and(Integration.SFTP, orgSpringframeworkIntegrationVersion)
                .and(Integration.STREAM, orgSpringframeworkIntegrationVersion)
                .and(Integration.SYSLOG, orgSpringframeworkIntegrationVersion)
                .and(Integration.TEST, orgSpringframeworkIntegrationVersion)
                .and(Integration.TWITTER, orgSpringframeworkIntegrationVersion)
                .and(Integration.WEBSOCKET, orgSpringframeworkIntegrationVersion)
                .and(Integration.WS, orgSpringframeworkIntegrationVersion)
                .and(Integration.XML, orgSpringframeworkIntegrationVersion)
                .and(Integration.XMPP, orgSpringframeworkIntegrationVersion)
                .and(Mobile.DEVICE, "1.1.5.RELEASE")
                .and(Plugin.GROUP, "1.1.0.RELEASE")
                .and(Security.ACL, orgSpringframeworkSecurityVersion)
                .and(Security.ASPECTS, orgSpringframeworkSecurityVersion)
                .and(Security.CAS, orgSpringframeworkSecurityVersion)
                .and(Security.CONFIG, orgSpringframeworkSecurityVersion)
                .and(Security.CORE, orgSpringframeworkSecurityVersion)
                .and(Security.CRYPTO, orgSpringframeworkSecurityVersion)
                .and(Security.LDAP, orgSpringframeworkSecurityVersion)
                .and(Security.OPENID, orgSpringframeworkSecurityVersion)
                .and(Security.REMOTING, orgSpringframeworkSecurityVersion)
                .and(Security.TAGLIBS, orgSpringframeworkSecurityVersion)
                .and(Security.WEB, orgSpringframeworkSecurityVersion)
                .and(Security.JWT, "1.0.3.RELEASE")
                .and(Social.CONFIG, "1.1.2.RELEASE")
                .and(Social.CORE, "1.1.2.RELEASE")
                .and(Social.FACEBOOK, "2.0.2.RELEASE")
                .and(Social.FACEBOOK_WEB, "2.0.2.RELEASE")
                .and(Social.LINKEDIN, "1.0.2.RELEASE")
                .and(Social.SECURITY, "1.1.2.RELEASE")
                .and(Social.TWITTER, "1.1.1.RELEASE")
                .and(Social.WEB, "1.1.2.RELEASE")
                .and(Ws.CORE, orgSpringframeworkWsVersion)
                .and(Ws.SECURITY, orgSpringframeworkWsVersion)
                .and(Ws.SUPPORT, orgSpringframeworkWsVersion)
                .and(Ws.TEST, orgSpringframeworkWsVersion)
                .and("org.thymeleaf:thymeleaf", orgThymeleafVersion)
                .and("org.thymeleaf:thymeleaf-spring4", orgThymeleafVersion)
                .and("org.thymeleaf.extras:thymeleaf-extras-conditionalcomments", "2.1.1.RELEASE")
                .and("org.thymeleaf.extras:thymeleaf-extras-springsecurity3", "2.1.2.RELEASE")
                .and("org.yaml:snakeyaml", "1.14")
                .and("redis.clients:jedis", "2.5.2")
                .and("wsdl4j:wsdl4j", "1.6.3");
    }

    public JkDependencyExclusions dependencyExclusions() {
        return JkDependencyExclusions.builder()
                .on("commons-beanutils:commons-beanutils", "commons-logging:commons-logging")
                .on("commons-digester:commons-digester", "commons-logging:commons-logging")
                .on("org.apache.httpcomponents:httpasyncclient", "commons-logging:commons-logging")
                .on("org.apache.httpcomponents:httpclient", "commons-logging:commons-logging")
                .on("org.apache.velocity:velocity-tools", "commons-logging:commons-logging")
                .on("org.crashub:crash.connectors.ssh", "commons-logging:commons-logging")
                .on("org.eclipse.jetty:jetty-jsp", "org.eclipse.jetty.orbit:javax.servlet")
                .on("org.hornetq:hornetq-jms-server", "org.jboss.spec.javax.transaction:jboss-transaction-api_1.1_spec")
                .on("org.spockframework:spock-core", "org.codehaus.groovy:groovy-all")
                .on(Fwk.CORE, "commons-logging:commons-logging")
                .on(Boot.STARTER, "commons-logging:commons-logging")
                .on(Boot.STARTER_TEST, "commons-logging:commons-logging")
                .on(Cloud.SPRING_SERVICE_CONNECTOR, "log4j:log4j")
                .on(Integration.HTTP, "commons-logging:commons-logging", "commons-logging:commons-logging-api")
                .on(Ws.CORE, "commons-logging:commons-logging")
                .on(Ws.SECURITY, "commons-logging:commons-logging")
                .on(Ws.SUPPORT, "commons-logging:commons-logging")
                .on(Ws.TEST, "commons-logging:commons-logging")
                .build();
    }

}
