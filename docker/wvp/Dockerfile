FROM maven:3.9-eclipse-temurin-17 AS builder

COPY . /build
WORKDIR /build
RUN mvn clean package -Dmaven.test.skip=true

WORKDIR /build/target
RUN mv wvp-pro-*.jar wvp.jar

FROM eclipse-temurin:17-jre

EXPOSE 18978/tcp
EXPOSE 8116/tcp
EXPOSE 8116/udp
EXPOSE 8080/tcp

ENV TZ=Asia/Shanghai
RUN mkdir -p /opt/wvp /opt/ylcx/wvp
WORKDIR /opt/wvp
COPY --from=builder /build/target /opt/wvp
COPY ./docker/wvp/wvp /opt/ylcx/wvp
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/opt/ylcx/", "-jar", "wvp.jar", "--spring.config.location=/opt/ylcx/wvp/application.yml"]
