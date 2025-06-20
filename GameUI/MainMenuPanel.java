package GameUI;

import core.GameModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * 타이핑 연습 게임의 메인 메뉴 패널
 * 
 * 프로그램 시작 시 표시되는 초기 화면으로, 
 * 게임 시작, 설정, 종료 기능을 제공합니다.
 * 
 * @author JAVA 중간 프로젝트
 * @version 1.0
 */
public class MainMenuPanel extends JPanel {
    private JButton newGameButton;   // 게임 시작 버튼
    private JButton settingsButton;  // 설정 화면 이동 버튼
    private JButton quitButton;      // 프로그램 종료 버튼

    /**
     * MainMenuPanel 생성자
     * 
     * GridBagLayout을 사용하여 타이틀과 버튼들을 
     * 세로로 정렬된 형태로 배치합니다.
     */
    public MainMenuPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;  // 전체 폭 사용
        gbc.fill = GridBagConstraints.HORIZONTAL;      // 수평으로 확장
        gbc.insets = new Insets(10, 0, 10, 0);        // 위아래 여백 설정

        // 게임 타이틀 라벨
        JLabel titleLabel = new JLabel("Typing Practice", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 36));

        // 게임 시작 버튼
        newGameButton = new JButton("시작");
        newGameButton.setFont(new Font("맑은 고딕", Font.PLAIN, 24));
        UIUtils.addButtonHoverEffect(newGameButton);

        // 설정 버튼
        settingsButton = new JButton("설정");
        settingsButton.setFont(new Font("맑은 고딕", Font.PLAIN, 24));
        UIUtils.addButtonHoverEffect(settingsButton);

        // 프로그램 종료 버튼 (즉시 종료 기능 포함)
        quitButton = new JButton("종료");
        quitButton.setFont(new Font("맑은 고딕", Font.PLAIN, 24));
        UIUtils.addButtonHoverEffect(quitButton);
        quitButton.addActionListener(e -> {
            SoundUtils.playClickSound();
            System.exit(0);
        });

        // 컴포넌트들을 레이아웃에 순서대로 추가
        add(titleLabel, gbc);
        add(Box.createVerticalStrut(30), gbc); // 타이틀과 버튼 사이의 공간
        add(newGameButton, gbc);
        add(settingsButton, gbc);
        add(quitButton, gbc);
    }

    /**
     * 외부에서 "시작" 버튼에 액션 리스너를 추가할 수 있도록 하는 메서드
     * 
     * @param listener 버튼 클릭 시 실행될 액션 리스너
     */
    public void addNewGameListener(ActionListener listener) {
        newGameButton.addActionListener(e -> {
            SoundUtils.playClickSound();
            listener.actionPerformed(e);
        });
    }

    /**
     * 외부에서 "설정" 버튼에 액션 리스너를 추가할 수 있도록 하는 메서드
     * 
     * @param listener 버튼 클릭 시 실행될 액션 리스너
     */
    public void addSettingsListener(ActionListener listener) {
        settingsButton.addActionListener(e -> {
            SoundUtils.playClickSound();
            listener.actionPerformed(e);
        });
    }

    /**
     * 테마를 패널에 적용합니다.
     * 
     * @param theme 적용할 테마
     */
    public void applyTheme(GameModel.Theme theme) {
        ThemeManager.applyTheme(this, theme);
    }
} 