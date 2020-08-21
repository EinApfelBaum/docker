FROM alpine:latest

COPY ./scripts/15min/* /etc/periodic/15min
COPY ./scripts/hourly/* /etc/periodic/hourly
COPY ./scripts/daily/* /etc/periodic/daily
COPY ./scripts/weekly/* /etc/periodic/weekly
COPY ./scripts/monthly/* /etc/periodic/monthly
COPY ./scripts/script/* /opt/bin/

RUN apk update && \
    apk upgrade && \
    apk add --no-cache mariadb-client && \
    chmod a+x /etc/periodic/15min/* && \
    chmod a+x /etc/periodic/hourly/* && \
    chmod a+x /etc/periodic/daily/* && \
    chmod a+x /etc/periodic/weekly/* && \
    chmod a+x /etc/periodic/monthly/* && \
    chmod a+x /opt/bin/*