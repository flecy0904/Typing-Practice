package GameUI;

import core.GameModel;
import javax.swing.*;
import java.awt.*;

/**
 * 타이핑 연습 게임의 설정 패널
 * 
 * 게임의 각종 설정을 변경할 수 있는 화면입니다.
 * 현재는 언어 선택 기능을 제공하며, 향후 추가 설정을 확장할 수 있습니다.
 * 
 * @author JAVA 중간 프로젝트
 * @version 1.0
 */
public class SettingsPanel extends JPanel {
    private JButton backButton;                          // 메인 메뉴로 돌아가기 버튼
    private JComboBox<GameModel.Language> languageComboBox; // 언어 선택 콤보박스
    private JComboBox<GameModel.Theme> themeComboBox;    // 테마 선택 콤보박스
    private GameModel gameModel;                         // 게임 로직 모델 참조
    private Runnable themeChangeCallback;                // 테마 변경 콜백

    /**
     * SettingsPanel 생성자
     * 
     * 설정 화면의 UI를 구성하고 언어 선택, 테마 선택 기능을 초기화합니다.
     * 
     * @param gameModel 게임 설정을 관리하는 모델 객체
     * @param showMenuCallback 메인 메뉴로 돌아가는 콜백 함수
     */
    public SettingsPanel(GameModel gameModel, Runnable showMenuCallback) {
        this(gameModel, showMenuCallback, null);
    }

