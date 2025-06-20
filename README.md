# 🎯 타이핑 연습 게임 (Typing Practice Game)

> Java Swing 기반의 종합 타자 연습 프로그램

![Java](https://img.shields.io/badge/Java-8%2B-orange)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue)
![License](https://img.shields.io/badge/License-Educational-green)

---

## 📝 프로젝트 소개

**타이핑 연습 게임**은 Java Swing을 활용하여 개발된 GUI 기반의 타자 연습 프로그램입니다. 
다양한 게임 모드와 언어 지원을 통해 효과적이고 재미있는 타이핑 연습 환경을 제공합니다.

---

## ✨ 주요 기능

### 🎮 게임 모드

#### 📖 일반 타자 연습
- **문장 연습**
  - 무작위 선정된 10개의 짧은 문장 타이핑
  - 실시간 정확도 및 타자 속도(WPM) 계산
  - 문장별 개별 성과 분석

- **장문 연습**
  - 긴 글 전체를 완성하는 지구력 훈련
  - 집중력 향상에 특화
  - 전체 완료 후 평균 성과 제공

#### 🎯 두더지 잡기 게임
- **60초 제한 시간**의 스피드 게임
- 화면 곳곳에 무작위로 나타나는 단어 입력
- 순발력과 정확성의 조화
- 최종 점수를 통한 성과 측정

### 🌍 다국어 지원
- **한국어** 및 **영어** 텍스트 완벽 지원
- 언어별 최적화된 연습 콘텐츠

---

## 🚀 빠른 시작

### 📋 시스템 요구사항
- **JDK 8 이상**

### 💻 설치 및 실행

1. **저장소 복제**
   ```bash
   git clone https://github.com/flecy0904/Typing-Practice.git
   cd Typing-Practice
   ```

2. **프로젝트 컴파일**
   ```bash
   javac -d out -cp . $(find . -name "*.java")
   ```

3. **게임 실행**
   ```bash
   java -cp ./out TypingPracticeGame
   ```

---

## 📁 프로젝트 구조

```
JAVA_midterm_Project/
├── 📂 core/                     # 🔧 게임 로직 & 모델
│   ├── GameModel.java           #   - 게임 상태 관리
│   ├── MoleGameModel.java       #   - 두더지 게임 로직
│   └── TextRepository.java     #   - 텍스트 데이터 관리
│
├── 📂 GameUI/                   # 🎨 사용자 인터페이스
│   ├── MainFrame.java           #   - 메인 프레임
│   ├── MainMenuPanel.java       #   - 메인 메뉴
│   ├── GamePanel.java           #   - 게임 화면
│   ├── MoleGamePanel.java       #   - 두더지 게임 화면
│   ├── SettingsPanel.java       #   - 설정 화면
│   └── ...                     #   - 기타 UI 컴포넌트
│
├── 📂 resources/               # 📚 게임 리소스
│   ├── typing_words.txt         #   - 영어 단어
│   ├── typing_words_ko.txt      #   - 한국어 단어
│   ├── typing_long_en.txt       #   - 영어 장문
│   ├── typing_long_ko.txt       #   - 한국어 장문
│   ├── mole_words_en.txt        #   - 영어 두더지 게임 단어
│   └── mole_words_ko.txt        #   - 한국어 두더지 게임 단어
│
└── TypingPracticeGame.java     # 🏁 메인 실행 클래스
```

---

## 🏗️ 아키텍처 설계

### MVC 패턴 적용

이 프로젝트는 **Model-View-Controller (MVC) 패턴**을 기반으로 설계되어 유지보수성과 확장성을 극대화했습니다.

| 구성 요소 | 역할 | 구현 위치 |
|-----------|------|-----------|
| **📊 Model** | 게임 상태 & 비즈니스 로직 | `core/` 패키지 |
| **🎨 View** | 사용자 인터페이스 & 시각적 표현 | `GameUI/` 패키지 |
| **🎮 Controller** | 사용자 입력 처리 & 이벤트 관리 | View 내 이벤트 리스너 |

### 핵심 설계 원칙
- **관심사의 분리**: 데이터 로직과 UI 로직의 명확한 분리
- **모듈화**: 각 기능별 독립적인 컴포넌트 구성
- **확장성**: 새로운 게임 모드나 언어 추가 용이
- **재사용성**: 공통 컴포넌트의 효율적 활용

---

## 🎯 게임 특징

- ⚡ **실시간 성과 측정**: 타자 속도(WPM)와 정확도 즉시 계산
- 🎲 **다양성**: 여러 게임 모드로 지루함 없는 연습
- 🌐 **국제화**: 한국어/영어 완벽 지원
- 🎨 **직관적 UI**: 사용하기 쉬운 깔끔한 인터페이스
- 📈 **진행도 추적**: 개선 사항을 한눈에 확인

---

## 🤝 기여하기

프로젝트 개선에 관심이 있으시다면 언제든 기여해주세요!

1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request

---

## 📄 라이선스

이 프로젝트는 **교육용 라이선스** 하에 배포됩니다. 학습 및 교육 목적으로 자유롭게 사용하실 수 있습니다.

---

<div align="center">

**⌨️ 즐거운 타이핑 연습 되세요! ⌨️**

</div>


