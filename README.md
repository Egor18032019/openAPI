"# YandexOpenApi"
 Запустить докер
sudo docker run --name mysql -d  -p 3306:3306  -e MYSQL_ROOT_PASSWORD=change-me --restart always mysql:latest
mvn exec:java -Dexec.mainClass="egor.enrollment.EnrollmentApp"

или
sudo docker-compose build
sudo docker-compose up

Сделал всё что успел 


 