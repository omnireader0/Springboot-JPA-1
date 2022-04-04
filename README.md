# [인프런] 실전! 스프링 부트와 JPA 활용 1

## 1. 개발 목표

목표 : 실무에서 웹 애플리케이션을 제대로 개발해보기

실무에 가까운 복잡한 예제

### 시리즈 1 : 웹 애플리케이션 개발

- 프로젝트 환경 설정
  - springboot, jpa, gradle, hibernate, thymeleaf
- 요구 사항 분석
- 도메인과 테이블 설계
- 아키텍처 구성
- 핵심 비즈니스 로직 개발(회원 상품 주문)
- 테스트
- 웹 계층 개발

### 시리즈 2 : API 개발과 성능 최적화

- REST API 개발
- 성능 최적화
  - JPA 극한의 조회 성능 최적화 노하우 전수
  - 복잡한 예제를 6단계로 성능 튜닝
  - 실무 JPA 성능 문제의 90% 해결

## 2. 프로젝트 환경 설정

### 목차

- 프로젝트 생성
- 라이브러리 살펴보기
- View 환경설정 -> 렌더링 위함
- H2 데이터베이스 설치
- JPA와 DB 설정, 동작 확인

### 2.1 프로젝트 생성

- 스프링 부트 스타터 (start.spring.io)
  - gradle
  - java11
  - springboot 2.5.4
  - groupId: jpabook
  - artifactId: jpashop

- 사용 기능: web, thymeleaf, jpa, h2, lombok
  - `Spring Web Starter`: web 애플리케이션 개발에 필요
  - `thymeleaf`: 모던 서버사이드 자바 템플릿 엔진
  - `Spring Data JPA`: 자바 Persistence API using Spring Data and Hibernate
  - `h2`: 내장해 쉽게 실행할 수 있는 Database
  - `lombok`: 간단한 어노테이션으로 반복되는 코드를 줄여주는 기능

프로젝트 다운로드 후 IntelliJ로 Import or Open

- 프로젝트를 실행해보면 tomcat이 8080 port에서 실행이 되었다는 로그가 뜸
- IntelliJ > Preference > Plugins > Lombok
- 롬북을 설치하면 아래 과정을 꼭 해주기 -> 어노테이션 enable
  - IntelliJ > Preference > Build, Execution, Deployment > Compiler > Annotation Processors
  - Enable annotation processing 선택

### 2.2 라이브러리 살펴보기

#### 의존관계 보는법

1) 프로젝트 쪽에서 터미널 열고 입력

```java
$ ./gradlew dependencies
```

2) IntelliJ의 우측 Gradle 탭에서도 dependency 확인

```java
spring-boot-starter-web 아래엔 embedded된 tomcat과 webmvc가 있음.
```

#### 톰캣

Java를 이용한 웹 애플리케이션의 다양한 규격(Spec)을 준수하여 JSP, HTML 파일들로 구성된 .war 파일(애플리케이션의 파일들을 압축해놓은 하나의 파일)을 배포해주는 엔진

스프링부트에 내장되어 있고, 자바를 이용한 웹 파일들을 배포해주는 엔진?

- `spring-boot-starter-data-jpa` 아래엔 `core`, `aop`, `jdbc`, `HikariCP`(커넥션 풀), `hibernate`, `logging`(`logback`, `slf4j`- logger를 찍는 인터페이스들의 모음) 등이 있음.
- Test에서 `spring-boot-starter-test`에는 `junit`과 `mockito`, `assertj`등이 있음.

#### 핵심 라이브러리

- Spring MVC
- Spring ORM
- JPA, 하이버네이트
- 스프링 데이터 JPA

#### 기타 라이브러리

- H2 데이터베이스 클라이언트 -> 다운받은 h2버전이랑 클라 h2버전 맞춰주는게 좋음
- 커넥션 풀 (boot 기본은 HikariCP)
- WEB (thymeleaf)
- 로깅 SLF4J & LobNack
- 테스트

### 2.3 View 환경설정

- thymeleaf 공식 사이트: https://www.thymeleaf.org/
- 스프링 공식 튜토리얼: https://spring.io/guides/gs/serving-web-content/
- 스프링부트 메뉴얼: https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-template-engines
- 꿀팁 : https://spring.io/guides - 필요한 거 검색하면 다 나온다! 기초 따라가고 싶을 때 찾는 가이드

#### thymeleaf

- 내츄럴 템플릿 : html 마크업을 지키면서 사용 가능
- 2.x 버전의 문제점이 있었으나 3.x버전이 되면서 성능, 효율이 좋아짐

#### 스프링 부트 thymeleaf가 viewName 매핑

- resources : templates/ + {ViewName} + .html

```java
@Controller
public class HelloController {
    // hello url 오면 이 컨트롤러 호출
    @GetMapping("hello")
    public String hello(Model model){
        model.addAttribute("data", "hello!");
        return "hello";
    }
}
```

- Controller에서 `Model`에 data를 싣어 View에 넘김 → `return "ViewName"` (.html이 생략됨)

