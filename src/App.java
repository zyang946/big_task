
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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


            System.out.println("============DEBUGGING============");
            System.out.println("全局符号表大小："+analyser.getGlobalTable().size());
            System.out.println("全局符号表：");
            for(Iterator<GlobalEntry> item = analyser.getGlobalTable().iterator();item.hasNext();){
                GlobalEntry tmpitem = item.next();
                System.out.println(tmpitem.toString());
            }
            System.out.println("起始函数：\n"+analyser.get_start());
            System.out.println("函数：");
            for (Iterator<Map.Entry<String, FunctionEntry>> item = analyser.getFunctionTable().entrySet().iterator(); item.hasNext();){
                Map.Entry<String, FunctionEntry> tmpitem = item.next();
                System.out.println(tmpitem.getKey() +"->" + tmpitem.getValue());
            }

            System.out.println("============编译后============");
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