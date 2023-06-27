FROM quay.io/keycloak/keycloak:latest as builder

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure a database vendor
ENV KC_DB=mariadb
ENV KC_HOSTNAME_PATH=/kc-auth

WORKDIR /opt/keycloak
# for demonstration purposes only, please make sure to use proper certificates in production instead
RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -keystore conf/server.keystore
RUN /opt/keycloak/bin/kc.sh build

# change these values to point to a running postgres instance
ENV KC_DB=mariadb
ENV KC_DB_URL=jdbc:mariadb://keycloakdb:3306/keycloakdb
ENV KC_DB_USERNAME=keycloak
ENV KC_DB_PASSWORD=keycloakpwd
ENV KC_PROXY=edge
ENV KC_HOSTNAME_STRICT=false
ENV KC_HOSTNAME_STRICT_BACKCHANNEL=false
ENV KC_HOSTNAME_URL={{externalURL}}/kc-auth/
ENV KC_HOSTNAME_ADMIN_URL={{externalURL}}/kc-auth/
# Nicht geholfen: ENV KC_FEATURES=token-exchange
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--optimized"]
