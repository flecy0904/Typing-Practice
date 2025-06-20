package core;

import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * 타이핑 연습 게임의 핵심 로직을 담당하는 모델 클래스
 * 
 * 게임 상태 관리, 입력 검증, 정확도 계산, 타수 측정 등의 
 * 비즈니스 로직을 처리합니다.
 * 슬라이딩 윈도우 방식의 실시간 타수 계산을 지원합니다.
 * 
 * @author JAVA 중간 프로젝트
 * @version 1.0
 */
public class GameModel {
    private static final int WINDOW_SEC = 5;  // 슬라이딩 창 길이 (초)
    private static final int SENTENCES_PER_GAME = 10;  // 한 게임당 문장 개수
    
    /**
     * 장문 연습용 텍스트 정보를 저장하는 레코드
     * @param title 사용자에게 보여질 제목
     * @param fileName 실제 텍스트 파일명
     */
    public record LongText(String title, String fileName) {}

    /**
     * 지원하는 언어 목록을 정의하는 열거형
     * 각 언어는 표시명과 해당 텍스트 파일명을 가집니다.
     */
    public enum Language {
        KOREAN("한국어", "typing_words_ko.txt", "mole_words_ko.txt", List.of(
                new LongText("잊혀진 정원", "typing_long_ko.txt")
        )),
        ENGLISH("English", "typing_words.txt", "mole_words_en.txt", List.of(
                new LongText("The Little Prince (Excerpt)", "typing_long_en.txt")
        ));
        
        private final String displayName;
        private final String sentenceFileName;
        private final String moleGameFileName;
        private final List<LongText> longTexts;
        
        /**
         * Language 열거형 생성자
         * @param displayName 사용자에게 표시될 언어명
         * @param sentenceFileName 해당 언어의 문장연습 텍스트 파일명
         * @param moleGameFileName 해당 언어의 두더지게임 단어 파일명
         * @param longTexts 해당 언어의 장문연습 텍스트 정보 리스트
         */
        Language(String displayName, String sentenceFileName, String moleGameFileName, List<LongText> longTexts) {
            this.displayName = displayName;
            this.sentenceFileName = sentenceFileName;
            this.moleGameFileName = moleGameFileName;
            this.longTexts = longTexts;
        }
        
        /**
         * 언어의 표시명을 반환합니다.
         * @return 사용자에게 표시될 언어명
         */
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * 언어에 해당하는 문장연습 텍스트 파일명을 반환합니다.
         * @return 문장연습 텍스트 파일명
         */
        public String getSentenceFileName() {
            return sentenceFileName;
        }

        public String getMoleGameFileName() {
            return moleGameFileName;
        }

        /**
         * 언어에 해당하는 장문연습 텍스트 정보 리스트를 반환합니다.
         * @return 장문연습 텍스트 정보 리스트
         */
        public List<LongText> getLongTexts() {
            return longTexts;
        }
    }

    /**
     * 타이핑 연습 게임에서 지원하는 테마 목록을 정의하는 열거형
     * 
     * 각 테마는 표시명과 색상 설정을 가지고 있습니다.
     */
    public enum Theme {
        LIGHT("라이트 모드"),
        DARK("다크 모드");
        
        private final String displayName;
        
        /**
         * Theme 열거형 생성자
         * @param displayName 사용자에게 표시될 테마명
         */
        Theme(String displayName) {
            this.displayName = displayName;
        }
        
        /**
         * 테마의 표시명을 반환합니다.
         * @return 사용자에게 표시될 테마명
         */
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 게임 난이도 열거형
     */
    public enum Difficulty {
        EASY("쉬움"),
        NORMAL("보통"),
        HARD("어려움");

        private final String displayName;

        Difficulty(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName; // JComboBox에서 표시될 이름
        }
    }
    
    /**
     * 게임 모드 열거형
     * 문장연습과 장문연습을 구분합니다.
     */
    public enum GameMode {
        SENTENCE("문장 연습", "짧은 문장 10개를 정확하고 빠르게 입력하는 연습을 합니다."),
        LONG_TEXT("장문 연습", "하나의 긴 글을 처음부터 끝까지 타이핑하며 집중력을 기릅니다."),
        MOLE_GAME("두더지 잡기", "화면에 나타나는 단어를 입력하여 두더지를 잡는 미니게임입니다.");
        
