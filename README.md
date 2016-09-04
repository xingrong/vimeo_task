# Introduction #

A sample application to track Vimeo views on the trailers of a number of movies at an hourly
frequency in order to understand the change in views throughout a 24-hour period.

# Key Technology #
* Java
* Maven
* Eclipse
* AWS DynamoDB
* AWS S3
* MySQL
* MyBatis
* JSON
* RESTful Webservice

# Deployment Instruction #
## Environment ##
* Ali Web Service EC2
* CentOS 6.5
* Java 1.8.0_101
* Maven 3.3.9
## Steps ##
* clone project code
```
cd /apps/svr
git clone git@github.com:xingrong/vimeo_task.git
cd vimeo_task
```
* create conf folder and copy configure files to conf folder
```
mkdir conf
```
* build project
```
sh ./bin/build
```
* run project
```
sh ./bin/run
```