# Используем JDK 17 с базой Alpine
FROM eclipse-temurin:17-jdk-alpine

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файлы проекта в контейнер
COPY . .

# Собираем и запускаем приложение
RUN ./mvnw clean package

CMD ["java", "-jar", "target/coffee_machine-0.0.1-SNAPSHOT.jar"]
