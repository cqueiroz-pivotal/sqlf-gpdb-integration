sqlf-gpdb-integration
=====================

To compile: mvn clean install

To use it on sqlfire: 

- copy target/sqlf-gpdb-integration.jar to sqlfire/ext-lib folder. 
- create table using:
call SYS.ADD_LISTENER('%%LISTENER NAME %%',
     'APP','%%TABLE NAME%%',
     'com.gopivotal.poc.sqlfgpdb.MyProxyDispatcher',     
     'connectionurl=jdbc:sqlfire:|numproxies=4|proxyTablePrefix=tableProxy|username=app|password=app',
      null);
      
- create async listener class name: com.gopivotal.poc.sqlfgpdb.MyBatchListener

