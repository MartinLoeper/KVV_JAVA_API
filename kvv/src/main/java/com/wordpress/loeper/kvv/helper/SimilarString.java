/*
 * DenPashkov 2012 
 * http://www.facebook.com/pashkovdenis
 * PhP Similar String  Implementation 
 * 30.07.2012 
 * 
 */
package com.wordpress.loeper.kvv.helper;

public class SimilarString {

    private String  string = "" ;
    private String string2 = ""; 
    public int procent = 0 ; 
    private int position1 =0 ; 
    private int position2 =0;

    // Similar String 
    public SimilarString(String str1,  String str2){
        this.string = str1.toLowerCase();   
        this.string2 = str2.toLowerCase(); 
    }
    public SimilarString() {

    }
    // Set string 
    public SimilarString setString(String str1,  String str2){
        this.string = str1.toLowerCase(); 
        this.string2 = str2.toLowerCase(); 
        return this ; 
    }

 //get Similar 
    public int  similar(){
        string= string.trim() ; 
        string2= string2.trim();
     int len_str1 = string.length() ;
        int len_str2 = string2.length() ; 

        int max= 0; 
        if (string.length()>1 && string2.length()>1 ){
            // iterate 
            for (int p=0  ; p<=len_str1; p++){
                for (int q=0  ; q<=len_str2; q++){
                    for(int l=0 ; (p + l < len_str1) && (q + l < len_str2) && (string.charAt(l) == string2.charAt(l)); l++){
                        if (l>max){
                            max=l ; 
                            position1 = p ; 
                            position2 = q; 
                        }
                    }
                }
            }

         //sim * 200.0 / (t1_len + t2_len)
        this.procent = max * 200 / ((string.length()) + (string2.length())  - (max) + (position2 - position1)   ) - (max*string.length() ) ;
        if (procent>100) procent = 100; 
        if (procent<0) procent = 0; 
        }
        return this.procent ; 
    }
}