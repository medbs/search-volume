#### Build the project
```
mvn clean build
```

#### Run the project
```
java -jar target/*.jar
```

#### Test the API
```
curl -X GET "localhost:8080/api/v1/estimate?keyWord=iphone+11"
```