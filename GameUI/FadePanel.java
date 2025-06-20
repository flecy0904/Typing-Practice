package GameUI;

import javax.swing.*;
import java.awt.*;

/**
 * 페이드 인/아웃 애니메이션을 지원하는 패널 클래스.
 * CardLayout을 감싸 부드러운 화면 전환을 구현합니다.
 */
public class FadePanel extends JPanel {
    private float alpha = 1.0f; // 시작 시 불투명
    private Timer timer;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private String currentCardName;
    private boolean isInitialDisplay = true; // 첫 화면인지 확인하는 플래그

    public FadePanel() {
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
    }

    // CardLayout에 컴포넌트 추가
    public void addCard(Component comp, String name) {
        cardPanel.add(comp, name);
    }

    @Override
    protected void paintChildren(Graphics g) {
        // 최적화: 완전히 불투명하면 AlphaComposite를 적용할 필요 없음
        if (alpha == 1.0f) {
            super.paintChildren(g);
            return;
        }
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        super.paintChildren(g2d);
        g2d.dispose();
    }

    public void showComponent(String name) {
        if (name.equals(currentCardName)) return;

        // 첫 화면 표시면 애니메이션 없이 즉시 표시
        if (isInitialDisplay) {
            isInitialDisplay = false;
            currentCardName = name;
            cardLayout.show(cardPanel, name);
            alpha = 1.0f;
            repaint();
        } else {
            fadeOut(() -> {
                currentCardName = name;
                cardLayout.show(cardPanel, name);
                fadeIn();
            });
        }
    }

    private void fadeIn() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        alpha = 0.0f;
        
        timer = new Timer(20, e -> {
            alpha += 0.1f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void fadeOut(Runnable onComplete) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        alpha = 1.0f;

        timer = new Timer(20, e -> {
            alpha -= 0.1f;
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                ((Timer)e.getSource()).stop();
                if (onComplete != null) {
                    SwingUtilities.invokeLater(onComplete);
                }
            }
            repaint();
        });
        timer.setRepeats(true);
        timer.start();
    }
} 