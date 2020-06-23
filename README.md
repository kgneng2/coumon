# Coumon
- kakao pay 과제 1
- Rest API 기반 쿠폰 시스템

## 개발 프레임 워크
- spring boot (web, jpa)
- h2 database 사용
- junit, rest assured test code 이용
- 언어 : kotlin

## 문제 해결 전략
### 필수 + 선택 + JWT
- Spring Boot + Kotlin + JPA 로 API server를 개발하였다.
- 외부 DB 장비 사용이 어려우니,, in memory DB 로 h2 database를 이용해보았다.
- test code는 junit, rest assured를 주로 사용했다.
- 인증관련해서 Customer Table 과, 쿠폰저장을 위해 Coupon Table을 설계헀다.
- Schema는 임의로 지정해보았으며, [coupon](https://github.com/kgneng2/coumon/blob/master/src/main/kotlin/com/kakao/pay/coumon/coupon/Coupon.kt), [customer](https://github.com/kgneng2/coumon/blob/master/src/main/kotlin/com/kakao/pay/coumon/customer/Customer.kt) 에서 확인할 수 있다.


### 랜덤한 코드의 쿠폰을 N개 생성하여 데이터베이스에 보관하는 API를 구현하세요.
- input 으로 N 개를 입력 받아서, 쿠폰을 N개 생성한다.
- DB에 넣으면 connection pool이 가득 차는 상황이 생길수 있어, 내부 queue 를 이용해서 insert 후에, ```@Scheduled``` 를 이용해서 1초마다 10000개 씩 db에 insert하였다. 따로 스케줄링을 확장해서 구현하면 더 좋을꺼같다. 예를 들어 queue 에 쌓인 갯수를 확인하며, 한번에 가져올수 있는 size조절 등등. 사실 내부 큐보다 외부 큐를 이용해서 api 단은 요청만 받고 뒤에서 db insert 처리하는게 좋아보인다.

### 생성된 쿠폰중 하나를 사용자에게 지급하는 API를 구현하세요.
- JWT token에서 customerId, loginId, password 값을 받아서 아직 할당받지 않은 쿠폰을 조회해서, customer 값을 업데이트 치고, coupon을 리턴한다. 

### 사용자에게 지급된 쿠폰을 조회하는 API를 구현하세요.
- customerId값을 이용해서, 조회한다. 쿠폰 만기일자가 많이 남은거 부터 로드 하였다.(최근에 받은거 부터 보여주기 위해서)
- ```select * from coupon where customerId =$customerId AND delFlag =false orderby expiredAt desc```

### 지급된 쿠폰중 하나를 사용하는 API를 구현하세요. 
- 위에 방법과 유사, 사용한 쿠폰의 경우에는 재사용이 불가능하므로 InvalidRequestException(400 ERROR)

### 지급된 쿠폰중 하나를 사용 취소하는 API를 구현하세요
- 위에 방법과 유사, 사용안한 쿠폰의 경우에는 InvalidRequestException(400 ERROR)

### 발급된 쿠폰중 당일 만료된 전체 쿠폰 목록을 조회하는 API
- N개 생성과 같이, JWT 값을 무시해서 진행한다. 해당 customer에게만 속한 만료된 쿠폰을 조회해야할지 고민이였으나,  전체 쿠폰 목록이므로 인증 무시하고 사용안하고 삭제 안한 쿠폰 대상으로 조회했다. 
- ``` select * from expiredAt = now() AND delFlag = false AND used = false ```

### 발급된 쿠폰중 만료 3일전 사용자에게 메세지(“쿠폰이 3일 후 만료됩니다.”)를 발송하는 기능을 구현하 세요. (실제 메세지를 발송하는것이 아닌 stdout 등으로 출력하시면 됩니다.)
- @Scheduled 를 이용했다. 
- 따로 batch 작업으로 뺴서 날리는게 좋아보인다. 쿠폰이 많아지고, 고객수도 증가하게 되면 groupBy query를 수행하고 알림을 보내야하는데 rest api application에서 동작보단 분리가 좋을꺼같다.


### API 인증을 위해 JWT(Json Web Token)를 이용해서 Token 기반 API 인증 기능을 개발하고 각 API 호출 시에 HTTP Header에 발급받은 토큰을 가지고 호출하세요
- User정보를 저장하기 위해 Customer Table을 생성하였다.
- 제약 사항(선택)에 있는 JWT token 인증을 이용해서 API를 설계 및 호출 테스트를 진행
- java-jwt를 사용해서 처리하였다.
- coupon 관련 API에게만 인증 적용을 진행해야했기 때문에 interceptor 나 filter 를 이용하려헀다.
  - 인증 과정에서 exception이 발생하여 exceptionHandler(ProblemExceptionHandler)에서 처리를 해야하는데, filter 의 경우에는 spring context 밖에 존재해서 handling 처리를 못한다.
  - interceptor는 spring의 dispatcher 서블릿이 controller를 호출할때 전, 후로 끼어들수 있어서 spring context 내부에 존재하므로 이용하였다.
  - interceptor에서는 jwt가 유효한지, JWTPayload에 있는 계정 정보가 저장 되어 있는지 확인한다.                                    
- JWT(JSON Web Tokens)를 발급받은 후 Header에 (Authorization : Bearer token)을 넣어 요청한다. interceptor 를 정상 통과한 후, JWTPayload 를 해석 후, id/password를 이용해서 계정확인후 Coupon API를 처리한다.
- ```CustomerController``` 는 JWT 선택 사항으로 인해 만든 signIn, signUp API 이다.
   - db에 저장할 때, password 는 sha256 Hex 단방향 암호화 하여 저장한다.(안전하게 저장하기위해..)
   - ~~signUp or signIn 후 token 정보를 header에 넣어 return하는게 좋아보이는데..?~~ 문제에서 발급 또는 출력 하여. response 에 넣었다.

#### 참고 이미지
![image](https://user-images.githubusercontent.com/7286378/85226188-3e3f4900-b411-11ea-9b7b-5842f2ad7bb4.png)

### 100억개 이상 쿠폰 관리 저장 관리 가능하도록 구현할것
- 전체적으로 날짜(만기일(expiredAt)에 indexing 필요-> 만기 알람에 필요하니..)로 파티셔닝을 진행한다.
- 데이터 양에 따라 일자별, 월별로 파티셔닝 하여 저장한다. 
- 데이터 베이스 샤딩하여 구축한다.
- hot/cold Database 를 나눠서 구축
- 이미 사용된 쿠폰이거나(확정 사용 => 추후엔 결제시 적용했다는 것도 추가해야함), 삭제한 쿠폰의 경우, 만기가 된 쿠폰 경우에는 cold database로 이동한다. 따로 조회할일이 없으니 history 관리 차원으로 이동한다. (HDFS가 적당해 보인다)
- hot의 경우에 사용하지 않은 쿠폰 대상으로 업로드하여 이용한다. (이용할 예정에 있는)



### 10만개 이상 벌크 csv Import 기능
- 아래 reference를 이용해서 구현하였다. 하지만 업로드 시 GC OOM이 발생하였다. 
- OOM 을 조절하기 위해서 JVM option 값 튜닝을 진행이 필요해보인다.
- 하지만 대량 insert 같은 경우에는 rest api 보다는, 다른 interface를 적용해보는게 좋을꺼같다. (예를 들어 RDB기준이라면 sql loader, spark job 등등..) 

#### reference
- https://cnpnote.tistory.com/entry/SPRING-%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B6%80%ED%8A%B8-Apache-Commons-FileUpload%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94-%EB%8C%80%EA%B7%9C%EB%AA%A8-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D-%ED%8C%8C%EC%9D%BC-%EC%97%85%EB%A1%9C%EB%93%9C
- https://github.com/bezkoder/spring-boot-upload-csv-files/blob/ff88f54b22/src/main/java/com/bezkoder/spring/files/csv/helper/CSVHelper.java


### 성능테스트 결과 / 피드백
- https://github.com/kgneng2/coumon/issues/1



------

## 빌드 및 실행 방법
1. ```git clone https://github.com/kgneng2/coumon.git ```
2. ```cd coumon```
3. ```sh bin/build.sh```//빌드
4. ```sh bin/start.sh```//시작
5. ```sh bin/test.sh``` //test code
6. ```sh bin/stop.sh``` //종료 
