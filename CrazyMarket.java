import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class CrazyMarket implements MyQueue<Customer>{

    String tekerleme = "O piti piti karamela sepeti "
            + "\nTerazi lastik jimnastik "
            + "\nBiz size geldik bitlendik Hamama gittik temizlendik.";

    Node front=null, back=null;
    private int queueSize; // queue size


    private class Node {
        Customer data=new Customer();
        Node next;
    }

    private class CustomerIterator implements Iterator<Customer>{
        private Node itr = front;
        @Override
        public boolean hasNext() {
            return itr != null;
        }
        @Override
        public Customer next() {
            Customer data = itr.data;
            itr = itr.next;
            return data;
        }
    }

    public Iterator<Customer> iterator() {
        return new CustomerIterator();
    }


    int numOfEnter=0;//Kuyruğa giren müşteri sayısı
    int numOfExit=0;//Kuyruktan çıkan ve kasaya giren müşteri sayısı
    int currenttime=0;//Mevcut zaman
    int nextEnterTime=0;//Sonraki giriş zamanı
    int kuyrukSure=0;//Kuyruktaki tüm müşterilerin hizmet alması için gereken süre
    int kasasüre=0;//Kasadaki müşterinin işinin bitmesine kalan süre
    int kasaId=0;//Kasadaki müşterinin Id'si


    public CrazyMarket(int numberOfCustomer) {//default tekerleme kullanır
        simulator(numberOfCustomer, tekerleme);
    }
    public CrazyMarket(int numberOfCustomer, String tekerleme) {//verileni kullanır
        simulator(numberOfCustomer, tekerleme);
    }


    public void simulator(int numberOfCustomer, String tekerleme){
        Random random=new Random();

        queueSize = 0;
        this.tekerleme=tekerleme;

        nextEnterTime= (currenttime+ random.nextInt(3));//ilk müşterinin gelme zamanını belirledik(0,1 veya 2. saniyede gelecek)

        while(!(numOfExit==numberOfCustomer+1)){//son müşteri kasadan ayrılana kadar
            System.out.println("\n"+currenttime+". Saniye'de olanlar ");

            //Sıraya girmeler
            //Sıradaki elemanın gelme vaktindeysek yeni müşteri gelir.
            //Bekleme süresi 0 olabileceğinden, aynı anda birden çok müşteri gelebilir. Bu yüzden while kullandım
            while(nextEnterTime==currenttime){

                //Yeni bir müşteri oluşturuyor, özelliklerini belirliyor ve listeye ekliyorum
                Customer giren = new Customer();
                giren.arrivalTime=currenttime;//Geliş zamanı, şuanki zaman
                giren.removalTime=random.nextInt(3)+1;//Kasada bekleme süresi 0-3 saniye arası
                giren.ID=numOfEnter+1;//Benzersiz bir ID verdik

                kuyrukSure+= giren.removalTime;//Kuyruktakilerin toplam bekleme süresini arttırdık

                enqueue(giren);//Queue üzerinde ekleme işlemi yaptık
                numOfEnter++;

                nextEnterTime= (currenttime+ random.nextInt(3));//Sıradaki müşterinin geliş zamanını ayarladık

                System.out.println("Yeni müşteri " +nextEnterTime+ ". saniyede girecek");
            }

            //Sıradan çıkmalar
            if(kasasüre==0){//Kasadaki adamın işi bittiyse
                if(kasaId!=0) {System.out.println("Müşteri " + kasaId + "  Kasadan çıktı"); kasaId=0;}

                if(!isEmpty()) {//Kuyrukta bekleyen varsa
                    numOfExit++;
                    if(numberOfCustomer>=numOfExit){//Eğer istenen sayıda müşteri kasaya girdiyse yeni müşteri alınmayacak
                        kasasüre=chooseCustomer().removalTime;//Kasanın meşguliyet süresini ayarladım
                        System.out.println("Bu müşteri kasada "+ kasasüre+ " saniye kalacak");
                        kuyrukSure-=kasasüre;
                    }
                }
            }
            else System.out.println("Kasanın Boşalmasına Kalan süre:" + kasasüre);

            if(queueSize>0){
                System.out.println("Kuyruktaki son müşterinin kasaya ulaşmasına kalan zaman:"+(kasasüre+kuyrukSure-back.data.removalTime));
            }
            //Mevcut durumda tüm müşterilerin hizmet alması için gereken süre

            if(kasasüre>0) {// biri varsa
                kasasüre--;
            }//Kasanın bekleme süresini azalttım

            currenttime++;

            System.out.println("Kuyruktaki insan sayısı:" + queueSize);
        }
        print();//Program bittiğinde kuyrukta kalan elemanları yazdırır.
    }

    public Customer chooseCustomer() {
        if((currenttime-front.data.arrivalTime)>10) { return dequeuNext(); } //En öndeki elemanın bekleme süresi 10'dan fazla ise
        else return dequeuWithCounting(tekerleme);//Değilse
    }

    @Override
    public boolean enqueue(Customer gelen) {

        Node oldback = back;
        back = new Node();
        back.data = gelen;
        back.next = null;
        if (isEmpty())
        {
            front = back;
        }
        else  {
            oldback.next = back;
        }
        queueSize++;

        System.out.print("Müşteri " + (gelen.ID)+ " Kuyruğa girdi.  ");
        return true;
    }

    @Override
    public Customer dequeuNext() {
        Customer data = front.data;
        front = front.next;
        queueSize--;
        if (isEmpty())
        {
            front=null;
            back = null;
        }

        System.out.print("Müşteri " + (data.ID)+ "  Kasada. ");
        kasaId= data.ID;
        return data;

    }

    @Override
    public Customer dequeuWithCounting(String tekerleme) {

        System.out.println("tekerlemeyle seçiliyor");

        int silinenIndex=(numberOfSyllable(tekerleme)%queueSize-1);
        if(silinenIndex==-1) silinenIndex=queueSize-1;

        int sayaç=0;
        int id=0;

        Iterator itr = this.iterator();
        while (sayaç != silinenIndex+1){//İteratörü silinecek yere getirdim.
            id = ((Customer)itr.next()).ID;
            sayaç++;
        }

        //Buraya kadar yapılanlar sayesinde silinecek müşterinin id'sini belirledik.

        System.out.println("\n"+id+". müşteriyi seçtim");

        Node d = front,prev = null;
        while(d != null){
            if(d.data.ID == id){
                if(front.data.ID == id){ // eğer front silinecekse özel durumdur
                    front = d.next;
                    break;
                }
                else {
                    prev.next = d.next; // normal nodelar silinirken
                    break;
                }

            }
            else { prev = d; d = d.next; } // bir sonraki nodeye geçme
        }

        if (back.data.ID==id) back=prev;

        //Belirlenen müşteriyi sildik.

        queueSize--;
        if (isEmpty())
        {
            front=null;
            back = null;
        }

        System.out.print("Müşteri " + id+ "  Kasada. ");
        kasaId= id;

        return d.data;
    }

    int numberOfSyllable(String tekerleme){//Girilen metindeki sesli harf sayısını bulur.
        int sayac=0;
        String sesliler="AEIOUaeiou";

        for(int i = 0 ; i<=tekerleme.length()-1;i++)
        {
            for(int j= 0 ; j<sesliler.length();j++) {
                if(tekerleme.charAt(i)==sesliler.charAt(j)) {
                    sayac++;
                    System.out.print(tekerleme.charAt(i)+" ");
                }
            }
        }
        return sayac;

    }

    @Override
    public int size() {
        return queueSize;
    }

    @Override
    public boolean isEmpty() {
        return (size() == 0);
    }//dolu-boş döndürüyor

    void print(){

        System.out.println("\n-----------------------------------------------------------------------------------------------");
        System.out.println("\nİstenilen sayıda müşteri kasaya ulaştı. Sırada "+queueSize+" müşteri kaldı");
        System.out.print("\nSırada bekleyen müşterilerin bilgileri:");

        if(isEmpty()){ System.out.println("boş"); return; }

        int süre=0;
        Iterator itr = iterator();

        while (itr.hasNext()){
            Customer item = (Customer) itr.next();
            System.out.print("\n"+item.ID+". müşterinin geliş zamanı: "+item.arrivalTime+"   Kasada bekleme süresi: "+item.removalTime+"   Kuyrukta beklediği süre: "+(currenttime- item.arrivalTime-1));
            //Kasaya ulaşmasına kalan zaman: "+(süre+kasasüre+1)
            süre+=item.removalTime;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        new CrazyMarket(100);
       // new CrazyMarket(100,"Uc tunc tas has hosaf");
    }
}