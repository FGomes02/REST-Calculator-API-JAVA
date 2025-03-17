# REST Calculator with Kafka

## Using Docker Compose
The project includes Dockerfiles for both modules and a docker-compose.yml file that defines the services for Kafka (and Zookeeper), the REST module, and the Calculator module.

### Build and Run All Services:
From the project root, run:
```sh
docker-compose up --build
```
This command will build the Docker images for the rest and calculator modules, and then start all the services defined in the docker-compose.yml file.

### Accessing the API:
Once the containers are running, you can test the API by sending an HTTP request to the REST service on port 8080. For example:
```sh
curl "http://localhost:8080/sum?a=1&b=2"
```
You should receive a JSON response similar to:
```sh
{
  "result": 3,
  "correlationId": "..."
}
```
### The code ZIP is also included in the repository
