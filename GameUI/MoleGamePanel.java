package GameUI;

import core.GameModel;
import core.MoleGameModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MoleGamePanel extends JPanel {

    private MoleGameModel moleGameModel;
    private GameModel gameModel;
    private Timer gameLoopTimer;
    private Timer gameTimer;
    private Random random = new Random();

    private JPanel gameAreaPanel; // 두더지가 나타날 영역
    private JTextField inputField;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private Runnable showMenuCallback;
    private JLabel countdownLabel; // 카운트다운을 위한 라벨

    public MoleGamePanel(GameModel gameModel, Runnable showMenuCallback) {
        this.gameModel = gameModel;
        this.showMenuCallback = showMenuCallback;
        setLayout(new BorderLayout());
        this.moleGameModel = new MoleGameModel(gameModel);

        // 게임 영역 초기화 - OverlayLayout 사용
        gameAreaPanel = new JPanel() {
            // 자식 컴포넌트들이 중앙에 오도록 정렬
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        OverlayLayout overlay = new OverlayLayout(gameAreaPanel);
        gameAreaPanel.setLayout(overlay);

        // 카운트다운 라벨 초기화
        countdownLabel = new JLabel("", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("맑은 고딕", Font.BOLD, 150));
        countdownLabel.setForeground(Color.WHITE);
        countdownLabel.setAlignmentX(0.5f); // 중앙 정렬
        countdownLabel.setAlignmentY(0.5f);
        countdownLabel.setVisible(false);

        // 두더지가 나타날 패널
        JPanel moleFieldPanel = new JPanel();
        moleFieldPanel.setLayout(null);
        moleFieldPanel.setOpaque(false); // 아래 패널이 보이도록 투명 처리

        // 배경색을 가진 패널
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(139, 69, 19)); // 흙색 배경

        gameAreaPanel.add(countdownLabel);
        gameAreaPanel.add(moleFieldPanel);
        gameAreaPanel.add(backgroundPanel);


        // 하단 패널 (입력 및 정보) 초기화
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        scoreLabel = new JLabel("점수: 0");
        scoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        timeLabel = new JLabel("시간: 60초");
        timeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        
        inputField = new JTextField(20);
        inputField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        
        JButton backButton = new JButton("메뉴로 돌아가기");
        UIUtils.addButtonHoverEffect(backButton);
        backButton.addActionListener(e -> {
            stopGame();
            showMenuCallback.run();
        });

        bottomPanel.add(scoreLabel);
        bottomPanel.add(inputField);
        bottomPanel.add(timeLabel);
        bottomPanel.add(backButton);

        add(gameAreaPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 일정 시간마다 두더지 웨이브를 생성하는 게임 루프
        gameLoopTimer = new Timer(1500, e -> spawnMoleWave());

        // 1초마다 게임 상태를 업데이트하는 전체 타이머
        gameTimer = new Timer(1000, e -> {
            moleGameModel.tick();
            updateTimeDisplay();
            if (moleGameModel.isTimeUp()) {
                gameOver();
            }
        });

        // 입력 필드에서 엔터 키 이벤트 처리
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String typedWord = inputField.getText().trim();
                checkWord(typedWord);
                inputField.setText(""); // 입력 필드 초기화
            }
        });
    }
    
    public void startGame() {
        // 게임 컴포넌트 초기화
        getMoleFieldPanel().removeAll();
        getMoleFieldPanel().repaint();
        inputField.setText("");
        inputField.setEnabled(false); // 카운트다운 중에는 비활성화

        // 게임 시작 시점에 현재 언어 설정에 맞는 새 모델을 생성
        this.moleGameModel = new MoleGameModel(gameModel);
        
        updateScoreDisplay();
        updateTimeDisplay();

        // 카운트다운 시작
        startCountdown();
    }

    private void startActualGame() {
        inputField.setEnabled(true);
        if (!gameLoopTimer.isRunning()) {
            gameLoopTimer.setInitialDelay(500); // 카운트다운 후 즉시 시작
            gameLoopTimer.start();
        }
        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
        inputField.requestFocusInWindow();
    }

    private void startCountdown() {
        countdownLabel.setVisible(true);
        
        Timer countdownTimer = new Timer(1000, null);
        countdownTimer.addActionListener(new ActionListener() {
            private int count = 3;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count > 0) {
                    countdownLabel.setText(String.valueOf(count));
                    count--;
                } else if (count == 0) {
                    countdownLabel.setFont(new Font("맑은 고딕", Font.BOLD, 100));
                    countdownLabel.setText("Start!");
                    count--;
                } else {
                    countdownTimer.stop();
                    countdownLabel.setVisible(false);
                    // 폰트 크기 원상 복구
                    countdownLabel.setFont(new Font("맑은 고딕", Font.BOLD, 150));
                    startActualGame();
                }
            }
        });
        // 즉시 첫 카운트 실행
        countdownTimer.setInitialDelay(0);
        countdownTimer.start();
    }

    public void stopGame() {
        if (gameLoopTimer.isRunning()) {
            gameLoopTimer.stop();
        }
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    private void spawnMoleWave() {
        GameModel.Difficulty difficulty = gameModel.getMoleGameDifficulty();
        int maxMoles;
        int molesToSpawn;

        switch (difficulty) {
            case EASY:
                maxMoles = 4;
                molesToSpawn = 1;
                break;
            case HARD:
                maxMoles = 8;
                molesToSpawn = random.nextInt(2) + 2; // 2~3마리
                break;
            default: // NORMAL
                maxMoles = 6;
                molesToSpawn = random.nextInt(2) + 1; // 1~2마리
                break;
        }

        // 화면에 두더지가 너무 많으면 웨이브를 건너뜀
        if (getMoleFieldPanel().getComponentCount() > maxMoles -1) {
            return;
        }

        for (int i = 0; i < molesToSpawn; i++) {
            spawnMole();
        }

        // 남은 시간에 따라 다음 웨이브까지의 딜레이를 조절하여 난이도 상승
        int timeLeft = moleGameModel.getTimeLeft();
        int baseDelay, randomDelay;

        switch (difficulty) {
            case EASY:
                baseDelay = (timeLeft > 30) ? 1800 : 1500;
                randomDelay = 800;
                break;
            case HARD:
                baseDelay = (timeLeft > 40) ? 500 : (timeLeft > 20 ? 300 : 200);
                randomDelay = 400;
                break;
            default: // NORMAL
                baseDelay = (timeLeft > 40) ? 1200 : (timeLeft > 20 ? 800 : 500);
                randomDelay = 700;
                break;
        }
        int nextDelay = baseDelay + random.nextInt(randomDelay);
        gameLoopTimer.setDelay(nextDelay);
    }

    private void spawnMole() {
        String word = moleGameModel.getRandomWord();
        if (word == null) return; // 사용할 단어가 없으면 중단

        JLabel moleLabel = new JLabel(word, SwingConstants.CENTER);
        moleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        moleLabel.setOpaque(true);
        moleLabel.setBackground(Color.YELLOW);
        moleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        moleLabel.setName(word); // JLabel에 단어 정보를 저장

        JPanel moleField = getMoleFieldPanel();
        int panelWidth = moleField.getWidth();
        int panelHeight = moleField.getHeight();
        if (panelWidth <= 100 || panelHeight <= 40) return; // 패널 크기가 아직 설정되지 않음

        Rectangle newMoleBounds;
        boolean overlaps;
        int attempts = 0;
        int maxAttempts = 10; // 최대 10번 새로운 위치 탐색

        // 다른 두더지와 겹치지 않는 위치를 찾음
        do {
            int x = random.nextInt(panelWidth - 100);
            int y = random.nextInt(panelHeight - 40);
            newMoleBounds = new Rectangle(x, y, 100, 40);
            
            overlaps = false;
            for (Component comp : moleField.getComponents()) {
                if (comp.getBounds().intersects(newMoleBounds)) {
                    overlaps = true;
                    break;
                }
            }
            attempts++;
        } while (overlaps && attempts < maxAttempts);


        // 겹치지 않는 위치를 찾았을 경우에만 두더지 추가
        if (!overlaps) {
            moleLabel.setBounds(newMoleBounds);
            moleField.add(moleLabel);
            moleField.repaint();

            GameModel.Difficulty difficulty = gameModel.getMoleGameDifficulty();
            int visibilityTime;
            switch (difficulty) {
                case EASY: visibilityTime = 2800; break;
                case HARD: visibilityTime = 1600; break;
                default: visibilityTime = 2000; break;
            }

            // 난이도에 따라 N초 뒤에 사라지는 타이머
            Timer moleVisibilityTimer = new Timer(visibilityTime, e -> {
                if (moleLabel.getParent() != null) { // 아직 안 잡혔으면
                    moleField.remove(moleLabel);
                    moleField.repaint();
                }
            });
            moleVisibilityTimer.setRepeats(false);
            moleVisibilityTimer.start();
        }
    }

    private void checkWord(String typedWord) {
        JPanel moleField = getMoleFieldPanel();
        // 게임 영역에 있는 모든 두더지(JLabel)를 확인
        for (Component comp : moleField.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel moleLabel = (JLabel) comp;
                if (moleLabel.getName().equals(typedWord)) {
                    // 단어 일치! 두더지를 잡음
                    moleField.remove(moleLabel);
                    moleField.repaint();
                    // TODO: 점수 올리는 로직 추가
                    // System.out.println("잡았다! -> " + typedWord);
                    
                    moleGameModel.moleHit(); // 모델에 점수 증가 요청
                    updateScoreDisplay(); // 화면에 점수 업데이트
                    
                    return; // 한 마리만 잡음
                }
            }
        }
    }

    private void gameOver() {
        stopGame();
        inputField.setEnabled(false);
        
        int choice = JOptionPane.showOptionDialog(this,
                "게임 종료!\n\n최종 점수: " + moleGameModel.getScore(),
                "게임 오버",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"다시 하기", "메뉴로 돌아가기"},
                "다시 하기");

        if (choice == JOptionPane.YES_OPTION) {
            startGame();
        } else {
            showMenuCallback.run();
        }
    }

    private void updateScoreDisplay() {
        scoreLabel.setText("점수: " + moleGameModel.getScore());
    }

    private void updateTimeDisplay() {
        timeLabel.setText("시간: " + moleGameModel.getTimeLeft() + "초");
    }

    // OverlayLayout에서 실제 두더지가 나타날 패널을 가져옴
    private JPanel getMoleFieldPanel() {
        for(Component c : gameAreaPanel.getComponents()) {
            // moleFieldPanel은 이름이 없으므로 다른 컴포넌트(countdownLabel)와 구분
            if (c instanceof JPanel && c.isOpaque() == false) {
                return (JPanel) c;
            }
        }
        return null; // 비정상적인 경우
    }

    public void applyTheme(GameModel.Theme theme) {
        // 배경 패널의 색상만 변경
        for(Component c : gameAreaPanel.getComponents()) {
            if (c instanceof JPanel && c.isOpaque()) {
                 Color bgColor = theme == GameModel.Theme.DARK ? new Color(70, 40, 10) : new Color(139, 69, 19);
                 c.setBackground(bgColor);
                 break;
            }
        }
        // 하단 패널과 라벨들 테마 적용 (추후 구현)
    }
} 