#### thymeleaf 템플릿엔진 동작 확인(hello.html) - 서버사이드 렌더링

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<p th:text="'안녕하세요. ' + ${data}" >안녕하세요. 손님</p>
</body>
</html>
```

- 순수 html 렌더링 시 "안녕하세요. 손님"이 출력, 서버 사이드 렌더링 시 안녕하세요. `data`가 출력.
- 애플리케이션 실행 -> http://localhost:8080/hello 입력 -> `안녕하세요. hello!` 데이터 확인

#### 정적 페이지 index.html 하나 만들기 , `static/index.html`

```java
<!DOCTYPE HTML>
  <html xmlns:th="http://www.thymeleaf.org">
       
     <head>
        <title>Hello</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    </head>
    <body>
    Hello
    <a href="/hello">hello</a>
    </body>
</html>
```

#### 참고

build.gradle 에 들어가서 다음 라이브러리를 추가한다.
spring-boot-devtools 라이브러리를 추가하면, html 파일을 컴파일만 해주면 서버 재시작 없이 View 파일 변경이 가능하다.

```java
implementation 'org.springframework.boot:spring-boot-devtools'
```

html 파일 컴파일 방법: 메뉴 build → Recompile

### 2.4 H2 데이터베이스 설치

#### 사전 설치 완료

본인은 h2가 여기에 설치되어 있음
C:\Program Files (x86)\H2\bin
h2 windows 배치 파일을 실행하면 자동으로 웹에 올라온다.
앞에 ip만 localhost로 바꿔주면 localhost:8082/login.jsp?jessionid~~~

#### H2 데이터베이스

개발이나 테스트 용도로 가볍고 편리한 DB, 웹 화면 제공

- 데이터베이스 파일 생성 방법
  - `jdbc:h2:~/jpashop` (최소 한번, 세션키 유지한 상태로 실행)
  - `~/jpashop.mv.db` 파일 생성 확인
  - 이후 부터는 `jdbc:h2:tcp://localhost/~/jpashop` 이렇게 접속

![img](https://user-images.githubusercontent.com/38436013/133990280-14193fc5-a5ce-4432-9a1b-703c0429ca8e.png)

### 2.5 JPA와 DB 설정, 동작 확인

- 스프링 공식 문서에서 찾아보기

https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/html/

#### main/resources/application.yml

```java
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/jpashop;MVCC=TRUE
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
#        show_sql: true
        format_sql: true

logging:
  level:
    org.hibernate.SQL: debug
```

- `ddl-auto: create` 옵션은 애플리케이션 실행 시점에 테이블을 drop 하고, 다시 생성한다.
- `show_sql` 옵션은 System.out 에 하이버네이트 실행 SQL을 남긴다. -> 안쓴다, 로그를 통해서만 하기
- `org.hibernate.SQL` 옵션은 logger를 통해 하이버네이ㅌ트 실행 SQL을 남긴다.

주의) application.yml 같은 yml 파일은 띄어쓰기(스페이스) 2칸으로 계층을 만든다.

#### 동작 하는지 확인하기

- 회원 엔티티

```java
@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;

}
```

- 회원 레포지토리

~~~java
@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save (Member member){
        em.persist(member);
        return member.getId();
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }
}
~~~

- 테스트

```java
@RunWith(SpringRunner.class) // junit에게 스프링관련 테스트할것을 알림
@SpringBootTest
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void save() {
        // given
        Member member = new Member();
        member.setUsername("member1");

        // when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedId);

        // then
        Assertions.assertThat(savedId).isEqualTo(findMember.getId());
        Assertions.assertThat(member.getUsername()).isEqualTo(findMember.getUsername());

    }
}
```

**테스트 실행 시 에러 발생**

> No EntityManager with actual transaction available for current thread

→ EntityManager를 통한 모든 데이터 변경은 항상 transaction 안에서 이루어져야 한다.
→ (Repository 또는 메서드에 *@Transactional*을 걸 수 있지만) 일단 Test에 추가해준다.

```java
	@Test
    @Transactional
    public void save() {
```

**테스트 성공**

H2 데이터베이스에 Member 테이블이 생성된 것을 확인할 수 있다.
`@Transactional`어노테이션이 테스트에 있으면 **테스트 후 Rollback**을 하므로, 저장된 데이터는 없다.
`@Rollback(false)` 어노테이션 추가 시 Rollback없이 커밋해 테스트 내용이 DB에 저장된다.

```java
    @Test
    @Transactional
    @Rollback(false)
    public void save() {
```

**저장한 것과 조회한 것이 같을까?**

`Assertions.assertThat(findMember).isEqualTo(member);` 결과는 두 객체가 동일
→ 같은 트랜잭션 안에서 데이터를 조회하고 저장하면 **영속성 컨텍스트가 동일**하다. 같은 영속성 컨텍스트 안에서는 `id`(식별자)값이 같으면 같은 엔티티로 식별한다. 1차 캐시에 저장된다.

#### 쿼리 파라미터 로그 남기기

- `org.hibernate.type: trace`

쿼리 상에는 "?"로 남지만 SQL 실행 파라미터를 로그로 남긴다.

> insert
> into
> member
> (username, id)
> values
> (?, ?)
>
> : binding parameter [1] as [VARCHAR] - [member1]

- https://github.com/gavlyukovskiy/spring-boot-data-source-decorator

```java
implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.6'
```

참고) 쿼리 파라미터를 로그로 남기는 외부 라이브러리는 시스템 자원을 사용하므로, 개발 단계에서는 편하 게 사용해도 된다. 하지만 운영시스템에 적용하려면 꼭 성능테스트를 하고 사용하는 것이 좋다.

- 결과

