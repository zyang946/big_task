

import java.io.*;
import java.util.List;
import java.util.Scanner;


public class App {
    public static void main(String[] args) throws FileNotFoundException, CompileError{
            InputStream input = new FileInputStream(args[0]);
            //InputStream input = new FileInputStream("1.txt");
            Scanner scanner = new Scanner(input);
            StringIter it = new StringIter(scanner);
            Tokenizer tokenizer = new Tokenizer(it);
            Analyser analyser = new Analyser(tokenizer);
            analyser.analyse();

            MiniVm out = new MiniVm(analyser.getGlobalTable(), analyser.getFunctionTable(), analyser.get_start());
            List<Byte> bytes = out.getOutput();
            byte[] result = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); ++i) {
                result[i] = bytes.get(i);
                System.out.println(bytes.get(i));
            }

            DataOutputStream output = new DataOutputStream(new FileOutputStream(new File(args[1])));
            //DataOutputStream output = new DataOutputStream(new FileOutputStream(new File("2.txt")));
            try {
				output.write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
}