    /**
     * SettingsPanel 생성자 (테마 변경 콜백 포함)
     * 
     * 설정 화면의 UI를 구성하고 언어 선택, 테마 선택 기능을 초기화합니다.
     * 
     * @param gameModel 게임 설정을 관리하는 모델 객체
     * @param showMenuCallback 메인 메뉴로 돌아가는 콜백 함수
     * @param themeChangeCallback 테마 변경 시 호출될 콜백 함수
     */
    public SettingsPanel(GameModel gameModel, Runnable showMenuCallback, Runnable themeChangeCallback) {
        this.gameModel = gameModel;
        this.themeChangeCallback = themeChangeCallback;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;  // 전체 폭 사용
        gbc.fill = GridBagConstraints.HORIZONTAL;      // 수평으로 확장
        gbc.insets = new Insets(10, 0, 10, 0);        // 위아래 여백 설정

        // 설정 페이지 타이틀
        JLabel settingsLabel = new JLabel("설정", SwingConstants.CENTER);
        settingsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 36));

        // 언어 선택 섹션 구성
        JPanel languagePanel = createLanguageSelectionPanel();

        // 테마 선택 섹션 구성
        JPanel themePanel = createThemeSelectionPanel();

        // 두더지 게임 난이도 선택 섹션 구성
        JPanel difficultyPanel = createDifficultySelectionPanel();

        // 메인 메뉴로 돌아가기 버튼
        backButton = new JButton("메인 메뉴로 돌아가기");
        backButton.setFont(new Font("맑은 고딕", Font.PLAIN, 24));
        backButton.addActionListener(e -> {
            SoundUtils.playClickSound();
            showMenuCallback.run();
        });
        UIUtils.addButtonHoverEffect(backButton);

        // 컴포넌트들을 레이아웃에 추가
        add(settingsLabel, gbc);
        add(Box.createVerticalStrut(30), gbc);  // 여백
        add(languagePanel, gbc);
        add(Box.createVerticalStrut(20), gbc);  // 여백
        add(themePanel, gbc);
        add(Box.createVerticalStrut(20), gbc);  // 여백
        add(difficultyPanel, gbc);
        add(Box.createVerticalStrut(30), gbc);  // 여백
        add(backButton, gbc);

        // 초기 테마 적용
        applyCurrentTheme();
    }

    /**
     * 언어 선택 패널을 생성합니다.
     * 
     * 콤보박스를 통해 한국어/영어를 선택할 수 있도록 구성되며,
     * 선택 변경 시 즉시 게임 모델에 반영됩니다.
     * 
     * @return 언어 선택 UI가 포함된 패널
     */
    private JPanel createLanguageSelectionPanel() {
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // 언어 선택 라벨
        JLabel languageLabel = new JLabel("언어 선택:");
        languageLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        
        // 언어 선택 콤보박스 생성 및 설정
        languageComboBox = new JComboBox<>(GameModel.Language.values());
        languageComboBox.setSelectedItem(gameModel.getCurrentLanguage());
        languageComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        
        // 콤보박스 표시 방식 커스터마이징 (언어 표시명 사용)
        languageComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof GameModel.Language) {
                    setText(((GameModel.Language) value).getDisplayName());
                }
                return this;
            }
        });
        
        // 언어 변경 이벤트 처리
        languageComboBox.addActionListener(e -> {
            SoundUtils.playClickSound();
            GameModel.Language selectedLanguage = (GameModel.Language) languageComboBox.getSelectedItem();
            if (selectedLanguage != null) {
                gameModel.setLanguage(selectedLanguage);
            }
        });
        
        UIUtils.addHoverEffect(languageComboBox);
        // 패널에 컴포넌트 추가
        languagePanel.add(languageLabel);
        languagePanel.add(languageComboBox);
        
        return languagePanel;
    }

    /**
     * 테마 선택 패널을 생성합니다.
     * 
     * 콤보박스를 통해 라이트 모드/다크 모드를 선택할 수 있도록 구성되며,
     * 선택 변경 시 즉시 게임 모델에 반영됩니다.
     * 
     * @return 테마 선택 UI가 포함된 패널
     */
    private JPanel createThemeSelectionPanel() {
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // 테마 선택 라벨
        JLabel themeLabel = new JLabel("테마 설정:");
        themeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        
        // 테마 선택 콤보박스 생성 및 설정
        themeComboBox = new JComboBox<>(GameModel.Theme.values());
        themeComboBox.setSelectedItem(gameModel.getCurrentTheme());
        themeComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        
        // 콤보박스 표시 방식 커스터마이징 (테마 표시명 사용)
        themeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof GameModel.Theme) {
                    setText(((GameModel.Theme) value).getDisplayName());
                }
                return this;
            }
        });
        
        // 테마 변경 이벤트 처리
        themeComboBox.addActionListener(e -> {
            SoundUtils.playClickSound();
            GameModel.Theme selectedTheme = (GameModel.Theme) themeComboBox.getSelectedItem();
            if (selectedTheme != null) {
                gameModel.setTheme(selectedTheme);
                applyCurrentTheme();
                if (themeChangeCallback != null) {
                    themeChangeCallback.run();
                }
            }
        });
        
        UIUtils.addHoverEffect(themeComboBox);
        // 패널에 컴포넌트 추가
        themePanel.add(themeLabel);
        themePanel.add(themeComboBox);
        
        return themePanel;
    }

    /**
     * 두더지 잡기 게임 난이도 선택 패널을 생성합니다.
     * @return 난이도 선택 UI가 포함된 패널
     */
    private JPanel createDifficultySelectionPanel() {
        JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel difficultyLabel = new JLabel("두더지 게임 난이도:");
        difficultyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));

        JComboBox<GameModel.Difficulty> difficultyComboBox = new JComboBox<>(GameModel.Difficulty.values());
        difficultyComboBox.setSelectedItem(gameModel.getMoleGameDifficulty());
        difficultyComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        
        difficultyComboBox.addActionListener(e -> {
            SoundUtils.playClickSound();
            GameModel.Difficulty selectedDifficulty = (GameModel.Difficulty) difficultyComboBox.getSelectedItem();
            if (selectedDifficulty != null) {
                gameModel.setMoleGameDifficulty(selectedDifficulty);
            }
        });
        
        UIUtils.addHoverEffect(difficultyComboBox);
        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(difficultyComboBox);

        return difficultyPanel;
    }

    /**
     * 현재 테마를 UI에 적용합니다.
     */
    private void applyCurrentTheme() {
        ThemeManager.applyTheme(this, gameModel.getCurrentTheme());
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