> insert into member (username, id) values (?, ?)
> insert into member (username, id) values ('member1', 1);
> 2021-09-20 22:04:32.890 INFO 1464 --- [ Test worker] p6spy : #1632143072890 | took 1ms | commit | connection 3| url jdbc:h2:tcp://localhost/~/jpashop

insert into member (username, id) values ('member1', 1); 식으로 쿼리 데이터 다 보인다.

## 3. 도메인 분석 설계

### 목표

- 요구사항 분석
- 도메인 모델과 테이블 설계
- 엔티티 클래스 개발
- 엔티티 설계시 주의점

### 3.1 요구 사항 분석

![img](https://user-images.githubusercontent.com/38436013/134281986-3656011f-7f99-4f45-a5a6-89c3196a8b64.png)

#### 기능 목록

- 회원 기능
  - 회원 등록
  - 회원 조회
- 상품 기능
  - 상품 등록
  - 상품 수정
  - 상품 조회
- 주문 기능
  - 상품 주문
  - 주문 내역 조회
  - 주문 취소
- 기타 요구사항
  - 상품은 재고 관리가 필요하다.
  - 상품의 종류는 도서, 음반, 영화가 있다.
  - 상품을 카테고리로 구분할 수 있다.
  - 상품 주문시 배송 정보를 입력할 수 있다.

### 3.2 도메인 모델과 테이블 설계

#### 도메인 모델

![img](https://user-images.githubusercontent.com/38436013/134282454-59edd059-aa20-4723-bf4c-c3c9fa9dacb8.png)

- 회원은 여러 상품 주문할 수 있다.
- 한 주문에는 여러 상품이, 한 상품은 여러 주문에 담길 수 있다. (N:N)
  → 주문상품이라는 엔티티를 추가해 N:N → 1:N & N:1로 풀어냈다.
- 주문 시에 배송 정보를 입력할 수 있다. (1:1)
- 상품은 도서, 음반, 영화 타입으로 구분되어 상속 구조로 표현했다.
- 한 카테고리에는 여러 상품이, 한 상품은 여러 카테고리에 들어갈 수 있다. (N:N)

#### 회원 엔티티 분석

![img](https://user-images.githubusercontent.com/38436013/134282473-60c58912-a284-4774-a14a-1aeaf923c110.png)

공통 속성인 PK는 generated되는 Long 타입으로 설계했다.

**회원 (Member)**

이름과 임베디드 타입인 주소(`Address`), 그리고 주문(`orders`) 리스트를 가진다.

**주문 (Orders)**

한 번 주문시 여러 상품을 주문할 수 있으므로 주문과 주문 상품( `OrderItem` )은 일대다 관계다.

주문은 상품을 주문한 회원과 배송 정보, 주문 날짜, 주문 상태(`status`)를 가진다.

주문 상태는 열거형을 사용했는데 주문(`order`), 취소(`cancel`)를 표현할 수 있다.

**주문상품(OrderItem)**

주문한 상품 정보와 주문 금액( `orderPrice` ), 주문 수량( `count` ) 정보를 가지고 있다. (보통 `OrderLine` , `LineItem` 으로 많이 표현한다.) 주문 금액은 주문한 시점과 가격 변동이 생길 수 있으므로 가지고 있어야 한다.

**상품(Item)**

이름, 가격, 재고수량( `stockQuantity` )을 가지고 있다. 상품을 주문하면 재고수량이 줄어든다. 상품의 종류로는 도서, 음반, 영화가 있는데 각각은 사용하는 속성이 조금씩 다르다.

**배송(Delivery)**

주문시 하나의 배송 정보를 생성한다. 주문과 배송은 일대일 관계다.

**카테고리(Category)**

상품과 다대다 관계를 맺는다. parent , child 로 부모, 자식 카테고리를 연결한다. (실무에서는 다:다는 사용하지 않는다. 1:다 다:1로 풀어야 한다.)

**주소(Address)**

값 타입(임베디드 타입)이다. 회원과 배송( `Delivery` )에서 사용한다.

실무에서 운영시에 주의할 점

- JPA의 다대다(@ManyToMany) 관계는 운영시에 사용하면 안 된다.
  → 1:다 & 다:1로 풀어내야 한다. (예시 : 카테고리-아이템)

- 가급적이면 양방향 연관관계보단 단방향 연관관계를 사용한다.
  (현재는 Member가 Order를 1:다로, Order도 Member를 가지고 있는 양방향 연관관계)
  → 실무에서는 회원이 주문을 참조하지 않고, 주문이 회원을 참조하는 것으로 충분하다. (객체지향적 사고가 필요하다.)

  \- 회원을 통해 주문이 일어난다 (x)
  \- 주문을 생성할 때 회원이 필요하다 (o)
  \- 주문 내역이 필요하면 Member를 찾아 List로 Order를 찾아오는 것이 아니라 Order의 필터링 조건에 Member가 들어가는 것.
  (사실 Member의 Order Collection은 필요 없다. 여기서는 일대다, 다대일의 양방향 연관관계를 써보기 위해서 추가했다.)

#### 회원 테이블 분석

![img](https://user-images.githubusercontent.com/38436013/134284002-7b580a6e-398b-4a41-80e8-8ea0e284851d.png)

**MEMBER**

회원 엔티티의 `Address` 임베디드 타입 정보가 회원 테이블에 그대로 들어갔다. 이것은 `DELIVERY` 테이블도 마찬가지다.

**ITEM**

앨범, 도서, 영화 타입을 통합해서 하나의 테이블로 만들었다. `DTYPE` 컬럼으로 타입을 구분한다. *(Single Table 전략)*

참고) 테이블명이 ORDER 가 아니라 `ORDERS` 인 것은 데이터베이스가 `order by` 때문에 예약어로 잡고 있는 경우가 많다. 그래서 관례상 `ORDERS` 를 많이 사용한다.

**CATEGORY**

객체에서는 `Category` 가 `Item` 을 List로, `Item` 이 `Category` 를 List로 가지며 다:다 관계를 만들 수 있다. 하지만 관계형 데이터 베이스에서는 중간에 `CATEGORY_ITEM` 이라는 매핑 테이블을 두어 N:N 관계를 N:1 관계와 1:N 관계로 풀어내야 한다.

**참고**

> 실제 코드에서는 DB에 소문자 + *(언더스코어) 스타일을 사용하겠다. > 데이터베이스 테이블명, 컬럼명에 대한 관례는 회사마다 다르다. 보통은 대문자 +* (언더스코어)나 소문자 + *(언더스코어) 방식 중에 하나를 지정해서 일관성 있게 사용한다. 강의에서 설명할 때는 객체와 차이를 나 타내기 위해 데이터베이스 테이블, 컬럼명은 대문자를 사용했지만, 실제 코드에서는 소문자 +* (언더스코 어) 스타일을 사용하겠다.

#### 연관관계 매핑 분석

**회원과 주문**

일대다, 다대일의 양방향 관계다. 따라서 연관관계의 주인을 정해야 하는데, 외래 키가 있는 주문을 연관관계의 주인으로 정하는 것이 좋다. 그러므로 `Order.member` 를 `ORDERS.MEMBER_ID` 외래 키와 매핑한다.
`Order.member`: 주인. 값 세팅 가능, `Member.orders`: 조회만 가능

**주문상품과 주문**

다대일 양방향 관계다. 외래 키가 주문상품에 있으므로 주문상품이 연관관계의 주인이다. 그러므로 `OrderItem.order` 를 `ORDER_ITEM.ORDER_ID` 외래 키와 매핑한다.

**주문상품과 상품**

다대일 단방향 관계다. `OrderItem.item` 을 `ORDER_ITEM.ITEM_ID` 외래 키와 매핑한다.

**주문과 배송**

일대일 양방향 관계다. `Order.delivery` 를 `ORDERS.DELIVERY_ID` 외래 키와 매핑한다.

**카테고리와 상품**

`@ManyToMany` 를 사용해서 매핑한다.(실무에서 `@ManyToMany`는 사용하지 말자. 여기서는 다대다 관계를 예제로 사용하기 위해 추가했을 뿐이다)

**참고) 외래 키가 있는 곳을 연관관계의 주인으로 정해라.**

> 연관관계의 주인은 단순히 외래 키를 누가 관리하냐의 문제이지 비즈니스상 우위에 있다고 주인으로 정하면 안된다. 예를 들어서 자동차와 바퀴가 있으면, 일대다 관계에서 항상 다쪽에 외래 키가 있으므로 외래 키가 있는 바퀴를 연관관계의 주인으로 정하면 된다. 물론 자동차를 연관관계의 주인으로 정하는 것이 불가능 한 것은 아니지만, 자동차를 연관관계의 주인으로 정하면 자동차가 관리하지 않는 바퀴 테이블의 외래 키 값이 업데이트 되므로 관리와 유지보수가 어렵고, 추가적으로 별도의 업데이트 쿼리가 발생하는 성능 문제도 있다.

### 3.3 엔티티 클래스 개발

> 키 : 모 클래스로 바로 들어가고 싶을 때.. 클래스이름 누르고 CTRL + B

- 예제에서는 설명을 쉽게하기 위해 엔티티 클래스에 Getter, Setter를 모두 열고, 최대한 단순하게 설계
- 실무에서는 가급적 Getter는 열어두고, Setter는 꼭 필요한 경우에만 사용

**참고 1) 이론적으로 Getter, Setter 모두 제공하지 않고, 꼭 필요한 별도의 메서드를 제공하는게 가장 이상적이다.**
하지만 실무에서 엔티티의 데이터는 조회할 일이 너무 많으므로, Getter의 경우 모두 열어두는 것이 편리하다. Getter는 아무리 호출해도 호출 하는 것 만으로 어떤 일이 발생하지는 않는다. 하지만 Setter는 문제가 다르다. Setter를 호출하면 데이터가 변한다. Setter를 막 열어두면 가까운 미래에 엔티티에가 도대체 왜 변경되는지 추적하기 점점 힘들어진다. 그래서 *엔티티를 변경할 때는 Setter 대신에 변경 지점이 명확하도록 변경을 위한 비즈니스 메서드를 별도로 제공*해야 한다.

**참고 2) 엔티티의 식별자는 `id` 를 사용하고 PK 컬럼명은 `member_id` 를 사용했다.**
엔티티는 타입(여기서는 `Member` )이 있으므로 `id` 필드만으로 쉽게 구분할 수 있다. 테이블은 타입이 없으므로 구분이 어렵다. 그리 고 테이블은 관례상 `테이블명 + id` 를 많이 사용한다. 참고로 객체에서 `id` 대신에 `memberId` 를 사용해도 된다. 중요한 것은 일관성이다.

