package cn.wxy.chip8.core;

import cn.wxy.chip8.constant.Chip8Constant;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Map;

public class Keyboard extends JPanel {

    private int keyTime;
    private final int keyScanRate;
    private final int[] keyBuffer;
    private final static Map<Character, Integer> keyMap = Chip8Constant.KEY_MAP;

    public Keyboard(int[] keyBuffer) {
        this.keyTime = 0;
        this.keyScanRate = 120;
        this.keyBuffer = keyBuffer;

        keyMap.forEach((k, v) -> {
            this.getInputMap().put(KeyStroke.getKeyStroke(k), String.valueOf(k));
            this.getActionMap().put(String.valueOf(k), new KeyAction(k, keyBuffer));
        });
    }

    public void poll_event() {
        if(this.keyTime == 0) {
            Arrays.fill(this.keyBuffer, 0);
        }
        this.keyTime++;
        this.keyTime %= this.keyScanRate;
    }

    static class KeyAction extends AbstractAction {
        private final Character key;
        private final int[] keyBuffer;

        public KeyAction(Character key, int[] keyBuffer) {
            this.key = key;
            this.keyBuffer = keyBuffer;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (keyMap.containsKey(key)) {
                this.keyBuffer[keyMap.get(key)] = 1;
            }
        }
    }
}
