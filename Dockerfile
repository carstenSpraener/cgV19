FROM gradle:8.2.1-jdk17 as build-environment

WORKDIR /cgv19

COPY core /cgv19/
COPY cartridges /cgv19/

RUN gradle dependencies

FROM build-environment as install

WORKDIR /cgv19
RUN gradle :cgv19-cli:installDist

FROM install
WORKDIR /out

COPY --from=install /cgv19/cgv19-cli/build/install/cgv19-cli/ /opt

VOLUME ["/opt/cartridges"]
VOLUME ["/out"]

CMD ["/opt/bin/cgv19", "-l"]
