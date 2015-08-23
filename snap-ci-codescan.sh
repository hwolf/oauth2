#/bin/sh

export SPRING_DATASOURCE_USERNAME=$SNAP_DB_MYSQL_USER
export SPRING_DATASOURCE_PASSWORD=$SNAP_DB_MYSQL_PASSWORD
export SPRING_DATASOURCE_URL=$SNAP_DB_MYSQL_JDBC_URL
export SPRING_DATASOURCE_DRIVERCLASSNAME=org.mariadb.jdbc.Driver

./gradlew --version
./gradlew --stacktrace test coverityCheck -x integrationTest -PrunCoverity=1
