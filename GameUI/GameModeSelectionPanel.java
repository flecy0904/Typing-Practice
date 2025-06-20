package GameUI;

import core.GameModel;
import javax.swing.*;
import java.awt.*;

/**
 * 게임 모드(문장연습/장문연습)를 선택하는 UI 패널입니다.
 * 
 * @author JAVA 중간 프로젝트
 * @version 1.0
 */
public class GameModeSelectionPanel extends JPanel {

    public GameModeSelectionPanel(GameModel gameModel, MainFrame mainFrame) {

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        JLabel titleLabel = new JLabel("게임 모드 선택");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(new JLabel(" "), gbc); // 간격

        // 단문 연습 버튼
        JButton sentenceButton = createModeButton(GameModel.GameMode.SENTENCE);
        sentenceButton.addActionListener(e -> mainFrame.startGame(GameModel.GameMode.SENTENCE));
        gbc.gridy = 2;
        add(sentenceButton, gbc);

        // 장문 연습 버튼
        JButton longTextButton = createModeButton(GameModel.GameMode.LONG_TEXT);
        longTextButton.addActionListener(e -> {
            var longTexts = gameModel.getCurrentLanguage().getLongTexts();
            if (longTexts.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                        "현재 언어에 등록된 장문 텍스트가 없습니다.",
                        "알림", JOptionPane.INFORMATION_MESSAGE);
            } else {
                mainFrame.startLongTextGame(longTexts.get(0)); // 첫 번째 장문으로 게임 시작
            }
        });
        gbc.gridy = 3;
        add(longTextButton, gbc);

        // 두더지 잡기 게임 버튼
        JButton moleGameButton = createModeButton(GameModel.GameMode.MOLE_GAME);
        moleGameButton.addActionListener(e -> mainFrame.startGame(GameModel.GameMode.MOLE_GAME));
        gbc.gridy = 4;
        add(moleGameButton, gbc);

        gbc.gridy = 5;
        add(new JLabel(" "), gbc); // 간격

        // 하단 버튼들 (돌아가기)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton backButton = new JButton("메뉴로 돌아가기");
        backButton.addActionListener(e -> {
            SoundUtils.playClickSound();
            mainFrame.showMainMenu();
        });
        UIUtils.addButtonHoverEffect(backButton);

        bottomPanel.add(backButton);
        gbc.gridy = 6;
        add(bottomPanel, gbc);
    }

    private JButton createModeButton(GameModel.GameMode mode) {
        JButton button = new JButton(String.format("<html><center><h2>%s</h2><p style='width: 200px'>%s</p></center></html>",
                mode.getDisplayName(), mode.getDescription()));
        button.setPreferredSize(new Dimension(300, 100));
        button.addActionListener(e -> SoundUtils.playClickSound());
        UIUtils.addButtonHoverEffect(button);
        return button;
    }

    public void updateTheme() {
        // 테마 업데이트 로직 (필요 시 구현)
    }
} 