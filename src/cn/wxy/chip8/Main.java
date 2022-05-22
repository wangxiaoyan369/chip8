package cn.wxy.chip8;

import cn.wxy.chip8.core.Machine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException {
        Machine machine = new Machine();
        InputStream is;
        if (args.length != 0) {
            File file = new File(args[0]);
            if (!file.exists()) {
                throw new RuntimeException("文件不存在：" + args[0]);
            }
            is = new FileInputStream(file);
        } else {
            is = Main.class.getResourceAsStream("/tetris.bin");
        }
        assert is != null;
        int[] data = readFile(is);
        machine.loadRom(data);
        machine.run();
    }

    public static int[] readFile(InputStream is) throws IOException {
        byte[] bytes = new byte[is.available()];
        int len = is.read(bytes);
        is.close();

        int[] data = new int[len];
        for (int i = 0; i < len; i++) {
            data[i] = bytes[i] & 0xff;
        }
        return data;
    }
}
