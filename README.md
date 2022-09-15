"# YandexOpenApi"
 Запустить докер
sudo docker run --name mysql -d  -p 3306:3306  -e MYSQL_ROOT_PASSWORD=change-me --restart always mysql:latest
mvn exec:java -Dexec.mainClass="egor.enrollment.EnrollmentApp"
Дальше как обычно



git remote add test git@github.com:Egor18032019/openAPI.git
git remote add work https://github.com/Egor18032019/openAPI.git