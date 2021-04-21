set DRONE_BRANCH=local
set DRONE_COMMIT_SHA=12345678

echo "step: commit-stage"
call mvn clean install -f pom.xml -B -ff -e -V -U -Dchangelist=.%DRONE_BRANCH% -Dsha1=.%DRONE_COMMIT_SHA% -Dsonar.login=%SONARQUBE_TOKEN% -Dsonar.skip=false