        private final String displayName;
        private final String description;
        
        /**
         * GameMode 열거형 생성자
         * @param displayName 사용자에게 표시될 모드명
         * @param description 모드 설명
         */
        GameMode(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        /**
         * 게임 모드의 표시명을 반환합니다.
         * @return 사용자에게 표시될 모드명
         */
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * 게임 모드의 설명을 반환합니다.
         * @return 모드 설명
         */
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 타이핑 이벤트를 기록하는 내부 클래스
     * 슬라이딩 윈도우 계산에 사용됩니다.
     */
    private static class TypingEvent {
        final Instant time;
        final int syllableCount;
        
        TypingEvent(Instant time, int syllableCount) {
            this.time = time;
            this.syllableCount = syllableCount;
        }
    }
    
    // 게임 데이터 관련 필드
    private List<String> practiceTexts;        // 연습용 문장 목록
    private String currentPracticeText;        // 현재 연습 중인 문장
    private int currentTypedChars = 0;         // 현재 입력된 문자 수
    private int correctChars = 0;              // 올바르게 입력된 문자 수
    private TextRepository textRepository;     // 텍스트 파일 관리 객체
    private Random random = new Random();      // 랜덤 문장 선택용
    
    // 게임 진행 관련 필드
    private boolean gameStarted = false;       // 게임 시작 여부
    private boolean gameCompleted = false;     // 게임 완료 여부 (10개 문장 완료)
    private int currentSentenceNumber = 1;     // 현재 문장 번호 (1~10)
    private Language currentLanguage = Language.KOREAN; // 현재 선택된 언어 (기본값: 한국어)
    private Theme currentTheme = Theme.LIGHT;  // 현재 선택된 테마 (기본값: 라이트 모드)
    private GameMode currentGameMode = GameMode.SENTENCE; // 현재 게임 모드 (기본값: 문장연습)
    private Difficulty moleGameDifficulty = Difficulty.NORMAL; // 두더지 게임 난이도
    
    // 향상된 타수 계산 관련 필드
    private final Deque<TypingEvent> typingWindow = new ArrayDeque<>(); // 슬라이딩 윈도우
    private int totalCharacters = 0;          // 전체 입력된 문자 수 (CPM 계산용)
    private Instant startTime = null;         // 게임 시작 시간
    private String lastProcessedInput = "";   // 마지막으로 처리된 입력
    private String currentInputText = "";      // 현재 입력 중인 텍스트
    
    // 완료 조건 설정
    private boolean allowLengthBasedCompletion = true;  // 길이 기반 완료 허용 여부 (기본: 활성화)
    
    // 각 문장별 결과 추적
    private List<Double> sentenceAccuracies = new ArrayList<>();  // 각 문장별 정확도
    private List<Double> sentenceWPMs = new ArrayList<>();        // 각 문장별 타수
    private List<String> completedInputTexts = new ArrayList<>(); // 완료된 문장들의 실제 입력 내용
    // 문장별 완료 상태는 더 이상 추적하지 않음 (단순화)
    
    // 중복 없는 문장 선택
    private List<String> selectedGameTexts = new ArrayList<>();   // 현재 게임용으로 선택된 10개 문장
    private int currentGameTextIndex = 0;                         // 현재 게임에서 사용 중인 문장 인덱스

    // 장문연습용 문장 분리
    private List<String> longTextSentences = new ArrayList<>();   // 장문을 문장 단위로 분리한 리스트
    private int currentLongTextSentenceIndex = 0;                 // 장문 내에서 현재 문장 인덱스
    private String originalLongText = "";                         // 원본 장문 텍스트 (전체)

    /**
     * GameModel 생성자
     * TextRepository를 초기화하고 기본 언어(한국어)의 텍스트를 로드합니다.
     */
    public GameModel() {
        this.textRepository = new TextRepository();
        loadTextsForCurrentLanguage();
        startNewGame();
    }

    /**
     * 현재 언어에 맞는 텍스트를 로드합니다.
     * 설정된 언어에 따라 해당 언어의 텍스트 파일을 읽어옵니다.
     */
    private void loadTextsForCurrentLanguage() {
        String fileName = currentLanguage.getSentenceFileName();
        String fullPath = "resources/" + fileName;
        practiceTexts = textRepository.loadTexts(fullPath);
        
        if (practiceTexts == null || practiceTexts.isEmpty()) {
            practiceTexts = new ArrayList<>();
            practiceTexts.add("Default text: Please check file path or content.");
        }
    }

    /**
     * 언어를 설정하고 해당 언어의 텍스트를 로드합니다.
     * @param language 설정할 언어
     */
    public void setLanguage(Language language) {
        this.currentLanguage = language;
        loadTextsForCurrentLanguage();
        startNewGame();
    }

    /**
     * 현재 설정된 언어를 반환합니다.
     * @return 현재 언어
     */
    public Language getCurrentLanguage() {
        return this.currentLanguage;
    }

    /**
     * 테마를 설정합니다.
     * @param theme 설정할 테마
     */
    public void setTheme(Theme theme) {
        this.currentTheme = theme;
    }

    /**
     * 현재 설정된 테마를 반환합니다.
     * @return 현재 테마
     */
    public Theme getCurrentTheme() {
        return this.currentTheme;
    }

    /**
     * 게임을 초기화하고 새로운 게임을 시작합니다.
     * 
     * 중복 없이 10개 문장을 미리 선택하고
     * 기존 진행 중이던 게임의 모든 데이터가 리셋됩니다.
     */
    public void startNewGame() {
        resetGameState();
        
        // 문장연습 모드에 대한 초기화만 담당
        setCurrentGameMode(GameMode.SENTENCE);
        selectGameTexts();
        
        if (!selectedGameTexts.isEmpty()) {
            currentGameTextIndex = 0;
            currentPracticeText = selectedGameTexts.get(currentGameTextIndex);
        } else {
            currentPracticeText = "연습할 문장을 불러올 수 없습니다. 파일을 확인해 주세요.";
        }
    }
    
    /**
     * 게임용으로 중복 없는 10개 문장을 미리 선택합니다.
     * 전체 문장 목록에서 랜덤하게 섞어서 최대 10개를 선택합니다.
     */
    private void selectGameTexts() {
        selectedGameTexts.clear();
        
        if (practiceTexts == null || practiceTexts.isEmpty()) {
            return;
        }
        
        // 전체 문장 목록을 복사해서 섞기
        List<String> shuffledTexts = new ArrayList<>(practiceTexts);
        Collections.shuffle(shuffledTexts, random);
        
        // 최대 10개 또는 전체 문장 수만큼 선택
        int selectCount = Math.min(SENTENCES_PER_GAME, shuffledTexts.size());
        for (int i = 0; i < selectCount; i++) {
            selectedGameTexts.add(shuffledTexts.get(i));
        }
    }

    /**
     * 장문 텍스트를 문장 단위로 분리합니다.
     * 마침표, 느낌표, 물음표를 기준으로 문장을 나누고 빈 문장은 제거합니다.
     * @param longText 분리할 장문 텍스트
     */
    private void splitLongTextIntoSentences(String longText) {
        longTextSentences.clear();
        currentLongTextSentenceIndex = 0;
        originalLongText = longText != null ? longText : "";  // 원본 장문 저장
        
        if (longText == null || longText.trim().isEmpty()) {
            return;
        }
        
        // 먼저 문단별로 분리한 후, 각 문단 내에서 문장 분리
        String[] paragraphs = longText.split("\n\n+");
        List<String> allSentences = new ArrayList<>();
        
        for (String paragraph : paragraphs) {
            String trimmedParagraph = paragraph.trim();
            if (!trimmedParagraph.isEmpty()) {
                // 각 문단 내에서 문장 분리
                String[] sentences = trimmedParagraph.split("(?<=[.!?])\\s+");
                for (String sentence : sentences) {
                    String trimmedSentence = sentence.trim();
                    if (!trimmedSentence.isEmpty()) {
                        allSentences.add(trimmedSentence);
                    }
                }
            }
        }
        
        // 분리된 문장들을 배열로 변환
        String[] sentences = allSentences.toArray(new String[0]);
        
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                longTextSentences.add(trimmed);
            }
        }
        
