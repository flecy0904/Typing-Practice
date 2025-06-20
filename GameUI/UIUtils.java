package GameUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIUtils {

    /**
     * JComponent에 마우스 호버 효과(손 모양 커서)를 추가합니다.
     *
     * @param component 호버 효과를 적용할 컴포넌트
     */
    public static void addHoverEffect(JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                component.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    /**
     * JButton에 마우스 호버 효과(손 모양 커서 + 배경색 변경)를 추가합니다.
     *
     * @param button 호버 효과를 적용할 버튼
     */
    public static void addButtonHoverEffect(JButton button) {
        final Color originalColor = button.getBackground();
        final boolean originalOpaque = button.isOpaque();
        final Color hoverColor = originalColor.brighter();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setOpaque(true);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setBackground(originalColor);
                button.setOpaque(originalOpaque);
            }
        });
    }
} 