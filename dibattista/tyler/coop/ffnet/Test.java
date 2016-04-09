public class Test{
    
    public static void main(String[] args){
        int total = 0;
        for(int i = 1; i < 21; i++){
            if(Math.random() < 0.3){
                continue;
            }
            total++;
            System.out.println(i);
        }
        System.out.println("Total: " + total);
    }
    
}