        // 분리된 문장이 없으면 원본 텍스트를 그대로 추가
        if (longTextSentences.isEmpty()) {
            longTextSentences.add(longText.trim());
        }
    }

    /**
     * 다음 문장을 설정합니다.
     * 미리 선택된 10개 문장 중 다음 문장으로 이동
     * 문장만 변경하고 게임 진행 상태(타수, 시간)는 유지됩니다.
     */
    public void setNewPracticeText() {
        // 현재 문장의 결과를 먼저 기록
        double currentAccuracy = getAccuracy();
        double currentWPM = getAverageWPM();
        sentenceAccuracies.add(currentAccuracy);
        sentenceWPMs.add(currentWPM);
        completedInputTexts.add(currentInputText);

        // 현재 문장 관련 상태 초기화
        currentTypedChars = 0;
        correctChars = 0;
        currentInputText = "";
        lastProcessedInput = "";

        if (currentGameMode == GameMode.LONG_TEXT) {
            // 장문연습: 다음 문장으로 이동
            currentLongTextSentenceIndex++;
            
            // 모든 문장 완료 확인
            if (currentLongTextSentenceIndex >= longTextSentences.size()) {
                gameCompleted = true;
                gameStarted = false;
                currentPracticeText = "장문 입력을 완료했습니다!";
                return;
            }
            
            // 다음 문장 설정
            currentPracticeText = longTextSentences.get(currentLongTextSentenceIndex);

        } else { // SENTENCE 모드
            // 문장 번호와 인덱스 증가
            currentSentenceNumber++;
            currentGameTextIndex++;
            
            // 10개 문장 완료 확인
            if (currentSentenceNumber > SENTENCES_PER_GAME || currentGameTextIndex >= selectedGameTexts.size()) {
                gameCompleted = true;
                gameStarted = false;
                currentPracticeText = "10개 문장 입력을 모두 완료했습니다!";
                return;
            }
            
            // 다음 문장 설정 (미리 선택된 문장 목록에서)
            if (currentGameTextIndex < selectedGameTexts.size()) {
                currentPracticeText = selectedGameTexts.get(currentGameTextIndex);
            }
        }
    }
    
