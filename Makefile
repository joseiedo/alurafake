.PHONY: test up down clean


test:
	docker-compose up -d
	mvn test

clean:
	docker-compose down -v