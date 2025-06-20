/**
 * 타이핑 연습 게임 메인 클래스
 */
import GameUI.MainFrame;
import core.GameModel;
import javax.swing.SwingUtilities;

public class TypingPracticeGame {

    /**
     * 프로그램 시작점
     * Swing EDT(Event Dispatch Thread)에서 GUI를 초기화합니다.
     * 
     * @param args 명령행 인자 (사용하지 않음)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            new MainFrame(model);
        });
    }
} 