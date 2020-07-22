FROM alpine:latest

COPY ./scripts/daily/* /etc/periodic/daily
COPY ./scripts/weekly/* /etc/periodic/weekly
COPY ./scripts/monthly/* /etc/periodic/monthly
COPY ./scripts/script/* /opt/bin/

RUN apk update && \
    apk upgrade && \
    apk add --no-cache mariadb-client && \
    chmod a+x /etc/periodic/daily/* && \
    chmod a+x /etc/periodic/weekly/* && \
    chmod a+x /etc/periodic/monthly/* && \
    chmod a+x /opt/bin/*