gemfirexd-gpdb-integration
=========================

To compile: mvn clean package

To use it on gemfirexd:

- copy target/sqlf-gpdb-integration.jar to gemfirexd/ext-lib folder.
- run DDL env_setup.ddl
    - remember to change gpdb server name, port and database. 

