package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MoleGameModel {
    private List<String> words;
    private Random random = new Random();

    private int score;
    private int timeLeft; // 초 단위
    private TextRepository textRepository = new TextRepository();

    /**
     * MoleGameModel 생성자
     * @param gameModel 전역 게임 모델
     */
    public MoleGameModel(GameModel gameModel) {
        loadWords(gameModel);
        startGame();
    }

    public void startGame() {
        score = 0;
        timeLeft = 60;
    }

    public void tick() {
        if (timeLeft > 0) {
            timeLeft--;
        }
    }

    public boolean isTimeUp() {
        return timeLeft <= 0;
    }

    public void moleHit() {
        score += 10; // 두더지를 잡으면 10점 추가
    }

    public int getScore() {
        return score;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    /**
     * 현재 언어 설정에 맞는 단어 목록을 불러옵니다.
     * @param gameModel 전역 게임 모델
     */
    private void loadWords(GameModel gameModel) {
        String fileName = gameModel.getCurrentLanguage().getMoleGameFileName();
        String resourcePath = "resources/" + fileName;
        this.words = textRepository.loadTexts(resourcePath);

        if (this.words == null || this.words.isEmpty()) {
            this.words = new ArrayList<>();
            this.words.add("error");
        }
    }

    public String getRandomWord() {
        if (words == null || words.isEmpty()) {
            return "단어없음";
        }
        return words.get(random.nextInt(words.size()));
    }
} 