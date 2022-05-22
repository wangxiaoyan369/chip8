package cn.wxy.chip8.core;

import cn.wxy.chip8.constant.Chip8Constant;

import javax.swing.JFrame;
import java.awt.Dimension;

public class Machine extends JFrame {
    private final Screen screen;
    private final Keyboard keyboard;
    private final CPU cpu;
    private boolean power;
    private final int width = Chip8Constant.WIDTH;
    private final int height = Chip8Constant.HEIGHT;
    private final int zoom = Chip8Constant.ZOOM;

    public Machine() {
        this.cpu = new CPU();
        this.screen = new Screen(width, height, zoom);
        this.keyboard = new Keyboard(this.cpu.keyPressedBuf);

        connectScreen(screen);
        connectKeyboard(keyboard);
        init();
    }

    public void connectScreen(Screen screen) {
        this.screen.setPreferredSize(new Dimension(width * zoom, height * zoom));
        this.add(screen);
        this.pack();
    }

    public void connectKeyboard(Keyboard keyboard){
        this.add(keyboard);
    }

    public void init(){
        this.setTitle(Chip8Constant.TITLE);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.power = true;
    }

    public void loadRom(int[] data) {
        this.cpu.loadRom(data);
    }

    public void run() {
        int cycles = 0;
        while (this.power) {
            this.cpu.cycle();
            this.keyboard.poll_event();
            if (this.cpu.drawFlag) {
                this.screen.render(this.cpu.screenBuf);
                this.cpu.drawFlag = false;
            }

            cycles += 1;
            try {
                Thread.sleep(1000 / Chip8Constant.CLOCK_SPEED);
                if (cycles >= Chip8Constant.CLOCK_SPEED / Chip8Constant.TIMER_SPEED) {
                    cycles = 0;
                    this.cpu.ticker();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