**참고 3) 실무에서는 `@ManyToMany` 를 사용하지 말자**
`@ManyToMany` 는 편리한 것 같지만, 중간 테이블( `CATEGORY_ITEM` )에 컬럼을 추가할 수 없고, 세밀하게 쿼리를 실행하기 어렵기 때문에 실무에서 사용하기에는 한계가 있다. 중간 엔티티( `CategoryItem` 를 만들고
`@ManyToOne` , `@OneToMany` 로 매핑해서 사용하자. 정리하면 대다대 매핑을 일대다, 다대일 매핑으로 풀어내서 사용하자.

**참고 4) '값 타입'은 변경 불가능하게 설계해야 한다.**
`@Setter` 를 제거하고, 생성자에서 값을 모두 초기화해서 변경 불가능한 클래스를 만들자. JPA 스펙상 엔티티나 임베디드 타입( `@Embeddable` )은 자바 기본 생성자(default constructor)를 *public* 또는 *protected* 로 설정해야 한다. public 으로 두는 것 보다는 protected 로 설정하는 것이 그나마 더 안전하다.
JPA가 이런 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수 있도록 지원해야 하기 때문이다.

#### 회원 엔티티

```java
@Entity
@Getter @Setter
public class Member {

    /* id는 엔티티 식별자, member_id는 pk 컬럼명이다.
    member_id는 테이블명+id 이런 의미. 테이블 타입이 없어서 구분해주기 위함
   * */
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") // Order엔티티의 member에 매핑됨
    private List<Order> orders = new ArrayList<>();
}
```

