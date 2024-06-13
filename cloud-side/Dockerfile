FROM golang:1.21-alpine3.17 as builder

RUN mkdir app
WORKDIR /app
COPY /src /app

RUN export CGO_ENABLED=1; export CC=gcc;
RUN go build

FROM alpine:3.17 as sentryflow
COPY --from=builder /app/cloud-side /
CMD ["/cloud-side"]