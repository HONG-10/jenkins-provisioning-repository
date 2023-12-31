간결한 답변은 Job DSL이 훨씬 더 오랫동안 존재했으며 Jenkins를 "코딩"하기 위한 Netflix의 오픈 소스 솔루션이었다는 것입니다.
이를 통해 Jenkins 작업을 스크립팅할 때 논리와 변수를 도입할 수 있으며 
일반적으로 이러한 작업을 사용하여 특정 프로젝트에 대한 일종의 "파이프라인"을 형성할 수 있습니다.
이 플러그인은 작업 템플릿 및 스크립팅을 활성화하는 일반적인 방법으로 많은 관심을 받았습니다.

Jenkins Pipeline(2.0)은 완전히 DSL을 기반으로 하는 Jenkins 작업의 새로운 화신이며,
Job DSL의 가장 일반적인 용도였던 단일 파이프라인을 채우기 위해 여러 작업을 함께 연결할 필요성을 없애려고 시도합니다.
원래 Pipeline DSL은 Job DSL이 제공하는 많은 기능을 제공하지 않으며,
위에서 언급한 것처럼 Pipeline 작업을 생성할 수 있으므로 이러한 기능을 함께 사용하여 파이프라인을 정의할 수 있습니다.

현재 IMO는 Jenkins 파이프라인을 스크립팅하기 위한 Jenkins 지원 메커니즘이며
Job DSL의 기능을 대부분 충족하거나 능가했기 때문에 Job DSL을 사용할 이유가 거의 없습니다.
새로운 플러그인은 Pipeline을 위해 기본적으로 개발되고 있으며, 그렇지 않은 플러그인은 Jenkins 개발자가 Pipeline과 통합하도록 권장하고 있습니다.Pipeline에는 다음과 같은 여러 가지 이점이 있습니다.

Job과 마찬가지로 Pipeline을 사용하여 작업을 "시드"할 필요가 없습니다.
파이프라인이 작업 자체이기 때문에 DSL.Job DSL을 사용하면,다른 작업을 생성하는 스크립트일 뿐입니다.
파이프라인을 사용하면 파이프라인 내에서 로직 미드스트림을 지정할 수 있는 매개 변수화된 수동 입력 단계와 같은 기능이 있습니다.
그럴 수 있는 논리는 Job DSL에 포함되는 작업은 작업 생성으로 제한됩니다.
파이프라인을 사용하면 논리를 직접 포함할 수 있습니다.
직장 내에서 Job DSL은 Build Pipeline Plugin 등을 사용하여 기본 전송 파이프라인을 생성하는 것이 훨씬 어렵습니다.
Pipeline을 사용하면 파일 크기가 줄어들고 구문이 짧아집니다.
또한 Job DSL을 사용하여 Pipeline 작업을 생성하는 경우 Jenkins Pipeline에서 즉시 사용할 수 있는 템플릿 기능을 고려할 때 더 이상 중요한 값을 보지 못했습니다.

마지막으로, 젠킨스 파이프라인은 현재 젠킨스의 가장 일반적인 특징입니다.
Jenkins World 2016 의제를 확인하면 세션의 약 50%가 파이프라인과 관련되어 있음을 알 수 있습니다.Job DSL에는 없음.