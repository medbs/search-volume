
### Prerequisites
* Java 8
* Docker engine & Docker-compose (optional)


### Build the project
Build

```
mvn clean build
```

### Run the project as jar
Run jar

```
java -jar target/*.jar
```

### Run the project as container
Run container
```
docker-compose up score
```

### Test the API
Curl the api 
```
curl -X GET "localhost:8080/api/v1/estimate?keyWord=iphone+11"
```