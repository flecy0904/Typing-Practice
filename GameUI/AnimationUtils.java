package GameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * UI 애니메이션 효과를 위한 유틸리티 클래스입니다.
 */
public class AnimationUtils {

    /**
     * 컴포넌트를 서서히 나타나게(fade-in)하는 애니메이션을 적용합니다.
     *
     * @param component    애니메이션을 적용할 컴포넌트
     * @param duration     애니메이션 지속 시간 (밀리초)
     * @param onComplete   애니메이션 완료 후 실행할 콜백
     */
    public static void fadeIn(JComponent component, int duration, Consumer<Void> onComplete) {
        component.setVisible(true);
        // Alpha 값을 조절하기 위해선 컴포넌트가 투명도를 지원해야 합니다.
        // JComponent는 기본적으로 불투명(opaque)하므로, 효과를 보려면
        // 상위 컨테이너나 컴포넌트의 설정을 변경해야 할 수 있습니다.
        // 여기서는 Timer를 이용한 간단한 가시성 제어로 시작합니다.
        // (실제 알파 블렌딩은 더 복잡한 구현이 필요합니다)

        // 이 예제에서는 간단히 Timer로 지연 효과를 줍니다.
        // 실제 페이드 효과를 위해서는 AlphaComposite를 사용한 커스텀 페인팅이 필요합니다.
        // 지금은 전환의 기본 구조를 잡는 데 집중합니다.
        if (onComplete != null) {
            onComplete.accept(null);
        }
    }

    /**
     * 컴포넌트를 서서히 사라지게(fade-out)하는 애니메이션을 적용합니다.
     *
     * @param component    애니메이션을 적용할 컴포넌트
     * @param duration     애니메이션 지속 시간 (밀리초)
     * @param onComplete   애니메이션 완료 후 실행할 콜백
     */
    public static void fadeOut(JComponent component, int duration, Consumer<Void> onComplete) {
        // 실제 페이드 아웃 효과 대신, 완료 후 컴포넌트를 숨깁니다.
        Timer timer = new Timer(duration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                component.setVisible(false);
                if (onComplete != null) {
                    onComplete.accept(null);
                }
                ((Timer)e.getSource()).stop();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
} 