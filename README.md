**How to start application?**
Some notes: tested with Terraform v1.4.6, Helm v3.7.0, kubectl v1.23.0, Docker Desktop 4.12.0 (85629)

* Locally via Docker Compose
   1. Build `./gradlew build`
   2. Run `docker-compose up` 
* Locally via Helm (Local K8S and Helm should be installed)
   1. Build `./gradlew build`
   2. Build Docker image for the application `docker build -f .\deploy\docker\Dockerfile -t url-shortener:latest .`
   3. Install Localstack release via HELM <p/>
      `cd ./deploy/charts/localstack`
      `helm repo add localstack-charts https://localstack.github.io/helm-charts` <p/>
      `helm upgrade -f values.local.yaml localstack-release localstack-charts/localstack`
   4. Deploy the application locally via HELM <p/>
      `cd ./deploy/charts/url-shortener`
      `helm install -f values.yaml -f values.local.yaml url-shortener .`
* Deploy to AWS 
   1. Run Terraform file `./deploy/terrafrom` to create VPC, EKS, DynamoDB, etc. <p/>
      Please override credentials in `main.tf` file (user must have permissions to create resources)
      `terraform init` from GitBash to clone all dependencies <p/>
      `terrafrom plan` <p/>
      `terrafrom apply` <p/>
      The creation of infrastructure can take > 20 minutes.
  2. Build Docker image for the application `docker build -f .\deploy\docker\Dockerfile -t url-shortener:latest .`
  3. Push Docker image to some repository, e.g. AWS ECR can be used.
  4. Update `./deploy/charts/url-shortener/values.aws.yaml` with correct image repository and user credentials(access to DynamoDB is required)
  5. Deploy the application via HELM <p/> 
        `cd ./deploy/charts/url-shortener`
        `helm install -f values.yaml -f values.aws.yaml url-shortener .`