#### 주문 엔티티

```java
@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;


    /* 회원 변경이 일어나는 경우, Member 클래스에서는 orders 리스트를, Order에서는 member를
    변경할 수 있다. 즉 객체 변경 일어나는 포인트가 두 곳인데, JPA에서는 한개만 변경하면 둘 다
    수정된다. 그렇다면 어디를 변경할까? 연관관계 주인인 Orders 엔티티의 외래키인 MEMBER_ID를 변경한다.
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 주문 회원

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // 주문 상품 엔티티의 order에 매핑됨
    private List<OrderItem> orderItems = new ArrayList<>();

    // 1:1 관계는 fk키를 둘 다 둘 수 있지만, 접근이 많은 곳을 연관관계 주인으로 하기
    @OneToOne(cascade = CascadeType.ALL, fetch =FetchType.LAZY )
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; // 배송 정보

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
```

#### 주문 상태 

```java
public enum OrderStatus {
    ORDER, CANCEL
}
```

#### 주문 상태 엔티티

~~~java
package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="order_id")
    private Order order;

    private int orderPrice; //주문 가격

    private int count;// 주문 수량

}
~~~

#### 상품 엔티티

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //상속 매핑 : 한테이블에서 처리
@DiscriminatorColumn(name = "dtype") // 싱글테이블에서 영화, 앨범 등을 구분하기 위해
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}
```

#### 상품 - 도서 엔티티

```java
@Entity
@DiscriminatorValue("B")
@Getter @Setter
public class Book extends Item {
    private String author;
    private String isbn;
}
```

#### 상품 - 음반 엔티티

```java
@Entity
@DiscriminatorValue("A")
@Getter
@Setter
public class Album extends Item {
    private String artist;
    private String etc;
}
```

#### 상품 - 영화 엔티티

```java
@Entity
@DiscriminatorValue("M")
@Getter @Setter
public class Movie extends Item{
    private String director;
    private String actor;
}
```

#### 배송 엔티티

```java
@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch= FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // //ENUM [READY(준비), COMP(배송)]
}
```

#### 배송 상태 

```java
public enum DeliveryStatus {
    READY, COMP
}
```

#### 카테고리 엔티티

```java
@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"), // 중간테이블의 카테코리 id
            inverseJoinColumns = @JoinColumn(name = "item_id")) // 중간테이블 -> 아이템으로 가는 id
    private List<Item> items = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 메서드==//
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
```

#### 주소 값 타입

```java
@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() { // jpa 스펙 상 만들어둠

    }
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
```

#### 테이블 만들고 h2 서버 열고, 애플리케이션 실행하면, h2 db에 테이블 생성된 것을 확인할 수 있다.

### 3.4 엔티티 설계시 주의점

#### 엔티티에는 가급적 Setter를 사용하지 말자

`Setter`가 모두 열려있다면 변경 포인트가 너무 많아서, 유지보수가 어렵다. → 나중에 리펙토링으로 `Setter` 제거 후 비즈니스 메서드로 개발.

#### 모든 연관관계는 지연로딩( `LAZY` )으로 설정!

- 즉시로딩( `EAGER` )은 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어렵다. 특히 JPQL을 실행할 때 N+1 문제가 발생한다.
- 실무에서 모든 연관관계는 **지연로딩**( `LAZY` )으로 설정해야 한다.
- 연관된 엔티티를 함께 DB에서 조회해야 하면, fetch join 또는 엔티티 그래프 기능을 사용한다.
- @XToOne(OneToOne, ManyToOne) 관계는 *기본이 즉시로딩이므로 직접 지연로딩으로 설정해야* 한다.
- @OneToMany 는 기본이 지연 로딩이다. lazy..

> 즉시 로딩이란 객체 A를 조회할 때 A와 연관된 객체들을 한 번에 가져오는 것이다.
> 지연 로딩이란 객체 A를 조회할 때는 A만 가져오고 연관된 애들은 저번 게시글에서 본 프락시 초기화 방법으로 가져온다.

#### `Cascade = ALL` 옵션을 활용 하자

```java
 @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // 주문 상품 엔티티의 order에 매핑
    private List<OrderItem> orderItems = new ArrayList<>();
