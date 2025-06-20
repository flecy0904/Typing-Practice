package GameUI;

import core.GameModel;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 테마 관리를 담당하는 클래스
 * 
 * 라이트 모드와 다크 모드를 전환하고, 
 * 모든 UI 컴포넌트에 적절한 색상을 적용합니다.
 * 
 * @author JAVA 중간 프로젝트
 * @version 1.0
 */
public class ThemeManager {
    
    // 라이트 테마 색상
    private static final Color LIGHT_BACKGROUND = Color.WHITE;
    private static final Color LIGHT_FOREGROUND = Color.BLACK;
    private static final Color LIGHT_BUTTON_BACKGROUND = new Color(230, 230, 230);
    private static final Color LIGHT_BUTTON_FOREGROUND = Color.BLACK;
    private static final Color LIGHT_BUTTON_HOVER = new Color(200, 200, 200);
    private static final Color LIGHT_BUTTON_PRESSED = new Color(180, 180, 180);
    private static final Color LIGHT_PANEL_BACKGROUND = new Color(245, 245, 245);
    private static final Color LIGHT_ACCENT = new Color(100, 149, 237);
    
    // 다크 테마 색상
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARK_FOREGROUND = new Color(220, 220, 220);
    private static final Color DARK_BUTTON_BACKGROUND = new Color(70, 70, 70);
    private static final Color DARK_BUTTON_FOREGROUND = new Color(220, 220, 220);
    private static final Color DARK_BUTTON_HOVER = new Color(90, 90, 90);
    private static final Color DARK_BUTTON_PRESSED = new Color(110, 110, 110);
    private static final Color DARK_PANEL_BACKGROUND = new Color(60, 60, 60);
    private static final Color DARK_TEXT_BACKGROUND = new Color(50, 50, 50);
    private static final Color DARK_ACCENT = new Color(100, 149, 237);
    
    /**
     * 현재 테마에 따라 배경색을 반환합니다.
     * @param theme 현재 테마
     * @return 배경색
     */
    public static Color getBackgroundColor(GameModel.Theme theme) {
        return theme == GameModel.Theme.DARK ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }
    
    /**
     * 현재 테마에 따라 전경색(텍스트 색상)을 반환합니다.
     * @param theme 현재 테마
     * @return 전경색
     */
    public static Color getForegroundColor(GameModel.Theme theme) {
        return theme == GameModel.Theme.DARK ? DARK_FOREGROUND : LIGHT_FOREGROUND;
    }
    
    /**
     * 현재 테마에 따라 버튼 배경색을 반환합니다.
     * @param theme 현재 테마
     * @return 버튼 배경색
     */
    public static Color getButtonBackgroundColor(GameModel.Theme theme) {
        return theme == GameModel.Theme.DARK ? DARK_BUTTON_BACKGROUND : LIGHT_BUTTON_BACKGROUND;
    }
    
    /**
     * 현재 테마에 따라 버튼 전경색을 반환합니다.
     * @param theme 현재 테마
     * @return 버튼 전경색
     */
    public static Color getButtonForegroundColor(GameModel.Theme theme) {
        return theme == GameModel.Theme.DARK ? DARK_BUTTON_FOREGROUND : LIGHT_BUTTON_FOREGROUND;
    }
    
    /**
     * 현재 테마에 따라 버튼 호버 시 배경색을 반환합니다.
     * @param theme 현재 테마
     * @return 버튼 호버 배경색
     */
    public static Color getButtonHoverColor(GameModel.Theme theme) {
        return theme == GameModel.Theme.DARK ? DARK_BUTTON_HOVER : LIGHT_BUTTON_HOVER;
    }
    
    /**
     * 현재 테마에 따라 버튼 눌림 시 배경색을 반환합니다.
     * @param theme 현재 테마
     * @return 버튼 눌림 배경색
     */
    public static Color getButtonPressedColor(GameModel.Theme theme) {
        return theme == GameModel.Theme.DARK ? DARK_BUTTON_PRESSED : LIGHT_BUTTON_PRESSED;
    }
    
    /**
     * 현재 테마에 따라 패널 배경색을 반환합니다.
     * @param theme 현재 테마
     * @return 패널 배경색
     */
    public static Color getPanelBackgroundColor(GameModel.Theme theme) {
        return theme == GameModel.Theme.DARK ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND;
    }
    
