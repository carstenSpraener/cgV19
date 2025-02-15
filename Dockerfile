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

# Start by:
#
# docker run --rm -v $(pwd):/out casigoreng/cgv19:24.1.1 /opt/bin/cgv19 -m <model> -c <cartridge>
# If you have your own cartridge:
# add -v <YOUR_CARTRIDGE_DIR>:/opt/cgv19/cartridges to work with your cartridges

CMD ["/opt/bin/cgv19", "--help"]