```

1. 완전 개인 소유인 경우 사용할 수 있다.
2. DDD의 Aggregate Root와 어울린다.
3. 애매하면 사용하지 않는다.

모든 엔티티는 값을 저장할 때 기본적으로 `persist` 를 각각 해줘야 하는데, `CascadeType` 을 `ALL` 로 설정했다면 연관된 엔티티까지 함께 저장된다.

통상적으로 권장하는 cascade 범위는, 완전히 개인 소유하는 엔티티일 때, 예를 들어서 게시판과 첨부파일이 있을 때 첨부파일은 게시판 엔티티만 참조하므로, 개인 소유다. 이런 경우에는 사용해도 된다. 그럼 반대로 개인 소유하지 않는 엔티티는? 예를 들어서, 회원, 상품 등등이 있다.

이 예제에서 Order → OrderItem을 개인소유 하기 때문에 cascade를 사용했다. 그런데 Order 입장에서 Delivery는 좀 애매하다. 여기서는 프로젝트 규모가 작기 때문에 매우 단순하게 표현했지만, 실무에서 프로젝트 규모가 커지면, Delivery는 여러곳에서 참조될 수 있다. 그러면 사용하면 안 된다.

#### 양방향 연관관계 사용시 편의 메서드를 활용 하자

양방향 연관관계 사용 시 양쪽에 값을 세팅해야 한다..

```java
member.getOrders().add(order);
order.setMember(member);
```

아래와 같이 연관관계 편의 메서드를 사용하면 위의 코드 작성시 발생할 수 있는 실수를 없앨 수 있다.

```java
//==연관관계 편의 메서드==//
public void setMember(Member member) {
		this.member = member;
		member.getOrders().add(this);
}
```

위와 같은 메서드를 사용하면 `orders.setMember(member)`만 호출하면 돼서 양방향으로 값을 세팅할 필요가 없어진다.

#### 컬렉션은 필드에서 초기화 하자

컬렉션은 필드에서 바로 초기화 하는 것이 안전하다.

```java
private List<Order> orders = new ArrayList<>(); // 이컬렉션 바꾸지 말고 그대로 쓰자
```

- `null` 문제에서 안전하다.
- 하이버네이트는 엔티티를 영속화 할 때, 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경한다. 만약 `getOrders()` 처럼 임의의 메서드에서 컬렉션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문제가 발생할 수 있다. 따라서 필드레벨에서 생성하는 것이 가장 안전하고, 코드도 간결하다.

```java
Member member = new Member();
System.out.println(member.getOrders().getClass());
em.persist(team); // 영속화 -> orders 컬렉션을 영속성 컨텍스트가 관리
System.out.println(member.getOrders().getClass()); // 하이버네이트가 관리하는 매커니즘으로 바꾼다.

