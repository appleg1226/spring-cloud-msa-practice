# Spring Cloud MSA Practice

## 개발 spec
- java8, maven
- spring boot: 8개의 어플리케이션 모두 스프링 부트 및 starter dependency로 구현. 모듈화하여 github에 업로드
- RabbitMQ: Docker로 관리(docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management)  
  Manager port: 15672   
- Zipkin: Docker로 관리(docker run -d --name zipkin -p 9411:9411 openzipkin/zipkin)
<br/>

## 개요
MSA 환경은 일반적인 모놀리식 아키텍쳐에 있는 모든 기능을 하나의 서비스처럼 사용할 수 있도록 하는 것이 관건이다.  
분명 다른 어플리케이션으로 이루어져있지만, 하나처럼 보이기 위해서 Spring cloud 프로젝트는 많은 편리한 기능을 제공한다.    
여러 어플리케이션이 연결되는 고리는 Eureka가,    
어플리케이션의 입구는 Zuul이,  
같은 기능을 하는 여러 개의 어플리케이션을 분배해주는 로드밸런서는 Ribbon이,    
서로 간의 호출에 문제가 생길 경우에는 Hystrix가,    
로깅은 Sleuth와 Zipkin이,  
설정 관리는 Config이 해준다.  
  
분명 MSA는 여러가지로 복잡한 구조를 가지고 있다.  
실제로는 디테일한 설정이 필요하지만, 단 몇 줄의 설정으로 서비스들을 간단하게 이을 수 있다.
이렇게 쉽게 된다고? 라고 느낀 적이 너무 많은 좋은 플랫폼이다.

스프링이 너무 많은 것을 해줘서 나의 개발 실력이 생각보다 많이 늘지 않을거란 말도 안되는 생각이 든 것은,  
그만큼 이 프로젝트가 많은 편리성을 제공해준다는 것이 아닐까 생각한다.

## 구현 순서(일정)

- [x] 서비스 어플리케이션 작성
- [x] 유레카 연동
- [x] Zuul 연동하여 routing 테스트
- [x] Ribbon 이용 client-side 로드밸런싱 테스트
- [x] Hystrix 적용
- [x] 서버 간 메시징 적용(RabbitMQ)
- [x] Sleuth 적용
- [x] Zipkin 적용하여 수집 / ui 대시보드 띄우기
- [x] Config 서버 생성 / github 연동
<br/>

## 이후 추가할 실습
- 보안 기능
- 마이크로서비스 테스트
- 도커/젠킨스/쿠버네티스와 관련된 공부 및 실습 

## 아키텍쳐(간단하게 만들어본 설계도)
<img src="/architecture.png">

Spring Cloud에 대한 책을 읽고 다음의 구조를 목표로 설계를 진행했다.

보다시피 하나의 MSA 서비스 구성을 위해서는 지금만 봐도 8개의 어플리케이션이 필요하다.

게이트웨이 1개 + config 서버 1개 + eureka 서버 1개 + zipkin 서버 1개 + 서비스 서버 4개 = 8개

이렇게 많은 요소들을 문제없이 적용하기 위해서는 관련된 학습과 삽질이 많이 필요할 것 같다.

유레카부터 로깅까지 순서대로 간단하게 어떤 역할을 하는지 정리해보겠다.

### 1) Eureka 
많은 어플리케이션들을 사용하는 데 있어서 사실 어떻게든 각자의 ip-adddress로 묶으면 굳이 별도의 라이브러리가 필요하지 않을 수도 있다. 그러나 그것들을 하나의 문서에 일일이 기록해가며 운용할 수도 없는 노릇이다. 그리고 어플리케이션 사이에 소속감(?)이 있다는 느낌도 아직까지 들지 않는다. 

그래서 나타난 것이 Eureka이다. 

Eureka는 그림처럼 소속 어플리케이션들을 등록하고 서로가 서로를 알 수 있도록 만들어주는 매칭 플랫폼이다. 이를 통해서 각 서비스들은 각자의 ip-address가 아닌 이름으로 서로를 소통할 수 있게 해준다.

본 실습에서는 8761 포트를 사용했으며, 대부분의 서비스가 eureka에서 id를 참조하여 동작한다. 

### 2) Zuul(Gateway):
api에 대한 고객들의 이벤트 처리, 그리고 다른 어플리케이션들에서의 api 콜은 원래는 각각의 ip 주소를 호출하고 각각 소통하는 식으로 했다면, Zuul을 이용해서는 이것을 한 곳에서 모아준다.

