This describes a simple workflow used to deploy this to an AWS EC2 instance using Elastic Beanstalk.
You can find a good tutorial on the general process [here](https://www.bezkoder.com/deploy-spring-boot-aws-eb/).

1. Create an AWS account and an access key. It's recommended to use temporary security credentials (see [here](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html)), but creating a root access key is fine for a quickstart.
2. Install the Elastic Beanstalk CLI using the command `pip3 install awsebcli --upgrade`.
3. Initialize the project using `eb init`. You will be asked to select the default region, as well as generate an SSH key.
4. Build the project using `./gradlew build`. This should create the `.jar` in `./build/libs/`.
5. Add the following to `.elasticbeanstalk/config.yml`:
    ```
   deploy:
     artifact: build/libs/cf-rank-0.0.1-SNAPSHOT.jar
    ```
6. Create the Elastic Beanstalk environment with `eb create --single --database`. You will be asked to choose a username and password for the database.
7. Add the following environment variables using `eb setenv`:
   ```
   eb setenv SERVER_PORT=5000
   eb setenv SPRING_DATASOURCE_URL=jdbc:mysql://{database-endpoint}/ebdb
   eb setenv SPRING_DATASOURCE_USERNAME={username}
   eb setenv SPRING_DATASOURCE_PASSWORD={password}
   eb setenv SPRING_JPA_HIBERNATE_DDL_AUTO=update
   eb setenv SPRING_PROFILES_ACTIVE=prod
   ```

   The environment variables can also be configured in the AWS console.   

   The database endpoint can be found in the console by going to your EB environment > configuration > edit networking and database > database endpoint. It should be of the form `xxx...rds.amazonaws.com:3306`.
8. Deploy the application with `eb deploy`.

To deploy updates, you only need to run `eb deploy` again.