    /**
     * 게임을 시작합니다.
     * 외부에서 호출되는 메서드로, 게임 상태를 초기화하고 시작 준비를 합니다.
     */
    public void startGame() {
        startNewGame();
    }
    
    /**
     * 게임 상태를 초기화합니다.
     * 모든 카운터와 플래그를 초기값으로 되돌립니다.
     */
    private void resetGameState() {
        currentTypedChars = 0;
        correctChars = 0;
        totalCharacters = 0;
        startTime = null;
        gameStarted = false;
        gameCompleted = false;           // 게임 완료 상태 리셋
        currentSentenceNumber = 1;       // 문장 번호 리셋
        typingWindow.clear();
        lastProcessedInput = "";
        currentInputText = "";
        
        // 문장별 결과 추적 리스트 초기화
        sentenceAccuracies.clear();
        sentenceWPMs.clear();
        completedInputTexts.clear();
        // 완료 상태 추적 제거
        
        // 게임용 문장 선택 관련 초기화
        selectedGameTexts.clear();
        currentGameTextIndex = 0;

        // 장문연습 관련 초기화
        longTextSentences.clear();
        currentLongTextSentenceIndex = 0;
        originalLongText = "";
    }
    
    /**
     * 첫 입력 시 게임 타이머를 시작합니다.
     * 게임이 이미 시작된 경우에는 호출되지 않습니다.
     */
    private void startGameIfNeeded() {
        if (!gameStarted) {
            startTime = Instant.now();
            gameStarted = true;
        }
    }

    /**
     * 현재 연습 중인 문장을 반환합니다.
     * @return 현재 연습 문장
     */
    public String getCurrentPracticeText() {
        return currentPracticeText;
    }

    /**
     * 게임이 완료되었는지 확인합니다. (10개 문장 모두 완료)
     * @return 게임 완료 여부
     */
    public boolean isGameCompleted() {
        return gameCompleted;
    }