    /**
     * 현재 테마에 따라 텍스트 영역 배경색을 반환합니다.
     * @param theme 현재 테마
     * @return 텍스트 영역 배경색
     */
    public static Color getTextBackgroundColor(GameModel.Theme theme) {
        return theme == GameModel.Theme.DARK ? DARK_TEXT_BACKGROUND : Color.WHITE;
    }
    
    /**
     * 현재 테마에 따라 강조색을 반환합니다.
     * @param theme 현재 테마
     * @return 강조색
     */
    public static Color getAccentColor(GameModel.Theme theme) {
        return theme == GameModel.Theme.DARK ? DARK_ACCENT : LIGHT_ACCENT;
    }
    
    /**
     * JPanel에 테마를 적용합니다.
     * @param panel 테마를 적용할 패널
     * @param theme 적용할 테마
     */
    public static void applyTheme(JPanel panel, GameModel.Theme theme) {
        panel.setBackground(getBackgroundColor(theme));
        panel.setForeground(getForegroundColor(theme));
        applyThemeToComponents(panel, theme);
    }
    
    /**
     * 컨테이너 내의 모든 컴포넌트에 테마를 적용합니다.
     * @param container 테마를 적용할 컨테이너
     * @param theme 적용할 테마
     */
    private static void applyThemeToComponents(Container container, GameModel.Theme theme) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                applyButtonHoverEffect(button, theme);
                button.setBorder(BorderFactory.createLineBorder(getAccentColor(theme), 2));
                button.setFocusPainted(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setForeground(getForegroundColor(theme));
            } else if (component instanceof JTextField) {
                JTextField textField = (JTextField) component;
                textField.setBackground(getTextBackgroundColor(theme));
                textField.setForeground(getForegroundColor(theme));
                textField.setCaretColor(getForegroundColor(theme));
                textField.setBorder(new LineBorder(getAccentColor(theme), 1));
            } else if (component instanceof JTextArea) {
                JTextArea textArea = (JTextArea) component;
                textArea.setBackground(getTextBackgroundColor(theme));
                textArea.setForeground(getForegroundColor(theme));
                textArea.setCaretColor(getForegroundColor(theme));
            } else if (component instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) component;
                comboBox.setBackground(getButtonBackgroundColor(theme));
                comboBox.setForeground(getForegroundColor(theme));
            } else if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                panel.setBackground(getPanelBackgroundColor(theme));
                panel.setForeground(getForegroundColor(theme));
                applyThemeToComponents(panel, theme);
            } else if (component instanceof Container) {
                applyThemeToComponents((Container) component, theme);
            }
        }
    }
    
    /**
     * 버튼에 호버 효과를 적용합니다.
     * @param button 효과를 적용할 버튼
     * @param theme 현재 테마
     */
    private static void applyButtonHoverEffect(JButton button, GameModel.Theme theme) {
        // 이미 리스너가 추가되어 있다면 중복 추가하지 않음
        if (button.getClientProperty("hoverEffectAdded") != null) {
            return;
        }
        button.putClientProperty("hoverEffectAdded", true);

        // 스타일(폰트, 커서, 테두리)만 설정
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(getAccentColor(theme), 2));
        button.setForeground(getButtonForegroundColor(theme));
        button.setBackground(getButtonBackgroundColor(theme)); // 최초 진입 시만 기본색

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(getButtonHoverColor(theme));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(getButtonBackgroundColor(theme));
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(getButtonPressedColor(theme));
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    if (button.contains(e.getPoint())) {
                        button.setBackground(getButtonHoverColor(theme));
                    } else {
                        button.setBackground(getButtonBackgroundColor(theme));
                    }
                }
            }
        });
    }
    
    /**
     * JFrame에 테마를 적용합니다.
     * @param frame 테마를 적용할 프레임
     * @param theme 적용할 테마
     */
    public static void applyTheme(JFrame frame, GameModel.Theme theme) {
        frame.getContentPane().setBackground(getBackgroundColor(theme));
        applyThemeToComponents(frame.getContentPane(), theme);
        frame.repaint();
    }
} 