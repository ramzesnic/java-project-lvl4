clean:
	./gradlew clean

.PHONY: build
build: 
	./gradlew clean build

install: clean
	./gradlew install

generate-migrations:
	./gradlew generateMigrations

run-dist:
	./build/install/app/bin/app

start:
	APP_ENV=development	./gradlew run

check-updates:
	APP_ENV=production ./gradlew dependencyUpdates

lint:
	./gradlew checkstyleMain

test:
	./gradlew test
