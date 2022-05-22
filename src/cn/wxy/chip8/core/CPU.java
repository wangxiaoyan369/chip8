package cn.wxy.chip8.core;

import cn.wxy.chip8.constant.Chip8Constant;

import java.util.Arrays;
import java.util.Random;

public class CPU {
    private final Memory memory;
    private final int[] reg;
    private int pc;
    private int i;
    final int[][] screenBuf;
    final int[] keyPressedBuf;
    private int delayTimer;
    private int soundTimer;
    private Instruction ir;
    boolean drawFlag;
    long lastTime;

    public CPU() {
        this.memory = new Memory();
        this.reg = new int[16];
        this.screenBuf = new int[Chip8Constant.HEIGHT][Chip8Constant.WIDTH];
        this.keyPressedBuf = new int[16];
        this.pc = Chip8Constant.START_ADDRESS;
        this.ir = null;
        this.drawFlag = false;
        this.lastTime = System.currentTimeMillis();
    }

    public void loadRom(int[] data) {
        for (int x = 0; x < data.length; x++) {
            this.memory.ram[Chip8Constant.START_ADDRESS + x] = data[x];
        }
    }

    public void clearScreen() {
        for (int[] ints : this.screenBuf) {
            Arrays.fill(ints, 0);
        }
    }

    public void cycle() {
        this.fetch();
        this.decode();
        this.execute();
    }

    public void ticker() {
        if (this.delayTimer > 0) {
            this.delayTimer -= 1;
        }
        if (this.soundTimer > 0) {
            this.soundTimer -= 1;
        }
    }

    public void fetch() {
        int high = this.memory.ram[this.pc];
        int low = this.memory.ram[this.pc + 1];
        int instruction = (high << 8) | low;
        this.pc += 2;
        this.ir = new Instruction(instruction);
    }

    public void decode() {
        this.ir.decode();
    }

    public void execute() {
        int x = this.ir.x;
        int y = this.ir.y;
        int n = this.ir.flag;
        int kk = this.ir.kk;
        int nnn = this.ir.nnn;
        int addr = this.ir.nnn;
        switch (this.ir.opcode) {
            case 0x0000:
                if (this.ir.kk == 0x00E0) {
                    this.clearScreen();
                    this.drawFlag = true;
                } else if (this.ir.kk == 0x00EE) {
                    this.pc = this.memory.stackPop();
                }
                break;
            case 0x1000:
                this.pc = nnn;
                break;
            case 0x2000:
                this.memory.stackPush(this.pc);
                this.pc = addr;
                break;
            case 0x3000:
                if (this.reg[x] == kk) {
                    this.pc += 2;
                }
                break;
            case 0x4000:
                if (this.reg[x] != kk) {
                    this.pc += 2;
                }
                break;
            case 0x5000:
                if (this.reg[x] == this.reg[y]) {
                    this.pc += 2;
                }
                break;
            case 0x6000:
                this.reg[x] = kk;
                break;
            case 0x7000:
                this.reg[x] += kk;
                this.reg[x] &= 0xff;
                break;
            case 0x8000:
                if (this.ir.flag == 0x0000) {
                    this.reg[x] = this.reg[y];
                } else if (this.ir.flag == 0x0001) {
                    this.reg[x] |= this.reg[y];
                } else if (this.ir.flag == 0x0002) {
                    this.reg[x] &= this.reg[y];
                } else if (this.ir.flag == 0x0003) {
                    this.reg[x] ^= this.reg[y];
                } else if (this.ir.flag == 0x0004) {
                    this.reg[x] += this.reg[y];
                    this.reg[0x0F] = this.reg[x] > 0xFF ? 0x01 : 0x00;
                    this.reg[x] &= 0xFF;
                } else if (this.ir.flag == 0x0005) {
                    this.reg[0x0F] = this.reg[x] < this.reg[y] ? 0x00 : 0x01;
                    this.reg[x] -= this.reg[y];
                    this.reg[x] &= 0xFF;
                } else if (this.ir.flag == 0x0006) {
                    this.reg[0x0F] = this.reg[x] & 0x01;
                    this.reg[x] >>= 1;
                } else if (this.ir.flag == 0x0007) {
                    this.reg[0x0F] = this.reg[x] < this.reg[y] ? 0x01 : 0x00;
                    this.reg[x] = this.reg[y] - this.reg[x];
                    this.reg[x] &= 0xFF;
                } else if (this.ir.flag == 0x000E) {
                    this.reg[0x0F] = (this.reg[x] >> 7) & 0x01;
                    this.reg[x] = this.reg[x] << 1;
                    this.reg[x] &= 0xFF;
                }
                break;
            case 0x9000:
                if (this.reg[x] != this.reg[y]) {
                    this.pc += 2;
                }
                break;
            case 0xA000:
                this.i = this.ir.nnn;
                break;
            case 0xB000:
                this.pc = this.reg[0] + addr;
                break;
            case 0xC000:
                this.reg[x] = new Random().nextInt(255) & kk;
                break;
            case 0xD000:
                int vx = this.reg[x];
                int vy = this.reg[y];
                this.reg[0xF] = 0;
                for (int yy = 0; yy < n; yy++) {
                    int sysByte = this.memory.ram[this.i + yy];
                    for (int xx = 0; xx < 8; xx++) {
                        int xCord = vx + xx;
                        int yCord = vy + yy;
                        if (xCord < Chip8Constant.WIDTH && yCord < Chip8Constant.HEIGHT) {
                            int sysBit = (sysByte >> (7 - xx)) & 0x01;
                            if ((this.screenBuf[yCord][xCord] & sysBit) == 1) {
                                this.reg[0xF] = 1;
                            }
                            this.screenBuf[yCord][xCord] ^= sysBit;
                        }
                    }
                }
                this.drawFlag = true;
                break;
            case 0xE000:
                if (this.ir.kk == 0x009E && this.keyPressedBuf[this.reg[x]] == 1) {
                    this.pc += 2;
                } else if (this.ir.kk == 0x00A1 && this.keyPressedBuf[this.reg[x]] == 0) {
                    this.pc += 2;
                }
                break;
            case 0xF000:
                if (this.ir.kk == 0x0007) {
                    this.reg[x] = this.delayTimer;
                } else if (this.ir.kk == 0x0015) {
                    this.delayTimer = this.reg[x];
                } else if (this.ir.kk == 0x0018) {
                    this.soundTimer = this.reg[x];
                } else if (this.ir.kk == 0x001E) {
                    this.i += this.reg[x];
                } else if (this.ir.kk == 0x000A) {
                    boolean pressed = false;
                    for (int a = 0; a < 16; a++) {
                        if (this.keyPressedBuf[a] == 1) {
                            this.reg[x] = a;
                            pressed = true;
                            break;
                        }
                    }
                    if (!pressed) {
                        this.pc -= 2;
                    }
                } else if (this.ir.kk == 0x0029) {
                    this.i = this.reg[x] * 5;
                } else if (this.ir.kk == 0x0033) {
                    this.memory.ram[this.i] = this.reg[x] / 100;
                    this.memory.ram[this.i + 1] = (this.reg[x] % 100) / 10;
                    this.memory.ram[this.i + 2] = (this.reg[x] % 100) % 10;
                } else if (this.ir.kk == 0x0055) {
                    for (int a = 0; a < x + 1; a++) {
                        this.memory.ram[this.i + a] = this.reg[a];
                    }
                } else if (this.ir.kk == 0x0065) {
                    for (int a = 0; a < x + 1; a++) {
                        this.reg[a] = this.memory.ram[this.i + a];
                    }
                }
                break;
        }
    }
}