//출력 결과
class java.util.ArrayList
class org.hibernate.collection.internal.PersistentBag
```

#### 테이블, 컬럼명 생성 전략

스프링 부트에서 하이버네이트 기본 매핑 전략을 변경해서 실제 테이블 필드명은 다르다.

[Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/2.1.3.RELEASE/reference/htmlsingle/#howto-configure-hibernate-naming-strategy)

[Index of /hibernate/orm/5.4/userguide/html_single](http://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/)

하이버네이트 기존 구현: 엔티티의 필드명을 그대로 테이블의 컬럼명으로 사용 ( `SpringPhysicalNamingStrategy` )

**스프링 부트 신규 설정 ( 엔티티(필드) → 테이블(컬럼) )**

1. 카멜 케이스 → 언더스코어 ( `memberPoint` → `member_point` )
2. `.` (점) → `_` (언더스코어)
3. 대문자 → 소문자

#### **적용 2 단계**

1. 논리명 생성: 명시적으로 컬럼, 테이블명을 직접 적지 않으면 `ImplicitNamingStrategy` 사용 `spring.jpa.hibernate.naming.implicit-strategy` : 테이블이나, 컬럼명을 명시하지 않을 때 논리명 적용
2. 물리명 적용:
   `spring.jpa.hibernate.naming.physical-strategy` : 모든 논리명에 적용됨, 실제 테이블에 적용 (`username` → `usernm` 등으로 회사 룰로 바꿀 수 있음)

#### 스프링 부트 기본 설정

```java
spring.jpa.hibernate.naming.implicit-strategy:
org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
spring.jpa.hibernate.naming.physical-strategy:
org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
```

## 4. 애플리케이션 구현 준비

### 4.1구현 요구 사항

![img](https://user-images.githubusercontent.com/38436013/134313729-3ef1c947-f63d-4508-9554-55adf4d221e3.png)

#### 기능 목록

- 회원 기능
  - 회원 등록
  - 회원 조회
- 상품 기능
  - 상품 등록
  - 상품 수정
  - 상품 조회
- 주문 기능
  - 상품 주문
  - 주문 내역 조회
  - 주문 취소

**예제를 단순화 하기 위해 다음 기능은 구현 X**

- 로그인과 권한 관리X
- 파라미터 검증과 예외 처리 단순화 -> mvc 에서 해야,,
- 상품은 도서만 사용
- 카테고리는 사용X
- 배송 정보는 사용X

### 4.2 애플리케이션 아키텍처

![img](https://user-images.githubusercontent.com/38436013/134313937-dade811f-ced4-4fe4-875b-d9dd30cf0a7b.png)

### 계층형 구조 사용

- controller, web: 웹 계층
- service: 비즈니스 로직, 트랜잭션 처리
- repository: JPA를 직접 사용하는 계층, 엔티티 매니저 사용
- domain: 엔티티가 모여 있는 계층, 모든 계층에서 사용

### 패키지 구조

- jpabook.jpashop
  - domain
  - exception
  - repository
  - service
  - web

### 개발 순서

**서비스, 리포지토리 계층을 개발 -> 테스트 케이스를 작성, 검증 -> 마지막에 웹 계층 적용**



## 5. 회원 도메인 개발

- 구현 기능
  - 회원 등록
  - 회원 목록 조회
- 순서
  - 회원 엔티티 코드 다시 보기
  - 회원 리포지토리 개발
  - 회원 서비스 개발
  - 회원 기능 테스트

### 5.1 회원 리포지토리 개발

~~~java
import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    // jpa에서 제공하는 메서드, 영속성컨텍스트에 멤버를 저장(나중에 트랜잭션 커밋될 때 디비에 반영)
    public void save(Member member) {
        em.persist(member);
    }

    // jpa에서 제공하는 ~, id값을 넘기면 멤버 찾아 반환
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // jpql과 반환 타입을 넘기면, 멤버를 리스트로 만들어서 리턴
    // jpql은 sql과 비슷. sql은 테이블을 쿼리, jpql은 엔티티를 쿼리함
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
    // 이름으로 회원 조회
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name",
                Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
~~~

**기술 설명**

- @Repository : 스프링 빈으로 등록,  JPA 예외를 스프링 기반 예외로 예외 변환(?)
- @PersistenceContext : 엔티티 메니저( EntityManager ) 주입
  - EntityManager : JPA는 스레드가 하나 생성될 때마다 EntityManagerFactory에서 EntityManager를 생성한다.
  - EntityManager는 내부적으로 DB 커넥션 풀을 사용하여 DB와 연결된다.
- `@PersistenceUnit` : 엔티티 메니터 팩토리( EntityManagerFactory ) 주입

**기능 설명**

- `save()` : `em.persist()`는 영속성 컨텍스트를 통해서 엔티티를 영속화한다.
- `findOne()`
- `findAll()`: JPQL을 호출하여 JPA에서 제공하는 메서드 호출만으로 쿼리를 작성
- `findByName()`

### 5.2 회원 서비스 개발

~~~java
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
        //영속성컨텍스트가 key-value 형태,
        // 디비에 저장되기 전에 id값이 key로 영속성컨텍스트에 존재함, 그래서 id 리턴
        }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers =
                memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
~~~

**기술 설명**

- `@Service`
- `@Transactional` : 트랜잭션, 영속성 컨텍스트
  - `readOnly=true` : 데이터의 변경이 없는 읽기 전용 메서드에 사용, 영속성 컨텍스트를 플러시 하지 않으므로 약간의 *성능 향상(읽기 전용에는 다 적용)*
  - 데이터베이스 드라이버가 지원하면 DB에서 성능 향상
- `@Autowired`
  - 생성자 주입 사용, 생성자가 하나면 생략 가능

**기능 설명**

- `join()`
- `findMembers()`
- `findOne()`

> **참고**: 실무에서는 검증 로직이 있어도 멀티 쓰레드 상황을 고려해서 회원 테이블의 회원명 컬럼에 유니크 제약 조건을 추가하는 것이 안전하다.

> **참고**: 스프링 필드 주입 대신에 생성자 주입을 사용하자.

**필드 주입**

- 권장하지 않는 코드 -> Setter 이용한 코드

~~~java

    private MemberRepository memberRepository;
	// 이렇게 되면 못바꿈 

	@Autowired
	public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
	// 그래서 이런식으로 setter 이용해 주입을 해준다.
~~~

setter 주입의 단점은 런타임시에 setter 이용해 값을 바꿀 수 있다. 
근데 개발 로딩시에는 중간에 값을 바꾸는 경우가 없으며, setter를 권장하지 않는다.

**생성자 주입**

- 권장하는 코드

~~~java
	
	private final MemberRepository memberRepository;
	@Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    ...
~~~

생성자 주입을 이용한다.
생성자 주입은 setter로 값 바꾸는 경우가 불가하며, 테스트케이스 작성할 때 에러를 잡아준다.

- 변경 불가능한 안전한 객체 생성 가능
- 생성자가 하나면 `@Autowired` 를 생략할 수 있다.
- `final` 키워드를 추가하면 컴파일 시점에 `memberRepository` 를 설정하지 않는 오류를 체크할 수 있다. (보통 기본 생성자를 추가할 때 발견)

**lombok**

@AllArgsConstructor : 모든 필드에 대해 생성자 만들어줌

@RequiredArgsConstructor : final 붙은 필드에 대해 생성자 만들어줌

- @RequiredArgsConstructor 를 쓰는 것을 선호 -> 테스트 케이스에서 실수 막을 수 있음 

```java
@RequiredArgsConstructor
public class MemberService {
    @Autowired
    private final MemberRepository memberRepository;
    ...
}
```

> 참고: 스프링 데이터 JPA를 사용하면 `EntityManager` 도 주입 가능

```java
@Repository
@RequiredArgsConstructor
public class MemberRepository {
    @Autowired
    private final EntityManager em;
    ...
}
```

### 5.3 회원 기능 테스트

#### 회원 가입 테스트 코드

```java
package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {
    
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    
    @Test
    public void 회원가입() throws Exception {
        
        //Given
        Member member = new Member();
        member.setName("kim");
        
        //When
        Long saveId = memberService.join(member);
        
        //Then
        assertEquals(member, memberRepository.findOne(saveId));
    }
    
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //Given
        Member member1 = new Member();
        member1.setName("kim");
        
        Member member2 = new Member();
        member2.setName("kim");
        
        //When
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다.
        
        //Then
        fail("예외가 발생해야 한다.");
    }
}
```

**기술 설명**

- `@RunWith(SpringRunner.class)` : 스프링과 junit 테스트 통합
- `@SpringBootTest` : 스프링 부트 띄우고 테스트(이게 없으면 `@Autowired` 다 실패)
- `@Transactional` : 반복 가능한 테스트 지원, 각각의 테스트를 실행할 때마다 트랜잭션을 시작하고 테스트가 끝나면 트랜잭션을 강제로 롤백 (이 어노테이션이 테스트 케이스에서 사용될 때만 롤백)

**기능 설명**

- 회원가입 테스트
- 중복 회원 예외처리 테스트

> 참고: 테스트 케이스 작성 고수 되는 마법: Given, When, Then (http://martinfowler.com/bliki/GivenWhenThen.html) 이 방법이 필수는 아니지만 이 방법을 기본으로 해서 다양하게 응용하는 것을 권장한다.

#### 테스트 케이스를 위한 설정

테스트는 케이스 **격리된 환경에서 실행하고, 끝나면 데이터를 초기화**하는 것이 좋다. 그런 면에서 메모리 DB를 사용하는 것이 가장 이상적이다. 추가로 테스트 케이스를 위한 스프링 환경과, 일반적으로 애플리케이션을 실행하는 환경은 보통 다르므로 설정 파일을 다르게 사용하자. 다음과 같이 간단하게 테스트용 설정 파일을 추가하면 된다.

```yaml
test/resources/application.yml
spring:
#  datasource:
#    url: jdbc:h2:mem:testdb
#    username: sa
#    password:
#    driver-class-name: org.h2.Driver
#  jpa:
#    hibernate:
#    ddl-auto: create
#  properties:
#    hibernate:
#      show_sql: true
#      format_sql: true
#  open-in-view: false

