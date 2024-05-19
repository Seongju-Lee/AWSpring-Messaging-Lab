# SnsTemplate & SnsClient

## What is SnsTemplate?
[Spring](https://docs.awspring.io/spring-cloud-aws/docs/3.0.4/reference/html/index.html)에서 제공하는 AWS SNS와의 통합을 위해 컴포넌트이다.  
SnsTemplate은 SNS 알림을 보내기 위한 고수준의 추상화를 제공한다. 이를 통해 개발자는 복잡한 SNS API를 직접 사용하지 않고도 간편하게 SNS 기능을 활용할 수 있다.

1. Message Publish: SNS Topic에 메시지를 쉽게 발행할 수 있다.
2. Create And Manage Topics: SNS Topic을 생성하거나, 기존 Topic을 관리하는 기능을 제공한다.
3. Manage Subscribe: Topic에 subscriber를 추가/제거 등의 구독 관리 기능을 제공한다.
4. Configure SNS: 다양한 SNS 설정을 관리할 수 있다.


## What is SnsClient?
AWS의 Java SDK에서 제공하는 클래스이다. SNS에 대한 더 세부적인 작업을 수행하기 위해 사용하면 좋다.  즉, Amazon SNS의 저수준 작업에 접근하기 위해서는 이를 사용하는 것이 좋다.   
실제로 SnsTemplate에서도 기본 필드로 SnsClient가 있고, 생성자를 통해 전달 받고있다.

Spring Cloud AWS는 SnsAutoConfiguration을 통해 SnsClient 빈을 자동으로 구성하기 때문에, 별도의 설정 없이도 이를 주입받아 사용 가능하다.  
자동 구성된 SnsClient Bean을 커스텀 설정하고 싶을 때는 Custom Bean을 생성해서 생성자에 주입할 수 있다. (e.g. region, credentials, group_id 등)