    /**
     * 현재 문장 번호를 반환합니다. (1~10)
     * @return 현재 문장 번호
     */
    public int getCurrentSentenceNumber() {
        if (currentGameMode == GameMode.LONG_TEXT) {
            return currentLongTextSentenceIndex + 1;
        }
        return currentSentenceNumber;
    }

    /**
     * 총 문장 개수를 반환합니다.
     * 문장연습: 10개, 장문연습: 분리된 문장 개수
     * @return 총 문장 개수
     */
    public int getTotalSentences() {
        if (currentGameMode == GameMode.LONG_TEXT) {
            return longTextSentences.size();
        }
        return SENTENCES_PER_GAME;
    }

    /**
     * 사용자 입력을 처리하고 정확도를 계산합니다.
     * 정규화를 통해 다양한 입력 형태를 일관되게 처리합니다.
     * @param typedText 사용자가 입력한 텍스트
     */
    public void processInput(String typedText) {
        // 게임 시작 (첫 입력 시)
        startGameIfNeeded();
        
        // 입력 텍스트 정규화
        String normalizedInput = normalizeText(typedText);
        String normalizedTarget = normalizeText(currentPracticeText);
        
        // 현재 입력 텍스트 저장
        currentInputText = normalizedInput;
        
        // 입력 길이 계산
        currentTypedChars = normalizedInput.length();
        
        // 올바른 문자 수 계산
        correctChars = 0;
        int minLength = Math.min(normalizedInput.length(), normalizedTarget.length());
        for (int i = 0; i < minLength; i++) {
            if (normalizedInput.charAt(i) == normalizedTarget.charAt(i)) {
                correctChars++;
            }
        }
        

        
        // 타이핑 이벤트 처리 (실시간 타수 계산용)
        processTypingEvents(lastProcessedInput, normalizedInput);
        lastProcessedInput = normalizedInput;
    }
    
    /**
     * 타이핑 이벤트를 처리하여 슬라이딩 윈도우를 업데이트합니다.
     * @param oldInput 이전 입력
     * @param newInput 새로운 입력
     */
    private void processTypingEvents(String oldInput, String newInput) {
        if (newInput.length() > oldInput.length()) {
            // 새로운 문자가 입력됨
            int newChars = newInput.length() - oldInput.length();
            
            Instant currentTime = Instant.now();
            typingWindow.addLast(new TypingEvent(currentTime, newChars));
            totalCharacters += newChars;
            
            // 오래된 이벤트 제거 (5초 이상 된 이벤트)
            while (!typingWindow.isEmpty()) {
                TypingEvent firstEvent = typingWindow.peekFirst();
                if (currentTime.minusSeconds(WINDOW_SEC).isAfter(firstEvent.time)) {
                    typingWindow.removeFirst();
                } else {
                    break;
                }
            }
        }
    }
    
    /**
     * 텍스트를 정규화합니다.
     * @param text 정규화할 텍스트
     * @return 정규화된 텍스트
     */
    private String normalizeText(String text) {
        if (text == null) return "";
        return Normalizer.normalize(text.trim(), Normalizer.Form.NFC);
    }
    
    /**
     * 타수 업데이트를 위한 메서드입니다.
     * 주기적으로 호출되어 슬라이딩 윈도우를 정리합니다.
     */
    public void updateWPM() {
        if (startTime == null) return;
        
        Instant currentTime = Instant.now();
        // 5초 이상 된 오래된 이벤트들 제거
        while (!typingWindow.isEmpty()) {
            TypingEvent firstEvent = typingWindow.peekFirst();
            if (currentTime.minusSeconds(WINDOW_SEC).isAfter(firstEvent.time)) {
                typingWindow.removeFirst();
            } else {
                break;
            }
        }
    }
    
    /**
     * 분당 문자수(CPM)를 반환합니다.
     * @return CPM 값
     */
    public double getCPM() {
        return getRealTimeWPM();
    }
    
