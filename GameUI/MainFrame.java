package GameUI;

import core.GameModel;
import javax.swing.*;
import java.awt.*;

/**
 * 타이핑 연습 게임의 메인 프레임 클래스
 * 
 * FadePanel을 사용하여 메인 메뉴, 게임 화면, 설정 화면 간의 
 * 부드러운 전환을 관리하는 최상위 GUI 컨테이너입니다.
 * 
 * @author JAVA 중간 프로젝트
 * @version 1.0
 */
public class MainFrame extends JFrame {
    private FadePanel fadePanel;        // 패널 전환 애니메이션을 위한 커스텀 패널
    private GamePanel gamePanel;
    private MoleGamePanel moleGamePanel;
    private MainMenuPanel mainMenuPanel;
    private SettingsPanel settingsPanel;
    private GameModeSelectionPanel gameModeSelectionPanel;
    private GameModel gameModel;

    /**
     * MainFrame 생성자
     * 
     * 윈도우를 초기화하고 모든 패널을 생성한 후 FadePanel으로 관리합니다.
     * 각 패널 간의 콜백 연결도 설정합니다.
     * 
     * @param gameModel 게임 로직을 담당하는 모델 객체
     */
    public MainFrame(GameModel gameModel) {
        // 윈도우 기본 설정
        setTitle("타이핑 연습");
        setSize(800, 600);
        setLocationRelativeTo(null);    // 화면 중앙에 위치
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);            // 크기 조절 불가

        fadePanel = new FadePanel();

        // 게임 모델 저장 및 각 패널 생성
        this.gameModel = gameModel;
        gamePanel = new GamePanel(gameModel, this::showMainMenu);
        moleGamePanel = new MoleGamePanel(gameModel, this::showMainMenu);
        mainMenuPanel = new MainMenuPanel();
        settingsPanel = new SettingsPanel(gameModel, this::showMainMenu, this::applyCurrentThemeToAllPanels);
        gameModeSelectionPanel = new GameModeSelectionPanel(gameModel, this);

        // FadePanel에 각 패널을 카드로 추가
        fadePanel.addCard(mainMenuPanel, "MainMenu");
        fadePanel.addCard(gameModeSelectionPanel, "GameModeSelection");
        fadePanel.addCard(gamePanel, "GamePanel");
        fadePanel.addCard(moleGamePanel, "MoleGame");
        fadePanel.addCard(settingsPanel, "Settings");

        // 메인 메뉴 패널의 버튼에 이벤트 리스너 연결
        mainMenuPanel.addNewGameListener(e -> showGameModeSelection());
        mainMenuPanel.addSettingsListener(e -> showSettingsPanel());

        // 메인 패널을 FadePanel에 추가
        add(fadePanel);
        
        // 프로그램 시작 시 메인 메뉴 표시
        showMainMenu();
        
        // 초기 테마 적용
        applyCurrentThemeToAllPanels();
        
        // 윈도우를 화면에 표시
        setVisible(true);
    }
    
    /**
     * 메인 메뉴 패널을 표시합니다.
     * 다른 패널에서 메뉴로 돌아올 때 호출됩니다.
     */
    public void showMainMenu() {
        fadePanel.showComponent("MainMenu");
    }

    /**
     * 게임 모드 선택 패널을 표시합니다.
     * 메인 메뉴에서 새 게임 버튼을 누를 때 호출됩니다.
     */
    public void showGameModeSelection() {
        fadePanel.showComponent("GameModeSelection");
    }

    /**
     * 선택된 게임 모드에 따라 게임을 시작합니다.
     *
     * @param mode 시작할 게임 모드
     */
    public void startGame(GameModel.GameMode mode) {
        if (mode == GameModel.GameMode.MOLE_GAME) {
            moleGamePanel.startGame();
            fadePanel.showComponent("MoleGame");
        } else {
            gameModel.setGameMode(mode);
            gamePanel.resetForSentenceGame();
            fadePanel.showComponent("GamePanel");
        }
    }

    /**
     * 장문 게임을 시작합니다.
     *
     * @param longText 선택한 장문
     */
    public void startLongTextGame(GameModel.LongText longText) {
        gameModel.startLongTextGame(longText);
        gamePanel.resetForLongTextGame();
        fadePanel.showComponent("GamePanel");
    }
    
    /**
     * 설정 패널을 표시합니다.
     * 언어 선택 등의 설정을 변경할 수 있습니다.
     */
    private void showSettingsPanel() {
        fadePanel.showComponent("Settings");
    }

    /**
     * 현재 설정된 테마를 전체 프레임에 적용합니다.
     */
    public void applyCurrentThemeToAllPanels() {
        GameModel.Theme currentTheme = gameModel.getCurrentTheme();
        
        // 프레임 자체에 테마 적용
        ThemeManager.applyTheme(this, currentTheme);
        
        // 각 패널에 개별적으로 테마 적용
        mainMenuPanel.applyTheme(currentTheme);
        gameModeSelectionPanel.updateTheme();
        gamePanel.applyTheme(currentTheme);
        moleGamePanel.applyTheme(currentTheme);
        settingsPanel.applyTheme(currentTheme);
    }
} 