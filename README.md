# Spring Boot 2 demo of Resilience4j

[![Build Status](https://travis-ci.org/resilience4j/resilience4j-spring-boot2-demo.svg?branch=master)](https://travis-ci.org/resilience4j/resilience4j-spring-boot2-demo)

This demo shows how to use the fault tolerance library [Resilience4j](https://github.com/resilience4j/resilience4j) in a Spring Boot 2 application.

See [User Guide](https://resilience4j.readme.io/docs/getting-started-3) for more details.

## Requirements
[Docker](https://docs.docker.com/install/) and [Docker Compose](https://docs.docker.com/compose/install/) installed.

## Getting Started

1. Use docker-compose to start grafana and prometheus servers.
- In the root folder:
```sh
docker-compose -f docker-compose.yml up
```
2. Start the demo project through the main class.

3. Check the prometheus:
- Open http://localhost:9090
- Access status -> Targets, both endpoints must be "UP"

4. Configure the grafana:
- Open http://localhost:3000
- Configure integration with Prometheus
    - Access configuration
    - Add data source
    - Select prometheus
    - Use url "http://localhost:9090" and access with value "Browser"

- Configure dashboard
    - Access "home"
    - Import dashboard
    - Upload dashboard.json from /docker

## License

Copyright 2019 Robert Winkler

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