    /**
     * 실시간 타수를 계산합니다 (슬라이딩 윈도우 기반).
     * @return 분당 문자수(CPM)
     */
    public double getRealTimeWPM() {
        if (typingWindow.isEmpty() || startTime == null) {
            return 0.0;
        }
        
        // 슬라이딩 윈도우 내의 문자 수 계산
        int recentChars = 0;
        for (TypingEvent event : typingWindow) {
            recentChars += event.syllableCount;
        }
        
        if (recentChars == 0) {
            return 0.0;
        }
        
        // 실제 윈도우 크기 계산 (최대 5초, 실제로는 더 짧을 수 있음)
        Instant currentTime = Instant.now();
        Instant oldestEventTime = typingWindow.peekFirst().time;
        double windowSeconds = Math.max(1.0, 
            currentTime.toEpochMilli() - oldestEventTime.toEpochMilli()) / 1000.0;
        
        // CPM 계산: (문자 수 / 시간(초)) * 60
        return (recentChars / windowSeconds) * 60.0;
    }
    
    /**
     * 평균 타수를 계산합니다.
     * @return 게임 시작부터 현재까지의 평균 분당 문자수(CPM)
     */
    public double getAverageWPM() {
        if (startTime == null || totalCharacters == 0) {
            return 0.0;
        }
        
        double elapsedMinutes = (Instant.now().toEpochMilli() - startTime.toEpochMilli()) / 60000.0;
        if (elapsedMinutes <= 0) {
            return 0.0;
        }
        
        // CPM = 총 문자 수 / 경과 시간(분)
        return totalCharacters / elapsedMinutes;
    }
    
    /**
     * 정확도를 백분율로 계산합니다.
     * @return 정확도 (0~100)
     */
    public double getAccuracy() {
        if (currentTypedChars == 0) {
            return 0.0;  // 아무것도 입력하지 않았으면 0%
        }
        
        return (double) correctChars / currentTypedChars * 100.0;
    }
    
    /**
     * 게임이 활성 상태인지 확인합니다.
     * @return 게임 활성 상태
     */
    public boolean isGameActive() {
        return gameStarted && !gameCompleted;
    }
    
    /**
     * 현재 입력된 문자 수를 반환합니다.
     * @return 현재 입력된 문자 수
     */
    public int getCurrentTypedChars() {
        return currentTypedChars;
    }
    
    /**
     * 올바르게 입력된 문자 수를 반환합니다.
     * @return 올바른 문자 수
     */
    public int getCorrectChars() {
        return correctChars;
    }

    /**
     * 현재 문장의 입력이 완료되었는지 확인합니다.
     * @param typedText 입력된 텍스트
     * @return 입력 완료 여부
     */
    public boolean isCurrentTextCompleted(String typedText) {
        if (typedText == null || currentPracticeText == null) {
            return false;
        }
        
        String normalizedInput = normalizeText(typedText);
        String normalizedTarget = normalizeText(currentPracticeText);
        
        if (allowLengthBasedCompletion) {
            // 길이 기반 완료: 길이만 맞으면 완료 (정확도와 무관하게 다음 문장으로 진행)
            return normalizedInput.length() >= normalizedTarget.length();
        } else {
            // 정확한 일치 필요
            return normalizedInput.equals(normalizedTarget);
        }
    }
    
    /**
     * 길이 기반 완료 허용 여부를 설정합니다.
     * @param allow 허용 여부
     */
    public void setAllowLengthBasedCompletion(boolean allow) {
        this.allowLengthBasedCompletion = allow;
    }
    
    /**
     * 길이 기반 완료 허용 여부를 반환합니다.
     * @return 허용 여부
     */
    public boolean isAllowLengthBasedCompletion() {
        return allowLengthBasedCompletion;
    }
    
