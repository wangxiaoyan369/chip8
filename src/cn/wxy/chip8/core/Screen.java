package cn.wxy.chip8.core;

import cn.wxy.chip8.constant.Chip8Constant;

import javax.swing.JPanel;
import java.awt.Graphics;

public class Screen extends JPanel {

    private final int width;
    private final int height;
    private final int zoom;

    private final int[][] frameBuffer;

    public Screen(int width, int height, int zoom) {
        this.width = width;
        this.height = height;
        this.zoom = zoom;
        this.frameBuffer = new int[height][width];
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Chip8Constant.BG_COLOR);
        g.fillRect(0, 0, width * zoom, height * zoom);
        g.setColor(Chip8Constant.FG_COLOR);
        for (int y = 0; y < frameBuffer.length; y++) {
            for (int x = 0; x < frameBuffer[y].length; x++) {
                if(this.frameBuffer[y][x] == 1) {
                    g.fillRect(x * zoom, y * zoom, zoom, zoom);
                }
            }
        }
    }
    public void render(int[][] data) {
        System.arraycopy(data, 0, this.frameBuffer, 0, this.frameBuffer.length);
        this.repaint();
    }
}