logging.level:
  org.hibernate.SQL: debug
#  org.hibernate.type: trace
```

이제 테스트에서 스프링을 실행하면 이 위치에 있는 설정 파일을 읽는다. (만약 이 위치에 없으면 `src/resources/application.yml` 을 읽는다.)

스프링 부트는 datasource 설정이 없으면, 기본적을 메모리 DB를 사용하고, driver-class도 현재 등록된 라이브러를 보고 찾아준다. 추가로 `ddl-auto` 도 `create-drop` 모드로 동작한다. 따라서 데이터소스나, JPA 관련된 별도의 추가 설정을 하지 않아도 된다.



## 6. 상품 도메인 개발

### 6.1 상품 엔티티 개발(비지니스 로직 추가)

#### 상품 엔티티 - 비지니스 로직 추가

~~~java
package jpabook.jpashop.domain.item;

import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import jpabook.jpashop.domain.Category;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {
    
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    
    private String name;
    private int price;
    private int stockQuantity;
    
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();
    
    //==비즈니스 로직==//
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }
    
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
~~~

#### 예외 추가

~~~~java
package jpabook.jpashop.exception;
public class NotEnoughStockException extends RuntimeException {
    
    public NotEnoughStockException() {
    }
    
    public NotEnoughStockException(String message) {
        super(message);
    }
    
    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }
}
~~~~

**비즈니스 로직 분석**

- 엔티티 쪽에 메서드 작성하는 이유 : 내부에서 재고 양을 바꾸는 것이 객체지향적, (밖에서 setter로 재고양을 가져와서 바꾸는 것이 아니라!)
  - 응집력 측면에서 해당하는 entity에 함수에 관련된 로직 넣기
  - 도메인 스스로 해결할 수 있는 경우는 도메인 안에서 처리하는 것도 좋은 방법이 됨

- `addStock()` 메서드는 파라미터로 넘어온 수만큼 재고를 늘린다. 이 메서드는 재고가 증가하거나 상품 주문을 취소해서 재고를 다시 늘려야 할 때 사용한다.
- `removeStock()` 메서드는 파라미터로 넘어온 수만큼 재고를 줄인다. 만약 재고가 부족하면 예외가 발생한다. 주로 상품을 주문할 때 사용한다.

### 6.2 상품 리포지토리 개발

~~~java
package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) { 
            em.persist(item); // 신규 등록
        } else {
            em.merge(item); // 갱신
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i",Item.class).getResultList();
    }
}
~~~

**기능 설명**

- `save()`
  - `id` 가 없으면 신규로 보고 `persist()` 실행
  - `id` 가 있으면 이미 데이터베이스에 저장된 엔티티를 수정한다고 보고, `merge()` 를 실행

### 6.3 상품 서비스 개발

~~~java
package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    
    private final ItemRepository itemRepository;
    
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }
    
    public List<Item> findItems() {
        return itemRepository.findAll();
    }
    
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
~~~

상품 서비스는 상품 리포지토리에 단순히 위임만 하는 클래스