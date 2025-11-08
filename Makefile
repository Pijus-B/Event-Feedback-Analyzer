.PHONY: up up-build up-build-java down down-volumes exec-java
M2_VOL ?= m2-cache

include .env

export DOCKER_BUILDKIT=1
export COMPOSE_BAKE=true

up:
	@docker compose up -d

up-build:
	@docker compose build
	@docker compose up -d --remove-orphans

up-build-java:
	@docker compose build analyzer-java
	@docker compose up -d --remove-orphans analyzer-java

down:
	@docker compose down

down-volumes:
	@docker compose down -v

exec-java:
	@docker compose exec $(SERVICE_PREFIX)-java sh

spotless-build:
	docker build \
	  -f docker/spotless/Dockerfile \
	  -t analyzer-formatter \
	  .

spotless-fix: spotless-build
	docker run --rm \
	  -v "$(PWD)":/Event-Feedback-Analyzer \
	  -v $(M2_VOL):/root/.m2 \
	  analyzer-formatter \
	  mvn spotless:apply

spotless-check: spotless-build
	docker run --rm \
	  -v "$(PWD)":/Event-Feedback-Analyzer \
	  -v $(M2_VOL):/root/.m2 \
	  analyzer-formatter \
	  mvn spotless:check
