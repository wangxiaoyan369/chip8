package cn.wxy.chip8.core;

public class Instruction {
    private final int val;
    int opcode;
    int x;
    int y;
    int n;
    int kk;
    int nnn;
    int flag;

    public Instruction(int val){
        this.val = val;
    }

    public void decode() {
        this.opcode = this.val & 0xF000;
        this.x = (this.val & 0x0F00) >> 8;
        this.y = (this.val & 0x00F0) >> 4;
        this.n = this.val & 0x000F;
        this.kk = this.val & 0x00FF;
        this.nnn = this.val & 0x0FFF;
        this.flag = this.val & 0x000F;
    }
}
