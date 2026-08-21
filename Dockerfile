FROM eclipse-temurin:17 AS build
WORKDIR /repo
COPY . .
RUN cd core && ./gradlew :cgv19-cli:installDist --no-daemon

FROM eclipse-temurin:17-jre-jammy
COPY --from=build /repo/core/cgv19-cli/build/install/cgv19-cli /opt/cgv19
WORKDIR /workdir
ENTRYPOINT ["/opt/cgv19/bin/cgv19"]
