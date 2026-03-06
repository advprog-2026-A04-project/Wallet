FROM docker.io/library/eclipse-temurin:21-jdk-alpine AS builder

# install Node for React
RUN apk add --no-cache nodejs npm

WORKDIR /src/wallet
COPY . .

# build React
WORKDIR /src/wallet/frontend
RUN npm install
RUN npm run build

# copy React build into Spring Boot static resources
RUN mkdir -p /src/wallet/src/main/resources/static
RUN cp -r dist/* /src/wallet/src/main/resources/static/

# build Spring Boot jar
WORKDIR /src/wallet
RUN chmod +x gradlew
RUN ./gradlew clean bootJar

FROM docker.io/library/eclipse-temurin:21-jre-alpine AS runner

ARG USER_NAME=wallet
ARG USER_UID=1000
ARG USER_GID=${USER_UID}

RUN addgroup -g ${USER_GID} ${USER_NAME} \
    && adduser -h /opt/wallet -D -u ${USER_UID} -G ${USER_NAME} ${USER_NAME}

USER ${USER_NAME}
WORKDIR /opt/wallet
COPY --from=builder --chown=${USER_UID}:${USER_GID} /src/wallet/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]
