package pojo;

import Threads.ThreadsMagic;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Product {

    private static Logger logger = Logger.getLogger(Product.class.getName());

    private String hash;
    private String previousHash;
    private Long TimeStamp;
    private int nonce;
    private int id;
    private int CodeOfProduct;
    private String TitleOfProduct;
    private int Price;
    private String DescriptionOfProduct;

    //-----------Default constructor--------------------------------------------
    public Product() {
    }
//--------- ctor με την data και τα απαραίτητα πεδία δημιουργίας του blockchain--------

    public Product(int id,int codeOfProduct,String titleOfProduct,int price,String descriptionOfProduct, Long timeStamp,String previousHash) {
        this.id = id;
        CodeOfProduct = codeOfProduct;
        this.previousHash = previousHash;
        TimeStamp = timeStamp;
        TitleOfProduct = titleOfProduct;
        Price = price;
        DescriptionOfProduct = descriptionOfProduct;
        hash = calculateBlockHash();
    }
    //--------- ctor με την data και τα απαραίτητα πεδία δημιουργίας του blockchain ΑΛΛΑ  ΧΩΡΙΣ ΤΟ ID--------
    public Product(int codeOfProduct,String titleOfProduct,int price,String descriptionOfProduct, Long timeStamp,String previousHash) {
        this.previousHash = previousHash;
        TimeStamp = timeStamp;
        this.nonce = nonce;
        CodeOfProduct = codeOfProduct;
        TitleOfProduct = titleOfProduct;
        Price = price;
        DescriptionOfProduct = descriptionOfProduct;
        hash = calculateBlockHash();
    }
    //------- Πλήρες ctor------------------------------------------------------------------------
    public Product(int id,int codeOfProduct,String titleOfProduct,int price,String descriptionOfProduct, Long timeStamp,String previousHash,String hash) {
        this.hash = hash;
        this.previousHash = previousHash;
        TimeStamp = timeStamp;
        this.id = id;
        CodeOfProduct = codeOfProduct;
        TitleOfProduct = titleOfProduct;
        Price = price;
        DescriptionOfProduct = descriptionOfProduct;
    }
//----------------------------------------Ακολουθούν μέθοδοι mining------------------------------------
   /* public String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!hash.substring(0, prefix).equals(prefixString)) {
            nonce++;
            hash = calculateBlockHash();
        }
        return hash;
    }*/

    public String mineBlock(int prefix) {
        Object lock = new Object();
        ArrayList<Integer> buffer = new ArrayList<>();


        synchronized (lock) {
            Thread t1 = new Thread(new ThreadsMagic(id,CodeOfProduct,TitleOfProduct,Price,DescriptionOfProduct,previousHash,TimeStamp,0,536870911,lock,prefix,buffer));
            Thread t2 = new Thread(new ThreadsMagic(id,CodeOfProduct,TitleOfProduct,Price,DescriptionOfProduct,previousHash,TimeStamp,536870912,1073741824,lock,prefix,buffer));
            Thread t3 = new Thread(new ThreadsMagic(id,CodeOfProduct,TitleOfProduct,Price,DescriptionOfProduct,previousHash,TimeStamp,1073741824,1610612736,lock,prefix,buffer));
            Thread t4 = new Thread(new ThreadsMagic(id,CodeOfProduct,TitleOfProduct,Price,DescriptionOfProduct,previousHash,TimeStamp,1610612737,2147483647,lock,prefix,buffer));


            t1.start();
            t2.start();
            t3.start();
            t4.start();

            try {
                //Περίμενε μέχρι να βρούν τα Threads το nonce και μόλις το βρούν συνεχίζουν απο το wait και θα πάρουν το nonce απο τον buffer
                lock.wait();
                nonce = buffer.get(0);
                hash = calculateBlockHash();
                return hash;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }

    public String calculateBlockHash() {
        String dataToHash = previousHash + Long.toString(TimeStamp) + Integer.toString(nonce) + Integer.toString(id) + Integer.toString(CodeOfProduct) + TitleOfProduct+ Integer.toString(Price) + DescriptionOfProduct;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }
//-------------------------getters and setters----------------------------------------
    public static Logger getLogger() {
        return logger;
    }
    public String getHash() {
        return hash;
    }
    public String getPreviousHash() {
        return previousHash;
    }
    public Long getTimeStamp() {
        return TimeStamp;
    }
    public int getNonce() {
        return nonce;
    }
    public int getId() {
        return id;
    }
    public int getCodeOfProduct() {
        return CodeOfProduct;
    }
    public String getTitleOfProduct() {
        return TitleOfProduct;
    }
    public int getPrice() {
        return Price;
    }
    public String getDescriptionOfProduct() {
        return DescriptionOfProduct;
    }
    public static void setLogger(Logger logger) { Product.logger = logger; }
    public void setHash(String hash) { this.hash = hash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }
    public void setTimeStamp(Long timeStamp) { TimeStamp = timeStamp; }
    public void setNonce(int nonce) { this.nonce = nonce; }
    public void setId(int id) { this.id = id; }
    public void setCodeOfProduct(int codeOfProduct) { CodeOfProduct = codeOfProduct; }
    public void setTitleOfProduct(String titleOfProduct) { TitleOfProduct = titleOfProduct; }
    public void setPrice(int price) { Price = price; }
    public void setDescriptionOfProduct(String descriptionOfProduct) { DescriptionOfProduct = descriptionOfProduct; }
}
