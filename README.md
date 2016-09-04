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
sh ./bin/run start
```

# About Rate Limit of Vimeo API #
* The API rate limit is generally based on a 15-minute rolling window (this window may change without notice). In other words, if you surpass the rate limit within a 15-minute time period your API app will be banned.
* The actual rate limit varies depending on the app making the request, authentication as another user or the app owner, the endpoint being requested, usage of our JSON filter parameters in the request uri, and the rate limit tier of the app in our system.
* Requests made after breaching the rate limit will return a 429 Too Many Requests error, with the date/time the ban will be lifted included in the header as the X-RateLimit-Reset value. Rate limit bans are enforced for roughly 60 minutes after the rate limit was initially breached (this duration may change without notice).
* If your app continues to make a significant number of requests to the API after we return the 429 Too Many Requests error, the IP address making requests may be banned. We strongly recommend apps cease making requests to the API once a 429 error is returned. Requests banned according to the IP address will return the banned IP address in the X-Banned-IP header of the 429 error response.
