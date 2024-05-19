# SNS의 작동방식

SNS(Simple Notification Service)는 완전관리형 pub/sub 서비스로, A2A와 A2P 두 가지 방식으로 알림을 전송한다.  

A2A(Application-to-Application)는 애플리케이션 간 통신을 의미한다. 이는 한 애플리케이션과 데이터를 교환하거나 상호작용하는 
방식을 의미한다.
주로 분산 시스템, 마이크로서비스, 이벤트 중심의 애플리케이션 간에 처리량이 많은 푸시 기반의 다대다 메시징을 제공한다. 
해당 Repository에서 다루는 SQS이 여기서 말하는 애플리케이션에 포함된다.

A2P(Application-to-Person)는 애플리케이션에서 사람에게로의 메시징을 의미한다. 주로 자동화된 메시지를 보내는 방식으로,
주로 SMS, 이메일, 푸시 알림 등을 통해 이루어진다.


<img src="https://github.com/Seongju-Lee/AWSpring-Messaging-Lab/assets/67941526/3168a6fd-8c47-41d4-b3a6-ab0823a5ab72" alt="standard-sns" width="80%">


# Amazon SNS 기능
해당 Repository에서는 SMS, 모바일 푸시 등의 기능은 제외하고 pub/sub 작동 방식에 대해 깊이있게 살펴본다.  
pub/sub 작동방식의 이해를 위한 SNS 기능에 대해 살펴볼 것이다.

## Topic types
### Standard Topics
메시지가 여러번 도착하거나 순서가 뒤섞여 도착해도 이를 처리할 수 있는 애플리케이션에 유용하다. AWS docs에서 소개하는 몇 가지 시나리오는 아래와 같다.  
- **미디어 인코딩 (Media Encoding)**  
  비디오나 오디오 파일을 여러 가지 형식으로 인코딩해야 하는 경우, 하나의 원본 파일을 여러 인코딩 서버로 팬아웃(fanning out)하여 동시에 인코딩 작업을 수행할 수 있다.
  인코딩 시간을 단축하고, 동시에 여러 형식의 파일을 생성할 수 있다.

- **사기 탐지 (Search Index)**  
    실시간으로 들어오는 거래 데이터를 분석하여 사기 행위를 감지한 경우, 해당 토픽을 사용해 트랜잭션 데이터를 여러 분석 시스템으로 전달할 수 있다.

- **알림 시스템 (Alerting Applications)**
  시스템 상태나 보안 경고 등을 빠르고 확실하게 전달해야 하는 경우, 여러 알림 채널로 동시에 메시지를 전송할 수 있다.


1. **Maximum throughput**  
    초당 거의 무제한의 메시지를 처리 가능하다. 이는 메시지를 매우 빠르고 대량으로 보내야 하는 경우 유용하다.


2. **Ordering**  
    메시지가 발행된 순서와 다르게 전달될 수 있다.  
   <img src="https://github.com/Seongju-Lee/AWSpring-Messaging-Lab/assets/67941526/76c3a2c5-3a59-4eb1-9d53-b2a12bdc8e17" alt="standard-sns" width="80%">


3. **deduplication**  
    메시지는 최소한 한 번 전달되지만, 때때로 동일한 메시지가 중복되어 전달 될 수 있다.


4. **Multiple subscription types**  
    메시지는 다양한 형태로 전달될 수 있다. 예를 들어, 애플리케이션 간의 메시지 전달(A2A)로는 Amazon SQS, Amazon Kinesis Data Firehose, AWS Lambda, HTTPS 등이 있다.


5. **Message fanout**  
   하나의 계정은 100,000개의 Standard topics를 지원할 수 있으며, 각 topic은 최대 1,250만 개의 구독을 지원한다.


### FIFO Topics
메시지의 순서가 중요하거나 중복이 허용되지 않는 애플리케이션 간의 메시징을 강화하기 위해 설계되었다. 
AWS docs에서 소개하는 시나리오는 아래와 같다.

- **은행 거래 기록(Bank Transaction Logging)**  
    금융 거래의 순서는 매우 중요하다. FIFO Topics는 거래가 발생한 순서대로 정확하게 기록할 수 있도록 보장한다. 또한, 중복거래가 발생하지 않도록 관리한다.  

1. **High throughput**  
   FIFO Topics는 초당 최대 300개의 메시지 또는 10MB의 데이터를 처리할 수 있다.


2. **Strict Ordering**  
   메시지가 발행되고 전달되는 순서가 엄격하게 보장된다. 즉, 먼저 들어온 메시지가 먼저 전달된다.  
   <img src="https://github.com/Seongju-Lee/AWSpring-Messaging-Lab/assets/67941526/597adc78-f3bd-4000-9d10-20b5917fe081" alt="fifo-sns" width="80%">


3. **Strict Deduplication**  
   중복 메시지는 전달되지 않는다. 메시지가 발행된 시간으로부터 5분 이내에 중복 제거가 이루어진다.


4. **Message fanout**  
   하나의 계정은 1,000개의 FIFO topics를 지원할 수 있으며, 각 topic은 최대 100개의 구독을 지원한다.


## Event sources and destinations
이벤트 기반 컴퓨팅은 subscriber가 publisher에 의해 트리거된 이벤트에 반응하여 자동으로 작업을 수행하는 모델이다. 
이는 독립적으로 작동하는 subscriber 서비스를 분리하면서 워크플로를 자동화하는데 적용할 수 있으며, 
**Amazon SNS는 다양한 AWS 이벤트 소스 및 이벤트 대상과 기본적으로 통합된 이벤트 중심 허브**이다.

<img src="https://github.com/Seongju-Lee/AWSpring-Messaging-Lab/assets/67941526/b20939c9-e179-4d39-bc2f-c0ffb5363c73" alt="EDA-SNS-workflow" width="80%">


## Message Filtering


## Message fanout and delivery

