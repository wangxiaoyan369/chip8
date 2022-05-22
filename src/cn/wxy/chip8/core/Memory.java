package cn.wxy.chip8.core;

import cn.wxy.chip8.constant.Chip8Constant;

import java.util.Stack;

public class Memory {
    public final int[] ram;
    private final Stack<Integer> stack;

    public Memory() {
        this.ram = new int[1024 * 4];
        System.arraycopy(Chip8Constant.FONTS, 0, this.ram, 0, Chip8Constant.FONTS.length);
        this.stack = new Stack<>();
    }

    public int stackPop() {
        return this.stack.pop();
    }

    public int stackPush(int n) {
        return this.stack.push(n);
    }
}
