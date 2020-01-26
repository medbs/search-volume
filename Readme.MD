
#### Prerequisites
* Java 8
* Docker engine & Docker-compose (optional)


#### Build the project
```
mvn clean build
```

#### Run the project as jar
```
java -jar target/*.jar
```

#### Run the project as container
```
docker-compose up score
```

### Test the API
```
curl -X GET "localhost:8080/api/v1/estimate?keyWord=iphone+11"
```