즉 간단하게 MSA 아키텍쳐로의 입구라고 보는 것이라고 설명할 수 있을 것 같다.
모든 외부로부터의 호출은 이 gateway를 거치며 게이트웨이에서는 유레카에 등록된 msa 서비스들을 경로명으로 구분하여 분배해주게 된다.(Routing)  
이 과정에서 필터링을 통해 여러가지 작업을 추가할 수 있다. 본 프로젝트에서는 필터링으로 로그 출력을 구현했다.

그림에서 보면 게이트웨이는 하나의 스프링 어플리케이션 단위로 관리된다.
gateway는 각 api에서 받은 응답을 호출한 대상에게 리본으로 로드밸런싱을 거친 뒤에 넘겨주게 된다.

### 3) Ribbon
ribbon은 간단하게는 로드밸런서라고 보면 된다. 기존 장비를 사용한 로드밸런서가 아닌, 소프트웨어로 구현된 로드밸런서이다. 기본적으로는 round-robin 방식으로 돌아가며, retry 기능도 지원한다.

본 프로젝트에서는 Zuul에 내장되어 별도의 선언 없이 사용가능하며, Eureka에서 서비스 목록을 확인하여 Round-Robin으로 분배한다.

### 4) Hystrix
각 기능의 장애 관리는 항상 복잡한 문제다. 그것을 관리해주는 것이 히스트릭스이다.  
히스트릭스는 각 어플리케이션에서 진행되는 api나 메서드 호출을 감시한다.  
그래서 혹시나 실패하게 되면 그것을 기록하고 실패를 대비해 만들어 놓은 메서드를 실행하기도 한다. 이러다가 문제가 많아지면 그것의 호출을 차단하기도 하는 서킷브레이커 패턴을 구현한 라이브러리다.  
뭔가 명확하게 설명은 안되지만, 여러 서비스들이 호출할 때 생기는 '연결'과 관련된 문제들을 파악하고 처리하는 데에 특화된 반드시 필요한 라이브러리라고 할 수 있다.

본 프로젝트에서는 간단하게 Zuul에만 Fallback을 적용했다.

### 5) Spring cloud stream
그림에 보면 RabbitMQ가 있는데, 원래는 그냥 spring과 통합된 기존 라이브러리를 써도 되지만 스프링 클라우드에서는 더욱 추상화되어 사용되는 것 같다.  
원래 spring-kafka를 사용했었는데 그것 이상으로 추상화되고 간단한 연결을 제공한다.  
얼마 전까지 Serializer 때문에 열심히 서치를 했었는데 그것들조차도 생략시켜버렸다. 


### 6) Spring cloud config
이건 왜 필요한지 궁금했는데, 내가 직접 운용을 해보지 않아서 시야가 좁기 때문인 것 같다.  
config 서버는 git에서 commit 정보가 업데이트 되면 자동적으로 거기에 있는 config 정보를 각 서버로 업데이트 시켜줄 수 있도록 할 수 있다.  
물론 중간에 여러 셋팅을 해주는 게 복잡하긴 하다만, 한 번 구축해 놓으면 원클릭으로 설정을 수정할 수 있어서 유용한 모듈이다.

Config 서버 때문에 bootstrap.yaml의 존재에 대해서도 알게 되었다.  
bootstrap에는 구동에 필요한 정보를 넣어 놓고, 나머지 설정에 관련된 것들은 git repo.에 올려서 관리한다.

본 프로젝트에서는 자동으로 push했을 시 설정을 refresh하는 것은 넣지 않았다.

### 7) Sleuth
분명 msa는 하나의 아키텍쳐이고 한 몸인데, 로깅이라는 것은 여전히 각자 따로 관리된다.   
그래서 도대체 하나의 요청이 어디 서버에서 무엇을 하는지 tracking이 쉽지가 않은데, 그것을 지원해주는 라이브러리이다. 
슬루스를 등록해놓으면 어디에서든 그 요청의 아이디값을 확인할 수 있어서 편리하게 모니터링이 가능하다.
Log 라이브러리를 기본적으로 사용하여 Trace 정보를 로그에 추가해준다.  

### 8) Zipkin
위의 sleuth에서 이어지는 툴인데, zipkin은 sleuth를 통해 만들어진 아이디를 중심으로 요청 정보 등을 수집해서 분석하고, ui로도 보여준다.   
즉 분산된 msa 시스템의 모니터라고 할 수 있다. 이 대시보드에는 시스템의 흐름도 보여주어 모니터링에 굉장히 유용하다.  
걸린 시간도 간편하게 볼 수 있기 때문에 MSA에 있어서는 필수 요소라고 할 수 있겠다.
