FROM eclipse-temurin:24-jre
RUN useradd -r -s /usr/sbin/nologin -u 10001 appuser
ARG APP_JAR="id-1.0.0.jar"
ENV APP_JAR=${APP_JAR}

WORKDIR /app
COPY target/${APP_JAR} /app/
COPY target/lib /app/lib
# 任意の起動設定
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=""


# デフォルトのHTTPポート
EXPOSE 8080

# 所有者変更して非rootで実行
RUN chown -R appuser:appuser /app
USER appuser

# POMで Manifest（Class-Path=lib/..., Main-Class=...）を付けている想定
ENTRYPOINT ["/bin/sh", "-c", "exec java $JAVA_OPTS -jar \"/app/${APP_JAR}\""]

# ※もし Manifest を付けていない場合は、代わりに下記を使う：
# ENTRYPOINT ["/bin/sh", "-c", "exec java $JAVA_OPTS -cp \"/app/${APP_JAR}:/app/lib/*\" com.kamioda.IdApplication"]
