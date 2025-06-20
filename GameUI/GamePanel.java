package GameUI;

import core.GameModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class GamePanel extends JPanel {
    private JTextPane targetTextPane;
    private JTextPane inputPane;
    private StyledDocument targetDoc;
    private StyledDocument inputDoc;
    private JLabel statusLabel;
    private JLabel wpmLabel;
    private JLabel progressLabel;
    private Timer statusUpdateTimer;
    private GameModel gameModel;
    private Runnable showMenuCallback;
    private boolean isProcessingInput = false;
    private boolean isComposing = false;
    private boolean isShowingCompleteDialog = false;

    private SimpleAttributeSet defaultStyle;
    private SimpleAttributeSet correctStyle;
    private SimpleAttributeSet incorrectStyle;
    private SimpleAttributeSet pendingStyle;
    private SimpleAttributeSet currentSentenceStyle;
    private SimpleAttributeSet fadedSentenceStyle;

    private JScrollPane targetScrollPane;
    private JScrollPane inputScrollPane;
    private JPanel bottomPanel;
    private JPanel topPanel;
    private JPanel centerPanel;

    public GamePanel(GameModel gameModel, Runnable showMenuCallback) {
        this.gameModel = gameModel;
        this.showMenuCallback = showMenuCallback;

        setLayout(new BorderLayout(10, 10));
        initializeStyles();
        createUIComponents();

        statusUpdateTimer = new Timer(300, e -> {
            if (gameModel.isGameActive()) {
                gameModel.updateWPM();
                updateStatusDisplay();
            }
        });
    }

    /**
     * 문장 연습 게임을 위해 패널을 리셋합니다.
     */
    public void resetForSentenceGame() {
        gameModel.startNewGame();
        setupLayoutForCurrentMode();
        refreshGameScreen();
        
        revalidate();
        repaint();
        inputPane.requestFocusInWindow();
    }
    
    /**
     * 장문 연습 게임을 위해 패널을 리셋합니다.
     * 이 메서드는 모델의 상태를 변경하지 않고 UI만 갱신합니다.
     */
    public void resetForLongTextGame() {
        // gameModel.startLongTextGame()은 MainFrame에서 이미 호출됨
        setupLayoutForCurrentMode();
        refreshGameScreen();

        SwingUtilities.invokeLater(() -> {
            targetScrollPane.getVerticalScrollBar().setValue(0);
            if (targetTextPane.getDocument().getLength() > 0) {
                targetTextPane.setCaretPosition(0);
            }
        });
        
        revalidate();
        repaint();
        inputPane.requestFocusInWindow();
    }

    public void startGame() {
        resetForSentenceGame();
    }

    private void createUIComponents() {
        targetTextPane = new JTextPane();
        targetTextPane.setEditable(false);
        targetDoc = targetTextPane.getStyledDocument();
        targetScrollPane = new JScrollPane(targetTextPane);
        targetScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        targetScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        inputPane = new JTextPane();
        inputPane.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        inputDoc = inputPane.getStyledDocument();
        inputScrollPane = new JScrollPane(inputPane);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        addInputListeners();

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressLabel = new JLabel("문장: 1/10");
        progressLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        statusLabel = new JLabel("정확도: 100.0%");
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        wpmLabel = new JLabel("타수: 0타/분");
        wpmLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        
        JButton nextButton = new JButton("다음 문장");
        JButton menuButton = new JButton("메뉴로 돌아가기");

        nextButton.addActionListener(e -> {
            SoundUtils.playClickSound();
            if (gameModel.isGameCompleted()) {
                showGameCompleteDialog();
            } else {
                gameModel.setNewPracticeText();
                refreshGameScreen();
            }
        });
        
        menuButton.addActionListener(e -> {
            SoundUtils.playClickSound();
            stopTimerAndShowMenu();
        });

        bottomPanel.add(progressLabel);
        bottomPanel.add(statusLabel);
        bottomPanel.add(wpmLabel);
        bottomPanel.add(nextButton);
        bottomPanel.add(menuButton);

        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("연습할 문장"));
        centerPanel = new JPanel(new GridBagLayout());
    }

    private void setupLayoutForCurrentMode() {
        removeAll();
        if (gameModel.getCurrentGameMode() == GameModel.GameMode.LONG_TEXT) {
            setupLongTextModeLayout();
        } else {
            setupSentenceModeLayout();
        }
    }

    private void setupLongTextModeLayout() {
        targetTextPane.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        targetTextPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPane.setBorder(BorderFactory.createTitledBorder("여기에 입력하세요"));
        inputScrollPane.setPreferredSize(new Dimension(700, 100));

        add(targetScrollPane, BorderLayout.CENTER);

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(inputScrollPane, BorderLayout.NORTH);
        southContainer.add(bottomPanel, BorderLayout.SOUTH);

        add(southContainer, BorderLayout.SOUTH);
    }

    private void setupSentenceModeLayout() {
        targetTextPane.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        targetTextPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        targetScrollPane.setPreferredSize(new Dimension(700, 120));

        inputPane.setBorder(BorderFactory.createTitledBorder("여기에 입력하세요"));
        inputScrollPane.setPreferredSize(new Dimension(700, 120));

        topPanel.removeAll();
        centerPanel.removeAll();
        topPanel.add(targetScrollPane);
        centerPanel.add(inputScrollPane);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addInputListeners() {
        inputPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stopTimerAndShowMenu();
                }
            }
        });

        inputPane.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                if (event.getCommittedCharacterCount() < event.getText().getEndIndex()) {
                    isComposing = true;
                } else {
                    isComposing = false;
                    SwingUtilities.invokeLater(() -> validateAndHighlight());
                }
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {}
        });

        inputDoc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!isProcessingInput && !isComposing) {
                    SwingUtilities.invokeLater(() -> validateAndHighlight());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isProcessingInput && !isComposing) {
                    SwingUtilities.invokeLater(() -> validateAndHighlight());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    private void validateAndHighlight() {
        if (isProcessingInput) return;
        
        try {
            isProcessingInput = true;
            String inputText = inputPane.getText();
            String targetText = gameModel.getCurrentPracticeText();
            
            if (targetText != null) {
                gameModel.processInput(inputText);
                
                if (!inputText.isEmpty() && !statusUpdateTimer.isRunning()) {
                    statusUpdateTimer.start();
                }
                
                updateTargetTextHighlight(inputText, targetText);
                
                if (gameModel.isCurrentTextCompleted(inputText)) {
                    completeCurrentText();
                }
            }
        } catch (Exception e) {
            System.err.println("입력 검증 중 오류: " + e.getMessage());
        } finally {
            isProcessingInput = false;
        }
    }

    private void updateTargetTextHighlight(String inputText, String targetText) {
        if (gameModel.getCurrentGameMode() == GameModel.GameMode.LONG_TEXT) {
            updateLongTextHighlight(inputText, targetText);
        } else {
            updateNormalTextHighlight(inputText, targetText);
        }
    }

    private void updateNormalTextHighlight(String inputText, String targetText) {
        try {
            int inputLength = inputText.length();
            int targetLength = targetText.length();

            targetDoc.setCharacterAttributes(0, targetDoc.getLength(), defaultStyle, true);

            for (int i = 0; i < inputLength; i++) {
                if (i < targetLength) {
                    SimpleAttributeSet style = (inputText.charAt(i) == targetText.charAt(i)) ? 
                        correctStyle : incorrectStyle;
                    targetDoc.setCharacterAttributes(i, 1, style, false);
                }
            }
            
            if (inputLength > targetLength) {
                targetDoc.setCharacterAttributes(0, targetLength, incorrectStyle, true);
            }
        } catch (Exception e) {
            System.err.println("하이라이트 업데이트 중 오류: " + e.getMessage());
        }
    }

    private int lastProcessedSentenceIndex = -1;
    private String lastProcessedInput = "";

    private void updateLongTextHighlight(String inputText, String targetText) {
        try {
            List<String> allSentences = gameModel.getLongTextSentences();
            int currentIndex = gameModel.getCurrentLongTextSentenceIndex();

            if (allSentences.isEmpty()) return;

            // 전체 텍스트를 처음 한 번만 생성
            if (targetDoc.getLength() == 0) {
                StringBuilder fullText = new StringBuilder();
                for (int i = 0; i < allSentences.size(); i++) {
                    fullText.append(allSentences.get(i));
                    if (i < allSentences.size() - 1) {
                        fullText.append("\n");
                    }
                }
                targetDoc.insertString(0, fullText.toString(), defaultStyle);
                lastProcessedSentenceIndex = -1; // 새 텍스트이므로 전체 재처리 필요
            }

            // 문장이 바뀌었거나 처음 처리하는 경우에만 전체 스타일 업데이트
            boolean needFullUpdate = (currentIndex != lastProcessedSentenceIndex);
            
            if (needFullUpdate) {
                updateAllSentenceStyles(allSentences, currentIndex);
                lastProcessedSentenceIndex = currentIndex;
            }

            // 현재 문장의 입력 하이라이트만 업데이트 (성능 최적화)
            if (!inputText.equals(lastProcessedInput)) {
                updateCurrentSentenceHighlight(inputText, targetText, allSentences, currentIndex);
                lastProcessedInput = inputText;
            }
            
            // 스크롤 처리는 입력이 비어있을 때만
            if (inputText.isEmpty() && needFullUpdate) {
                scrollToCurrentSentence(allSentences, currentIndex);
            }
        } catch (Exception e) {
            System.err.println("장문 하이라이트 업데이트 중 오류: " + e.getMessage());
        }
    }

    private void updateAllSentenceStyles(List<String> allSentences, int currentIndex) {
        int startPos = 0;
        List<String> completedInputs = gameModel.getCompletedInputTexts();
        
        for (int i = 0; i < allSentences.size(); i++) {
            String sentence = allSentences.get(i);
            int length = sentence.length();

            if (i < currentIndex) {
                // 완료된 문장들
                if (i < completedInputs.size()) {
                    highlightCompletedSentence(startPos, completedInputs.get(i), sentence);
                } else {
                    targetDoc.setCharacterAttributes(startPos, length, defaultStyle, true);
                }
            } else if (i == currentIndex) {
                // 현재 문장
                targetDoc.setCharacterAttributes(startPos, length, currentSentenceStyle, true);
            } else {
                // 미래 문장들
                targetDoc.setCharacterAttributes(startPos, length, fadedSentenceStyle, true);
            }

            startPos += length + 1;
        }
    }

    private void updateCurrentSentenceHighlight(String inputText, String targetText, 
                                              List<String> allSentences, int currentIndex) {
        // 현재 문장의 시작 위치 계산
        int currentSentenceStart = 0;
        for (int i = 0; i < currentIndex && i < allSentences.size(); i++) {
            currentSentenceStart += allSentences.get(i).length() + 1;
        }

        // 현재 문장 전체를 기본 스타일로 설정
        int targetLength = targetText.length();
        targetDoc.setCharacterAttributes(currentSentenceStart, targetLength, currentSentenceStyle, true);

        // 입력된 부분만 하이라이트 처리
        int inputLength = Math.min(inputText.length(), targetLength);
        if (inputLength > 0) {
            // 정확한 부분과 틀린 부분을 구분하여 일괄 처리
            int correctEnd = 0;
            for (int i = 0; i < inputLength; i++) {
                if (inputText.charAt(i) == targetText.charAt(i)) {
                    correctEnd = i + 1;
                } else {
                    break;
                }
            }
            
            // 정확한 부분 하이라이트
            if (correctEnd > 0) {
                targetDoc.setCharacterAttributes(currentSentenceStart, correctEnd, correctStyle, false);
            }
            
            // 틀린 부분 하이라이트
            if (correctEnd < inputLength) {
                targetDoc.setCharacterAttributes(currentSentenceStart + correctEnd, 
                    inputLength - correctEnd, incorrectStyle, false);
            }
        }

        // 입력이 타겟보다 긴 경우 전체를 틀린 것으로 표시
        if (inputText.length() > targetLength) {
            targetDoc.setCharacterAttributes(currentSentenceStart, targetLength, incorrectStyle, true);
        }
    }

    private void scrollToCurrentSentence(List<String> allSentences, int currentIndex) {
        int currentSentenceStart = 0;
        for (int i = 0; i < currentIndex && i < allSentences.size(); i++) {
            currentSentenceStart += allSentences.get(i).length() + 1;
        }

        final int currentPos = currentSentenceStart;
        SwingUtilities.invokeLater(() -> {
            try {
                targetTextPane.setCaretPosition(currentPos);
                targetTextPane.getCaret().setVisible(false);
                
                Rectangle2D rect2D = targetTextPane.modelToView2D(currentPos);
                if (rect2D != null) {
                    Rectangle rect = rect2D.getBounds();
                    rect.y = Math.max(0, rect.y - 50);
                    targetTextPane.scrollRectToVisible(rect);
                }
            } catch (BadLocationException e) {
                targetScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }

    private void highlightCompletedSentence(int startPos, String completedInput, String targetSentence) {
        int minLength = Math.min(completedInput.length(), targetSentence.length());
        for (int j = 0; j < minLength; j++) {
            SimpleAttributeSet style = (completedInput.charAt(j) == targetSentence.charAt(j)) ? 
                correctStyle : incorrectStyle;
            targetDoc.setCharacterAttributes(startPos + j, 1, style, false);
        }
        
        if (targetSentence.length() > completedInput.length()) {
            targetDoc.setCharacterAttributes(startPos + completedInput.length(), 
                targetSentence.length() - completedInput.length(), incorrectStyle, true);
        } else if (completedInput.length() > targetSentence.length()) {
            targetDoc.setCharacterAttributes(startPos, targetSentence.length(), incorrectStyle, true);
        }
    }

    private void completeCurrentText() {
        try {
            SoundUtils.playSuccessSound();
            gameModel.setNewPracticeText();

            if (gameModel.isGameCompleted()) {
                showGameCompleteDialog();
            } else {
                refreshGameScreen();
            }
        } catch (Exception e) {
            System.err.println("완료 처리 중 오류: " + e.getMessage());
        }
    }

    private void refreshGameScreen() {
        if (statusUpdateTimer.isRunning()) {
            statusUpdateTimer.stop();
        }

        try {
            if (gameModel.getCurrentGameMode() == GameModel.GameMode.LONG_TEXT) {
                targetDoc.remove(0, targetDoc.getLength());
                // 캐시 초기화 - 새로운 게임이므로 전체 재처리 필요
                lastProcessedSentenceIndex = -1;
                lastProcessedInput = "";
                updateLongTextHighlight("", gameModel.getCurrentPracticeText());
                
                SwingUtilities.invokeLater(() -> {
                    targetScrollPane.getVerticalScrollBar().setValue(0);
                    targetTextPane.setCaretPosition(0);
                });
            } else {
                String targetText = gameModel.getCurrentPracticeText();
                targetDoc.remove(0, targetDoc.getLength());
                targetDoc.insertString(0, targetText, defaultStyle);
            }

            inputDoc.remove(0, inputDoc.getLength());
            inputPane.requestFocusInWindow();
            updateStatusDisplay();
            
            if (!statusUpdateTimer.isRunning()) {
                statusUpdateTimer.start();
            }
        } catch (BadLocationException e) {
            System.err.println("화면 새로고침 중 오류: " + e.getMessage());
        }
    }

    private void updateStatusDisplay() {
        int currentSentence = gameModel.getCurrentSentenceNumber();
        int totalSentences = gameModel.getTotalSentences();
        progressLabel.setText(String.format("문장: %d/%d", currentSentence, totalSentences));

        double accuracy = gameModel.getAccuracy();
        int typed = gameModel.getCurrentTypedChars();
        int correct = gameModel.getCorrectChars();
        statusLabel.setText(String.format("정확도: %.1f%% (%d/%d)", accuracy, correct, typed));

        double averageWPM = gameModel.getAverageWPM();
        wpmLabel.setText(String.format("타수: %.0f타/분", averageWPM));
    }

    private void stopTimerAndShowMenu() {
        if (statusUpdateTimer.isRunning()) {
            statusUpdateTimer.stop();
        }
        gameModel.stopGame();
        showMenuCallback.run();
    }

    public void applyTheme(GameModel.Theme theme) {
        ThemeManager.applyTheme(this, theme);
        updateStylesForTheme(theme);

        if (targetTextPane != null) {
            targetTextPane.setBackground(ThemeManager.getTextBackgroundColor(theme));
            targetTextPane.setForeground(ThemeManager.getForegroundColor(theme));
        }

        if (inputPane != null) {
            inputPane.setBackground(ThemeManager.getTextBackgroundColor(theme));
            inputPane.setForeground(ThemeManager.getForegroundColor(theme));
            inputPane.setCaretColor(ThemeManager.getForegroundColor(theme));
        }

        refreshTextStyles();
    }

    private void initializeStyles() {
        defaultStyle = new SimpleAttributeSet();
        correctStyle = new SimpleAttributeSet();
        incorrectStyle = new SimpleAttributeSet();
        pendingStyle = new SimpleAttributeSet();
        currentSentenceStyle = new SimpleAttributeSet();
        fadedSentenceStyle = new SimpleAttributeSet();
    }

    private void updateStylesForTheme(GameModel.Theme theme) {
        Color foregroundColor = ThemeManager.getForegroundColor(theme);
        Color backgroundColor = ThemeManager.getBackgroundColor(theme);

        StyleConstants.setForeground(defaultStyle, foregroundColor);
        StyleConstants.setBackground(defaultStyle, backgroundColor);

        StyleConstants.setForeground(correctStyle, new Color(0, 100, 200));
        StyleConstants.setBackground(correctStyle, backgroundColor);
        StyleConstants.setBold(correctStyle, true);

        StyleConstants.setForeground(incorrectStyle, new Color(200, 0, 0));
        StyleConstants.setBackground(incorrectStyle, backgroundColor);
        StyleConstants.setBold(incorrectStyle, true);

        StyleConstants.setForeground(pendingStyle, 
            theme == GameModel.Theme.DARK ? new Color(160, 160, 160) : Color.GRAY);
        StyleConstants.setBackground(pendingStyle, backgroundColor);

        StyleConstants.setForeground(currentSentenceStyle, foregroundColor);
        StyleConstants.setBackground(currentSentenceStyle, backgroundColor);
        StyleConstants.setBold(currentSentenceStyle, true);

        StyleConstants.setForeground(fadedSentenceStyle, 
            theme == GameModel.Theme.DARK ? new Color(120, 120, 120) : new Color(150, 150, 150));
        StyleConstants.setBackground(fadedSentenceStyle, backgroundColor);
    }

    private void refreshTextStyles() {
        if (targetTextPane != null && targetDoc != null) {
            try {
                String inputText = inputPane != null ? inputPane.getText() : "";
                String targetText = gameModel.getCurrentPracticeText();
                if (targetText != null && !targetText.isEmpty()) {
                    updateTargetTextHighlight(inputText, targetText);
                }
            } catch (Exception e) {
                System.err.println("텍스트 스타일 새로고침 중 오류: " + e.getMessage());
            }
        }
    }

    private void showGameCompleteDialog() {
        if (isShowingCompleteDialog) return;
        isShowingCompleteDialog = true;

        try {
            if (statusUpdateTimer.isRunning()) {
                statusUpdateTimer.stop();
            }

            String message = createCompletionMessage();

            int choice = JOptionPane.showConfirmDialog(this, message, "게임 완료",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                SoundUtils.playClickSound();
                startGame();
            } else {
                SoundUtils.playClickSound();
                stopTimerAndShowMenu();
            }
        } finally {
            isShowingCompleteDialog = false;
        }
    }

    private String createCompletionMessage() {
        if (gameModel.getCurrentGameMode() == GameModel.GameMode.SENTENCE) {
            double averageAccuracy = gameModel.getAverageAccuracy();
            double averageWPM = gameModel.getAverageWPMOfSentences();
            int completedCount = gameModel.getCompletedSentenceCount();
            return String.format(
                    "🎉 문장연습 완료! 🎉\n\n" +
                    "📊 10개 문장 평균 결과:\n" +
                    "• 완료된 문장: %d개\n" +
                    "• 평균 정확도: %.1f%%\n" +
                    "• 평균 타수: %.0f타/분 (CPM)\n\n" +
                    "🏆 모든 문장을 완료하셨습니다!\n" +
                    "다시 게임하시겠습니까?",
                    completedCount, averageAccuracy, averageWPM);
        } else {
            double finalAccuracy = gameModel.getAccuracy();
            double finalWPM = gameModel.getAverageWPM();
            int totalSentences = gameModel.getTotalSentences();
            return String.format(
                    "🎉 장문연습 완료! 🎉\n\n" +
                    "📊 최종 결과:\n" +
                    "• 완료한 문장 수: %d개\n" +
                    "• 최종 정확도: %.1f%%\n" +
                    "• 최종 타수: %.0f타/분 (CPM)\n\n" +
                    "🏆 장문의 모든 문장을 완료하셨습니다!\n" +
                    "다시 게임하시겠습니까?",
                    totalSentences, finalAccuracy, finalWPM);
        }
    }
} 