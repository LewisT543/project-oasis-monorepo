FROM adoptopenjdk:11-jdk-hotspot

RUN \
  curl -sL "https://github.com/sbt/sbt/releases/download/v1.9.8/sbt-1.9.8.tgz" | tar zx -C /usr/share && \
  mv /usr/share/sbt/bin/sbt /usr/local/bin && \
  chmod +x /usr/local/bin/sbt

WORKDIR /oasis/calculationaggregationservice
COPY . /oasis/calculationaggregationservice

RUN pwd
RUN sbt compile stage

RUN ls ./target

CMD ["./target/universal/stage/bin/calculationaggregationservice", "-Dplay.Mode=dev"]