package GameUI;

import java.awt.Toolkit;

/**
 * 게임에서 사용하는 소리 효과를 관리하는 유틸리티 클래스입니다.
 * 
 * 현재는 시스템 기본 비프음을 사용하지만, 추후 커스텀 사운드 파일을
 * 추가할 수 있도록 확장 가능하게 설계되었습니다.
 */
public class SoundUtils {
    
    /**
     * 버튼 클릭 시 재생할 소리 효과입니다.
     * 시스템 기본 비프음을 사용합니다.
     */
    public static void playClickSound() {
        try {
            // 시스템 기본 비프음 재생
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
            // 소리 재생 실패 시 무시 (필수 기능이 아니므로)
            System.err.println("클릭 소리 재생 실패: " + e.getMessage());
        }
    }
    
    /**
     * 성공 시 재생할 소리 효과입니다.
     * 현재는 클릭 소리와 동일하지만, 추후 다른 소리로 변경 가능합니다.
     */
    public static void playSuccessSound() {
        try {
            // 두 번의 짧은 비프음으로 성공 표현
            Toolkit.getDefaultToolkit().beep();
            Thread.sleep(50);
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
            System.err.println("성공 소리 재생 실패: " + e.getMessage());
        }
    }
    
    /**
     * 오류 시 재생할 소리 효과입니다.
     * 현재는 클릭 소리와 동일하지만, 추후 다른 소리로 변경 가능합니다.
     */
    public static void playErrorSound() {
        try {
            // 긴 비프음으로 오류 표현
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
            System.err.println("오류 소리 재생 실패: " + e.getMessage());
        }
    }
} 