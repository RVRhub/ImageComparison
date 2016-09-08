#Image Comparison
The program in Java that compares any 2 images and shows the differences visually.


Technologies
    Java 1.8
    Spring 4.3.2.RELEASE
    Apache Tomcat/8.5.4
    Thymeleaf 2.1.5.RELEASE


How to run the application?:

Two ways:
    1. WEB:
        - mvn clean package
        - java -jar target/image-comparison-1.0-SNAPSHOT.jar
        - Go to url: http://0.0.0.0:8080/
    2. Console:
        - We need to find a class: com.rybak.agileengine.Main
        - Configure Settings: firstImage/secondImage/resultFileName
        - Run it