import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.concurrent.TimeUnit;

public class DataStore {
    public TtlHashMap<String, Path> myMap = new TtlHashMap<String, Path>();
    
    String defaultPath = "D:\\";
    private static String getContent(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public void create() throws Exception{
    	Scanner sc = new Scanner(System.in);
    	System.out.println("enter key in caps");
    	String key = sc.next();
    	if(!key.equals(key.toUpperCase())) {
    		System.out.println("key should have upper case letters only");
    		return;
    	}
    	if(key.length()>32){
    		System.out.println("key should have less than 32 chars");
    		return;
    	}
    	if(myMap.containsKey(key)){
    		System.out.println("key already exists");
    	}

    	System.out.println("Do you have file location?(y/N):");
    	String ch = sc.next();
    	if (ch.charAt(0)=='y') {
    		System.out.print("Enter path: ");
	   		String path = sc.next();
	   		Path osPath = Paths.get(path);
		   	if (!Files.exists(osPath)) {
		   		System.out.println("INVALID path");
	   			return;
	   		}
		   	if(!path.endsWith(".txt")){
		   		System.out.println("Not text file");
	   			return;
	   		}
	   		File file = new File(path);
	   		long fileSizeInBytes = file.length();
	   		long fileSizeInMB = fileSizeInBytes / (1024*1024);
	   		if(fileSizeInMB>1024){
	   			System.out.println("file size greater than 1GB");
	   			return;
	   		}
	   		System.out.println("enter time to live in seconds, enter 0 if no ttl");
	   		long ttl = sc.nextLong();
	   		myMap.put(key, osPath);
	   		myMap.putTtl(key, TimeUnit.SECONDS, ttl);
    	}
    	else if(ch.charAt(0)=='N'){
    		List<String> lines = Arrays.asList(key);
			Path file = Paths.get(defaultPath+key+".txt");
			Files.write(file, lines, StandardCharsets.UTF_8);
			System.out.println("enter time to live in seconds, enter 0 if no ttl");
	   		long ttl = sc.nextLong();
	   		myMap.put(key, file);
	   		myMap.putTtl(key, TimeUnit.SECONDS, ttl);
    	}
    	else{System.out.println("invalid Character");}


    }

    private void read() {
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Enter key:");
    	String key = sc.next();
    	if(myMap.containsKey(key)){
    		Path path = myMap.get(key);
    		String content = getContent(path.toString());
    		System.out.println(content);
    	}
    	else{
    		System.out.println("INVALID key");
    	}
    }

    private void delete() {
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Enter key:");
    	String key = sc.next();
    	if(myMap.containsKey(key)){
    		Path path = myMap.remove(key);
    		System.out.println("Deleted the "+path.toString()+" file from store!!");
    	}
    	else{
    		System.out.println("INVALID key");
    	}
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
    	DataStore ds = new DataStore();
    	try{
    		while(true){
    			System.out.print("1.create 2.read 3.delete 4.exit\nenter choice:");
    			int choice = sc.nextInt();
    			switch(choice){
    				case 1: ds.create();break;
    				case 2: ds.read();
    						break;
    				case 3: ds.delete();
    						break;
    				case 4: return;
    				default: System.out.println("invalid choice!!");
    			}
    	}
    	}catch(Exception e){
    		System.out.println(e.toString());
    		e.printStackTrace();
    	}
    }
}