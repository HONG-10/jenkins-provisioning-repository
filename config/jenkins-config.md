# Jenkins GUI Configure

## 1. jenkins 접속 후 admin 계정의 비밀번호 변경 
```sh
# jenkins 초기 비밀번호 확인
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

## 2. User Defined Time Zone 값 'Asia/Seoul' 로 변경

## 3. 'Jenkins 관리' => 'Global Tool Configuration'
### - 'JDK' : openjdk-8, openjdk-11 등록

## 4. 'Jenkins 관리' => '환경 설정'
### - 'Global properties' 세팅
###   => GRADLE_USER_HOME : /jenkins-dependencies/.gradle
### - Usage Statistics : 'Help make Jenkins better by sending anonymous usage statistics and crash reports to the Jenkins project.' 체크 해제