    /**
     * 10개 문장의 평균 정확도를 계산합니다.
     * @return 평균 정확도
     */
    public double getAverageAccuracy() {
        if (sentenceAccuracies.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (Double accuracy : sentenceAccuracies) {
            sum += accuracy;
        }
        
        return sum / sentenceAccuracies.size();
    }
    
    /**
     * 10개 문장의 평균 타수를 계산합니다.
     * @return 평균 타수 (CPM)
     */
    public double getAverageWPMOfSentences() {
        if (sentenceWPMs.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (Double wpm : sentenceWPMs) {
            sum += wpm;
        }
        
        return sum / sentenceWPMs.size();
    }
    
    /**
     * 완료된 문장 개수를 반환합니다.
     * @return 완료된 문장 개수
     */
    public int getCompletedSentenceCount() {
        return sentenceAccuracies.size();
    }

    /**
     * 완료된 문장들의 입력 내용을 반환합니다.
     * @return 완료된 문장들의 입력 내용 리스트
     */
    public List<String> getCompletedInputTexts() {
        return new ArrayList<>(completedInputTexts);
    }
    
    /**
     * 입력이 올바른지 확인합니다.
     * @param typedText 입력된 텍스트
     * @return 입력 정확성 여부
     */
    public boolean isInputCorrect(String typedText) {
        if (typedText == null || currentPracticeText == null) {
            return false;
        }
        
        String normalizedInput = normalizeText(typedText);
        String normalizedTarget = normalizeText(currentPracticeText);
        
        return normalizedTarget.startsWith(normalizedInput);
    }

    /**
     * 장문연습에서 전체 문장 리스트를 반환합니다.
     * 문장연습에서는 빈 리스트를 반환합니다.
     * @return 장문의 모든 문장 리스트
     */
    public List<String> getLongTextSentences() {
        if (currentGameMode == GameMode.LONG_TEXT) {
            return new ArrayList<>(longTextSentences);  // 복사본 반환 (안전성)
        }
        return new ArrayList<>();  // 문장연습에서는 빈 리스트
    }

    /**
     * 장문연습에서 현재 문장의 인덱스를 반환합니다.
     * 문장연습에서는 -1을 반환합니다.
     * @return 현재 문장 인덱스 (0부터 시작)
     */
    public int getCurrentLongTextSentenceIndex() {
        if (currentGameMode == GameMode.LONG_TEXT) {
            return currentLongTextSentenceIndex;
        }
        return -1;  // 문장연습에서는 -1
    }

    /**
     * 장문연습에서 전체 원본 장문 텍스트를 반환합니다.
     * 문장연습에서는 빈 문자열을 반환합니다.
     * @return 원본 장문 텍스트
     */
    public String getFullLongText() {
        if (currentGameMode == GameMode.LONG_TEXT) {
            return originalLongText;
        }
        return "";  // 문장연습에서는 빈 문자열
    }

    /**
     * 게임 모드를 설정합니다.
     * @param gameMode 설정할 게임 모드
     */
    public void setGameMode(GameMode gameMode) {
        this.currentGameMode = gameMode;
        loadTextsForCurrentLanguage();
    }

    /**
     * 현재 설정된 게임 모드를 반환합니다.
     * @return 현재 게임 모드
     */
    public GameMode getCurrentGameMode() {
        return currentGameMode;
    }

    public void setCurrentGameMode(GameMode mode) {
        this.currentGameMode = mode;
    }

    /**
     * 현재 활성화된 게임을 중지합니다.
     * 타이머를 멈추고 게임 상태를 비활성화합니다.
     */
    public void stopGame() {
        gameStarted = false;
    }

    public void setGameCompleted(boolean gameCompleted) {
        this.gameCompleted = gameCompleted;
    }

    public void toggleTheme() {
        currentTheme = (currentTheme == Theme.LIGHT) ? Theme.DARK : Theme.LIGHT;
    }

    /**
     * 특정 장문으로 게임을 시작합니다.
     * @param longText 선택된 장문 텍스트 정보
     */
    public void startLongTextGame(LongText longText) {
        // 1. 게임 상태 초기화
        resetGameState();
        
        // 2. 게임 모드 설정
        setCurrentGameMode(GameMode.LONG_TEXT);
        
        // 3. 장문 텍스트 불러오기 및 분리
        String fullPath = "resources/" + longText.fileName();
        String content = textRepository.loadFullText(fullPath);
        
        this.originalLongText = content;
        splitLongTextIntoSentences(content);
        
        // 4. 첫 문장 설정
        if (longTextSentences != null && !longTextSentences.isEmpty()) {
            currentLongTextSentenceIndex = 0;
            currentPracticeText = longTextSentences.get(currentLongTextSentenceIndex);
        } else {
            currentPracticeText = "장문 텍스트를 불러올 수 없습니다.";
        }
    }

    public Difficulty getMoleGameDifficulty() {
        return moleGameDifficulty;
    }

    public void setMoleGameDifficulty(Difficulty moleGameDifficulty) {
        this.moleGameDifficulty = moleGameDifficulty;
    }
} 