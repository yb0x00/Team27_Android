# 🐶 함께 찾는 반려동물, Together Pet
![image](https://github.com/user-attachments/assets/0a98c2d7-bced-4846-9193-adba2231770b)

<br>

## 목차
- 프로젝트 소개 및  기획 의도
- 팀원 구성
- 배포
- 개발 문서
- 프로젝트 브랜치 전략
- 아키텍처
- 프로젝트 구조
- 프로젝트 주요 기능
- 주요 이슈 사항

<br>

## 프로젝트 소개 및 기획 의도
**Together Pet**은 **잃어버린 반려동물의 제보나, 발견한 실종동물의 정보를 쉽게 공유할 수 있는 서비스**입니다.

매년 반려동물 양육가구가 증가하는 만큼, 유실/유기 동물도 매년 증가하고 있습니다.
<br>하지만 잃어버린 반려동물을 찾는 과정은 정말 쉽지 않습니다. 유기 동물 보호소를 찾아 다니거나 sns, 전단지를 통해 기약 없이 제보를 기다리는 수 밖에 없습니다.

Together Pet은 동물들의 위치 정보를 훨씬 쉽고 빠르게 공유할 수 있는 창구를 제공합니다.
<br>유기동물을 발견한 사람들은 발견한 동물에 대한 정보를 쉽게 많은 사람들에게 공유하거나 주인에게 제보할 수 있고, 이를 통해 반려동물을 잃어버린 주인은 더욱 빠르고 쉽게 반려동물과 재회할 수 있도록 서비스를 기획하였습니다.

![image](https://github.com/user-attachments/assets/da2dece5-5927-42b8-a7c2-7e5491b2a701)
반려동물을 찾는 것을 돕는 큰 힘은 바로 **지역 커뮤니티**라고 생각하였습니다.
<br>산책 기록 기능을 통해 사람들이 일상적으로 앱을 사용할 수 있게 하고,
<br>산책 중 실종 반려동물 정보를 손쉽게 열람하고 공유할 수 있도록 하여 함께 찾기에 대한 참여를 독려합니다.
<br>커뮤니티를 활성화하기 위해 추가로 반려 일기와 우리 동네 커뮤니티 기능을 제공할 예정입니다.

지역 중심의 반려인 커뮤니티를 기반으로, 실종 반려동물 찾기를 자연스럽게 돕는 플랫폼을 제공하여
<br>서로 도움을 주고받을 수 있는 생태계를 구축하고자 합니다.


> :computer: **Note**
>
> MVP(Minimally Viable Produect)에서는 실종 정보/목격 정보 등록 및 열람, 산책 기록을 중심으로 기능을 구현하였습니다

<br>

## 팀원 구성
**[Android]**
<div style="display: flex; justify-content: flex-start;">
    
|                                                     **오진우**                                                      |                                                     **최영빈**                                                      |
|:----------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/8a55698a-8a26-4d0f-af29-1ed5ecf5ebdf" height=150 width=150> | <img src="https://github.com/user-attachments/assets/70ad69bb-82ed-4612-bf58-7b71646c8bcd" height=150 width=150> |
|                                                  **Android_조장**                                                  |                                                 **Android_기획리더**                                                 |
|                                                  스플래시 화면, 간편 로그인, 산책하기 기능                                                 |                                                  홈 화면, 반려동물 같이 찾기 기능                                                    |


**[고마운 조력자]**
<div style="display: flex; justify-content: flex-start;">

|                                                     **이현기**                                                      |
|:----------------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/df8f872f-6dd4-47fd-934c-10aede3eafb2" height=150 width=150> |
|                                                  **BE_테크리더**                                                  |
|                                                  서버와의 데이터 송수신 기능                                               |

</div>


> :computer: **Note**
>
> 백엔드 팀 구성은 [백엔드 레포](https://github.com/kakao-tech-campus-2nd-step3/Team27_BE)에서 확인 가능합니다

<br>

## 배포
- [원스토어](https://m.onestore.co.kr/ko-kr/apps/appsDetail.omp?prodId=0000779558)에서 다운로드 할 수 있습니다

  
<img src="https://github.com/user-attachments/assets/0bbd105c-dc2d-4db9-99a8-1d948aaa245d" height=450 width=220>

<br>

<details>
  <summary>추가 정보</summary>
    <br>
  <div style="margin-left: 20px;">
    <ul>
      <li>위치 정보를 원활하게 활용하기 위해 <strong>아마존 앱스토어</strong> 배포를 시도하였습니다.</li>
      <li>원할한 앱 동작을 위해 Retrofit 관련 ProGuard 규칙을 추가하여 아마존 앱스토어에 업데이트 버전을 등록하는 것을 고려하고 있습니다.
        <br>
          <br>
        <img src="https://github.com/user-attachments/assets/2504d4ed-c522-4829-b3ab-2469d1c8ecf9" height="450" width="220" style="margin-left: 20px;">
      </li>
    </ul>
  </div>
</details>

<br>

## 개발 문서
- [와이어프레임](https://www.figma.com/proto/5j2O200pBFZDR4bAit5Iam/KTC-27%EC%A1%B0?node-id=1860-4058&node-type=section&t=fFr1Y5zW9251MUsz-1&scaling=scale-down&content-scaling=fixed&page-id=85%3A1081&starting-point-node-id=423%3A4217&show-proto-sidebar=1)
  <br>
  
- [API 명세서](https://quickest-asterisk-75d.notion.site/API-6d3b77b528b14cfa8b7dc8cd81d95872)
  <br>
  
- 개발 환경 설정
    * 안드로이드 스튜디오 버전 : Android Studio Iguana | 2023.2.1 Patch 2
    * java 버전 : 23.0.1
    * Android Gradle Plugin 버전 : 8.3.1
    * Gradle 버전 : 8.4
    * Sdk 버전
        + compileSdk : 34
        + minSdk : 26
        + targetSdk : 34
    * 카카오맵 사용을 위해 실물 기기를 사용하는 것을 권장
    * API 키 사용 : 팀원이 요청할 시 Github Secrets를 통해 공유

<br>

    
  * 프로젝트 브랜치 전략

  <img src="https://github.com/user-attachments/assets/0b00dde1-7cc4-4689-b640-7bb5842bddb1" alt="환경 설정" style="margin-right: 20px;">


## 아키텍처
- 데이터베이스 설계도
  
- 데이터 흐름도
  

<br>

## 프로젝트 구조

<br>

## 프로젝트 주요 기능


<br>

## 주요 이슈 사항
